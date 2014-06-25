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

		holder.mapButton.setTag(holder);

		holder.mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				String uri = "http://maps.google.com/maps?q=" + holder.state.getText().toString().trim() + " "
						+ holder.pincode.getText().toString().trim();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				v.getContext().startActivity(intent);
			}

		});

		holder.shareButton.setTag(holder);

		holder.shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				sharingIntent.setType("text/plain");
				String shareSubject = "Pincode";
				String shareContent = "Office Name : " + holder.officeName.getText().toString().trim() + "\n";
				shareContent = shareContent + "Pincode : " + holder.pincode.getText().toString().trim() + "\n";
				shareContent = shareContent + "Status : " + holder.stauts.getText().toString().trim() + "\n";
				if (holder.suboffice.getText().toString().trim().length() > 0) {
					shareContent = shareContent + "Sub Office : " + holder.suboffice.getText().toString() + "\n";
				}
				if (holder.headoffice.getText().toString().trim().length() > 0) {
					shareContent = shareContent + "Head Office : " + holder.headoffice.getText().toString() + "\n";
				}
				shareContent = shareContent + "Location : " + holder.location.getText().toString() + "\n";
				shareContent = shareContent + "State : " + holder.state.getText().toString() + "\n";
				if (holder.telephoneNumber.getText().toString().trim().length() > 0) {
					shareContent = shareContent + "Telephone : " + holder.telephoneNumber.getText().toString() + "\n";
				}
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
				v.getContext().startActivity(
						Intent.createChooser(sharingIntent, v.getContext().getResources().getText(R.string.send_to)));
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
			holder.suboffice.setText("");
			holder.subofficeRow.setVisibility(View.GONE);
		}

		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)).trim().length() > 0) {
			holder.headofficeRow.setVisibility(View.VISIBLE);
			holder.headoffice.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.HEAD_OFFICE)));
		} else {
			holder.headoffice.setText("");
			holder.headofficeRow.setVisibility(View.GONE);
		}

		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)).trim().length() > 0) {
			holder.locationRow.setVisibility(View.VISIBLE);
			holder.location.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.LOCATION_NAME)));
		} else {
			holder.location.setText("");
			holder.locationRow.setVisibility(View.GONE);
		}
		if (cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)) != null
				&& cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)).trim().length() > 0) {
			holder.telephoneRow.setVisibility(View.VISIBLE);
			holder.telephoneNumber.setText(cursor.getString(cursor.getColumnIndex(PinFinderSQLiteHelper.TELEPHONE)));
			Linkify.addLinks(holder.telephoneNumber, Linkify.ALL);
		} else {
			holder.telephoneNumber.setText("");
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
