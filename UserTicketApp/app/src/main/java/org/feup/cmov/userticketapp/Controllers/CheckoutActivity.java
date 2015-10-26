package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Helpers.DividerItemDecoration;
import org.feup.cmov.userticketapp.Models.BuyTicketOptions;
import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.BuyTickets;
import org.feup.cmov.userticketapp.Services.GetItinerary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        final Context context = this;

        new GetItinerary(this, new GetItinerary.OnGetItineraryTaskCompleted() {
            @Override
            public void onTaskCompleted(Itinerary itinerary) {
                mAdapter.setItinerary(itinerary);

                TextView tripCostText = (TextView) findViewById(R.id.tickets_cost_text);
                tripCostText.setText(String.format(
                        getString(R.string.tickets_cost_text),
                        itinerary.getCost() / 100, itinerary.getCost() % 100));

                setCanBuyTickets(itinerary);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(fromStation, toStation);

    }

    public void onConfirmBuyTicketsClickHandler(View view) {
        BuyTicketOptions options = new BuyTicketOptions();

        options.setStartStation(sharedData.getFromStation());
        options.setEndStation(sharedData.getToStation());
        options.setCreditCardNumber(sharedData.getCreditCardNumber());
        options.setCreditCardMonth(sharedData.getCreditCardMonth());
        options.setCreditCardYear(sharedData.getCreditCardYear());
        options.setCreditCardCode(sharedData.getCreditCardCode());
        options.setDate(sharedData.getSelectedDate());
        options.setArraySeatNumber(sharedData.getArraySeatNumber());

        final Context context = this;
        new BuyTickets(this, new BuyTickets.OnBuyTicketsTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Ticket> tickets) {
                if (tickets == null) {
                    Toast.makeText(context, "Server returned error. Please try later.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                Toast.makeText(context, "Tickets bough successfully", Toast.LENGTH_SHORT)
                        .show();

                Intent intent = new Intent(getBaseContext(), TicketsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(options);
    }

}
