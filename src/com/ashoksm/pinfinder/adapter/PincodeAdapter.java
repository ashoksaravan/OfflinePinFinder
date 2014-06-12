package com.ashoksm.pinfinder.adapter;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class PincodeAdapter extends CursorAdapter {

	public PincodeAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
		}

		holder.officeName = (TextView) view.findViewById(R.id.officeName);

		holder.mapButton = (ImageView) view.findViewById(R.id.mapButton);

		holder.shareButton = (ImageView) view.findViewById(R.id.shareButton);

		holder.pincode = (TextView) view.findViewById(R.id.pincode);

		holder.stauts = (TextView) view.findViewById(R.id.stauts);

		holder.suboffice = (TextView) view.findViewById(R.id.subofficeName);

		holder.subofficeRow = (LinearLayout) view.findViewById(R.id.subofficeRow);

		holder.headoffice = (TextView) view.findViewById(R.id.headofficeName);

		holder.headofficeRow = (LinearLayout) view.findViewById(R.id.headofficeRow);

		holder.location = (TextView) view.findViewById(R.id.locationName);

		holder.locationRow = (LinearLayout) view.findViewById(R.id.locationRow);

		holder.telephoneNumber = (TextView) view.findViewById(R.id.telephoneNumber);

		holder.telephoneRow = (LinearLayout) view.findViewById(R.id.telephoneRow);

		holder.state = (TextView) view.findViewById(R.id.stateName);

		view.setTag(holder);

		holder.mapButton.setTag(cursor);

		holder.mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Cursor c = (Cursor) v.getTag();
				String uri = "http://maps.google.com/maps?q="
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.STATE_NAME)).trim() + " "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.PIN_CODE)).trim();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				v.getContext().startActivity(intent);
			}

		});

		holder.shareButton.setTag(cursor);

		holder.shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Cursor c = (Cursor) v.getTag();
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				sharingIntent.setType("text/plain");
				String shareSubject = "Pincode";
				String shareContent = "Office Name : "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.ID)).trim() + "\n";
				shareContent = shareContent + "Pincode : "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.PIN_CODE)).trim() + "\n";
				shareContent = shareContent + "Status : "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.STATUS_NAME)).trim() + "\n";
				if (c.getString(c.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)) != null
						&& c.getString(c.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)).trim().length() > 0) {
					shareContent = shareContent + "Sub Office : "
							+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)) + "\n";
				}
				if (c.getString(c.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)) != null
						&& c.getString(c.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)).trim().length() > 0) {
					shareContent = shareContent + "Head Office : "
							+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)) + "\n";
				}
				shareContent = shareContent + "Location : "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)) + "\n";
				shareContent = shareContent + "State : "
						+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.STATE_NAME)) + "\n";
				if (c.getString(c.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)) != null
						&& c.getString(c.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)).trim().length() > 0) {
					shareContent = shareContent + "Telephone : "
							+ c.getString(c.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)) + "\n";
				}
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
				v.getContext().startActivity(Intent.createChooser(sharingIntent,
						v.getContext().getResources().getText(R.string.send_to)));
			}

		});

		holder.officeName.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.ID)));
		holder.pincode.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.PIN_CODE)));
		holder.stauts.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.STATUS_NAME)));
		holder.state.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.STATE_NAME)));

		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)).trim().length() > 0) {
			holder.subofficeRow.setVisibility(View.VISIBLE);
			holder.suboffice.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.SUB_OFFICE)));
		} else {
			holder.subofficeRow.setVisibility(View.GONE);
		}

		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)).trim().length() > 0) {
			holder.headofficeRow.setVisibility(View.VISIBLE);
			holder.headoffice.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)));
		} else {
			holder.headofficeRow.setVisibility(View.GONE);
		}

		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)).trim().length() > 0) {
			holder.locationRow.setVisibility(View.VISIBLE);
			holder.location.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)));
		} else {
			holder.locationRow.setVisibility(View.GONE);
		}
		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)).trim().length() > 0) {
			holder.telephoneRow.setVisibility(View.VISIBLE);
			holder.telephoneNumber.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)));
			Linkify.addLinks(holder.telephoneNumber, Linkify.ALL);
		} else {
			holder.telephoneRow.setVisibility(View.GONE);
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.office_custom_grid, parent, false);
		return retView;
	}

	static class ViewHolder {
		TextView officeName;
		ImageView mapButton;
		ImageView shareButton;
		TextView pincode;
		TextView stauts;
		TextView suboffice;
		LinearLayout subofficeRow;
		TextView headoffice;
		LinearLayout headofficeRow;
		TextView location;
		LinearLayout locationRow;
		TextView telephoneNumber;
		LinearLayout telephoneRow;
		TextView state;
	}
}
