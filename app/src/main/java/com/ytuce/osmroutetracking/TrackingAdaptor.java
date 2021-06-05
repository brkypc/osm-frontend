package com.ytuce.osmroutetracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ytuce.osmroutetracking.api.Results;
import com.ytuce.osmroutetracking.utility.TimeHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackingAdaptor extends RecyclerView.Adapter<TrackingAdaptor.ViewHolder> {

    private final Context context;
    private List<Results> trackingList;
    private final TrackingSelectionListener trackingSelectionListener;

    public TrackingAdaptor(Context context, TrackingSelectionListener listener) {
        this.context = context;
        this.trackingSelectionListener = listener;
    }

    public interface TrackingSelectionListener {
        void addTracking(int trackingId);
        void deleteTracking(int trackingId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trackings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Results results = trackingList.get(position);
        holder.trackingIdTextView.setText(String.valueOf(results.getTrackingId()));
        holder.trackingTimeTextView.setText(TimeHelper.timestampToDate(results.getTime()));

        holder.trackingCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                trackingSelectionListener.addTracking(results.getTrackingId());
            } else {
                trackingSelectionListener.deleteTracking(results.getTrackingId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trackingList != null) {
            return trackingList.size();
        }
        return 0;
    }

    public void setTrackingList(List<Results> trackingList) {
        this.trackingList = trackingList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trackingIdTextView;
        public TextView trackingTimeTextView;
        public CheckBox trackingCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingIdTextView = itemView.findViewById(R.id.textView_trackingId);
            trackingTimeTextView = itemView.findViewById(R.id.textView_trackingTime);
            trackingCheckBox = itemView.findViewById(R.id.checkBox_trackingCheck);
        }
    }
}
