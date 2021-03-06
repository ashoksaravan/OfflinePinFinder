package com.ashoksm.pinfinder.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class StationRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<StationRecyclerViewAdapter.ViewHolder> {

    private Activity context;

    public StationRecyclerViewAdapter(Activity context, Cursor cursor) {
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
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().toString()
                            .equals(context.getResources().getString(R.string.share))) {
                        share(viewHolder);
                    } else {
                        openGMAP(viewHolder);
                    }
                    return false;
                });
            }
        });

        holder.stationName.setOnClickListener(view -> {
            Intent intent = new Intent(context, StationDetailsActivity.class);
            intent.putExtra(StationsFragment.EXTRA_STATION, holder.stationCode.getText()
                    .toString());
            intent.putExtra(StationsFragment.EXTRA_CITY, holder.stationName.getText()
                    .toString() + " (" + holder.stationCode.getText().toString() + ")");
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_out_left, 0);
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
    }

    private void openGMAP(ViewHolder h) {
        String uri = "http://maps.google.com/maps?q=" + h.stationName.getText().toString()
                + " train station";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.maps_not_found, Toast.LENGTH_LONG).show();
        }
    }

    private void share(ViewHolder h) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        String shareSubject = "Station Details";
        String shareContent = "Station Code : " + h.stationCode.getText().toString() + "\n";
        shareContent = shareContent + "Station Name : " + h.stationName.getText().toString() + "\n";
        shareContent = shareContent + "Location : " + h.location.getText().toString() + "\n";
        shareContent = shareContent + "Trains Passing Via : " + h.trainsPassingVia.getText()
                .toString() + "\n";
        shareContent = shareContent + "State : " + h.state.getText().toString();
        shareContent = shareContent + "City Name : " + h.city.getText().toString() + "\n";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getResources().getText(R.string.send_to)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            options = view.findViewById(R.id.options);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            stationCode = view.findViewById(R.id.station_code);
            stationName = view.findViewById(R.id.station_name);
            location = view.findViewById(R.id.location);
            trainsPassingVia = view.findViewById(R.id.trains_passing_via);
            stateRow = view.findViewById(R.id.state_row);
            cityRow = view.findViewById(R.id.city_row);
            v = view;
        }

    }

}
