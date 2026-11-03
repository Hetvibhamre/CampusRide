package com.example.campusride;

import android.content.Context;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Context context;
    ArrayList<ScheduleModel> list;
    OnItemActionListener listener;
    boolean isAdmin;

    public interface OnItemActionListener {
        void onDelete(int position);
        void onEdit(int position);
    }

    public ScheduleAdapter(Context context, ArrayList<ScheduleModel> list,
                           OnItemActionListener listener, boolean isAdmin) {

        this.context = context;
        this.list = list;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvTime, tvType;
        Button btnDelete, btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvType = itemView.findViewById(R.id.tvType);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleModel m = list.get(position);
        holder.tvLabel.setText(m.getLabel());
        holder.tvTime.setText(m.getTime());
        holder.tvType.setText(m.getType());

        if (isAdmin) {

            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);

            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(position);
            });

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(position);
            });

        } else {

            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}