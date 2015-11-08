package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.Models.TicketDbHelper;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.ApiService;
import org.feup.cmov.userticketapp.Services.GetTickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TicketsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeContainer;
    private TicketDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!ApiService.isUserSignedIn(this)) {
            finish();
            return;
        }
        
        mDbHelper = new TicketDbHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.tickets_recycler_view);

        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TicketsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTickets();
            }
        });

        ArrayList<Ticket> storedTickets = getTicketsFromDatabase();
        mAdapter.setTickets(storedTickets);

        getTickets();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private final Context context = this;

    public void getTickets() {
        new GetTickets(this, new GetTickets.OnGetTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Ticket> tickets) {
                mAdapter.setTickets(tickets);
                swipeContainer.setRefreshing(false);

                saveTicketsToDatabase(tickets);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                swipeContainer.setRefreshing(false);
            }
        }).execute();
    }

    public void saveTicketsToDatabase(List<Ticket> tickets) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        TicketDbHelper.deleteAllTickets(db);

        for (Ticket ticket : tickets) {
            TicketDbHelper.insertTicket(db, ticket);
        }
    }

    public ArrayList<Ticket> getTicketsFromDatabase() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Ticket[] tickets = TicketDbHelper.getAllTickets(db);
        return new ArrayList<>(Arrays.asList(tickets));
    }
}
