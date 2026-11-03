package com.example.campusride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.google.firebase.database.*;

public class HomeFragment extends Fragment {

    RecyclerView recycler;
    ArrayList<AnnouncementModel> list;
    AnnouncementAdapter adapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔥 RecyclerView setup
        recycler = view.findViewById(R.id.recyclerAnnouncements);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new AnnouncementAdapter(getContext(), list);
        recycler.setAdapter(adapter);

        // 🔥 Firebase Listener
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Announcements");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    AnnouncementModel data = s.getValue(AnnouncementModel.class);

                    if (data != null) {
                        list.add(data);
                    }
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