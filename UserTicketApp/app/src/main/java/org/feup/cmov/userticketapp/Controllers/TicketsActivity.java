package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetTickets;

import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TicketsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeContainer;

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

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTickets();
            }
        });

        getTickets();
    }

    private final Context context = this;

    public void getTickets() {
        new GetTickets(this, new GetTickets.OnGetTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Ticket> tickets) {
                mAdapter.setTickets(tickets);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                swipeContainer.setRefreshing(false);
            }
        }).execute();
    }
}
