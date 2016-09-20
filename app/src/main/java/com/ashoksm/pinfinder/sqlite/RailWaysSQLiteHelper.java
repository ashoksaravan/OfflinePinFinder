package com.ashoksm.pinfinder.sqlite;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RailWaysSQLiteHelper extends SQLiteOpenHelper {

    // Common column names
    public static final String ID = "_id";
    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String LOCATION = "location";
    public static final String TRAINS_PASSING_VIA = "trains_passing_via";
    public static final String CITY = "city";
    public static final String STATE = "state";
    private static final String TRAIN_NO = "train_no";
    public static final String TRAIN_NAME = "train_name";
    public static final String STARTS = "starts";
    public static final String ENDS = "ends";
    public static final String DAYS = "days";
    public static final String PANTRY = "pantry";
    public static final String STOP_TIME = "stop_time";
    public static final String MON = "mon";
    public static final String TUE = "tue";
    public static final String WED = "wed";
    public static final String THU = "thu";
    public static final String FRI = "fri";
    public static final String SAT = "sat";
    public static final String SUN = "sun";
    private static final String STATION_NO = "station_no";
    public static final String DISTANCE_TRAVELED = "distance_traveled";
    public static final String ROUTE = "route";

    // Logcat tag
    private static final String CLASS_NAME = RTOSQLiteHelper.class.getName();
    // Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "ashoksm.railways";
    // Table Names
    private static final String TABLE_STATIONS = "stations_t";
    private static final String TABLE_TRAINS = "trains_t";
    private static final String TABLE_STATION_DETAIL = "station_detail_t";
    private static final String TABLE_TRAIN_DETAIL = "train_detail_t";
    // Create Statement
    private static final String CREATE_STATIONS_TABLE = "CREATE TABLE " + TABLE_STATIONS + "("
            + STATION_CODE + " TEXT, " + STATION_NAME + " TEXT, " + LOCATION + " TEXT, "
            + TRAINS_PASSING_VIA + " INTEGER, " + STATE + " TEXT, " + CITY + " TEXT, PRIMARY KEY ("
            + STATION_CODE + "))";
    private static final String CREATE_TRAINS_TABLE = "CREATE TABLE " + TABLE_TRAINS + "("
            + TRAIN_NO + " INTEGER, " + TRAIN_NAME + " TEXT, " + STARTS + " TEXT, " + ENDS
            + " TEXT, " + DAYS + " TEXT, " + PANTRY + " TEXT, PRIMARY KEY (" + TRAIN_NO + "))";
    private static final String CREATE_STATION_DETAIL_TABLE = "CREATE TABLE " + TABLE_STATION_DETAIL
            + "(" + STATION_CODE + " TEXT, " + TRAIN_NO + " INTEGER, " + STARTS + " TEXT, " + ENDS
            + " TEXT, " + STOP_TIME + " TEXT, " + MON + " TEXT, " + TUE + " TEXT, " + WED
            + " TEXT, " + THU + " TEXT, " + FRI + " TEXT, " + SAT + " TEXT, " + SUN
            + " TEXT, FOREIGN KEY(" + STATION_CODE + ") REFERENCES " + TABLE_STATIONS + "("
            + STATION_CODE + "), FOREIGN KEY(" + TRAIN_NO + ") REFERENCES " + TABLE_TRAINS + "("
            + TRAIN_NO + ") PRIMARY " + "KEY (" + STATION_CODE + ", " + TRAIN_NO + "))";
    private static final String CREATE_TRAIN_DETAIL_TABLE = "CREATE TABLE " + TABLE_TRAIN_DETAIL
            + "(" + STATION_NO + " INTEGER, " + TRAIN_NO + " INTEGER, " + STATION_CODE + " TEXT, "
            + STARTS + " TEXT, " + ENDS + " TEXT, " + STOP_TIME + " TEXT, " + DISTANCE_TRAVELED
            + " TEXT, " + DAYS + " TEXT, " + ROUTE + " TEXT, " + " FOREIGN KEY(" + STATION_CODE
            + ") REFERENCES " + TABLE_STATIONS + "(" + STATION_CODE + "), FOREIGN KEY(" + TRAIN_NO
            + ") REFERENCES " + TABLE_TRAINS + "(" + TRAIN_NO + ") PRIMARY " + "KEY (" + STATION_NO
            + ", " + STATION_CODE + ", " + TRAIN_NO + "))";
    // Activity
    private Activity context;

    public RailWaysSQLiteHelper(Activity contextIn) {
        super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = contextIn;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(CLASS_NAME, CREATE_STATIONS_TABLE);
        db.execSQL(CREATE_STATIONS_TABLE);

        Log.d(CLASS_NAME, CREATE_TRAINS_TABLE);
        db.execSQL(CREATE_TRAINS_TABLE);

        Log.d(CLASS_NAME, CREATE_STATION_DETAIL_TABLE);
        db.execSQL(CREATE_STATION_DETAIL_TABLE);

        Log.d(CLASS_NAME, CREATE_TRAIN_DETAIL_TABLE);
        db.execSQL(CREATE_TRAIN_DETAIL_TABLE);

        String insertStmt;
        try {
            db.beginTransaction();
            // insert stations
            BufferedReader insertReader =
                    new BufferedReader(new InputStreamReader(
                            context.getAssets().open("sql/railway/stations.sql")));
            while (insertReader.ready()) {
                insertStmt = insertReader.readLine();
                if (insertStmt != null) {
                    db.execSQL(insertStmt);
                }
            }
            insertReader.close();

            // insert trains
            insertReader =
                    new BufferedReader(new InputStreamReader(
                            context.getAssets().open("sql/railway/trains.sql")));
            while (insertReader.ready()) {
                insertStmt = insertReader.readLine();
                if (insertStmt != null) {
                    db.execSQL(insertStmt);
                }
            }
            insertReader.close();

            //insert station details
            String[] stationDetails = context.getAssets().list("sql/railway");
            for (String stationDetail : stationDetails) {
                if (stationDetail.startsWith("trainspassingviastation")) {
                    insertReader =
                            new BufferedReader(new InputStreamReader(
                                    context.getAssets().open("sql/railway/" + stationDetail)));
                    while (insertReader.ready()) {
                        insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            db.execSQL(insertStmt);
                        }
                    }
                    insertReader.close();
                }
            }

            //insert station details
            String[] trainDetails = context.getAssets().list("sql/railway");
            for (String trainDetail : trainDetails) {
                if (trainDetail.startsWith("traindetails")) {
                    insertReader =
                            new BufferedReader(new InputStreamReader(
                                    context.getAssets().open("sql/railway/" + trainDetail)));
                    while (insertReader.ready()) {
                        insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            db.execSQL(insertStmt);
                        }
                    }
                    insertReader.close();
                }
            }

            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.e(CLASS_NAME, e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAIN_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINS);

        // create new tables
        onCreate(db);
    }

    public String[] getStationCodes() {
        String query = "SELECT " + STATION_CODE + " || ' - ' || " + STATION_NAME + " AS _id FROM "
                + TABLE_STATIONS;
        return getCodes(query);
    }

    public String[] getTrainNos() {
        String query = "SELECT " + TRAIN_NO + " || ' - ' || " + TRAIN_NAME + " AS _id FROM "
                + TABLE_TRAINS;
        return getCodes(query);
    }

    @NonNull
    private String[] getCodes(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        String[] codes = new String[c.getCount()];
        c.moveToFirst();

        int i = 0;
        while (!c.isAfterLast()) {
            codes[i] = c.getString(c.getColumnIndex("_id"));
            c.moveToNext();
            i++;
        }

        c.close();
        return codes;
    }

    public String[] getStates() {
        String query = "SELECT DISTINCT " + STATE + " AS _id FROM " + TABLE_STATIONS;
        return getCodes(query);
    }

    public String[] getCities() {
        String query = "SELECT DISTINCT " + CITY + " AS _id FROM " + TABLE_STATIONS;
        return getCodes(query);
    }

    public Cursor findStations(String station, String state, String city, String action) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + STATION_CODE + " AS _id, " + STATION_NAME + ", " + LOCATION + ","
                + TRAINS_PASSING_VIA + ", " + STATE + ", " + CITY + " FROM " + TABLE_STATIONS
                + " WHERE LOWER(REPLACE(" + STATE + ",' ','')) LIKE '%" + state + "%'"
                + " AND LOWER(REPLACE(" + CITY + ",' ','')) LIKE '%" + city + "%' ";
        if (action != null && action.trim().length() > 0) {
            query = query + " AND (LOWER(" + STATION_CODE + ") = '" + station + "' OR LOWER("
                    + STATION_NAME + ") = '" + station + "') ";
        } else {
            query = query + " AND (LOWER(REPLACE(" + STATION_CODE + ",' ','')) LIKE '%"
                    + station + "%' OR LOWER(REPLACE(" + STATION_NAME + ",' ','')) LIKE '%"
                    + station + "%') ";
        }
        return db.rawQuery(query, null);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public Cursor getStationDetails(String stationCode, boolean xLargeScreen) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (xLargeScreen) {
            query = "SELECT 0 AS order_by, 'Train Name(Train No)' AS _id, 'Arrives' AS starts, "
                    + "'Departs' AS ends, 'Stop Time' AS stop_time, 'y' AS mon, 'y' AS tue, 'y' AS "
                    + "wed, 'y' AS thu, 'y' AS fri, 'y' AS sat, 'y' AS sun UNION ";
        }
        query = query + "SELECT 1 AS order_by," + TRAIN_NAME + " || ' (' || t." + TRAIN_NO
                + " || ')'" + " AS _id, sd." + STARTS + ", " + "sd." + ENDS + ", " + "sd."
                + STOP_TIME + ", " + MON + ", " + TUE + ", " + WED + ", " + THU + ", " + FRI + ", "
                + SAT + ", " + SUN + " FROM " + TABLE_STATION_DETAIL + " sd INNER JOIN "
                + TABLE_TRAINS + " t ON sd." + TRAIN_NO + " = t." + TRAIN_NO + " WHERE "
                + STATION_CODE + " = '" + stationCode + "' ";
        query = query + (xLargeScreen ? "ORDER BY order_by" : "ORDER BY " + TRAIN_NAME);
        return db.rawQuery(query, null);
    }

    public Cursor getStationCodes(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + STATION_CODE + " AS _id FROM " + TABLE_STATIONS + " WHERE "
                + "LOWER(REPLACE(" + STATION_CODE + ",' ','')) LIKE '%" + s + "%' ";
        return db.rawQuery(query, null);
    }

    public Cursor getStationNames(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + STATION_NAME + " AS _id FROM " + TABLE_STATIONS + " WHERE "
                + "LOWER(REPLACE(" + STATION_NAME + ",' ','')) LIKE '%" + s + "%' ";
        return db.rawQuery(query, null);
    }

    public Cursor findTrainsByNoOrName(String trainNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TRAIN_NO + " AS _id, " + TRAIN_NAME + ", " + STARTS + ", "
                + ENDS + ", " + DAYS + ", " + PANTRY + " FROM " + TABLE_TRAINS;
        if (trainNo.contains(" - ")) {
            String[] params = trainNo.split(" - ");
            query = query + " WHERE " + TRAIN_NO + " = " + params[0] + " AND LOWER(" + TRAIN_NAME
                    + ") = '" + params[1].toLowerCase() + "'";
        } else if (trainNo.trim().length() > 0) {
            query = query + " WHERE " + TRAIN_NO + " LIKE '%" + trainNo + "%' OR LOWER(REPLACE("
                    + TRAIN_NAME + ",' ','')) LIKE '%" + trainNo.toLowerCase().replaceAll(" ", "")
                    + "%'";
        }
        return db.rawQuery(query, null);
    }

    public Cursor findTrainsByStation(String start, String ends) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TRAIN_NO + " AS _id, " + TRAIN_NAME + ", " + STARTS
                + ", " + ENDS + ", " + DAYS + ", " + PANTRY + " FROM " + TABLE_TRAINS;

        List<Integer> trainNos = new ArrayList<>();

        //get trains from starting station
        SparseIntArray stationMap = new SparseIntArray();
        String startStnQuery = "SELECT " + TRAIN_NO + " AS _id, " + STATION_NO + " FROM "
                + TABLE_TRAIN_DETAIL + " WHERE LOWER(" + STATION_CODE + ") = '" + start.split(" -"
                + " ")[0].toLowerCase() + "'";
        Cursor c = db.rawQuery(startStnQuery, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            stationMap.put(c.getInt(c.getColumnIndex("_id")),
                    c.getInt(c.getColumnIndex(STATION_NO)));
            c.moveToNext();
        }
        c.close();

        //get trains from starting station
        if (ends.length() > 0) {
            String stopStnQuery = "SELECT " + TRAIN_NO + " AS _id, " + STATION_NO + " FROM "
                    + TABLE_TRAIN_DETAIL + " WHERE " + STATION_CODE + " = '" + ends.split(" - ")[0]
                    + "'";
            c = db.rawQuery(stopStnQuery, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Integer startNo = stationMap.get(c.getInt(c.getColumnIndex("_id")));
                if (startNo != 0 && c.getInt(c.getColumnIndex(STATION_NO)) - startNo > 0) {
                    trainNos.add(c.getInt(c.getColumnIndex("_id")));
                }
                c.moveToNext();
            }
            c.close();
        } else {
            for (int i = 0; i < stationMap.size(); i++) {
                trainNos.add(stationMap.keyAt(i));
            }
        }

        if (!trainNos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean flag = false;
            for (Integer trainNo : trainNos) {
                if (flag) {
                    sb.append(", ");
                }
                sb.append("'");
                sb.append(trainNo);
                sb.append("'");
                flag = true;
            }
            query = query + " WHERE " + TRAIN_NO + " IN (" + sb.toString() + ")";
        }
        Log.e("QUERY", query);
        return db.rawQuery(query, null);
    }

    public Cursor getTrains(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TRAIN_NO + " || ' - ' || " + TRAIN_NAME + " AS _id FROM "
                + TABLE_TRAINS;
        if (queryTxt.length() > 0) {
            query = query + " WHERE " + TRAIN_NO + " LIKE '%" + queryTxt + "%' OR " + TRAIN_NAME
                    + " LIKE '%" + queryTxt + "%'";
        }
        return db.rawQuery(query, null);
    }

    public Cursor getStns(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + STATION_CODE + " || ' - ' || " + STATION_NAME + " AS _id FROM "
                + TABLE_STATIONS;
        if (queryTxt.length() > 0) {
            query = query + " WHERE " + STATION_CODE + " LIKE '%" + queryTxt + "%' OR " +
                    STATION_NAME + " LIKE '%" + queryTxt + "%'";
        }
        return db.rawQuery(query, null);
    }

    public Cursor getRouteAndSchedule(String trainNo, boolean xLargeScreen) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (xLargeScreen) {
            query = "SELECT 0 AS _id, 'Station Name(Code)' AS station_code, 'Arrives' AS starts, "
                    + "'Departs' AS ends, 'Stop Time' AS stop_time, 'Distance travelled' "
                    + "AS distance_traveled, 'Day' AS days, 'Route' AS route UNION ";
        }
        query = query + "SELECT " + STATION_NO + " AS _id, " + STATION_NAME + " || ' (' || t."
                + STATION_CODE + " || ')'" + " AS " + STATION_CODE + ", " + STARTS + ", " + ENDS
                + ", " + STOP_TIME + ", " + DISTANCE_TRAVELED + ", " + DAYS + ", " + ROUTE
                + " FROM " + TABLE_TRAIN_DETAIL + " t INNER JOIN " + TABLE_STATIONS
                + " sd ON sd." + STATION_CODE + " = t." + STATION_CODE + " WHERE "
                + TRAIN_NO + " = '" + trainNo + "' ORDER BY _id";
        return db.rawQuery(query, null);
    }
}
