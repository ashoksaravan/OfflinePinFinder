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
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;

public class STDAdapter extends CursorAdapter {

	public STDAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
		}

		//holder.shareButton = (ImageView) view.findViewById(R.id.stdShareButton);

		holder.city = (TextView) view.findViewById(R.id.cityName);

		holder.state = (TextView) view.findViewById(R.id.stdStateName);

		holder.stdCode = (TextView) view.findViewById(R.id.stdCode);

		view.setTag(holder);

//		holder.shareButton.setTag(cursor);
//
//		holder.shareButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Cursor c = (Cursor) v.getTag();
//				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//				sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				sharingIntent.setType("text/plain");
//				String shareSubject = "STD Code";
//				String shareContent = "State : " + c.getString(c.getColumnIndex(STDSQLiteHelper.STATE_NAME)) + "\n";
//				shareContent = shareContent + "City Name : " + c.getString(c.getColumnIndex(STDSQLiteHelper.CITY))
//						+ "\n";
//				shareContent = shareContent + "STD Code : " + c.getString(c.getColumnIndex(STDSQLiteHelper.ID))
//						+ "\n";
//				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
//				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
//				v.getContext().startActivity(
//						Intent.createChooser(sharingIntent, v.getContext().getResources().getText(R.string.send_to)));
//			}
//
//		});

		holder.city.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.CITY)));
		holder.state.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.STATE_NAME)));
		holder.stdCode.setText(cursor.getString(cursor.getColumnIndex(STDSQLiteHelper.ID)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.std_custom_grid, parent, false);
		return retView;
	}

	static class ViewHolder {
		TextView state;
		ImageView shareButton;
		TextView city;
		TextView stdCode;
	}
}
