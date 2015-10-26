package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.SharedDataFactory;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetItinerary;
import org.feup.cmov.userticketapp.Services.GetTickets;

import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TicketsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.tickets_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TicketsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        final Context context = this;

        new GetTickets(this, new GetTickets.OnGetTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Ticket> tickets) {
                mAdapter.setTickets(tickets);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute();
    }
}
