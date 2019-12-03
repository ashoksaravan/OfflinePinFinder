package com.ashoksm.pinfinder.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashoksm.pinfinder.DisplayIPCResultActivity;
import com.ashoksm.pinfinder.IPCFragment;
import com.ashoksm.pinfinder.R;
import com.ashoksm.pinfinder.sqlite.IPCSQLiteHelper;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IPCRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<IPCRecyclerViewAdapter.ViewHolder> {

    private Activity activity;

    public IPCRecyclerViewAdapter(Activity activityIn, Cursor cursor) {
        super(cursor);
        activity = activityIn;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor, int position) {
        String ipc = cursor.getString(cursor.getColumnIndex(IPCSQLiteHelper.ID));
        holder.isDescriptionAvailable =
                cursor.getString(cursor.getColumnIndex(IPCSQLiteHelper.IS_DESCRIPTION_AVAILABLE));
        holder.ipc.setText(ipc);
        if ("Contents".equals(ipc)) {
            holder.particular.setVisibility(View.GONE);
            holder.ipc.setTypeface(null, Typeface.BOLD);
        } else {
            holder.particular.setVisibility(View.VISIBLE);
            String particular = cursor.getString(cursor.getColumnIndex(IPCSQLiteHelper.PARTICULAR));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.particular
                        .setText(Html.fromHtml(particular, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            } else {
                holder.particular.setText(Html.fromHtml(particular));
            }

            if (particular.startsWith("<font")) {
                holder.particular.setClickable(true);
            } else {
                holder.particular.setClickable(false);
            }

            if (!ipc.startsWith("Chapter") && !"Sections".equals(ipc)) {
                holder.ipc.setTypeface(null, Typeface.NORMAL);
                holder.particular.setTypeface(null, Typeface.NORMAL);
            } else {
                holder.ipc.setTypeface(null, Typeface.BOLD);
                holder.particular.setTypeface(null, Typeface.BOLD);
            }

            holder.particular.setOnClickListener(v -> {
                if ("Y".equalsIgnoreCase(holder.isDescriptionAvailable)) {
                    Intent intent = new Intent(activity, DisplayIPCResultActivity.class);
                    intent.putExtra(IPCFragment.EXTRA_IPC, holder.ipc.getText().toString());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_out_left, 0);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ipc_grid, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView ipc;
        JustifiedTextView particular;
        String isDescriptionAvailable;

        public ViewHolder(View view) {
            super(view);
            ipc = view.findViewById(R.id.ipc_id);
            particular = view.findViewById(R.id.particulars);
        }

    }

}
