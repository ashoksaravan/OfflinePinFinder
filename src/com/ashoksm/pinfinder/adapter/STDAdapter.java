package com.ashoksm.pinfinder.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
	public void bindView(View view, final Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
		}

		holder.city = (TextView) view.findViewById(R.id.cityName);

		holder.state = (TextView) view.findViewById(R.id.stdStateName);

		holder.stdCode = (TextView) view.findViewById(R.id.stdCode);

		holder.options = (ImageView) view.findViewById(R.id.options);

		view.setTag(holder);

		holder.options.setTag(holder);

		holder.options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu menu = new PopupMenu(context, v);
				menu.getMenuInflater().inflate(R.menu.options_menu, menu.getMenu());

				try {
					Field[] fields = menu.getClass().getDeclaredFields();
					for (Field field : fields) {
						if ("mPopup".equals(field.getName())) {
							field.setAccessible(true);
							Object menuPopupHelper = field.get(menu);
							Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
							Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
							setForceIcons.invoke(menuPopupHelper, true);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				menu.show();
				final ViewHolder viewHolder = (ViewHolder) v.getTag();
				menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getTitle().toString().equals(context.getResources().getString(R.string.share))) {
							Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
							sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							sharingIntent.setType("text/plain");
							String shareSubject = "STD Code";
							String shareContent = "STD Code : " + viewHolder.stdCode.getText().toString() + "\n";
							shareContent = shareContent + "City Name : " + viewHolder.city.getText().toString() + "\n";
							shareContent = shareContent + "State : " + viewHolder.state.getText().toString();
							sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
							sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
							context.startActivity(Intent.createChooser(sharingIntent,
									context.getResources().getText(R.string.send_to)));
						} else {
							String uri = "http://maps.google.com/maps?q=" + viewHolder.city.getText().toString() + ", "
									+ viewHolder.state.getText().toString();
							Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
							intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
							context.startActivity(intent);
						}

						return false;
					}

				});
			}
		});

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
		ImageView options;
		TextView city;
		TextView stdCode;
	}
}
