package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Models.SharedPreferencesFactory;
import org.feup.cmov.userticketapp.Services.ApiService;

public class MainActivity extends AppCompatActivity implements MapFragment.StationsMapListener, NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeContainer;
    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    private ImageView drawerImageView;
    private TextView drawerNameTextView;
    private TextView drawerEmailTextView;
    private Menu drawerMenu;

    private Button loginButton;
    private Button seeMyTicketsButton;

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

        View navigationHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        drawerImageView = (ImageView) navigationHeaderView.findViewById(R.id.image_view);
        drawerNameTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_name);
        drawerEmailTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_email);

        drawerMenu = navigationView.getMenu();

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_identifier), Context.MODE_PRIVATE);
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if (key.equals(getString(R.string.shared_preferences_user_sign_in))) {
                    updateDrawer();
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        loginButton = (Button) findViewById(R.id.login_button);
        seeMyTicketsButton = (Button) findViewById(R.id.tickets_button);

        if (ApiService.isUserSignedIn(this)) {
            loginButton.setVisibility(View.GONE);
            seeMyTicketsButton.setVisibility(View.VISIBLE);
        } else {
            seeMyTicketsButton.setVisibility(View.GONE);
        }

        updateDrawer();

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

    private void updateDrawer() {

        boolean isUserSignedIn = SharedPreferencesFactory.getBooleanValueFromPreferences(getString(R.string.shared_preferences_user_sign_in), sharedPreferences);

        drawerMenu.findItem(R.id.buy_ticket).setVisible(isUserSignedIn);
        drawerMenu.findItem(R.id.my_tickets).setVisible(isUserSignedIn);
        drawerMenu.findItem(R.id.sign_in_menu).setVisible(!isUserSignedIn);
        drawerMenu.findItem(R.id.sign_out_menu).setVisible(isUserSignedIn);

        if (isUserSignedIn) {
            drawerImageView.setImageResource(R.drawable.ic_users_user_icon);
            drawerNameTextView.setText(SharedPreferencesFactory.getStringValueFromPreferences(getString(R.string.shared_preferences_user_name_key), sharedPreferences));
            drawerEmailTextView.setText(SharedPreferencesFactory.getStringValueFromPreferences(getString(R.string.shared_preferences_user_email_key), sharedPreferences));
            loginButton.setVisibility(View.GONE);
            seeMyTicketsButton.setVisibility(View.VISIBLE);
        } else {
            drawerImageView.setImageResource(R.mipmap.ic_launcher);
            drawerNameTextView.setText(getString(R.string.app_name));
            drawerEmailTextView.setText(" ");
            loginButton.setVisibility(View.VISIBLE);
            seeMyTicketsButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onSeeMyTicketsButtonClick(View view) {
        Intent intent = new Intent(getBaseContext(), TicketsActivity.class);
        startActivity(intent);
    }

    public void onLoginOrRegisterButtonClick(View view) {
        Intent intent = new Intent(getBaseContext(), SignInActivity.class);
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

            Intent intent = new Intent(getBaseContext(), FromToActivity.class);
            startActivity(intent);

        } else if (id == R.id.my_tickets) {

            Intent intent = new Intent(getBaseContext(), TicketsActivity.class);
            startActivity(intent);

        } else if (id == R.id.sign_in) {

            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(intent);

        } else if (id == R.id.sign_up) {

            Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
            startActivity(intent);

        } else if (id == R.id.sign_out) {
            ApiService.signOut(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
