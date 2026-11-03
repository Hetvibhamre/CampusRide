package com.example.campusride;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    Context context;
    ArrayList<AnnouncementModel> list; // 🔥 MODEL LIST

    public AnnouncementAdapter(Context context, ArrayList<AnnouncementModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvAnnouncementText);
            tvTime = itemView.findViewById(R.id.tvTime); // 🔥 timestamp view
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AnnouncementModel model = list.get(position);

        // 🔥 TEXT
        holder.tvText.setText(model.getText());

        // 🔥 TIMESTAMP FORMAT
        long time = model.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(new Date(time));

        holder.tvTime.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}