package com.ashoksm.pinfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;

public class BankBranchAdapter extends CursorAdapter {

	private String bankName;

	public BankBranchAdapter(Context context, Cursor c, boolean autoRequery, String bankNameIn) {
		super(context, c, autoRequery);
		this.bankName = bankNameIn;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
		}

		holder.branchName = (TextView) view.findViewById(R.id.branch);

		holder.mapButton = (ImageView) view.findViewById(R.id.bankMapButton);

		holder.shareButton = (ImageView) view.findViewById(R.id.bankShareButton);

		holder.city = (TextView) view.findViewById(R.id.city);

		holder.district = (TextView) view.findViewById(R.id.bankDistrict);

		holder.state = (TextView) view.findViewById(R.id.bankStateName);

		holder.address = (TextView) view.findViewById(R.id.address);

		holder.contact = (TextView) view.findViewById(R.id.contact);

		holder.ifsc = (TextView) view.findViewById(R.id.ifsc);

		holder.micr = (TextView) view.findViewById(R.id.micr);

		view.setTag(holder);

		holder.mapButton.setTag(holder);

		holder.mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				String uri = "http://maps.google.com/maps?q=" + bankName + ", "
						+ holder.address.getText().toString().trim().replaceAll(" ", "+");
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
				String shareSubject = "Branch Details";
				String shareContent = "Branch Name : " + holder.branchName.getText().toString().trim() + "\n";
				shareContent = shareContent + "City : " + holder.city.getText().toString().trim() + "\n";
				shareContent = shareContent + "District : " + holder.district.getText().toString().trim() + "\n";
				shareContent = shareContent + "State : " + holder.state.getText().toString() + "\n";
				shareContent = shareContent + "Address : " + holder.address.getText().toString() + "\n";
				shareContent = shareContent + "Contact : " + holder.contact.getText().toString() + "\n";
				shareContent = shareContent + "IFSC : " + holder.ifsc.getText().toString() + "\n";
				shareContent = shareContent + "MICR : " + holder.micr.getText().toString() + "\n";
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
				v.getContext().startActivity(
						Intent.createChooser(sharingIntent, v.getContext().getResources().getText(R.string.send_to)));
			}

		});

		holder.branchName.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.NAME)));
		holder.city.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.CITY)));
		holder.district.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.DISTRICT)));
		holder.state.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.STATE)));
		holder.contact.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.CONTACT)));
		holder.address.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.ADDRESS)));
		holder.ifsc.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.ID)));
		holder.micr.setText(cursor.getString(cursor.getColumnIndex(BankBranchSQLiteHelper.MICR)));
		Linkify.addLinks(holder.contact, Linkify.ALL);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.bank_custom_grid, parent, false);
		return retView;
	}

	static class ViewHolder {
		TextView branchName;
		ImageView mapButton;
		ImageView shareButton;
		TextView city;
		TextView district;
		TextView state;
		TextView address;
		TextView contact;
		TextView ifsc;
		TextView micr;
	}
}
