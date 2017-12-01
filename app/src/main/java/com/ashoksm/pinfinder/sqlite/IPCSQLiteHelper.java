package com.ashoksm.pinfinder.sqlite;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IPCSQLiteHelper extends SQLiteOpenHelper {

    // Common column names
    public static final String ID = "_id";
    public static final String PARTICULAR = "particular";
    public static final String IS_DESCRIPTION_AVAILABLE = "is_description_available";
    private static final String DESCRIPTION = "description";
    private static final String HEADER = "header";

    // Logcat tag
    private static final String CLASS_NAME = IPCSQLiteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ashoksm.ipc";

    // Table Names
    private static final String TABLE_IPC = "ipc";
    private static final String TABLE_IPC_DETAILS = "ipc_details";
    private static final String TABLE_FOOT_NOTES = "foot_notes";

    // table create statements
    private static final String CREATE_IPC_TABLE = "CREATE TABLE " + TABLE_IPC + "(" + ID
            + " TEXT, " + PARTICULAR + " TEXT, " + IS_DESCRIPTION_AVAILABLE +
            " TEXT, PRIMARY KEY (" + ID + "))";
    private static final String CREATE_IPC_DETAILS = "CREATE TABLE " + TABLE_IPC_DETAILS + "(" + ID
            + " TEXT, " + HEADER + " TEXT, " + DESCRIPTION + " TEXT, PRIMARY KEY (" + ID + "))";
    private static final String CREATE_FOOT_NOTES = "CREATE TABLE " + TABLE_FOOT_NOTES
            + "(" + ID + " TEXT, " + DESCRIPTION + " TEXT, PRIMARY KEY (" + ID + "))";

    private Activity context;
    private DonutProgress progressBar;

    public IPCSQLiteHelper(Activity contextIn, DonutProgress progressBarIn) {
        super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
        context = contextIn;
        progressBar = progressBarIn;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // crate tables
        db.execSQL(CREATE_IPC_TABLE);
        db.execSQL(CREATE_IPC_DETAILS);
        db.execSQL(CREATE_FOOT_NOTES);

        // insert ipc
        insert(db, "sql/ipc/ipc.sql", 25);

        // insert ipc details
        insert(db, "sql/ipc/ipc_details.sql", 50);

        // insert foot notes
        insert(db, "sql/ipc/foot_notes.sql", 75);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IPC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IPC_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOT_NOTES);

        // create new tables
        onCreate(db);
    }

    private void insert(SQLiteDatabase db, String fileName, int progress) {
        try {
            db.beginTransaction();
            // Open the resource
            InputStream insertsStream = context.getAssets().open(fileName);
            BufferedReader insertReader =
                    new BufferedReader(new InputStreamReader(insertsStream));
            while (insertReader.ready()) {
                db.execSQL(insertReader.readLine());
            }
            insertReader.close();
            if (!context.isFinishing()) {
                context.runOnUiThread(() -> progressBar.setProgress(progress));
            }

            db.setTransactionSuccessful();
        } catch (IOException ioEx) {
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public Cursor findIPC() {
        SQLiteDatabase db = this.getReadableDatabase();
        String select =
                "SELECT " + ID + ", CASE WHEN " + IS_DESCRIPTION_AVAILABLE
                        + " = 'Y' THEN '<font color=\"#FF5252\"><u>' ||" + PARTICULAR
                        + " || '</u></font>' ELSE " + PARTICULAR + " END " + PARTICULAR
                        + ", " + IS_DESCRIPTION_AVAILABLE + " FROM " + TABLE_IPC;
        return db.rawQuery(select, null);
    }

    public String[] getDescription(String ipc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select =
                "SELECT " + HEADER + ", " + DESCRIPTION + " FROM " + TABLE_IPC_DETAILS + " WHERE " +
                        ID +
                        " = ?";
        Cursor cursor = db.rawQuery(select, new String[]{ipc});
        cursor.moveToFirst();
        String header = cursor.getString(0);
        String description = cursor.getString(1);
        cursor.close();
        return new String[]{header, description};
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
