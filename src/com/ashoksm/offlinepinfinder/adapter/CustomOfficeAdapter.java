package com.ashoksm.offlinepinfinder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ashoksm.offlinepinfinder.R;
import com.ashoksm.offlinepinfinder.to.Office;

public class CustomOfficeAdapter extends BaseAdapter {

	Context mContext;

	private LayoutInflater mInflater;

	ArrayList<Office> offices;

	public CustomOfficeAdapter(Context context, ArrayList<Office> officesIn) {
		mContext = context;
		offices = officesIn;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return offices.size();
	}

	@Override
	public Object getItem(int position) {
		return offices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View v;

		if (convertView == null) {
			v = mInflater.inflate(R.layout.office_custom_grid, parent, false);
		} else {
			v = convertView;

		}
		if (convertView == null) {
			holder = new ViewHolder();
		} else {
			holder = (ViewHolder) v.getTag();
		}
		if (holder != null) {
			holder.officeName = (TextView) v.findViewById(R.id.officeName);

			holder.officeName.setPadding(2, 2, 2, 2);

			holder.pincode = (TextView) v.findViewById(R.id.pincode);

			holder.pincode.setPadding(2, 2, 2, 2);

			holder.stauts = (TextView) v.findViewById(R.id.stauts);

			holder.stauts.setPadding(2, 2, 2, 2);

			holder.suboffice = (TextView) v.findViewById(R.id.subofficeName);

			holder.suboffice.setPadding(2, 2, 2, 2);

			holder.headoffice = (TextView) v.findViewById(R.id.headofficeName);

			holder.headoffice.setPadding(2, 2, 2, 2);

			holder.location = (TextView) v.findViewById(R.id.locationName);

			holder.location.setPadding(2, 2, 2, 2);

			holder.telephoneNumber = (TextView) v.findViewById(R.id.telephoneNumber);

			holder.telephoneNumber.setPadding(2, 2, 2, 2);
				v.setTag(holder);
			v.setBackgroundColor(Color.rgb(245, 245, 245));

			holder.officeName.setText(offices.get(position).getOfficeName());
			holder.pincode.setText(offices.get(position).getPinCode());
			holder.stauts.setText(offices.get(position).getStatus());
			if (offices.get(position).getSuboffice() != null
					&& offices.get(position).getSuboffice().trim().length() > 0) {
				holder.suboffice.setText(offices.get(position).getSuboffice());
			} else {
				holder.suboffice.setText("N/A");
			}
			holder.headoffice.setText(offices.get(position).getHeadoffice());
			if (offices.get(position).getLocation() != null && offices.get(position).getLocation().trim().length() > 0) {
				holder.location.setText(offices.get(position).getLocation());
			} else {
				holder.location.setText("N/A");
			}
			if (offices.get(position).getTelephone() != null
					&& offices.get(position).getTelephone().trim().length() > 0) {
				holder.telephoneNumber.setText(offices.get(position).getTelephone());
				Linkify.addLinks(holder.telephoneNumber, Linkify.ALL);
			} else {
				holder.telephoneNumber.setText("N/A");
			}
		}

		return v;
	}

	static class ViewHolder {
		TextView officeName;
		TextView pincode;
		TextView stauts;
		TextView suboffice;
		TextView headoffice;
		TextView location;
		TextView telephoneNumber;
	}
}
