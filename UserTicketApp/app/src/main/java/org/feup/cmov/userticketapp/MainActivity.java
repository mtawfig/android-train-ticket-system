package org.feup.cmov.userticketapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final static int STATION_CIRCLE_RADIUS = 1000;
    final static int CLICK_MARGIN_ERROR = 500;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        new ApiService.GetStations(new ApiService.OnGetStationsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Station> stations) {
                drawStations(stations);
            }
        }).execute();
    }

    public Map<String, Station> groupStationsById(List<Station> stations) {
        HashMap<String, Station> map = new HashMap<>();
        for (Station station : stations) {
            String key = station.getStationId();
            if(!map.containsKey(key)) {
                map.put(key, station);
            }
        }
        return map;
    }

    public void drawStations (final List<Station> stations) {
        Map<String, Station> stationsById = groupStationsById(stations);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(stations.get(0).getLocation()));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, marker.getSnippet(), duration);
                toast.show();

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

            IconGenerator iconFactory = new IconGenerator(this);

            iconFactory.setRotation(-station.getLabelRotationDegrees());
            iconFactory.setContentRotation(station.getLabelRotationDegrees());

            for (Station.Connection connection : station.getConnections()) {
                Pair<Station, Station> pair = new Pair<>(station, stationsById.get(connection.getToStationId()));
                if(!stationPairs.contains(pair)) {
                    stationPairs.add(pair);
                }
            }

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

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(iconFactory.makeIcon(station.getName())))
                    .snippet(station.getName())
                    .position(offsetLatLng)
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
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

                Toast toast;
                if (clickedStation != null) {
                    toast = Toast.makeText(context, clickedStation.getName(), Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(context, "Didn't click any station", Toast.LENGTH_SHORT);
                }
                toast.show();

            }
        });
    }
}
