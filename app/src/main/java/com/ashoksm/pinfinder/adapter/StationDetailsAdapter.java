package com.ashoksm.pinfinder.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

public class StationDetailsAdapter
        extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    public StationDetailsAdapter(Cursor cursor) {
        super(cursor);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor, int
            position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.trainName
                    .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ID)));
            holder.arrives
                    .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STARTS)));
            holder.departs
                    .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ENDS)));
            holder.stopTime.setText(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.STOP_TIME)));
            StringBuilder sb = new StringBuilder();
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.MON)))) {
                sb.append('M');
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.TUE)))) {
                sb.append(sb.length() > 0 ? "-TU" : "TU");
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.WED)))) {
                sb.append(sb.length() > 0 ? "-W" : 'W');
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.THU)))) {
                sb.append(sb.length() > 0 ? "-TH" : "TH");
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.FRI)))) {
                sb.append(sb.length() > 0 ? "-F" : 'F');
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.SAT)))) {
                sb.append(sb.length() > 0 ? "-SA" : "SA");
            }
            if ("Y".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex
                    (RailWaysSQLiteHelper.SUN)))) {
                sb.append(sb.length() > 0 ? "-SU" : "SU");
            }
            holder.days.setText(sb.toString());
        } else {
            ViewHeaderHolder holder = (ViewHeaderHolder) viewHolder;
            holder.trainName.setTextColor(Color.parseColor("#FF5252"));
            holder.trainName.setTypeface(null, Typeface.BOLD);
            holder.arrives.setTextColor(Color.parseColor("#FF5252"));
            holder.arrives.setTypeface(null, Typeface.BOLD);
            holder.departs.setTextColor(Color.parseColor("#FF5252"));
            holder.departs.setTypeface(null, Typeface.BOLD);
            holder.stopTime.setTextColor(Color.parseColor("#FF5252"));
            holder.stopTime.setTypeface(null, Typeface.BOLD);
            holder.days.setTextColor(Color.parseColor("#FF5252"));
            holder.days.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stations_details_grid, parent, false);
        if (viewType == TYPE_ITEM) {
            return new ViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            return new ViewHeaderHolder(itemView);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trainName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView days;

        public ViewHolder(View view) {
            super(view);
            days = (TextView) view.findViewById(R.id.days);
            trainName = (TextView) view.findViewById(R.id.train_name);
            arrives = (TextView) view.findViewById(R.id.arrives);
            departs = (TextView) view.findViewById(R.id.departs);
            stopTime = (TextView) view.findViewById(R.id.stop_time);
        }

    }

    public static class ViewHeaderHolder extends RecyclerView.ViewHolder {

        TextView trainName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView days;

        public ViewHeaderHolder(View view) {
            super(view);
            days = (TextView) view.findViewById(R.id.days);
            trainName = (TextView) view.findViewById(R.id.train_name);
            arrives = (TextView) view.findViewById(R.id.arrives);
            departs = (TextView) view.findViewById(R.id.departs);
            stopTime = (TextView) view.findViewById(R.id.stop_time);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
