package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;

public class RTOAdapter extends CursorAdapter {

	public RTOAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
		}

		holder.shareButton = (ImageView) view.findViewById(R.id.rtoShareButton);

		holder.mapButton = (ImageView) view.findViewById(R.id.rtoMapButton);

		holder.city = (TextView) view.findViewById(R.id.rCityName);

		holder.state = (TextView) view.findViewById(R.id.rtoStateName);

		holder.rtoCode = (TextView) view.findViewById(R.id.rtoCode);

		view.setTag(holder);

		holder.mapButton.setTag(holder);

		holder.mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				String uri = "http://maps.google.com/maps?q=" + holder.city.getText().toString() + ", "
						+ holder.state.getText().toString();
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
				String shareSubject = "RTO Code";
				String shareContent = "RTO Code : " + holder.rtoCode.getText().toString() + "\n";
				shareContent = shareContent + "City Name : " + holder.city.getText().toString() + "\n";
				shareContent = shareContent + "State : " + holder.state.getText().toString();
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
				v.getContext().startActivity(
						Intent.createChooser(sharingIntent, v.getContext().getResources().getText(R.string.send_to)));
			}

		});

		holder.city.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.CITY)));
		holder.state.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.STATE_NAME)));
		holder.rtoCode.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.ID)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.rto_custom_grid, parent, false);
		return retView;
	}

	static class ViewHolder {
		TextView state;
		ImageView shareButton;
		ImageView mapButton;
		TextView city;
		TextView rtoCode;
	}
}
