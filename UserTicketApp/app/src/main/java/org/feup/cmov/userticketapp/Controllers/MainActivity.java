package org.feup.cmov.userticketapp.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;

public class MainActivity extends AppCompatActivity implements MapFragment.StationsMapListener, NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeContainer;
    private SharedDataFactory sharedData = SharedDataFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        if (station != null) {
            sharedData.setSelectedStation(station);
            Intent intent = new Intent(getBaseContext(), TimetableActivity.class);
            startActivity(intent);
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.buy_ticket) {
            // Handle the camera action
        } else if (id == R.id.timetables) {

        } else if (id == R.id.my_tickets) {

        } else if (id == R.id.sign_in) {

            Intent intent = new Intent(getBaseContext(), SigninActivity.class);
            startActivity(intent);

        } else if (id == R.id.sign_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
