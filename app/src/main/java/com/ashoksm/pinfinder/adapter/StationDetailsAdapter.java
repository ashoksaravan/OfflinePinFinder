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
import com.ashoksm.pinfinder.RouteAndScheduleActivity;
import com.ashoksm.pinfinder.TrainsFragment;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

public class StationDetailsAdapter
        extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Activity context;
    private boolean largeScreen;
    public StationDetailsAdapter(Cursor cursor, boolean xLargeScreen, Activity contextIn) {
        super(cursor);
        largeScreen = xLargeScreen;
        context = contextIn;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor, int
            position) {
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            String trainName = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ID));
            trainName = "<font color='#FF5252'><u>" + trainName.replaceAll("[(]",
                    "</u></font>(");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.trainName.setText(Html.fromHtml(trainName,
                        Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            } else {
                holder.trainName.setText(Html.fromHtml(trainName));
            }
            holder.arrives
                    .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STARTS)));
            holder.departs
                    .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ENDS)));
            String stopTime =
                    cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STOP_TIME));
            if (stopTime != null && stopTime.trim().length() == 0) {
                stopTime = "N/A";
            }
            holder.stopTime.setText(stopTime);
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
            holder.trainName.setOnClickListener(v -> {
                Intent intent = new Intent(context, RouteAndScheduleActivity.class);
                String value = holder.trainName.getText().toString();
                intent.putExtra(TrainsFragment.EXTRA_TRAIN, value.substring(value.indexOf
                        ("(") + 1, value.indexOf(")")));
                intent.putExtra(TrainsFragment.EXTRA_STARTS, value.substring(0, value.indexOf
                        ("(")));
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_out_left, 0);
            });
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
            if (largeScreen) {
                return new ViewHeaderHolder(itemView);
            } else {
                return new ViewHolder(itemView);
            }
        }
        return null;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trainName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView days;

        public ViewHolder(View view) {
            super(view);
            days = view.findViewById(R.id.days);
            trainName = view.findViewById(R.id.train_name);
            arrives = view.findViewById(R.id.arrives);
            departs = view.findViewById(R.id.departs);
            stopTime = view.findViewById(R.id.stop_time);
        }

    }

    private static class ViewHeaderHolder extends RecyclerView.ViewHolder {

        TextView trainName;
        TextView arrives;
        TextView departs;
        TextView stopTime;
        TextView days;

        ViewHeaderHolder(View view) {
            super(view);
            days = view.findViewById(R.id.days);
            trainName = view.findViewById(R.id.train_name);
            arrives = view.findViewById(R.id.arrives);
            departs = view.findViewById(R.id.departs);
            stopTime = view.findViewById(R.id.stop_time);
        }

    }
}
