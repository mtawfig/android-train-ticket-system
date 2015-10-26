package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Services.GetStations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback {

    final static int STATION_CIRCLE_RADIUS = 1000;
    final static int CLICK_MARGIN_ERROR = 500;

    private GoogleMap mMap;
    private StationsMapListener listener;
    private HashMap<Station, Marker> markers = new HashMap<>();

    private SharedDataFactory sharedData = SharedDataFactory.getInstance();

    public MapFragment() {}

    public GoogleMap getGoogleMap() {
        return mMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        fetchAndDrawStations();
    }

    public void fetchAndDrawStations() {
        final Context context = getActivity();
        new GetStations(context, new GetStations.OnGetStationsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Station> stations) {
                drawStations(stations);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute();
    }

    public HashMap<Integer, Station> groupStationsById(List<Station> stations) {
        HashMap<Integer, Station> map = new HashMap<>();
        for (Station station : stations) {
            Integer key = station.getStationId();
            if(!map.containsKey(key)) {
                map.put(key, station);
            }
        }
        return map;
    }

    public interface StationsMapListener {
        void onStationClickHandler(Station station);
        void onNoStationsFound();
        void onStationsLoaded();
    }

    public void setStationClickListener(StationsMapListener listener) {
        this.listener = listener;
    }

    public void delegateStationToListener(Station station) {
        if (listener != null) {
            listener.onStationClickHandler(station);
        }
    }

    public void delegateNoStationsFoundToListener() {
        if (listener != null) {
            listener.onNoStationsFound();
        }
    }

    public void delegateStationsLoadedToListener() {
        if (listener != null) {
            listener.onStationsLoaded();
        }
    }

    public void drawStations (final List<Station> stations) {
        mMap.clear();
        markers.clear();

        HashMap<Integer, Station> stationsById = groupStationsById(stations);

        if (stations.size() == 0) {
            delegateNoStationsFoundToListener();
            return;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(stations.get(0).getLocation()));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String stationName = marker.getSnippet();

                for (Station station : stations) {
                    if (station.getName().equals(stationName)) {
                        delegateStationToListener(station);
                        break;
                    }
                }

                return true;
            }
        });

        ArrayList<Pair<Station, Station>> stationPairs = new ArrayList<>();

        for(Station station : stations) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(station.getLocation())
                    .fillColor(Color.BLUE)
                    .strokeColor(Color.BLUE)
                    .radius(STATION_CIRCLE_RADIUS); // In meters
            mMap.addCircle(circleOptions);

            for (Station.Connection connection : station.getConnections()) {
                Pair<Station, Station> pair = new Pair<>(station, stationsById.get(connection.getToStationId()));
                if(!stationPairs.contains(pair)) {
                    stationPairs.add(pair);
                }
            }

            putMarker(station, null);
        }

        for(Pair<Station, Station> connection : stationPairs) {
            PolylineOptions rectOptions = new PolylineOptions()
                    .color(Color.BLUE)
                    .add(connection.first.getLocation())
                    .add(connection.second.getLocation());

            mMap.addPolyline(rectOptions);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickLatLng) {

                Station clickedStation = null;
                for (Station station : stations) {
                    float[] distance = new float[2];
                    LatLng stationLatLng = station.getLocation();
                    Location.distanceBetween(
                            clickLatLng.latitude, clickLatLng.longitude,
                            stationLatLng.latitude, stationLatLng.longitude,
                            distance);
                    if (distance[0] < STATION_CIRCLE_RADIUS + CLICK_MARGIN_ERROR) {
                        clickedStation = station;
                        break;
                    }
                }

                delegateStationToListener(clickedStation);
            }
        });

        delegateStationsLoadedToListener();
    }

    public Marker putMarker(Station station, Integer style) {
        IconGenerator iconFactory = new IconGenerator(getActivity());

        iconFactory.setRotation(-station.getLabelRotationDegrees());
        if (style != null) {
            iconFactory.setStyle(style);
        }
        iconFactory.setContentRotation(station.getLabelRotationDegrees());

        LatLng location = station.getLocation();

        LatLng offsetLatLng = location;
        switch(station.getLabelRotationDegrees()) {
            case Station.LABEL_LEFT:
                offsetLatLng = new LatLng(
                        location.latitude,
                        location.longitude - (180/Math.PI)*(STATION_CIRCLE_RADIUS/6378137.0)/Math.cos(Math.PI/180.0*location.latitude)
                );
                break;
            case Station.LABEL_DOWN:
                offsetLatLng = new LatLng(
                        location.latitude - (180/Math.PI)*(STATION_CIRCLE_RADIUS/6378137.0),
                        location.longitude
                );
                break;
            case Station.LABEL_UP:
                offsetLatLng = new LatLng(
                        location.latitude + (180/Math.PI)*(STATION_CIRCLE_RADIUS/6378137.0),
                        location.longitude
                );
                break;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .fromBitmap(iconFactory.makeIcon(station.getName())))
                .snippet(station.getName())
                .position(offsetLatLng)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));

        markers.put(station, marker);
        return marker;
    }

    public void removeMarker(Station station) {
        Marker marker = markers.get(station);
        if (marker != null) {
            marker.remove();
            markers.remove(station);
        }
    }

    public boolean setFromStation(Station station) {
        Station oldStation = sharedData.getFromStation();
        if (sharedData.setFromStation(station)) {
            removeMarker(oldStation);
            putMarker(station, IconGenerator.STYLE_GREEN);
            return true;
        }
        return false;
    }

    public boolean setToStation(Station station) {
        Station oldStation = sharedData.getToStation();
        if (sharedData.setToStation(station)) {
            removeMarker(oldStation);
            putMarker(station, IconGenerator.STYLE_RED);
            return true;
        }
        return false;
    }

    public boolean isFromAndToStationSet() {
        return sharedData.isFromAndToStationSet();
    }
}