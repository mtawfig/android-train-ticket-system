package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.GetItineraryOptions;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetItinerary;
import org.feup.cmov.userticketapp.Helpers.DividerItemDecoration;

import java.util.Date;

public class ItineraryActivity extends AppCompatActivity implements DatePickerDialog.DatePickerDialogListener {

    private ItineraryAdapter mAdapter;

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

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
        setContentView(R.layout.activity_itinerary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buyTicketsButton = (Button) findViewById(R.id.buy_tickets_button);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.itinerary_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new ItineraryAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        getItinerary();
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

    public void onBuyTicketsClickHandler(View view) {
        if (!canBuyTickets) {
            return;
        }

        Intent intent = new Intent(getBaseContext(), PaymentActivity.class);
        startActivity(intent);
    }

    public void onSelectDateClickHandler(View view) {
        DatePickerDialog dialog = new DatePickerDialog();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        getItinerary();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void getItinerary() {
        final Context context = this;
        final Station fromStation = sharedData.getFromStation();
        final Station toStation = sharedData.getToStation();
        final Date selectedDate = sharedData.getSelectedDate();

        new GetItinerary(this, new GetItinerary.OnGetItineraryTaskCompleted() {
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

                TextView tripDateText = (TextView) findViewById(R.id.trip_date);
                String text = android.text.format.DateUtils.formatDateTime(getApplicationContext(),
                        itinerary.getDate(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                tripDateText.setText(text);

                setCanBuyTickets(itinerary);

            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(new GetItineraryOptions(fromStation, toStation, selectedDate));

    }
}