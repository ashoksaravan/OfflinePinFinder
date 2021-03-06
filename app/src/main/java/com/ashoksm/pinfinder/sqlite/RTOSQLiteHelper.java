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

public class RTOSQLiteHelper extends SQLiteOpenHelper {

    // Common column names
    public static final String CITY = "city";
    public static final String STATE_NAME = "state_name";
    public static final String ID = "_id";
    // Logcat tag
    private static final String CLASS_NAME = RTOSQLiteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "ashoksm.rto";

    // Table Names
    private static final String TABLE_RTO = "rto_t";
    private static final String TABLE_STATE = "state_t";
    private static final String STATE = "state";
    private static final String RTO_CODE = "rto_code";
    // table create statements
    private static final String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "(" + STATE
            + " INTEGER, " + STATE_NAME + " TEXT, PRIMARY KEY (" + STATE + "))";
    private static final String CREATE_STD_TABLE = "CREATE TABLE " + TABLE_RTO + "(" + STATE
            + " INTEGER, " + RTO_CODE + " TEXT, " + CITY + " TEXT, FOREIGN KEY(" + STATE
            + ") REFERENCES " + TABLE_STATE + "(" + STATE + "), PRIMARY KEY (" + STATE + "," + CITY
            + "," + RTO_CODE + "))";
    private static boolean ON_CREATE;
    private Activity context;
    private DonutProgress progressBar;

    public RTOSQLiteHelper(Activity contextIn, DonutProgress progressBarIn) {
        super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
        context = contextIn;
        progressBar = progressBarIn;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ON_CREATE = true;
        // crate tables
        Log.d(CLASS_NAME, CREATE_STATE_TABLE);
        db.execSQL(CREATE_STATE_TABLE);

        Log.d(CLASS_NAME, CREATE_STD_TABLE);
        db.execSQL(CREATE_STD_TABLE);

        // insert states
        insertStates(db);

        // insert rto codes
        insertRTOCodes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);

        // create new tables
        onCreate(db);
    }

    private void insertRTOCodes(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            String[] fileNames = context.getAssets().list("sql/rto");
            double i = 1.00d;
            for (String name : fileNames) {
                if (name.endsWith(".sql")) {
                    // Open the resource
                    InputStream insertsStream = context.getAssets().open("sql/rto/" + name);
                    BufferedReader insertReader =
                            new BufferedReader(new InputStreamReader(insertsStream));

                    while (insertReader.ready()) {
                        String insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            db.execSQL(insertStmt);
                        }
                    }
                    insertReader.close();
                }
                if (!context.isFinishing()) {
                    final Double percentage = (i / (double) fileNames.length) * 100.00d;
                    context.runOnUiThread(() -> progressBar.setProgress(percentage.intValue()));
                }
                i++;
            }

            db.setTransactionSuccessful();
        } catch (IOException ioEx) {
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertStates(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            // Open the resource
            InputStream insertsStream = context.getAssets().open("sql/pincode/states.sql");
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

            while (insertReader.ready()) {
                String insertStmt = insertReader.readLine();
                if (insertStmt != null) {
                    db.execSQL(insertStmt);
                }
            }
            insertReader.close();
            db.setTransactionSuccessful();
        } catch (IOException ioEx) {
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param stateName stateName
     * @param cityName  cityName
     * @param action    action
     * @return Cursor
     */
    public Cursor findRTOCodes(final String stateName, final String cityName, String action) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  s." + STATE_NAME + ", " + CITY + ", " + RTO_CODE + " AS _id FROM "
                + TABLE_RTO + " st INNER JOIN " + TABLE_STATE + " s ON st." + STATE + " = s."
                + STATE;
        String where = "";
        if (action.length() > 0) {
            where = " WHERE (LOWER(st." + CITY + ") = '" + cityName + "' OR LOWER(st." + RTO_CODE
                    + ") = '" + cityName + "')";
        } else {
            if (stateName.trim().length() > 0) {
                where = " WHERE LOWER(REPLACE(s." + STATE_NAME + ",' ','')) LIKE '%" + stateName +
                        "%'";
            }
            if (cityName.trim().length() > 0 && where.trim().length() > 0) {
                where = where + " AND (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName
                        + "%' OR LOWER(REPLACE(st." + RTO_CODE + ",' ','')) LIKE '%" + cityName +
                        "%')";
            } else if (cityName.trim().length() > 0) {
                where = " WHERE (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName
                        + "%' OR LOWER(REPLACE(st." + RTO_CODE + ",' ','')) LIKE '%" + cityName
                        + "%')";
            }
        }
        String orderBy = " ORDER BY 1, 3, 2";
        String selectQuery = select + where + orderBy;
        Log.d(CLASS_NAME, selectQuery);

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(() -> progressBar.setProgress(50));
        }
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param favRTOCodes stateName
     * @return Cursor
     */
    public Cursor findFavRTOCodes(final String favRTOCodes) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  s." + STATE_NAME + ", " + CITY + ", " + RTO_CODE + " AS _id FROM "
                + TABLE_RTO + " st INNER JOIN " + TABLE_STATE + " s ON st." + STATE + " = s."
                + STATE + " WHERE " + RTO_CODE + " IN (" + favRTOCodes + ") ORDER BY 1, 3, 2";
        Log.d(CLASS_NAME, select);

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(() -> progressBar.setProgress(50));
        }

        return db.rawQuery(select, null);
    }

    public Cursor getAllCityNames(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  " + CITY + " AS _id FROM " + TABLE_RTO + " WHERE " + CITY
                + " LIKE '%" + queryTxt + "%' AND " + CITY + " NOT LIKE '%yet to be%' AND "
                + CITY + " NOT LIKE '%temporary%' AND " + CITY + " NOT LIKE '%(dns)%' AND "
                + CITY + " NOT LIKE '%--%' ORDER BY " + CITY;

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(() -> progressBar.setProgress(50));
        }
        return db.rawQuery(select, null);
    }

    public Cursor getAllRTOCodes(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  " + RTO_CODE + " AS _id FROM " + TABLE_RTO + " WHERE " + RTO_CODE
                + " LIKE '%" + queryTxt + "%' ORDER BY " + RTO_CODE;

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(() -> progressBar.setProgress(50));
        }

        return db.rawQuery(select, null);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
