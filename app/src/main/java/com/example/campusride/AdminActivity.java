package com.example.campusride;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Dialog;
import android.widget.EditText;



public class AdminActivity extends AppCompatActivity {

    EditText etName, etSap;
    Spinner spProgramme, spBranch, spYear;
    Button btnGenerate, btnNewPass, btnImportCSV, btnValidity;

    LinearLayout generateSection;
    LinearLayout sectionPass, sectionSchedule, sectionAnnouncement;

    DatabaseHelper databaseHelper;
    FirebaseAuth mAuth;

    RecyclerView recyclerView;
    PassAdapter adapter;
    ArrayList<StudentModel> passList;

    String selectedDate = "";

    TextView tabPasses, tabSchedule, tabAnnouncement;
    RecyclerView scheduleRecyclerView;
    ScheduleAdapter scheduleAdapter;
    ArrayList<ScheduleModel> scheduleList;
    TextView tvNoSchedule;
    RecyclerView announcementRecyclerView;
    ArrayList<AnnouncementModel> announcementList;
    AnnouncementAdapter announcementAdapter;
    TextView tvNoAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        announcementRecyclerView = findViewById(R.id.announcementRecyclerView);
        tvNoAnnouncement = findViewById(R.id.tvNoAnnouncement);

        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        announcementRecyclerView.setAdapter(announcementAdapter);
        ItemTouchHelper.SimpleCallback swipe =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {

                        int position = holder.getAdapterPosition();

                        if (position == RecyclerView.NO_POSITION) return;

                        AnnouncementModel model = announcementList.get(position);

                        if (model == null || model.getKey() == null) {
                            Toast.makeText(AdminActivity.this, "Error deleting", Toast.LENGTH_SHORT).show();
                            announcementAdapter.notifyDataSetChanged();
                            return;
                        }

                        FirebaseDatabase.getInstance()
                                .getReference("Announcements")
                                .child(model.getKey())
                                .removeValue();

                        Toast.makeText(AdminActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                };

        new ItemTouchHelper(swipe).attachToRecyclerView(announcementRecyclerView);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Announcements");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                announcementList.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    AnnouncementModel data = s.getValue(AnnouncementModel.class);

                    if (data != null) {
                        data.setKey(s.getKey()); // 🔥 VERY IMPORTANT
                        announcementList.add(data);
                    }
                }

                announcementAdapter.notifyDataSetChanged();

