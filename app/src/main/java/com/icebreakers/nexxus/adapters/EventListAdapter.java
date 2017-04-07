package com.icebreakers.nexxus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.MeetupEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by radhikak on 4/7/17.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private static final String TAG = NexxusApplication.BASE_TAG + EventListAdapter.class.getName();
    List<MeetupEvent> events;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm");

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvEventName)
        TextView tvName;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.tvVenue)
        TextView tvVenue;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public EventListAdapter(List<MeetupEvent> articles) {
        this.events = articles;
    }

    public MeetupEvent getItem(int position) {
        return events.get(position);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, null);
        return new EventViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

        MeetupEvent event = events.get(position);

        holder.tvName.setText(event.getName());

        // TODO better date format
        holder.tvTime.setText(dateFormat.format(new Date(event.getTime())));

        // TODO expand address
        if (event.getVenue() != null)
        holder.tvVenue.setText(event.getVenue().getAddress1());

        // TODO load group image
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
