package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketsViewHolder> {
    private static List<Ticket> mTickets;
    private static Context mContext;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static abstract class TicketsViewHolder extends RecyclerView.ViewHolder {
        protected View mView;
        public TicketsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public abstract void bindView(Ticket ticket);
    }

    public static class VHHeader extends TicketsViewHolder {
        public VHHeader(View v) {
            super(v);
        }

        @Override
        public void bindView(Ticket ticket) {
            TextView sectionText = (TextView) mView.findViewById(R.id.section_text);
            sectionText.setText(mContext.getString(R.string.tickets_details));
        }
    }

    public static class VHItem extends TicketsViewHolder {
        public VHItem(View v) {
            super(v);
            v.findViewById(R.id.seat_select_layout).setVisibility(View.GONE);
        }

        @Override
        public void bindView(Ticket ticket) {
            TextView ticketTitleText = (TextView) mView.findViewById(R.id.ticket_title);
            ticketTitleText.setText(String.format(
                    ticketTitleText.getText().toString(),
                    ticket.getFromStation().getName(), ticket.getToStation().getName()));

            String date = android.text.format.DateUtils.formatDateTime(mContext,
                    ticket.getDate(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            TextView ticketDateText = (TextView) mView.findViewById(R.id.ticket_date_hours);
            ticketDateText.setText(String.format(
                    ticketDateText.getText().toString(),
                    date, ticket.getHoursStart(), ticket.getMinutesStart()));
        }
    }

    public TicketsAdapter(Context context) {
        mContext = context;
    }

    public void setTickets(List<Ticket> tickets) {
        mTickets = tickets;
        this.notifyDataSetChanged();
    }

    @Override
    public TicketsAdapter.TicketsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.section, parent, false);
            return new VHHeader(v);
        }
        else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ticket_item, parent, false);
            return new VHItem(v);
        }
        else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(TicketsAdapter.TicketsViewHolder holder, int position) {
        Ticket ticket = mTickets.get(position);
        holder.bindView(ticket);
        // if (position == 0) {
        //     holder.bindView(null);
        // } else {
        //     Ticket ticket = mTickets.get(position - 1);
        //     holder.bindView(ticket);
        // }
    }

    private boolean isPositionHeader(int position) {
        return false;
        // return position == 0;
    }

    @Override
    public int getItemCount() {
        if (mTickets == null){
            // return 1;
            return 0;
        } else {
            // return mTickets.size() + 1;
            return mTickets.size();
        }
    }
}