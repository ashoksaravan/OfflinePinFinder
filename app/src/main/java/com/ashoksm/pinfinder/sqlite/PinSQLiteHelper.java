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

public class PinSQLiteHelper extends SQLiteOpenHelper {

    //Progress Bar
    private Activity context;
    private DonutProgress progressBar;
    private static boolean ON_CREATE;

    // Logcat tag
    private static final String CLASS_NAME = PinSQLiteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 13;

    // Database Name
    private static final String DATABASE_NAME = "ashoksm.pinfinder";

    // Table Names
    private static final String TABLE_POST_OFFICE = "post_office_t";
    private static final String TABLE_STATE = "state_t";
    private static final String TABLE_DISTRICT = "district_t";
    private static final String TABLE_LOCATION = "location_t";
    private static final String TABLE_STATUS = "status_t";

    // Common column names
    private static final String NAME = "name";
    public static final String PIN_CODE = "pin_code";
    private static final String STATUS_CODE = "status_code";
    public static final String SUB_OFFICE = "sub_office";
    public static final String HEAD_OFFICE = "head_office";
    private static final String LOCATION = "location";
    private static final String DISTRICT = "district";
    private static final String STATE = "state";
    public static final String TELEPHONE = "telephone";
    public static final String STATE_NAME = "state_name";
    public static final String DISTRICT_NAME = "district_name";
    public static final String LOCATION_NAME = "location_name";
    public static final String STATUS_NAME = "status_name";
    public static final String ID = "_id";

    // post_office_t table create statement
    private static final String CREATE_STATE_TABLE =
            "CREATE TABLE " + TABLE_STATE + "(" + STATE + " INTEGER, "
                    + STATE_NAME + " TEXT, " + "PRIMARY KEY (" + STATE + "))";

    private static final String CREATE_DISTRICT_TABLE =
            "CREATE TABLE " + TABLE_DISTRICT + "(" + STATE + " INTEGER, "
                    + DISTRICT + " INTEGER, " + DISTRICT_NAME + " TEXT, " + "FOREIGN KEY(" + STATE +
                    ") REFERENCES "
                    + TABLE_STATE + "(" + STATE + "), " + "PRIMARY KEY (" + STATE + ", " +
                    DISTRICT + "))";

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + TABLE_LOCATION + "(" + LOCATION
                    + " INTEGER, " + LOCATION_NAME + " TEXT, " + "PRIMARY KEY (" + LOCATION + "))";

    private static final String CREATE_STATUS_TABLE =
            "CREATE TABLE " + TABLE_STATUS + "(" + STATUS_CODE
                    + " INTEGER, " + STATUS_NAME + " TEXT, " + "PRIMARY KEY (" + STATUS_CODE + "))";

    private static final String CREATE_PINCODE_TABLE =
            "CREATE TABLE " + TABLE_POST_OFFICE + "(" + NAME + " TEXT,"
                    + PIN_CODE + " INTEGER, " + DISTRICT + " INTEGER, " + STATE + " INTEGER, " +
                    STATUS_CODE + " INTEGER, "
                    + SUB_OFFICE + " TEXT, " + HEAD_OFFICE + " TEXT, " + LOCATION + " INTEGER, " +
                    TELEPHONE + " TEXT, "
                    + "FOREIGN KEY(" + STATE + ") REFERENCES " + TABLE_STATE + "(" + STATE + "), " +
                    "FOREIGN KEY(" + DISTRICT
                    + ") REFERENCES " + TABLE_DISTRICT + "(" + DISTRICT + "), " + "FOREIGN KEY(" +
                    LOCATION + ") REFERENCES "
                    + TABLE_LOCATION + "(" + LOCATION + "), " + "FOREIGN KEY(" + STATUS_CODE +
                    ") REFERENCES "
                    + TABLE_STATUS + "(" + STATUS_CODE + "), " + "PRIMARY KEY (" + NAME + "," +
                    PIN_CODE + "," + DISTRICT + ","
                    + STATE + "))";

