package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

		holder.city = (TextView) view.findViewById(R.id.rCityName);

		holder.state = (TextView) view.findViewById(R.id.rtoStateName);

		holder.rtoCode = (TextView) view.findViewById(R.id.rtoCode);

		view.setTag(holder);

		holder.shareButton.setTag(cursor);

		holder.shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Cursor c = (Cursor) v.getTag();
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				sharingIntent.setType("text/plain");
				String shareSubject = "STD Code";
				String shareContent = "State : " + c.getString(c.getColumnIndex(RTOSQLiteHelper.STATE_NAME)) + "\n";
				shareContent = shareContent + "City Name : " + c.getString(c.getColumnIndex(RTOSQLiteHelper.CITY))
						+ "\n";
				shareContent = shareContent + "STD Code : " + c.getString(c.getColumnIndex(RTOSQLiteHelper.ID)) + "\n";
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
		TextView city;
		TextView rtoCode;
	}
}
