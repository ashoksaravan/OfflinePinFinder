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
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IFSCRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<IFSCRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private String bankName;
    private int lastPosition = -1;
    private SharedPreferences sharedPreferences;
    private boolean showFav;

    public IFSCRecyclerViewAdapter(Context contextIn, Cursor cursor, String bankNameIn,
                                   SharedPreferences sharedPreferencesIn, boolean showFavIn) {
        super(cursor);
        this.bankName = bankNameIn;
        this.context = contextIn;
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
                            String shareSubject = "Branch Details";
                            String shareContent = "Branch Name : " +
                                    viewHolder.branchName.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "City : " +
                                    viewHolder.city.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "District : "
                                    + viewHolder.district.getText().toString().trim() + "\n";
                            shareContent = shareContent + "State : " +
                                    viewHolder.state.getText().toString() + "\n";
                            shareContent = shareContent + "Address : " +
                                    viewHolder.address.getText().toString() + "\n";
                            shareContent = shareContent + "Contact : " +
                                    viewHolder.contact.getText().toString() + "\n";
                            shareContent = shareContent + "IFSC : " +
                                    viewHolder.ifsc.getText().toString() + "\n";
                            shareContent = shareContent + "MICR : " +
                                    viewHolder.micr.getText().toString() + "\n";
                            sharingIntent
                                    .putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                            context.startActivity(Intent.createChooser(sharingIntent,
                                    context.getResources().getText(R.string.send_to)));
                        } else if (item.getTitle().toString().equalsIgnoreCase(
                                context.getResources().getString(R.string.add_to_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String ifscs = sharedPreferences.getString("ifscs", null);
                            String ifsc = viewHolder.ifsc.getText().toString().trim();
                            if (ifscs != null && ifscs.trim().length() > 0) {
                                if (!ifscs.contains(ifsc)) {
                                    ifscs = ifscs + ",'" +
                                            viewHolder.ifsc.getText().toString().trim() + "'";
                                    Toast.makeText(context, "Added Successfully!!!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Already Exist!!!", Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                ifscs = "'" + viewHolder.ifsc.getText().toString().trim() + "'";
                                Toast.makeText(context, "Added Successfully!!!", Toast.LENGTH_LONG)
                                        .show();
                            }
                            editor.putString("ifscs", ifscs);
                            editor.apply();
                        } else if (item.getTitle().toString().equalsIgnoreCase(
                                context.getResources().getString(R.string.del_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String ifscs = sharedPreferences.getString("ifscs", null);
                            String ifsc = "'" + viewHolder.ifsc.getText().toString().trim() + "'";
                            if (ifscs != null) {
                                ifscs = ifscs.replaceAll(ifsc, "");
                                ifscs = ifscs.replaceAll(",,", ",");
                                if (ifscs.startsWith(",")) {
                                    ifscs = ifscs.replaceFirst(",", "");
                                }
                                if (ifscs.endsWith(",")) {
                                    ifscs = ifscs.substring(0, ifscs.length() - 1);
                                }
                            }
                            Toast.makeText(context, "Removed Successfully!!!", Toast.LENGTH_LONG)
                                    .show();
                            editor.putString("ifscs", ifscs);
                            editor.apply();
                        } else {
                            if (bankName == null) {
                                bankName = viewHolder.bankName.getText().toString();
                            }
                            String uri = "http://maps.google.com/maps?q=" + bankName + ", "
                                    + viewHolder.branchName.getText();
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

        String branchName = cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.NAME));
        holder.branchName.setText(
                branchName.equalsIgnoreCase("Rtgs-ho") ? branchName.toUpperCase() : branchName);
        holder.city.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.CITY)));
        holder.district
                .setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.DISTRICT)));
        holder.state.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.STATE)));
        String contact = cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.CONTACT));
        holder.contact.setText(contact.equalsIgnoreCase("0") ? "NA" : contact);
        holder.address
                .setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.ADDRESS)));
        holder.ifsc.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.ID)));
        holder.micr.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.MICR)));
        Linkify.addLinks(holder.contact, Linkify.ALL);
        holder.bankNameRow.setVisibility(View.VISIBLE);
        holder.bankName
                .setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.BANK)));
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
                .inflate(R.layout.bank_custom_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView branchName;
        ImageButton options;
        TextView city;
        TextView district;
        TextView state;
        TextView address;
        TextView contact;
        TextView ifsc;
        TextView micr;
        View v;
        LinearLayout bankNameRow;
        TextView bankName;

        public ViewHolder(View view) {
            super(view);
            branchName = (TextView) view.findViewById(R.id.branch);
            options = (ImageButton) view.findViewById(R.id.options);
            city = (TextView) view.findViewById(R.id.city);
            district = (TextView) view.findViewById(R.id.bankDistrict);
            state = (TextView) view.findViewById(R.id.bankStateName);
            address = (TextView) view.findViewById(R.id.address);
            contact = (TextView) view.findViewById(R.id.contact);
            ifsc = (TextView) view.findViewById(R.id.ifsc);
            micr = (TextView) view.findViewById(R.id.micr);
            bankNameRow = (LinearLayout) view.findViewById(R.id.bankNameRow);
            bankName = (TextView) view.findViewById(R.id.bankName);
            v = view;
        }

    }
}
