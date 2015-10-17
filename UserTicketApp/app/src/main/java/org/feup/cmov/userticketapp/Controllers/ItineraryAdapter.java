package org.feup.cmov.userticketapp.Controllers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.R;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    private Itinerary mItinerary;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public ItineraryAdapter(Itinerary itinerary) {
        mItinerary = itinerary;
    }

    @Override
    public ItineraryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itinerary_step_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        // ...
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView textView = (TextView) holder.mView.findViewById(R.id.step_text_view);
        textView.setText(mItinerary.getSteps().get(position).getEndStation().getName());
        // TODO set all the pretty view stuff
    }

    @Override
    public int getItemCount() {
        return mItinerary.getSteps().size();
    }
}