                if (announcementList.isEmpty()) {
                    tvNoAnnouncement.setVisibility(View.VISIBLE);
                } else {
                    tvNoAnnouncement.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();   // 🔥 logout

            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        etName = findViewById(R.id.etName);
        etSap = findViewById(R.id.etSap);
        spProgramme = findViewById(R.id.spProgramme);
        spBranch = findViewById(R.id.spBranch);
        spYear = findViewById(R.id.spYear);
        btnGenerate = findViewById(R.id.btnGenerate);

        btnNewPass = findViewById(R.id.btnNewPass);
        btnImportCSV = findViewById(R.id.btnImportCSV);
        btnValidity = findViewById(R.id.btnValidity);

        generateSection = findViewById(R.id.generateSection);
        recyclerView = findViewById(R.id.passRecyclerView);
        sectionPass = findViewById(R.id.sectionPass);
        sectionSchedule = findViewById(R.id.sectionSchedule);
        sectionAnnouncement = findViewById(R.id.sectionAnnouncement);

        tabPasses = findViewById(R.id.tabPasses);
        tabSchedule = findViewById(R.id.tabSchedule);
        tabAnnouncement = findViewById(R.id.tabAnnouncement);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        Button btnNewAnnouncement = findViewById(R.id.btnNewAnnouncement);

        btnNewAnnouncement.setOnClickListener(v -> showAnnouncementDialog());
        tvNoSchedule = findViewById(R.id.tvNoSchedule);
        Button btnAddSchedule = findViewById(R.id.btnAddSchedule);

        btnAddSchedule.setOnClickListener(v -> {
            showAddScheduleDialog();
        });

        databaseHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        passList = new ArrayList<>();
        adapter = new PassAdapter(this, passList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadData();

        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(this, scheduleList, new ScheduleAdapter.OnItemActionListener() {

            @Override
            public void onDelete(int position) {
                int id = scheduleList.get(position).getId();   // 🔥 GET ID

                databaseHelper.deleteSchedule(id);            // 🔥 DELETE FROM DB

                loadSchedules();                              // 🔥 RELOAD CLEAN DATA
                syncSchedulesToFirebase();


                Toast.makeText(AdminActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEdit(int position) {
                ScheduleModel schedule = scheduleList.get(position);
                showEditScheduleDialog(schedule);
            }
        }, true);

        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(scheduleAdapter);

        loadSchedules();

        btnNewPass.setOnClickListener(v -> {
            if (generateSection.getVisibility() == View.VISIBLE) {
                generateSection.setVisibility(View.GONE);
            } else {
                generateSection.setVisibility(View.VISIBLE);
            }
        });

        btnImportCSV.setOnClickListener(v -> {
            importCSV();
            Toast.makeText(this, "Students Imported to Firebase", Toast.LENGTH_SHORT).show();
        });

        btnValidity.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    AdminActivity.this,
                    (view, y, m, d) -> {
                        selectedDate = d + "/" + (m + 1) + "/" + y;
                        btnValidity.setText("Valid Till: " + selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        setupSpinners();
        btnGenerate.setOnClickListener(v -> generatePass());

        tabPasses.setOnClickListener(v -> {
            resetTabs();
            tabPasses.setBackgroundResource(R.drawable.tab_selected_bg);
            tabPasses.setTextColor(Color.WHITE);
            sectionPass.setVisibility(View.VISIBLE);
            sectionSchedule.setVisibility(View.GONE);
            sectionAnnouncement.setVisibility(View.GONE);
        });

        tabSchedule.setOnClickListener(v -> {



            resetTabs();
            tabSchedule.setBackgroundResource(R.drawable.tab_selected_bg);
            tabSchedule.setTextColor(Color.WHITE);
            sectionPass.setVisibility(View.GONE);
            sectionSchedule.setVisibility(View.VISIBLE);
            sectionAnnouncement.setVisibility(View.GONE);
        });

        tabAnnouncement.setOnClickListener(v -> {
            resetTabs();

            tabAnnouncement.setBackgroundResource(R.drawable.tab_selected_bg);
            tabAnnouncement.setTextColor(Color.WHITE);

            sectionPass.setVisibility(View.GONE);
            sectionSchedule.setVisibility(View.GONE);
            sectionAnnouncement.setVisibility(View.VISIBLE);
        });

    }

    // 🔥 LOAD SCHEDULES
    private void loadSchedules() {
        scheduleList.clear();
        scheduleList.addAll(databaseHelper.getAllSchedules());
        scheduleAdapter.notifyDataSetChanged();

// 🔥 EMPTY TEXT CONTROL
        if (scheduleList.isEmpty()) {
            tvNoSchedule.setVisibility(View.VISIBLE);
        } else {
            tvNoSchedule.setVisibility(View.GONE);
        };
    }

    private void resetTabs() {
        tabPasses.setBackgroundColor(Color.TRANSPARENT);
        tabSchedule.setBackgroundColor(Color.TRANSPARENT);
        tabAnnouncement.setBackgroundColor(Color.TRANSPARENT);


        tabPasses.setTextColor(Color.BLACK);
        tabSchedule.setTextColor(Color.BLACK);
        tabAnnouncement.setTextColor(Color.BLACK);

    }

    private void loadData() {
        passList.clear();
        passList.addAll(databaseHelper.getAllPasses());
        adapter.notifyDataSetChanged();
    }

    private void setupSpinners() {

        String[] programmes = {"BTech", "MBATech", "BPharm", "MBAPharm"};
        String[] years = {"1st", "2nd", "3rd", "4th"};
        String[] branches = {"CS", "IT", "CE", "AIML", "DS"};

        ArrayAdapter<String> progAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, programmes) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgramme.setAdapter(progAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, branches) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);

        spProgramme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spProgramme.getSelectedItem().toString();
                if (selected.equals("BTech") || selected.equals("MBATech")) {
                    spBranch.setVisibility(View.VISIBLE);
                } else {
                    spBranch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void generatePass() {

        String name = etName.getText().toString().trim();
        String sap = etSap.getText().toString().trim();
        String programme = spProgramme.getSelectedItem().toString();
        String branch = spBranch.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sap)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select validity date", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentModel existing = databaseHelper.getPassBySapId(sap);
        if (existing != null) {
            Toast.makeText(this, "Pass already exists for this SAP ID", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = databaseHelper.insertPass(
                sap, name, programme, branch, year, selectedDate
        );

        if (!isInserted) {
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Pass Generated", Toast.LENGTH_SHORT).show();

        loadData();
        generateSection.setVisibility(View.GONE);

        String email = sap + "@campusride.com";
        String password = "mpstme";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .child("name").setValue(name);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .child("sapId").setValue(sap);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .child("role").setValue("student");
                    }
                });
    }

    private void importCSV() {
        try {
            InputStream is = getAssets().open("students.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                String sap = data[0];
                String name = data[1];

                String email = sap + "@campusride.com";
                String password = "mpstme";

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(uid)
                                        .child("name").setValue(name);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(uid)
                                        .child("sapId").setValue(sap);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(uid)
                                        .child("role").setValue("student");
                            }
                        });
            }

            Toast.makeText(this, "CSV Imported to Firebase", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    // 👇 ADD THIS BELOW importCSV() METHOD
    private void showAddScheduleDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_schedule);
        dialog.setCancelable(true);

        Spinner spShift = dialog.findViewById(R.id.spShift);
        EditText etTime = dialog.findViewById(R.id.etTime);
        Spinner spType = dialog.findViewById(R.id.spType);
        Button btnAdd = dialog.findViewById(R.id.btnAddScheduleDialog);
        Button btnCancel = dialog.findViewById(R.id.btnCancelDialog);

        String[] types = {"Pickup", "Drop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, types) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getView(position, convertView, parent);

                text.setTextColor(Color.BLACK);   // 🔥 SELECTED TEXT FIX
                text.setTextSize(16);

                return text;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getDropDownView(position, convertView, parent);

                text.setTextColor(Color.WHITE);   // dropdown text
                text.setBackgroundColor(Color.parseColor("#444444")); // same dark UI
                text.setPadding(30, 30, 30, 30);

                return text;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        String[] shifts = {"Morning", "Afternoon", "Evening"};

        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shifts);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spShift.setAdapter(shiftAdapter);
        etTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {

                        String amPm = (selectedHour >= 12) ? "PM" : "AM";
                        int hour12 = selectedHour % 12;
                        if (hour12 == 0) hour12 = 12;

                        String time = hour12 + ":" +
                                String.format("%02d", selectedMinute) + " " + amPm;

                        etTime.setText(time);
                    },
                    hour,
                    minute,
                    false
            );

            timePicker.show();
        });



