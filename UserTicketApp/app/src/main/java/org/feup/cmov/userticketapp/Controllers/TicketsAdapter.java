package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;
import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketsViewHolder> {
    private static List<Ticket> mTickets = new ArrayList<>();
    private static Context mContext;

    public static class TicketsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
        private Ticket mTicket;
        private TextView ticketTitleText;
        private TextView ticketDateText;
        private CardView cardView;
        private View mView;

        public TicketsViewHolder(View v) {
            super(v);
            mView = v;
            v.findViewById(R.id.seat_select_layout).setVisibility(View.GONE);
            v.setOnClickListener(this);
            ticketTitleText = (TextView) mView.findViewById(R.id.ticket_title);
            ticketDateText = (TextView) mView.findViewById(R.id.ticket_date_hours);
            cardView = (CardView) mView.findViewById(R.id.card_view);
        }

        public void bindView(Ticket ticket) {
            mTicket = ticket;

            ticketTitleText.setText(String.format(
                    ticketTitleText.getText().toString(),
                    ticket.getFromStation().getName(), ticket.getToStation().getName()));

            String date = android.text.format.DateUtils.formatDateTime(mContext,
                    ticket.getDate(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            ticketDateText.setText(String.format(
                    ticketDateText.getText().toString(),
                    date, ticket.getHoursStart(), ticket.getMinutesStart()));

            if (ticket.getUsed()) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorItemUsed));
            }
        }

        @Override
        public void onClick(View v) {
            sharedData.setSelectedTicket(mTicket);
            Intent intent = new Intent(mContext, TicketActivity.class);
            mContext.startActivity(intent);
        }
    }

    public TicketsAdapter(Context context) {
        mContext = context;
        setHasStableIds(true);
    }

    public void setTickets(List<Ticket> tickets) {
        mTickets.clear();
        mTickets.addAll(tickets);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return mTickets.get(i).getTicketId();
    }

    @Override
    public TicketsAdapter.TicketsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);
        return new TicketsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TicketsAdapter.TicketsViewHolder holder, int position) {
        Ticket ticket = mTickets.get(position);
        holder.bindView(ticket);
    }

    @Override
    public int getItemCount() {
        if (mTickets == null){
            return 0;
        } else {
            return mTickets.size();
        }
    }
}