package org.feup.cmov.userticketapp.Controllers;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Helpers.DividerItemDecoration;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetItinerary;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CheckoutAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedDataFactory sharedData = SharedDataFactory.getInstance();

    private boolean canBuyTickets = false;
    private Button buyTicketsButton;

    private void setCanBuyTickets(Itinerary itinerary) {
        boolean isPossible = true;
        for(Itinerary.Step step : itinerary.getSteps()) {
            if (step.getFreeSeats().size() == 0) {
                isPossible = false;
                break;
            }
        }
        canBuyTickets = isPossible;
        buyTicketsButton.setEnabled(canBuyTickets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Station fromStation = sharedData.getFromStation();
        final Station toStation = sharedData.getToStation();

        buyTicketsButton = (Button) findViewById(R.id.confirm_buy_tickets_button);

        mRecyclerView = (RecyclerView) findViewById(R.id.checkout_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CheckoutAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new GetItinerary(new GetItinerary.OnGetItineraryTaskCompleted() {
            @Override
            public void onTaskCompleted(Itinerary itinerary) {
                mAdapter.setItinerary(itinerary);

                TextView tripCostText = (TextView) findViewById(R.id.tickets_cost_text);
                tripCostText.setText(String.format(
                        getString(R.string.tickets_cost_text),
                        itinerary.getCost() / 100, itinerary.getCost() % 100));

                setCanBuyTickets(itinerary);
            }
        }).execute(fromStation, toStation);

    }

}
