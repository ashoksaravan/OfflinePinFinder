package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.StationDetailsActivity;
import com.ashoksm.pinfinder.StationsFragment;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StationRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<StationRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private int lastPosition = -1;

    public StationRecyclerViewAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor, int position) {
        holder.options.setTag(holder);

        holder.options.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenuInflater().inflate(R.menu.options_menu, menu.getMenu());

                Menu popupMenu = menu.getMenu();
                popupMenu.findItem(R.id.addToFav).setVisible(false);
                popupMenu.findItem(R.id.deleteFav).setVisible(false);

                try {
                    Field[] fields = menu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(menu);
                            Class<?> classPopupHelper =
                                    Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons =
                                    classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.getMessage());
                }

                menu.show();
                final ViewHolder viewHolder = (ViewHolder) v.getTag();
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString()
                                .equals(context.getResources().getString(R.string.share))) {
                            share(viewHolder);
                        } else {
                            openGMAP(viewHolder);
                        }
                        return false;
                    }

                });
            }
        });

        holder.stationName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StationDetailsActivity.class);
                intent.putExtra(StationsFragment.EXTRA_STATION, holder.stationCode.getText()
                        .toString());
                context.startActivity(intent);
            }
        });

        String city = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.CITY));
        if (city != null && city.trim().length() > 0) {
            holder.city.setText(city);
            holder.cityRow.setVisibility(View.VISIBLE);
        } else {
            holder.cityRow.setVisibility(View.GONE);
        }
        String station = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STATE));
        if (station != null && station.trim().length() > 0) {
            holder.state.setText(station);
            holder.stateRow.setVisibility(View.VISIBLE);
        } else {
            holder.stateRow.setVisibility(View.GONE);
        }
        holder.stationCode
                .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ID)));
        String stationName =
                cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STATION_NAME));
        SpannableString spanString = new SpannableString(stationName);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        holder.stationName.setText(spanString);
        holder.location
                .setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.LOCATION)));
        holder.trainsPassingVia.setText(cursor.getString(cursor.getColumnIndex
                (RailWaysSQLiteHelper.TRAINS_PASSING_VIA)));
        setAnimation(holder.v, position);
    }

    private void openGMAP(ViewHolder viewHolder) {
        String uri = "http://maps.google.com/maps?q=" +
                viewHolder.city.getText().toString() + ", " + viewHolder.state.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.maps_not_found, Toast.LENGTH_LONG).show();
        }
    }

    private void share(ViewHolder viewHolder) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        String shareSubject = "Station Code";
        String shareContent =
                "Station Code : " + viewHolder.stationCode.getText().toString() + "\n";
        shareContent =
                shareContent + "Station Name : " + viewHolder.stationName.getText().toString() +
                        "\n";
        shareContent =
                shareContent + "Location : " + viewHolder.location.getText().toString() + "\n";
        shareContent = shareContent + "Trains Passing Via : " +
                viewHolder.trainsPassingVia.getText().toString() + "\n";
        shareContent = shareContent + "State : " + viewHolder.state.getText().toString();
        shareContent = shareContent + "City Name : " + viewHolder.city.getText().toString() + "\n";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getResources().getText(R.string.send_to)));
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's
        // animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
        }
        lastPosition = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stations_custom_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView stationCode;
        TextView stationName;
        TextView location;
        TextView trainsPassingVia;
        ImageButton options;
        TextView state;
        TextView city;
        LinearLayout stateRow;
        LinearLayout cityRow;
        View v;

        public ViewHolder(View view) {
            super(view);
            options = (ImageButton) view.findViewById(R.id.options);
            city = (TextView) view.findViewById(R.id.city);
            state = (TextView) view.findViewById(R.id.state);
            stationCode = (TextView) view.findViewById(R.id.station_code);
            stationName = (TextView) view.findViewById(R.id.station_name);
            location = (TextView) view.findViewById(R.id.location);
            trainsPassingVia = (TextView) view.findViewById(R.id.trains_passing_via);
            stateRow = (LinearLayout) view.findViewById(R.id.state_row);
            cityRow = (LinearLayout) view.findViewById(R.id.city_row);
            v = view;
        }

    }

}
