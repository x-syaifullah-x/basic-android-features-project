package com.example.androidlabs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "database";
    private static final int VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreateTable = "CREATE TABLE " + DatabaseSchema.Messages.NAME + "(" + DatabaseSchema.Messages.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseSchema.Messages.Columns.IS_SEND + " INTEGER, " + DatabaseSchema.Messages.Columns.TEXT + " TEXT)";
        db.execSQL(queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
