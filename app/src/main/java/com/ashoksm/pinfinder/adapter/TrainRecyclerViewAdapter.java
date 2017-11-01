package com.ashoksm.pinfinder.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.RouteAndScheduleActivity;
import com.ashoksm.pinfinder.TrainsFragment;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TrainRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<TrainRecyclerViewAdapter.ViewHolder> {

    private Activity context;

    public TrainRecyclerViewAdapter(Activity context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor, int position) {
        holder.options.setTag(holder);

        holder.options.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenuInflater().inflate(R.menu.options_menu, menu.getMenu());

                Menu popupMenu = menu.getMenu();
                popupMenu.findItem(R.id.addToFav).setVisible(false);
                popupMenu.findItem(R.id.deleteFav).setVisible(false);
                popupMenu.findItem(R.id.viewOnMap).setTitle("Navigate")
                        .setIcon(R.drawable.ic_maps_navigation);

                try {
                    Field[] fields = menu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(menu);
                            Class<?> classPopupHelper =
                                    Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons =
                                    classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.getMessage());
                }

                menu.show();
                final ViewHolder viewHolder = (ViewHolder) v.getTag();
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().toString()
                            .equals(context.getResources().getString(R.string.share))) {
                        share(viewHolder);
                    } else {
                        openGMAP(viewHolder);
                    }
                    return false;
                });
            }
        });

        holder.trainName.setOnClickListener(view -> {
            Intent intent = new Intent(context, RouteAndScheduleActivity.class);
            intent.putExtra(TrainsFragment.EXTRA_TRAIN, holder.trainNo.getText().toString());
            intent.putExtra(TrainsFragment.EXTRA_STARTS, holder.trainName.getText().toString());
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_out_left, 0);
        });

        String pantry = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.PANTRY));
        if (pantry != null && pantry.trim().length() > 0) {
            holder.pantry.setText(pantry);
        } else {
            holder.pantry.setText(context.getResources().getText(R.string.no));
        }
        holder.days.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.DAYS)));
        holder.trainNo.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ID)));
        String trainName = cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.TRAIN_NAME));
        SpannableString spanString = new SpannableString(trainName);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        holder.trainName.setText(spanString);
        holder.starts.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.STARTS)));
        holder.ends.setText(cursor.getString(cursor.getColumnIndex(RailWaysSQLiteHelper.ENDS)));
    }

    private void share(ViewHolder vh) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        String shareSubject = "Train Details";
        String shareContent = "Train No : " + vh.trainNo.getText().toString() + "\n";
        shareContent = shareContent + "Train Name : " + vh.trainName.getText().toString() + "\n";
        shareContent = shareContent + "Starts : " + vh.starts.getText().toString() + "\n";
        shareContent = shareContent + "Ends : " + vh.ends.getText().toString() + "\n";
        shareContent = shareContent + "Days : " + vh.days.getText().toString() + "\n";
        shareContent = shareContent + "Pantry : " + vh.pantry.getText().toString();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getResources().getText(R.string.send_to)));
    }

    private void openGMAP(ViewHolder h) {
        String uri = "http://maps.google.com/maps?saddr=" + h.starts.getText().toString()
                + " train station&daddr=" + h.ends.getText().toString() + " train station&dirflg=r";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.maps_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_custom_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trainNo;
        TextView trainName;
        TextView starts;
        TextView ends;
        ImageButton options;
        TextView days;
        TextView pantry;
        View v;

        public ViewHolder(View view) {
            super(view);
            options = view.findViewById(R.id.options);
            trainNo = view.findViewById(R.id.train_no);
            trainName = view.findViewById(R.id.train_name);
            starts = view.findViewById(R.id.starts);
            ends = view.findViewById(R.id.ends);
            days = view.findViewById(R.id.days);
            pantry = view.findViewById(R.id.pantry);
            v = view;
        }

    }

}
