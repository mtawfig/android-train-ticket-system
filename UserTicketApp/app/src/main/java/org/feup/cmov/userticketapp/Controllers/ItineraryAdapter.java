package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.R;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {
    private Itinerary mItinerary;
    private static Context mContext;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static abstract class ItineraryViewHolder extends RecyclerView.ViewHolder {

        public ItineraryViewHolder(View itemView) {
            super(itemView);
        }
        
        public abstract void bindView(Itinerary.Step step);
    }

    public static class VHHeader extends ItineraryViewHolder {
        public View mView;
        public VHHeader(View v) {
            super(v);
            mView = v;
        }

        @Override
        public void bindView(Itinerary.Step step) {
            TextView sectionText = (TextView) mView.findViewById(R.id.section_text);
            sectionText.setText(mContext.getString(R.string.trip_step_details));
        }
    }

    public static class VHItem extends ItineraryViewHolder {
        public View mView;
        public VHItem(View v) {
            super(v);
            mView = v;
        }

        @Override
        public void bindView(Itinerary.Step step) {
            TextView startStationText = (TextView) mView.findViewById(R.id.start_station_name);
            startStationText.setText(String.format(
                    startStationText.getText().toString(),
                    step.getStartStation().getName()));

            TextView endStationText = (TextView) mView.findViewById(R.id.end_station_name);
            endStationText.setText(String.format(
                    endStationText.getText().toString(),
                    step.getEndStation().getName()));

            TextView startTimeText = (TextView) mView.findViewById(R.id.start_time);
            startTimeText.setText(String.format(
                    startTimeText.getText().toString(),
                    step.getHoursStart(), step.getMinutesStart()));

            TextView endTimeText = (TextView) mView.findViewById(R.id.end_time);
            endTimeText.setText(String.format(
                    endTimeText.getText().toString(),
                    step.getHoursEnd(), step.getMinutesEnd()));

            TextView lineNameText = (TextView) mView.findViewById(R.id.line_and_stops);
            lineNameText.setText(String.format(
                    lineNameText.getText().toString(),
                    step.getLine(), step.getNumberOfStops(), step.getFreeSeats()));

            if (step.getWait() == null) {
                View waitView = mView.findViewById(R.id.wait_layout);
                waitView.setVisibility(View.GONE);
            } else {
                TextView waitText = (TextView) mView.findViewById(R.id.wait_text);
                waitText.setText(String.format(
                        waitText.getText().toString(),
                        step.getWait()));
            }
        }
    }

    public ItineraryAdapter(Context context) {
        mContext = context;
    }

    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;
        this.notifyDataSetChanged();
    }

    @Override
    public ItineraryAdapter.ItineraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.section, parent, false);
            return new VHHeader(v);
        }
        else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itinerary_step_item, parent, false);
            return new VHItem(v);
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ItineraryAdapter.ItineraryViewHolder holder, int position) {
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