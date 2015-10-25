package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private static Itinerary mItinerary;
    private static Context mContext;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static abstract class CheckoutViewHolder extends RecyclerView.ViewHolder {
        protected View mView;
        public CheckoutViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public abstract void bindView(Itinerary.Step step);
    }

    public static class VHHeader extends CheckoutViewHolder {
        public VHHeader(View v) {
            super(v);
        }

        @Override
        public void bindView(Itinerary.Step step) {
            TextView sectionText = (TextView) mView.findViewById(R.id.section_text);
            sectionText.setText(mContext.getString(R.string.tickets_details));
        }
    }

    public static class VHItem extends CheckoutViewHolder {
        public VHItem(View v) {
            super(v);
        }

        @Override
        public void bindView(Itinerary.Step step) {
            TextView ticketTitleText = (TextView) mView.findViewById(R.id.ticket_title);
            ticketTitleText.setText(String.format(
                    ticketTitleText.getText().toString(),
                    step.getStartStation().getName(), step.getEndStation().getName()));

            String date = android.text.format.DateUtils.formatDateTime(mContext,
                    mItinerary.getDate(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            TextView ticketDateText = (TextView) mView.findViewById(R.id.ticket_date_hours);
            ticketDateText.setText(String.format(
                    ticketDateText.getText().toString(),
                    date, step.getHoursStart(), step.getMinutesStart()));

            Spinner spinner = (Spinner) mView.findViewById(R.id.seat_number_spinner);

            ArrayList<Integer> freeSeats = new ArrayList<>(step.getFreeSeats());
            for (int i = 0; i < freeSeats.size(); i++) {
                freeSeats.set(i, freeSeats.get(i) + 1);
            }
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, step.getFreeSeats());
            spinner.setAdapter(adapter);
        }
    }

    public CheckoutAdapter(Context context) {
        mContext = context;
    }

    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;
        this.notifyDataSetChanged();
    }

    @Override
    public CheckoutAdapter.CheckoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(CheckoutAdapter.CheckoutViewHolder holder, int position) {
        if (position == 0) {
            holder.bindView(null);
        } else {
            Itinerary.Step step = mItinerary.getSteps().get(position - 1);
            holder.bindView(step);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        if (mItinerary == null){
            return 1;
        } else {
            return mItinerary.getSteps().size() + 1;
        }
    }
}