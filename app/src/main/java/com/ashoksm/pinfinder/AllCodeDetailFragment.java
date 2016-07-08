package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link AllCodeListActivity}
 * in two-pane mode (on tablets) or a {@link AllCodeDetailActivity}
 * on handsets.
 */
public class AllCodeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The fragment argument representing the item name that this fragment
     * represents.
     */
    public static final String ARG_ITEM_NAME = "item_name";

    /**
     * The dummy content this fragment is presenting.
     */
    private Cursor cursor;

    private SharedPreferences sharedPreferences;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllCodeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("AllCodeFinder", Context
                .MODE_PRIVATE);
        PinFinderSQLiteHelper pinFinderSQLiteHelper = new PinFinderSQLiteHelper(getActivity());
        String pincode = getArguments().getString(ARG_ITEM_ID);
        String name = getArguments().getString(ARG_ITEM_NAME);
        if (pincode != null && name != null) {
            cursor = pinFinderSQLiteHelper
                    .getOfficeDetail(pincode.replaceAll("'", "''"),
                            name.replaceAll("'", "''"));
            cursor.moveToFirst();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getPincodeView(inflater, container);
    }

    @NonNull
    private View getPincodeView(LayoutInflater vi, ViewGroup container) {
        View v = vi.inflate(R.layout.office_custom_grid, container, false);

        final TextView officeName = (TextView) v.findViewById(R.id.officeName);
        ImageButton options = (ImageButton) v.findViewById(R.id.options);
        final TextView pincode = (TextView) v.findViewById(R.id.pincode);
        final TextView status = (TextView) v.findViewById(R.id.status);
        final TextView subOffice = (TextView) v.findViewById(R.id.subofficeName);
        LinearLayout subOfficeRow = (LinearLayout) v.findViewById(R.id.subofficeRow);
        final TextView headOffice = (TextView) v.findViewById(R.id.headofficeName);
        LinearLayout headOfficeRow = (LinearLayout) v.findViewById(R.id.headofficeRow);
        final TextView location = (TextView) v.findViewById(R.id.locationName);
        LinearLayout locationRow = (LinearLayout) v.findViewById(R.id.locationRow);
        final TextView telephoneNumber = (TextView) v.findViewById(R.id.telephoneNumber);
        LinearLayout telephoneRow = (LinearLayout) v.findViewById(R.id.telephoneRow);
        final TextView state = (TextView) v.findViewById(R.id.stateName);


        options.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), v);
                menu.getMenuInflater().inflate(R.menu.options_menu, menu.getMenu());

                Menu popupMenu = menu.getMenu();
                popupMenu.findItem(R.id.addToFav).setVisible(false);

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
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString()
                                .equals(getResources()
                                        .getString(R.string.share))) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            sharingIntent.setType("text/plain");
                            String shareSubject = "Pincode";
                            String shareContent = "Office Name : " +
                                    officeName.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "Pincode : " +
                                    pincode.getText().toString().trim()
                                    + "\n";
                            shareContent = shareContent + "Status : " +
                                    status.getText().toString().trim()
                                    + "\n";
                            if (subOffice.getText().toString().trim().length() > 0) {
                                shareContent = shareContent + "Sub Office : "
                                        + subOffice.getText().toString() + "\n";
                            }
                            if (headOffice.getText().toString().trim().length() > 0) {
                                shareContent = shareContent + "Head Office : "
                                        + headOffice.getText().toString() + "\n";
                            }
                            shareContent = shareContent + "Location : " +
                                    location.getText().toString()
                                    + "\n";
                            shareContent = shareContent + "State : " +
                                    state.getText().toString() + "\n";
                            if (telephoneNumber.getText().toString().trim().length() >
                                    0) {
                                shareContent = shareContent + "Telephone : "
                                        + telephoneNumber.getText().toString() + "\n";
                            }
                            sharingIntent
                                    .putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                            startActivity(Intent.createChooser(sharingIntent,
                                    getResources().getText(R.string.send_to)));
                        } else if (item.getTitle().toString().equalsIgnoreCase(
                                getResources().getString(R.string.add_to_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String pincodes = sharedPreferences.getString("pincodes", null);
                            String pincodeStr = pincode.getText().toString().trim();
                            if (pincodes != null && pincodes.trim().length() > 0) {
                                if (!pincodes.contains(pincodeStr)) {
                                    pincodes = pincodes + "," +
                                            pincode.getText().toString().trim();
                                    Toast.makeText(getContext(), "Added Successfully!!!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Already Exist!!!",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                pincodes = pincode.getText().toString().trim();
                                Toast.makeText(getContext(), "Added Successfully!!!",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                            editor.putString("pincodes", pincodes);
                            editor.apply();
                        } else if (item.getTitle().toString()
                                .equalsIgnoreCase(
                                        getResources().getString(R.string.del_fav))) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String pincodes = sharedPreferences.getString("pincodes", null);
                            String pincodeStr = pincode.getText().toString().trim();
                            if (pincodes != null) {
                                pincodes = pincodes.replaceAll(pincodeStr, "");
                                pincodes = pincodes.replaceAll(",,", ",");
                                if (pincodes.startsWith(",")) {
                                    pincodes = pincodes.replaceFirst(",", "");
                                }
                                if (pincodes.endsWith(",")) {
                                    pincodes = pincodes.substring(0, pincodes.length() - 1);
                                }
                            }
                            Toast.makeText(getContext(), "Removed Successfully!!!",
                                    Toast.LENGTH_LONG)
                                    .show();
                            editor.putString("pincodes", pincodes);
                            editor.apply();
                        } else {
                            String uri = "http://maps.google.com/maps?q="
                                    + state.getText().toString().trim() + " "
                                    + pincode.getText().toString().trim();
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps",
                                    "com.google.android.maps.MapsActivity");
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), R.string.mapsNotFount,
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }

                        return false;
                    }

                });
            }
        });

        officeName
                .setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.ID)));
        pincode
                .setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.PIN_CODE)));
        status.setText(
                cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.STATUS_NAME)));
        state
                .setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.STATE_NAME)));

        if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)) != null
                && cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)).trim()
                .length() > 0) {
            subOfficeRow.setVisibility(View.VISIBLE);
            subOffice.setText(
                    cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)));
        } else {
            subOffice.setText("");
            subOfficeRow.setVisibility(View.GONE);
        }

        if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)) != null
                && cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)).trim()
                .length() > 0) {
            headOfficeRow.setVisibility(View.VISIBLE);
            headOffice.setText(
                    cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)));
        } else {
            headOffice.setText("");
            headOfficeRow.setVisibility(View.GONE);
        }

        if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)) != null
                &&
                cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)).trim()
                        .length() > 0) {
            locationRow.setVisibility(View.VISIBLE);
            String locationStr =
                    cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)) +
                            " Taluk of " +
                            cursor.getString(
                                    cursor.getColumnIndex(PinFinderSQLiteHelper.DISTRICT_NAME)) +
                            " District";
            location.setText(locationStr);
        } else {
            location.setText("");
            locationRow.setVisibility(View.GONE);
        }
        if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)) != null
                && cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)).trim()
                .length() > 0) {
            telephoneRow.setVisibility(View.VISIBLE);
            telephoneNumber.setText(
                    cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)));
            Linkify.addLinks(telephoneNumber, Linkify.ALL);
        } else {
            telephoneNumber.setText("");
            telephoneRow.setVisibility(View.GONE);
        }
        return v;
    }
}
