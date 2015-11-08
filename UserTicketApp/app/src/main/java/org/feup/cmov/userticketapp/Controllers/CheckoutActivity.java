package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.BuyTicketOptions;
import org.feup.cmov.userticketapp.Models.CreditCard;
import org.feup.cmov.userticketapp.Models.DatabaseHelper;
import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.GetItineraryOptions;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.BuyTickets;
import org.feup.cmov.userticketapp.Services.GetItinerary;

import java.util.ArrayList;
import java.util.Date;

public class CheckoutActivity extends AppCompatActivity {

    private CheckoutAdapter mAdapter;

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    private Button buyTicketsButton;

    private void setCanBuyTickets(Itinerary itinerary) {
        boolean isPossible = true;
        for(Itinerary.Step step : itinerary.getSteps()) {
            if (step.getFreeSeats().size() == 0) {
                isPossible = false;
                break;
            }
        }
        boolean canBuyTickets = isPossible;
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
        Date selectedDate = sharedData.getSelectedDate();

        buyTicketsButton = (Button) findViewById(R.id.confirm_buy_tickets_button);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.checkout_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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
        }).execute(new GetItineraryOptions(fromStation, toStation, selectedDate));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onConfirmBuyTicketsClickHandler(View view) {
        BuyTicketOptions options = new BuyTicketOptions();

        options.setStartStation(sharedData.getFromStation());
        options.setEndStation(sharedData.getToStation());
        options.setCreditCard(sharedData.getCreditCard());
        options.setDate(sharedData.getSelectedDate());
        options.setArraySeatNumber(sharedData.getArraySeatNumber());

        final Context context = this;
        new BuyTickets(this, new BuyTickets.OnBuyTicketsTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Ticket> tickets) {
                Toast.makeText(context, "Tickets bough successfully", Toast.LENGTH_SHORT)
                        .show();

                DatabaseHelper dbHelper = new DatabaseHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                DatabaseHelper.insertCreditCard(db, sharedData.getCreditCard());

                Intent intent = new Intent(getBaseContext(), TicketsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();

                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent);
            }
        }).execute(options);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }
}
