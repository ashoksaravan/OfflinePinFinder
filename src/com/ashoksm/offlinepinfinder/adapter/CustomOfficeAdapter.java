package com.ashoksm.offlinepinfinder.adapter;

import java.util.ArrayList;

import android.content.Context;
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
		// TODO Auto-generated method stub
		return offices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.office_custom_grid, parent, false);

			holder = new ViewHolder();

			holder.txtId = (TextView) convertView.findViewById(R.id.txtId);

			holder.txtId.setPadding(2, 2, 2, 2);

			holder.txtName = (TextView) convertView.findViewById(R.id.txtName);

			holder.txtName.setPadding(2, 2, 2, 2);

			if (position == 0) {

				convertView.setTag(holder);
			}
		}

		else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder != null) {
			holder.txtId.setText(offices.get(position).getOfficeName());
			holder.txtName.setText(offices.get(position).getPinCode());
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtId;
		TextView txtName;
	}
}
