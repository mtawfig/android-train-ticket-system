package org.feup.cmov.userticketapp.Controllers;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;

import java.util.HashMap;

public class FromToActivity extends AppCompatActivity implements MapFragment.StationsMapListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private MapFragment mapFragment;
    private Button calculateRouteButton;
    private Boolean hasSelectedFromStation = false;

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.setStationClickListener(this);

        calculateRouteButton = (Button) findViewById(R.id.button_calculate_route);
        calculateRouteButton.setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_from_to, menu);
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
        } else if (id == android.R.id.home) {

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStationClickHandler(Station station) {
        if (station == null) {
            return;
        }

        int index = mViewPager.getCurrentItem();
        SectionsPagerAdapter adapter = ((SectionsPagerAdapter)mViewPager.getAdapter());
        StationTextFragment fragment = adapter.getFragment(index);

        Boolean stationChanged = false;
        if (index == SectionsPagerAdapter.FROM_STATION) {
            stationChanged = mapFragment.setFromStation(station);
            if (!hasSelectedFromStation) {
                hasSelectedFromStation = true;
                mViewPager.setCurrentItem(SectionsPagerAdapter.TO_STATION);
            }
        } else if (index == SectionsPagerAdapter.TO_STATION) {
            stationChanged = mapFragment.setToStation(station);
        }

        if (stationChanged) {
            fragment.setStation(station);
        }

        if (mapFragment.isFromAndToStationSet()) {
            calculateRouteButton.setEnabled(true);
        } else {
            calculateRouteButton.setEnabled(false);
        }
    }

    @Override
    public void onNoStationsFound() {

    }

    @Override
    public void onStationsLoaded() {

    }

    public void onCalculateRouteClickHandler(View view) {
        if (sharedData.getFromStation() == null || sharedData.getToStation() == null) {
            return;
        }

        Intent intent = new Intent(getBaseContext(), ItineraryActivity.class);
        startActivity(intent);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public static final int FROM_STATION = 0;
        public static final int TO_STATION = 1;

        HashMap<Integer, StationTextFragment> mPageReferenceMap = new HashMap<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            StationTextFragment fragment = StationTextFragment.newInstance(position);
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case FROM_STATION:
                    return "From";
                case TO_STATION:
                    return "To";
            }
            return null;
        }

        public StationTextFragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }
    }

    public static class StationTextFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private Station station;
        private EditText editText;
        private TextView fromOrToStationText;
        private int mSectionNumber;

        public static StationTextFragment newInstance(int sectionNumber) {
            StationTextFragment fragment = new StationTextFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public StationTextFragment() {}

        public void setStation(Station station) {
            this.station = station;
            editText.setText(station.getName());
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_from_to, container, false);
            editText = (EditText) rootView.findViewById(R.id.select_station_text);

            Bundle args = getArguments();
            mSectionNumber = args.getInt(ARG_SECTION_NUMBER);

            fromOrToStationText = (TextView) rootView.findViewById(R.id.from_or_to_text);
            if (mSectionNumber == SectionsPagerAdapter.FROM_STATION) {
                fromOrToStationText.setText(getString(R.string.from_station));
            } else {
                fromOrToStationText.setText(getString(R.string.to_station));
            }
            return rootView;
        }
    }
}
