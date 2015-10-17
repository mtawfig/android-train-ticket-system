package org.feup.cmov.userticketapp.Controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.ApiService;

import java.util.List;

public class ItineraryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedDataFactory sharedData = SharedDataFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Station fromStation = sharedData.getFromStation();
        Station toStation = sharedData.getToStation();

        mRecyclerView = (RecyclerView) findViewById(R.id.itinerary_recycler_view);

        // TODO set to true if list has a fixed size
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new ApiService.GetItinerary(new ApiService.OnGetItineraryTaskCompleted() {
            @Override
            public void onTaskCompleted(Itinerary itinerary) {
                mAdapter = new ItineraryAdapter(itinerary);
                mRecyclerView.setAdapter(mAdapter);
            }
        }).execute(fromStation, toStation);
    }
}