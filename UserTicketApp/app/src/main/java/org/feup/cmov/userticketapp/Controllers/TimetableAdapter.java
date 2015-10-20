package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Timetable;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;

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