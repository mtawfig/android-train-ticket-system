package org.feup.cmov.userticketapp.Controllers;

import android.support.design.widget.TabLayout;
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

import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;

import java.util.HashMap;

public class FromToActivity extends AppCompatActivity implements MapFragment.StationsMapListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private MapFragment mapFragment;
    private Button calculateRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStationClickHandler(Station station) {
        int index = mViewPager.getCurrentItem();
        SectionsPagerAdapter adapter = ((SectionsPagerAdapter)mViewPager.getAdapter());
        StationTextFragment fragment = adapter.getFragment(index);

        Boolean stationChanged = false;
        if (index == SectionsPagerAdapter.FROM_STATION) {
            stationChanged = mapFragment.setFromStation(station);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private static final int FROM_STATION = 0;
        private static final int TO_STATION = 1;

        HashMap<Integer, StationTextFragment> mPageReferenceMap = new HashMap<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            StationTextFragment fragment = StationTextFragment.newInstance();
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

        private Station station;
        private EditText editText;

        public static StationTextFragment newInstance() {
            StationTextFragment fragment = new StationTextFragment();
            // Bundle args = new Bundle();
            // args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            // fragment.setArguments(args);
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
            return rootView;
        }
    }
}
