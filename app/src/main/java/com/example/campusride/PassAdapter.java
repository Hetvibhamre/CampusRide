package com.example.campusride;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.ViewHolder> {

    ArrayList<StudentModel> list;
    Context context;
    DatabaseHelper databaseHelper;

    public PassAdapter(Context context, ArrayList<StudentModel> list) {
        this.context = context;
        this.list = list;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pass_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudentModel model = list.get(position);

        holder.name.setText(model.getName());

        holder.details.setText(
                "Programme: " + model.getProgramme() +
                        " | Branch: " + model.getBranch() +
                        " | Year: " + model.getYear() +
                        " | Valid Till: " + model.getValidity() +
                        " | SAP ID: " + model.getSapId()
        );

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Pass")
                    .setMessage("Are you sure you want to delete this pass?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        databaseHelper.deletePass(model.getSapId());

                        int pos = holder.getAdapterPosition();
                        list.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, list.size());

                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, details;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.studentName);
            details = itemView.findViewById(R.id.studentDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}