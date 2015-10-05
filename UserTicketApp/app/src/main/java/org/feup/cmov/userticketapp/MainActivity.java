package org.feup.cmov.userticketapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        LatLng campanha = new LatLng(41.1505929,-8.5859497);
        LatLng espinho = new LatLng(41.0043836,-8.6456402);
        LatLng francelos = new LatLng(41.0812921,-8.6475706);
        LatLng saoromao = new LatLng(41.277871,-8.5536718);
        LatLng ermesinde = new LatLng(41.2169514,-8.5540581);
        LatLng parada = new LatLng(41.1602043,-8.3726025);
        LatLng saomartinho = new LatLng(41.1603533,-8.4695933);

        LatLng[] stations = {campanha, espinho, francelos, saoromao, ermesinde, parada, saomartinho};

        ArrayList<Pair<LatLng, LatLng>> connections = new ArrayList<>();
        connections.add(new Pair<>(campanha, francelos));
        connections.add(new Pair<>(francelos, espinho));
        connections.add(new Pair<>(campanha, ermesinde));
        connections.add(new Pair<>(ermesinde, saoromao));
        connections.add(new Pair<>(campanha, saomartinho));
        connections.add(new Pair<>(saomartinho, parada));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(campanha));

        for(LatLng station : stations) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(station)
                    .fillColor(Color.BLUE)
                    .strokeColor(Color.BLUE)
                    .radius(1000); // In meters
             mMap.addCircle(circleOptions);
        }

        for(Pair<LatLng, LatLng> connection : connections) {
            PolylineOptions rectOptions = new PolylineOptions()
                    .color(Color.BLUE)
                    .add(connection.first)
                    .add(connection.second);

            mMap.addPolyline(rectOptions);
        }

        final Context context = this;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, latLng.toString(), duration);
                toast.show();
            }
        });
    }
}
