package com.ashoksm.pinfinder.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;

public class RTORecyclerViewAdapter extends CursorRecyclerViewAdapter<RTORecyclerViewAdapter.ViewHolder> {

	private Context context;
	private int lastPosition = -1;

	public RTORecyclerViewAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		this.context = context;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		TextView state;
		ImageButton options;
		TextView city;
		TextView rtoCode;
		View v;

		public ViewHolder(View view) {
			super(view);
			options = (ImageButton) view.findViewById(R.id.options);
			city = (TextView) view.findViewById(R.id.rCityName);
			state = (TextView) view.findViewById(R.id.rtoStateName);
			rtoCode = (TextView) view.findViewById(R.id.rtoCode);
			v = view;
		}

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, Cursor cursor, int position) {
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
					Log.e("Failed to create options menu : ", e.getMessage());
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
							String shareSubject = "RTO Code";
							String shareContent = "RTO Code : " + viewHolder.rtoCode.getText().toString() + "\n";
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

		holder.city.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.CITY)));
		holder.state.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.STATE_NAME)));
		holder.rtoCode.setText(cursor.getString(cursor.getColumnIndex(RTOSQLiteHelper.ID)));
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
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rto_custom_grid, parent, false);
		ViewHolder vh = new ViewHolder(itemView);
		return vh;
	}

}
