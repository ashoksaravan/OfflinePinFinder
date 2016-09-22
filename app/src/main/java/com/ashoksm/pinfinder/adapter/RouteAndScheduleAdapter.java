package com.ashoksm.pinfinder.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.StationDetailsActivity;
import com.ashoksm.pinfinder.StationsFragment;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

public class RouteAndScheduleAdapter
        extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private Activity context;
    private boolean largeScreen;

    public RouteAndScheduleAdapter(Cursor cursor, boolean xLargeScreen, Activity contextIn) {
        super(cursor);
        largeScreen = xLargeScreen;
        context = contextIn;
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor, int
            position) {
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            holder.no.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ID)));
            String stationName = cursor.getString(
                    cursor.getColumnIndex(RailWaysSQLiteHelper.STATION_CODE));
            stationName = "<font color='#FF5252'><u>" + stationName.replaceAll("[(]",
                    "</u></font>(");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.stationName.setText(
                        Html.fromHtml(stationName, Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.stationName.setText(Html.fromHtml(stationName));
            }
            String start = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STARTS));
            holder.arrives.setText(start);
            if ("Starts".equalsIgnoreCase(start)) {
                holder.arrives.setTypeface(null, Typeface.BOLD);
            } else {
                holder.arrives.setTypeface(null, Typeface.NORMAL);

            }
            String end = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ENDS));
            holder.departs.setText(end);
            if ("Ends".equalsIgnoreCase(end)) {
                holder.departs.setTypeface(null, Typeface.BOLD);
            } else {
                holder.departs.setTypeface(null, Typeface.NORMAL);
            }
            holder.stopTime.setText(
                    cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STOP_TIME)));
            holder.distanceTravelled.setText(
                    cursor.getString(
                            cursor.getColumnIndex(RailWaysSQLiteHelper.DISTANCE_TRAVELED)));
            holder.day.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.DAYS)));
            holder.route.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper
                    .ROUTE)));

            holder.stationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StationDetailsActivity.class);
                    String s = holder.stationName.getText().toString();
                    intent.putExtra(StationsFragment.EXTRA_STATION,
                            s.substring(s.indexOf("(") + 1, s.indexOf(")")));
                    intent.putExtra(StationsFragment.EXTRA_CITY, s);
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.slide_out_left, 0);
                }
            });
        } else {
            ViewHeaderHolder holder = (ViewHeaderHolder) viewHolder;
            holder.no.setTextColor(Color.parseColor("#FF5252"));
            holder.no.setTypeface(null, Typeface.BOLD);
            holder.stationName.setTextColor(Color.parseColor("#FF5252"));
            holder.stationName.setTypeface(null, Typeface.BOLD);
            holder.arrives.setTextColor(Color.parseColor("#FF5252"));
            holder.arrives.setTypeface(null, Typeface.BOLD);
            holder.departs.setTextColor(Color.parseColor("#FF5252"));
            holder.departs.setTypeface(null, Typeface.BOLD);
            holder.stopTime.setTextColor(Color.parseColor("#FF5252"));
            holder.stopTime.setTypeface(null, Typeface.BOLD);
            holder.distanceTravelled.setTextColor(Color.parseColor("#FF5252"));
            holder.distanceTravelled.setTypeface(null, Typeface.BOLD);
            holder.day.setTextColor(Color.parseColor("#FF5252"));
            holder.day.setTypeface(null, Typeface.BOLD);
            holder.route.setTextColor(Color.parseColor("#FF5252"));
            holder.route.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_and_schedule_grid, parent, false);
        if (viewType == TYPE_ITEM) {
            return new ViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            if (largeScreen) {
                return new ViewHeaderHolder(itemView);
            } else {
                return new ViewHolder(itemView);
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView no;
        TextView stationName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView distanceTravelled;
        TextView day;
        TextView route;

        public ViewHolder(View view) {
            super(view);
            no = (TextView) view.findViewById(R.id.no);
            stationName = (TextView) view.findViewById(R.id.station_name);
            arrives = (TextView) view.findViewById(R.id.arrives);
            departs = (TextView) view.findViewById(R.id.departs);
            stopTime = (TextView) view.findViewById(R.id.stop_time);
            distanceTravelled = (TextView) view.findViewById(R.id.distance_travelled);
            day = (TextView) view.findViewById(R.id.day);
            route = (TextView) view.findViewById(R.id.route);
        }

    }

    private static class ViewHeaderHolder extends RecyclerView.ViewHolder {

        TextView no;
        TextView stationName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView distanceTravelled;
        TextView day;
        TextView route;

        ViewHeaderHolder(View view) {
            super(view);
            no = (TextView) view.findViewById(R.id.no);
            stationName = (TextView) view.findViewById(R.id.station_name);
            arrives = (TextView) view.findViewById(R.id.arrives);
            departs = (TextView) view.findViewById(R.id.departs);
            stopTime = (TextView) view.findViewById(R.id.stop_time);
            distanceTravelled = (TextView) view.findViewById(R.id.distance_travelled);
            day = (TextView) view.findViewById(R.id.day);
            route = (TextView) view.findViewById(R.id.route);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