    public PinSQLiteHelper(Activity contextIn, DonutProgress progressBarIn) {
        super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
        context = contextIn;
        progressBar = progressBarIn;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ON_CREATE = true;
        // creating required tables
        Log.d(CLASS_NAME, CREATE_STATE_TABLE);
        db.execSQL(CREATE_STATE_TABLE);

        Log.d(CLASS_NAME, CREATE_DISTRICT_TABLE);
        db.execSQL(CREATE_DISTRICT_TABLE);

        Log.d(CLASS_NAME, CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);

        Log.d(CLASS_NAME, CREATE_STATUS_TABLE);
        db.execSQL(CREATE_STATUS_TABLE);

        Log.d(CLASS_NAME, CREATE_PINCODE_TABLE);
        db.execSQL(CREATE_PINCODE_TABLE);

        // insert state
        insertStates(db);
        if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(1);
                }
            });
        }

        //insert status
        insertStatus(db);
        if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(2);
                }
            });
        }

        // insert districts
        insertDistricts(db);
        if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(3);
                }
            });
        }

        // insert locations
        insertLocations(db);
        if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(5);
                }
            });
        }

        // insert pincodes
        insertPincodes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_OFFICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);

        // create new tables
        onCreate(db);
    }

    private void insertPincodes(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            double i = 1.00d;
            String[] fileNames = context.getAssets().list("sql/pincode");
            for (String name : fileNames) {
                if (name.endsWith(".sql") && !name.equals("states.sql") &&
                        !name.equals("district.sql")
                        && !name.equals("locations.sql") && !name.equals("status.sql")) {
                    // Open the resource
                    InputStream insertsStream = context.getAssets().open("sql/pincode/" + name);
                    BufferedReader insertReader =
                            new BufferedReader(new InputStreamReader(insertsStream));

                    while (insertReader.ready()) {
                        String insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            db.execSQL(insertStmt);
                        }
                    }
                    insertReader.close();
                    if (!context.isFinishing()) {
                        final Double percentage =
                                (i / ((double) fileNames.length - 4.00d)) * 95.00d;
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                progressBar.setProgress(percentage.intValue() + 5);
                            }
                        });
                    }
                    i++;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ioEx) {
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

    private void insertStatus(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            // Open the resource
            InputStream insertsStream = context.getAssets().open("sql/pincode/status.sql");
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
            ioEx.printStackTrace();
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertDistricts(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            // Open the resource
            InputStream insertsStream = context.getAssets().open("sql/pincode/district.sql");
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

    private void insertLocations(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            // Open the resource
            InputStream insertsStream = context.getAssets().open("sql/pincode/locations.sql");
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
     * @param stateName  stateName
     * @param districtIn districtIn
     * @param nameOrCode nameOrCode
     * @return List<Office>
     */
    public Cursor findMatchingOffices(final String stateName, final String districtIn,
                                      final String nameOrCode) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  " + NAME + " AS _id, " + PIN_CODE + ", " + STATUS_NAME + ", "
                + SUB_OFFICE + ", " + HEAD_OFFICE + ", l." + LOCATION_NAME + ", d." + DISTRICT_NAME
                + ", s." + STATE_NAME + ", " + TELEPHONE + " FROM " + TABLE_POST_OFFICE + " ps"
                + " INNER JOIN " + TABLE_STATE + " s ON ps." + STATE + " = s." + STATE
                + " INNER JOIN " + TABLE_DISTRICT + " d ON ps." + DISTRICT + " = d." + DISTRICT
                + " AND d." + STATE + " = s." + STATE + " INNER JOIN " + TABLE_LOCATION
                + " l ON ps." + LOCATION + " = l." + LOCATION + " INNER JOIN " + TABLE_STATUS
                + " sc ON ps." + STATUS_CODE + " = sc." + STATUS_CODE;
        String where = "";

        if (stateName.trim().length() > 0) {
            where = " WHERE LOWER(REPLACE(s." + STATE_NAME + ",' ','')) LIKE '%" + stateName + "%'";
        }
        if (districtIn.trim().length() > 0) {
            if (where.length() > 0) {
                where = where + " AND LOWER(REPLACE(d." + DISTRICT_NAME + ",' ','')) LIKE '%" +
                        districtIn + "%'";
            } else {
                where = " WHERE LOWER(REPLACE(d." + DISTRICT_NAME + ",' ','')) LIKE '%" +
                        districtIn + "%'";
            }
        }
        if (nameOrCode.trim().length() > 0) {
            if (where.length() > 0) {
                where = where + " AND (LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + nameOrCode +
                        "%' OR LOWER("
                        + PIN_CODE + ") LIKE '%" + nameOrCode + "%')";
            } else {
                where = " WHERE LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + nameOrCode +
                        "%' OR LOWER(" + PIN_CODE
                        + ") LIKE '%" + nameOrCode + "%'";
            }
        }
        String selectQuery = select + where + " ORDER BY " + NAME;
        Log.d(CLASS_NAME, selectQuery);

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(50);
                }
            });
        }

        return db.rawQuery(selectQuery, null);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public Cursor findFavOffices(final String pincodes) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  " + NAME + " AS _id, " + PIN_CODE + ", " + STATUS_NAME + ", "
                + SUB_OFFICE + ", " + HEAD_OFFICE + ", l." + LOCATION_NAME + ", d." + DISTRICT_NAME
                + ", s." + STATE_NAME + ", " + TELEPHONE + " FROM " + TABLE_POST_OFFICE + " ps"
                + " INNER JOIN " + TABLE_STATE + " s ON ps." + STATE + " = s." + STATE
                + " INNER JOIN " + TABLE_DISTRICT + " d ON ps." + DISTRICT + " = d." + DISTRICT
                + " AND d." + STATE + " = s." + STATE + " INNER JOIN " + TABLE_LOCATION
                + " l ON ps." + LOCATION + " = l." + LOCATION + " INNER JOIN " + TABLE_STATUS
                + " sc ON ps." + STATUS_CODE + " = sc." + STATUS_CODE;
        String where = " WHERE " + PIN_CODE + " IN (" + pincodes + ")";
        String selectQuery = select + where;
        Log.d(CLASS_NAME, selectQuery);

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(50);
                }
            });
        }

        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllPinCodes(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  DISTINCT " + PIN_CODE + " AS _id FROM " + TABLE_POST_OFFICE +
                " WHERE " + PIN_CODE + " LIKE '%" + queryTxt + "%' ORDER BY " + PIN_CODE;

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(50);
                }
            });
        }

        return db.rawQuery(select, null);
    }

    public Cursor getAllOfficeNames(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  DISTINCT " + NAME + " AS _id FROM " + TABLE_POST_OFFICE +
                " WHERE " + NAME + " LIKE '%" + queryTxt + "%' ORDER BY " + NAME;

        if (ON_CREATE) {
            ON_CREATE = false;
        } else if (!context.isFinishing()) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setProgress(50);
                }
            });
        }

        return db.rawQuery(select, null);
    }

}
