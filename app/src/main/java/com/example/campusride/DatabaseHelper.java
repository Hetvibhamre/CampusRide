package com.example.campusride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusRide.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PASSES = "passes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    private static final String COLUMN_SAP = "sapId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PROGRAMME = "programme";
    private static final String COLUMN_BRANCH = "branch";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_VALIDITY = "validity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_ROLE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_PASS_TABLE = "CREATE TABLE " + TABLE_PASSES + "("
                + COLUMN_SAP + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_PROGRAMME + " TEXT, "
                + COLUMN_BRANCH + " TEXT, "
                + COLUMN_YEAR + " TEXT, "
                + COLUMN_VALIDITY + " TEXT"
                + ")";
        db.execSQL(CREATE_PASS_TABLE);
        db.execSQL("CREATE TABLE schedules (id INTEGER PRIMARY KEY AUTOINCREMENT, label TEXT, time TEXT, type TEXT)");

        insertUser(db, "admin", "admin123", "admin");
        insertUser(db, "70012300090", "mpstme", "student");
        insertUser(db, "70012300091", "mpstme", "student");
        insertUser(db, "70012300092", "mpstme", "student");
        insertUser(db, "70012300093", "mpstme", "student");
        insertUser(db, "70012300094", "mpstme", "student");
        insertUser(db, "70012300095", "mpstme", "student");
        insertUser(db, "70012300096", "mpstme", "student");
        insertUser(db, "70012300097", "mpstme", "student");
        insertUser(db, "70012300098", "mpstme", "student");
        insertUser(db, "70012300099", "mpstme", "student");
        insertUser(db, "security", "secure123", "security");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSES);
        onCreate(db);
    }

    private void insertUser(SQLiteDatabase db, String username, String password, String role) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        db.insert(TABLE_USERS, null, values);
    }

    public String checkUser(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM " + TABLE_USERS +
                        " WHERE username=? AND password=?",
                new String[]{username, password});

        if (cursor.moveToFirst()) {

            String role = cursor.getString(0);
            cursor.close();
            return role;

        } else {

            cursor.close();
            return null;
        }
    }

    public boolean insertPass(String sapId, String name, String programme, String branch, String year, String validity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_SAP, sapId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PROGRAMME, programme);
        values.put(COLUMN_BRANCH, branch);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_VALIDITY, validity);

        long result = db.insert(TABLE_PASSES, null, values);

        return result != -1;
    }

    public ArrayList<StudentModel> getAllPasses() {

        ArrayList<StudentModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PASSES, null);

        if (cursor.moveToFirst()) {

            do {

                String sapId = cursor.getString(0);
                String name = cursor.getString(1);
                String programme = cursor.getString(2);
                String branch = cursor.getString(3);
                String year = cursor.getString(4);
                String validity = cursor.getString(5);

                list.add(new StudentModel(name, programme, branch, year, sapId, validity));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    // 🔥 NEW METHOD (NO BREAKAGE)
    public ArrayList<StudentModel> getActivePasses() {

        ArrayList<StudentModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PASSES + " WHERE validity != 'PENDING'", null);

        if (cursor.moveToFirst()) {

            do {

                String sapId = cursor.getString(0);
                String name = cursor.getString(1);
                String programme = cursor.getString(2);
                String branch = cursor.getString(3);
                String year = cursor.getString(4);
                String validity = cursor.getString(5);

                list.add(new StudentModel(name, programme, branch, year, sapId, validity));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    public StudentModel getPassBySapId(String sapId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PASSES + " WHERE " + COLUMN_SAP + "=?",
                new String[]{sapId}
        );

        if (cursor.moveToFirst()) {

            String name = cursor.getString(1);
            String programme = cursor.getString(2);
            String branch = cursor.getString(3);
            String year = cursor.getString(4);
            String validity = cursor.getString(5);

            cursor.close();

            return new StudentModel(name, programme, branch, year, sapId, validity);
        }

        cursor.close();
        return null;
    }

    public void deletePass(String sapId) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PASSES, COLUMN_SAP + "=?", new String[]{sapId});
    }
    // 🔥 LINE ~186 ADD HERE
    public boolean insertSchedule(String label, String time, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("label", label);
        cv.put("time", time);
        cv.put("type", type);

        long result = db.insert("schedules", null, cv);
        return result != -1;
    }
    // 🔥 LINE ~200 ADD HERE
    public ArrayList<ScheduleModel> getAllSchedules() {
        ArrayList<ScheduleModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM schedules", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new ScheduleModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // 🔥 DELETE SCHEDULE FROM DATABASE
    public void deleteSchedule(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("schedules", "id=?", new String[]{String.valueOf(id)});
    }
    // 🔥 UPDATE SCHEDULE
    public void updateSchedule(int id, String label, String time, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("label", label);
        cv.put("time", time);
        cv.put("type", type);

        db.update("schedules", cv, "id=?", new String[]{String.valueOf(id)});
    }
}