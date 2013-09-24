package com.ashoksm.pinfinder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.to.Office;

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

			holder.pincode = (TextView) v.findViewById(R.id.pincode);

			holder.stauts = (TextView) v.findViewById(R.id.stauts);

			holder.suboffice = (TextView) v.findViewById(R.id.subofficeName);

			holder.subofficeRow = (LinearLayout) v.findViewById(R.id.subofficeRow);

			holder.headoffice = (TextView) v.findViewById(R.id.headofficeName);

			holder.headofficeRow = (LinearLayout) v.findViewById(R.id.headofficeRow);

			holder.location = (TextView) v.findViewById(R.id.locationName);

			holder.locationRow = (LinearLayout) v.findViewById(R.id.locationRow);

			holder.telephoneNumber = (TextView) v.findViewById(R.id.telephoneNumber);

			holder.telephoneRow = (LinearLayout) v.findViewById(R.id.telephoneRow);

			holder.state = (TextView) v.findViewById(R.id.stateName);

			v.setTag(holder);

			holder.officeName.setText(offices.get(position).getOfficeName());
			holder.pincode.setText(offices.get(position).getPinCode());
			holder.stauts.setText(offices.get(position).getStatus());
			holder.state.setText(offices.get(position).getStateName());

			if (offices.get(position).getSuboffice() != null
					&& offices.get(position).getSuboffice().trim().length() > 0) {
				holder.subofficeRow.setVisibility(View.VISIBLE);
				holder.suboffice.setText(offices.get(position).getSuboffice());
			} else {
				holder.subofficeRow.setVisibility(View.GONE);
			}

			if (offices.get(position).getHeadoffice() != null
					&& offices.get(position).getHeadoffice().trim().length() > 0) {
				holder.headofficeRow.setVisibility(View.VISIBLE);
				holder.headoffice.setText(offices.get(position).getHeadoffice());
			} else {
				holder.headofficeRow.setVisibility(View.GONE);
			}

			if (offices.get(position).getLocation() != null && offices.get(position).getLocation().trim().length() > 0) {
				holder.locationRow.setVisibility(View.VISIBLE);
				holder.location.setText(offices.get(position).getLocation());
			} else {
				holder.locationRow.setVisibility(View.GONE);
			}
			if (offices.get(position).getTelephone() != null
					&& offices.get(position).getTelephone().trim().length() > 0) {
				holder.telephoneRow.setVisibility(View.VISIBLE);
				holder.telephoneNumber.setText(offices.get(position).getTelephone());
				Linkify.addLinks(holder.telephoneNumber, Linkify.ALL);
			} else {
				holder.telephoneRow.setVisibility(View.GONE);
			}
		}

		return v;
	}

	static class ViewHolder {
		TextView officeName;
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
