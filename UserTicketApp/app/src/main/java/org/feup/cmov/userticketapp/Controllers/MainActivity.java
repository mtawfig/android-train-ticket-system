package org.feup.cmov.userticketapp.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;

public class MainActivity extends AppCompatActivity implements MapFragment.StationsMapListener {

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.setStationClickListener(this);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mapFragment.fetchAndDrawStations();
            }
        });
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

    public void onGetMeSomeWhereButtonClick(View view) {
        Intent intent = new Intent(getBaseContext(), FromToActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStationClickHandler(Station station) {
        Toast toast;
        if (station != null) {
            toast = Toast.makeText(this, station.getName(), Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, "Didn't click any station", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    @Override
    public void onNoStationsFound() {
        Toast.makeText(this, "No connection to server. Please refresh later.", Toast.LENGTH_SHORT)
                .show();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onStationsLoaded() {
        swipeContainer.setRefreshing(false);
    }
}
