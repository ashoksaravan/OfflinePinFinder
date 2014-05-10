package com.ashoksm.pinfinder.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.to.Office;

public class CustomOfficeAdapter extends BaseAdapter {

	Context mContext;

	private LayoutInflater mInflater;

	List<Office> offices;

	public CustomOfficeAdapter(Context context, List<Office> officesIn) {
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

			holder.mapButton = (ImageView) v.findViewById(R.id.mapButton);

			holder.shareButton = (ImageView) v.findViewById(R.id.shareButton);

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

			holder.mapButton.setTag(position);

			holder.mapButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					String uri = "http://maps.google.com/maps?q=" + offices.get(pos).getStateName() + " "
							+ offices.get(pos).getPinCode();
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					mContext.startActivity(intent);
				}

			});

			holder.shareButton.setTag(position);

			holder.shareButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					String shareSubject = "Pincode";
					String shareContent = "Office Name : " + offices.get(pos).getOfficeName() + "\n";
					shareContent = shareContent + "Pincode : " + offices.get(pos).getPinCode() + "\n";
					shareContent = shareContent + "Status : " + offices.get(pos).getStatus() + "\n";
					if (offices.get(pos).getSuboffice() != null && offices.get(pos).getSuboffice().trim().length() > 0) {
						shareContent = shareContent + "Sub Office : " + offices.get(pos).getSuboffice() + "\n";
					}
					if (offices.get(pos).getHeadoffice() != null
							&& offices.get(pos).getHeadoffice().trim().length() > 0) {
						shareContent = shareContent + "Head Office : " + offices.get(pos).getHeadoffice() + "\n";
					}
					shareContent = shareContent + "Location : " + offices.get(pos).getLocation() + "\n";
					shareContent = shareContent + "State : " + offices.get(pos).getStateName() + "\n";
					if (offices.get(pos).getTelephone() != null && offices.get(pos).getTelephone().trim().length() > 0) {
						shareContent = shareContent + "Telephone : " + offices.get(pos).getTelephone() + "\n";
					}
					sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
					mContext.startActivity(Intent.createChooser(sharingIntent,
							mContext.getResources().getText(R.string.send_to)));
				}

			});

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
