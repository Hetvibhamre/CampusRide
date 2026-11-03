package com.example.campusride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    ArrayList<ScheduleModel> scheduleList;

    public ScheduleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // 🔥 RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerView);
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(), scheduleList, null, false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 🔥 Firebase listener
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schedules");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                scheduleList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ScheduleModel model = ds.getValue(ScheduleModel.class);
                    scheduleList.add(model);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return view;
    }
}