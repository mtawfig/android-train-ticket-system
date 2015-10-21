package org.feup.cmov.userticketapp.Controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetItinerary;
import org.feup.cmov.userticketapp.Services.GetTimetables;
import org.feup.cmov.userticketapp.Views.DividerItemDecoration;

public class ItineraryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ItineraryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedDataFactory sharedData = SharedDataFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Station fromStation = sharedData.getFromStation();
        final Station toStation = sharedData.getToStation();

        mRecyclerView = (RecyclerView) findViewById(R.id.itinerary_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new ItineraryAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new GetItinerary(new GetItinerary.OnGetItineraryTaskCompleted() {
            @Override
            public void onTaskCompleted(Itinerary itinerary) {
                mAdapter.setItinerary(itinerary);

                TextView tripNameText = (TextView) findViewById(R.id.trip_name_text);
                tripNameText.setText(String.format(
                        getString(R.string.trip_name_text),
                        fromStation.getName(), toStation.getName()));

                TextView sectionText = (TextView) findViewById(R.id.trip_duration_text);

                Boolean isAboveOneHourWithZeroMinutes = itinerary.getDuration() > 60 && itinerary.getDuration() % 60 == 0;
                Boolean isAboveOneHour = itinerary.getDuration() > 60;

                if (isAboveOneHourWithZeroMinutes) {
                    sectionText.setText(String.format(
                            getString(R.string.trip_duration_text_hours),
                            itinerary.getDuration() / 60));
                } else if (isAboveOneHour) {
                    sectionText.setText(String.format(
                            getString(R.string.trip_duration_text_hours_minutes),
                            itinerary.getDuration() / 60, itinerary.getDuration() % 60));
                } else {
                    sectionText.setText(String.format(
                            getString(R.string.trip_duration_text_minutes),
                            itinerary.getDuration()));
                }

                TextView tripCostText = (TextView) findViewById(R.id.trip_cost_text);
                tripCostText.setText(String.format(
                        getString(R.string.trip_cost_text),
                        itinerary.getCost() / 100, itinerary.getCost() % 100));

            }
        }).execute(fromStation, toStation);
    }

    public void onCalculateRouteClickHandler(View view) {

    }
}