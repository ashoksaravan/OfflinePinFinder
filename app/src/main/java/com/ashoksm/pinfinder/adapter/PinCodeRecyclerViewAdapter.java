package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
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
import com.ashoksm.pinfinder.sqlite.PinSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PinCodeRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<PinCodeRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private int lastPosition = -1;
    private SharedPreferences sharedPreferences;
    private boolean showFav;

    public PinCodeRecyclerViewAdapter(Context context, Cursor cursor,
                                      SharedPreferences sharedPreferencesIn,
                                      boolean showFavIn) {
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
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString()
                                .equals(context.getResources().getString(R.string.share))) {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            sharingIntent.setType("text/plain");
                            String shareSubject = "Pincode";
                            String shareContent = "Office Name : " +
                                    viewHolder.officeName.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "Pincode : " +
                                    viewHolder.pincode.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "Status : " +
                                    viewHolder.status.getText().toString().trim()
                                    + "\n";
                            if (viewHolder.subOffice.getText().toString().trim().length() > 0) {
                                shareContent = shareContent + "Sub Office : "
                                        + viewHolder.subOffice.getText().toString() + "\n";
                            }
                            if (viewHolder.headOffice.getText().toString().trim().length() > 0) {
                                shareContent = shareContent + "Head Office : "
                                        + viewHolder.headOffice.getText().toString() + "\n";
                            }
                            shareContent = shareContent + "Location : " +
                                    viewHolder.location.getText().toString()
                                    + "\n";
                            shareContent = shareContent + "State : " +
                                    viewHolder.state.getText().toString() + "\n";
                            if (viewHolder.telephoneNumber.getText().toString().trim().length() >
                                    0) {
                                shareContent = shareContent + "Telephone : "
                                        + viewHolder.telephoneNumber.getText().toString() + "\n";
                            }
                            sharingIntent
                                    .putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                            context.startActivity(Intent.createChooser(sharingIntent,
                                    context.getResources().getText(R.string.send_to)));
                        } else if (item.getTitle().toString().equalsIgnoreCase(
                                context.getResources().getString(R.string.add_to_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String pincodes = sharedPreferences.getString("pincodes", null);
                            String pincode = viewHolder.pincode.getText().toString().trim();
                            if (pincodes != null && pincodes.trim().length() > 0) {
                                if (!pincodes.contains(pincode)) {
                                    pincodes = pincodes + "," +
                                            viewHolder.pincode.getText().toString().trim();
                                    Toast.makeText(context, "Added Successfully!!!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Already Exist!!!", Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                pincodes = viewHolder.pincode.getText().toString().trim();
                                Toast.makeText(context, "Added Successfully!!!", Toast.LENGTH_LONG)
                                        .show();
                            }
                            editor.putString("pincodes", pincodes);
                            editor.apply();
                        } else if (item.getTitle().toString()
                                .equalsIgnoreCase(
                                        context.getResources().getString(R.string.del_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String pincodes = sharedPreferences.getString("pincodes", null);
                            String pincode = viewHolder.pincode.getText().toString().trim();
                            if (pincodes != null) {
                                pincodes = pincodes.replaceAll(pincode, "");
                                pincodes = pincodes.replaceAll(",,", ",");
                                if (pincodes.startsWith(",")) {
                                    pincodes = pincodes.replaceFirst(",", "");
                                }
                                if (pincodes.endsWith(",")) {
                                    pincodes = pincodes.substring(0, pincodes.length() - 1);
                                }
                            }
                            Toast.makeText(context, "Removed Successfully!!!", Toast.LENGTH_LONG)
                                    .show();
                            editor.putString("pincodes", pincodes);
                            editor.apply();
                        } else {
                            String uri = "http://maps.google.com/maps?q="
                                    + viewHolder.state.getText().toString().trim() + " "
                                    + viewHolder.pincode.getText().toString().trim();
                            Intent intent =
                                    new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
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
                    }

                });
            }
        });

        holder.officeName
                .setText(cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.ID)));
        holder.pincode
                .setText(cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.PIN_CODE)));
        holder.status.setText(
                cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.STATUS_NAME)));
        holder.state
                .setText(cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.STATE_NAME)));

        if (cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.SUB_OFFICE)) != null
                && cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.SUB_OFFICE)).trim()
                .length() > 0) {
            holder.subOfficeRow.setVisibility(View.VISIBLE);
            holder.subOffice.setText(
                    cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.SUB_OFFICE)));
        } else {
            holder.subOffice.setText("");
            holder.subOfficeRow.setVisibility(View.GONE);
        }

        if (cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.HEAD_OFFICE)) != null
                && cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.HEAD_OFFICE)).trim()
                .length() > 0) {
            holder.headOfficeRow.setVisibility(View.VISIBLE);
            holder.headOffice.setText(
                    cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.HEAD_OFFICE)));
        } else {
            holder.headOffice.setText("");
            holder.headOfficeRow.setVisibility(View.GONE);
        }

        if (cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.LOCATION_NAME)) != null
                &&
                cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.LOCATION_NAME)).trim()
                        .length() > 0) {
            holder.locationRow.setVisibility(View.VISIBLE);
            String location =
                    cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.LOCATION_NAME)) +
                            " Taluk of " +
                            cursor.getString(
                                    cursor.getColumnIndex(PinSQLiteHelper.DISTRICT_NAME)) +
                            " District";
            holder.location.setText(location);
        } else {
            holder.location.setText("");
            holder.locationRow.setVisibility(View.GONE);
        }
        if (cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.TELEPHONE)) != null
                && cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.TELEPHONE)).trim()
                .length() > 0) {
            holder.telephoneRow.setVisibility(View.VISIBLE);
            holder.telephoneNumber.setText(
                    cursor.getString(cursor.getColumnIndex(PinSQLiteHelper.TELEPHONE)));
            Linkify.addLinks(holder.telephoneNumber, Linkify.ALL);
        } else {
            holder.telephoneNumber.setText("");
            holder.telephoneRow.setVisibility(View.GONE);
        }
        setAnimation(holder.v, position);
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
                .inflate(R.layout.office_custom_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView officeName;
        ImageButton options;
        TextView pincode;
        TextView status;
        TextView subOffice;
        LinearLayout subOfficeRow;
        TextView headOffice;
        LinearLayout headOfficeRow;
        TextView location;
        LinearLayout locationRow;
        TextView telephoneNumber;
        LinearLayout telephoneRow;
        TextView state;
        View v;

        public ViewHolder(View view) {
            super(view);
            officeName = (TextView) view.findViewById(R.id.officeName);
            options = (ImageButton) view.findViewById(R.id.options);
            pincode = (TextView) view.findViewById(R.id.pincode);
            status = (TextView) view.findViewById(R.id.status);
            subOffice = (TextView) view.findViewById(R.id.subOfficeName);
            subOfficeRow = (LinearLayout) view.findViewById(R.id.subOfficeRow);
            headOffice = (TextView) view.findViewById(R.id.headOfficeName);
            headOfficeRow = (LinearLayout) view.findViewById(R.id.headOfficeRow);
            location = (TextView) view.findViewById(R.id.locationName);
            locationRow = (LinearLayout) view.findViewById(R.id.locationRow);
            telephoneNumber = (TextView) view.findViewById(R.id.telephoneNumber);
            telephoneRow = (LinearLayout) view.findViewById(R.id.telephoneRow);
            state = (TextView) view.findViewById(R.id.stateName);
            v = view;
        }

    }

}
