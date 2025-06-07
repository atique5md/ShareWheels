package com.example.sharewheel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBSharewheel extends SQLiteOpenHelper {
    private static final String DB_NAME = "sharewheelDB";
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static final int DBversion = 1;

    private static final String TABLE_Swheel = "ShareWheel";
    private static final String KEY_ID = "id";
    private static final String KEY_Driver = "Driver";
    private static final String KEY_Rider = "Rider";
    private static final String KEY_Current_Location = "CurrentLocation";
    private static final String KEY_Search_Location = "SearchLocation";

    public DBSharewheel(Context context) {
        super(context, DB_NAME, factory, DBversion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_Swheel + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_Driver + " TEXT, "
                + KEY_Rider + " TEXT, "
                + KEY_Current_Location + " TEXT, "
                + KEY_Search_Location + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Swheel);
        onCreate(db);
    }

    // ✅ Method to insert data

    public void insertLocationData(String driver, String rider, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Driver", driver);
        values.put("Rider", rider);
        values.put("Location", location);
        db.insert("ShareWheel", null, values);
        db.close();
    }

}