        btnAdd.setOnClickListener(v -> {

            String label = spShift.getSelectedItem().toString();
            String time = etTime.getText().toString();
            String type = spType.getSelectedItem().toString();

            if (label.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = databaseHelper.insertSchedule(label, time, type);
            if (inserted) {
                Toast.makeText(this, "Schedule Added", Toast.LENGTH_SHORT).show();

                loadSchedules();              // 🔥 FIRST update list
                syncSchedulesToFirebase();   // 🔥 THEN sync

            } else {
                Toast.makeText(this, "Error saving schedule", Toast.LENGTH_SHORT).show();
            }

            if (inserted) {
                Toast.makeText(this, "Schedule Added", Toast.LENGTH_SHORT).show();
                loadSchedules();
            } else {
                Toast.makeText(this, "Error saving schedule", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void showEditScheduleDialog(ScheduleModel schedule) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_schedule);
        dialog.setCancelable(true);


        EditText etTime = dialog.findViewById(R.id.etTime);
        etTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {

                        String amPm = (selectedHour >= 12) ? "PM" : "AM";
                        int hour12 = selectedHour % 12;
                        if (hour12 == 0) hour12 = 12;

                        String time = hour12 + ":" +
                                String.format("%02d", selectedMinute) + " " + amPm;

                        etTime.setText(time);
                    },
                    hour,
                    minute,
                    false
            );

            timePicker.show();
        });
        Spinner spShift = dialog.findViewById(R.id.spShift);
        Spinner spType = dialog.findViewById(R.id.spType);
        Button btnAdd = dialog.findViewById(R.id.btnAddScheduleDialog);
        Button btnCancel = dialog.findViewById(R.id.btnCancelDialog);

        // 🔥 PREFILL DATA

        etTime.setText(schedule.getTime());

        String[] types = {"Pickup", "Drop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        String[] shifts = {"Morning", "Afternoon", "Evening"};

        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shifts);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spShift.setAdapter(shiftAdapter);

// 🔥 SET SELECTED SHIFT
        if (schedule.getLabel().equals("Morning")) {
            spShift.setSelection(0);
        } else if (schedule.getLabel().equals("Afternoon")) {
            spShift.setSelection(1);
        } else {
            spShift.setSelection(2);
        }

        // 🔥 SET SELECTED TYPE
        if (schedule.getType().equals("Pickup")) {
            spType.setSelection(0);
        } else {
            spType.setSelection(1);
        }

        btnAdd.setText("Update"); // button text change

        btnAdd.setOnClickListener(v -> {

            String label = spShift.getSelectedItem().toString();
            String time = etTime.getText().toString();
            String type = spType.getSelectedItem().toString();

            if (label.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 UPDATE DB
            databaseHelper.updateSchedule(schedule.getId(), label, time, type);

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

            loadSchedules(); // refresh list
            syncSchedulesToFirebase();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void syncSchedulesToFirebase() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schedules");

        // 🔥 CLEAR OLD DATA
        ref.removeValue().addOnCompleteListener(task -> {

            // 🔥 RE-ADD ALL DATA
            for (ScheduleModel s : scheduleList) {
                ref.push().setValue(s);
            }
        });
    }
    private void showAnnouncementDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_announcement);

        EditText et = dialog.findViewById(R.id.etAnnouncement);
        Button btn = dialog.findViewById(R.id.btnAddAnnouncement);

        btn.setOnClickListener(v -> {

            String text = et.getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter announcement", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Announcements");

            long time = System.currentTimeMillis();

            AnnouncementModel model = new AnnouncementModel(text, time);

            ref.push().setValue(model);

            Toast.makeText(this, "Announcement Added", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

}