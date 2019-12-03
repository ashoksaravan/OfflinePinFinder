package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class STDRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<STDRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private SharedPreferences sharedPreferences;
    private boolean showFav;

    public STDRecyclerViewAdapter(Context context, Cursor cursor,
                                  SharedPreferences sharedPreferencesIn, boolean showFavIn) {
        super(cursor);
        this.context = context;
        this.sharedPreferences = sharedPreferencesIn;
        this.showFav = showFavIn;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor, int position) {
        holder.options.setTag(holder);

        holder.options.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenuInflater().inflate(R.menu.options_menu, menu.getMenu());

                Menu popupMenu = menu.getMenu();
                if (showFav) {
                    popupMenu.findItem(R.id.addToFav).setVisible(false);
                } else {
                    popupMenu.findItem(R.id.deleteFav).setVisible(false);
                }

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
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sharingIntent.setType("text/plain");
                        String shareSubject = "STD Code";
                        String shareContent =
                                "STD Code : " + viewHolder.stdCode.getText().toString() + "\n";
                        shareContent = shareContent + "City Name : " +
                                viewHolder.city.getText().toString() + "\n";
                        shareContent = shareContent + "State : " +
                                viewHolder.state.getText().toString();
                        sharingIntent
                                .putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                        context.startActivity(Intent.createChooser(sharingIntent,
                                context.getResources().getText(R.string.send_to)));
                    } else if (item.getTitle().toString()
                            .equals(context.getResources().getString(R.string.add_to_fav))) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String stdCodes = sharedPreferences.getString("STDcodes", null);
                        String stdCode = viewHolder.stdCode.getText().toString().trim();
                        if (stdCodes != null && stdCodes.trim().length() > 0) {
                            if (!stdCodes.contains(stdCode)) {
                                stdCodes = stdCodes + ",'" +
                                        viewHolder.stdCode.getText().toString().trim() + "'";
                                Toast.makeText(context, "Added Successfully!!!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Already Exist!!!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            stdCodes =
                                    "'" + viewHolder.stdCode.getText().toString().trim() + "'";
                            Toast.makeText(context, "Added Successfully!!!", Toast.LENGTH_LONG)
                                    .show();
                        }
                        editor.putString("STDcodes", stdCodes);
                        editor.apply();
                    } else if (item.getTitle().toString()
                            .equalsIgnoreCase(
                                    context.getResources().getString(R.string.del_fav))) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String stdCodes = sharedPreferences.getString("STDcodes", null);
                        String stdCode =
                                "'" + viewHolder.stdCode.getText().toString().trim() + "'";
                        if (stdCodes != null) {
                            stdCodes = stdCodes.replaceAll(stdCode, "");
                            stdCodes = stdCodes.replaceAll(",,", ",");
                            if (stdCodes.startsWith(",")) {
                                stdCodes = stdCodes.replaceFirst(",", "");
                            }
                            if (stdCodes.endsWith(",")) {
                                stdCodes = stdCodes.substring(0, stdCodes.length() - 1);
                            }
                        }
                        Toast.makeText(context, "Removed Successfully!!!", Toast.LENGTH_LONG)
                                .show();
                        editor.putString("STDcodes", stdCodes);
                        editor.apply();
                    } else {
                        String uri = "http://maps.google.com/maps?q=" +
                                viewHolder.city.getText().toString() + ", "
                                + viewHolder.state.getText().toString();
                        Intent intent =
                                new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, R.string.maps_not_found, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    return false;
                });
            }
        });

        holder.city.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.CITY)));
        holder.state.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.STATE_NAME)));
        holder.stdCode.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.ID)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.std_custom_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView state;
        ImageButton options;
        TextView city;
        TextView stdCode;
        View v;

        public ViewHolder(View view) {
            super(view);
            city = view.findViewById(R.id.cityName);
            state = view.findViewById(R.id.stdStateName);
            stdCode = view.findViewById(R.id.stdCode);
            options = view.findViewById(R.id.options);
            v = view;
        }
    }
}
