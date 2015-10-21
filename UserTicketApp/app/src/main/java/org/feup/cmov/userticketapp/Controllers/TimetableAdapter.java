package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Timetable;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {
    private ArrayList<Timetable> mTimetables;
    private static Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }

        public void bindView(Timetable timetable) {
            TextView sectionText = (TextView) mView.findViewById(R.id.info_text);
            sectionText.setText("Line " + timetable.getLine() + ", direction " + timetable.getDirection());

            TableLayout tableLayout = (TableLayout) mView.findViewById(R.id.table_layout);
            tableLayout.setStretchAllColumns(true);
            tableLayout.bringToFront();

            ArrayList<ArrayList<Timetable.Trip>> tripsByNumber = timetable.getTripsByNumber();

            ArrayList<Timetable.Trip> firstTripByNumber = tripsByNumber.get(0);
            TableRow headerRow =  new TableRow(mContext);

            Timetable.Trip headerStartingStationTrip = firstTripByNumber.get(0);
            TextView headerStartingStationTimeText = new TextView(mContext);
            headerStartingStationTimeText.setText(String.format(
                    mContext.getString(R.string.station_name),
                    headerStartingStationTrip.getFromStation().getName()));
            headerStartingStationTimeText.setTypeface(null, Typeface.BOLD);
            headerRow.addView(headerStartingStationTimeText);

            for(Timetable.Trip trip : firstTripByNumber ) {
                TextView textHeader = new TextView(mContext);
                textHeader.setText(String.format(
                        mContext.getString(R.string.station_name),
                        trip.getToStation().getName()));
                textHeader.setTypeface(null, Typeface.BOLD);
                headerRow.addView(textHeader);
            }
            tableLayout.addView(headerRow);


            for(ArrayList<Timetable.Trip> tripByNumber : tripsByNumber ) {
                TableRow tr =  new TableRow(mContext);

                Timetable.Trip startingStationTrip = tripByNumber.get(0);
                TextView startingStationTimeText = new TextView(mContext);
                startingStationTimeText.setText(String.format(
                        mContext.getString(R.string.time_hh_mm),
                        startingStationTrip.getHoursStart(), startingStationTrip.getMinutesStart()));
                tr.addView(startingStationTimeText);

                for(Timetable.Trip trip : tripByNumber ) {
                    TextView timeText = new TextView(mContext);
                    timeText.setText(String.format(
                            mContext.getString(R.string.time_hh_mm),
                            trip.getHoursEnd(), trip.getMinutesEnd()));
                    tr.addView(timeText);
                }
                tableLayout.addView(tr);
            }
        }
    }

    public TimetableAdapter(Context context) {
        mContext = context;
    }

    public void setTimetable(ArrayList<Timetable> timetables) {
        mTimetables = timetables;
        notifyDataSetChanged();
    }

    @Override
    public TimetableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.timetable_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Timetable timetable = mTimetables.get(position);
        holder.bindView(timetable);
    }

    @Override
    public int getItemCount() {
        if (mTimetables == null){
            return 0;
        } else {
            return mTimetables.size();
        }
    }
}