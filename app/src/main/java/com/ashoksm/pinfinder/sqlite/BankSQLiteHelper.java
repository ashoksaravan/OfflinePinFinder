package com.ashoksm.pinfinder.sqlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BankSQLiteHelper extends SQLiteOpenHelper {

    private Activity context;
    private ProgressDialog mProgressDialog;
    // Logcat tag
    private static final String CLASS_NAME = BankSQLiteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 17;

    // Database Name
    private static final String DATABASE_NAME = "ashoksm.bankbranch";

    // Table Names
    private static final String TABLE_BANK_BRANCH = "bank_branch_t";
    private static final String TABLE_LOCATION = "bank_loc_t";

    // Common column names
    public static final String NAME = "name";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String CONTACT = "contact";
    private static final String LOCATION = "location";
    public static final String DISTRICT = "district";
    public static final String STATE = "state";
    public static final String BANK = "bank";
    private static final String IFSC = "ifsc";
    public static final String MICR = "micr";
    public static final String ID = "_id";

    // table create statement
    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
            + LOCATION + " INTEGER, " + BANK + " TEXT, " + STATE + " TEXT, " + DISTRICT + " TEXT, "
            + "PRIMARY KEY (" + LOCATION + ", " + BANK + ", " + STATE + ", " + DISTRICT + "))";

    private static final String CREATE_BANK_BRANCH_TABLE = "CREATE TABLE " + TABLE_BANK_BRANCH + "("
            + NAME + " TEXT," + CITY + " TEXT, " + ADDRESS + " TEXT, " + CONTACT + " TEXT, " + MICR
            + " INTEGER, " + IFSC + " TEXT, " + LOCATION + " INTEGER, " + "FOREIGN KEY("
            + LOCATION + ") REFERENCES " + TABLE_LOCATION + "(" + LOCATION + "), " + "PRIMARY KEY ("
            + NAME + "," + IFSC + "," + LOCATION + "))";

    public BankSQLiteHelper(Activity contextIn) {
        super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
        context = contextIn;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage("Initializing Database…");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        });
        // crate tables
        Log.d(CLASS_NAME, CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);

        Log.d(CLASS_NAME, CREATE_BANK_BRANCH_TABLE);
        db.execSQL(CREATE_BANK_BRANCH_TABLE);

        // insert locations
        insertLocations(db);

        // insert bank branches
        insertBankBranches(db);
        context.runOnUiThread(new Runnable() {
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANK_BRANCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // create new tables
        onCreate(db);
    }

    private void insertBankBranches(SQLiteDatabase db) {
        String insertStmt;
        try {
            db.beginTransaction();
            double i = 1.00d;
            String[] fileNames = context.getAssets().list("sql/ifsc");
            for (String name : fileNames) {
                if (name.endsWith(".sql") && !name.startsWith("banklocation")) {
                    // Open the resource
                    InputStream insertsStream = context.getAssets().open("sql/ifsc/" + name);
                    BufferedReader insertReader =
                            new BufferedReader(new InputStreamReader(insertsStream));

                    while (insertReader.ready()) {
                        insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            try {
                                db.execSQL(insertStmt);
                            } catch (SQLException e) {
                                Log.e(CLASS_NAME, name);
                                throw e;
                            }
                        }
                    }
                    insertReader.close();
                }
                final Double percentage = (i / (double) fileNames.length) * 90.00d;
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        mProgressDialog.setProgress(percentage.intValue() + 10);
                    }
                });
                i++;
            }
            db.setTransactionSuccessful();
        } catch (Exception ioEx) {
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertLocations(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            String[] fileNames = context.getAssets().list("sql/ifsc");
            for (String name : fileNames) {
                if (name.endsWith(".sql") && name.startsWith("banklocation")) {
                    // Open the resource
                    InputStream insertsStream = context.getAssets().open("sql/ifsc/" + name);
                    BufferedReader insertReader =
                            new BufferedReader(new InputStreamReader(insertsStream));

                    while (insertReader.ready()) {
                        String insertStmt = insertReader.readLine();
                        if (insertStmt != null) {
                            db.execSQL(insertStmt);
                        }
                    }
                    insertReader.close();
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.setProgress(3);
                        }
                    });
                }
            }
            db.setTransactionSuccessful();
            context.runOnUiThread(new Runnable() {
                public void run() {
                    mProgressDialog.setProgress(10);
                }
            });
        } catch (IOException ioEx) {
            Log.e(CLASS_NAME, ioEx.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param stateName  stateName
     * @param districtIn districtIn
     * @param bankName   bankName
     * @param branchName branchName
     * @return Cursor
     */
    public Cursor findIfscCodes(final String stateName, final String districtIn,
                                final String bankName, final String branchName, String action) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  l." + BANK + ", " + NAME + ", " + CITY + ", l." + STATE + ", l."
                + DISTRICT + ", " + ADDRESS + ", " + CONTACT + "," + MICR + ", " + IFSC
                + " AS _id FROM " + TABLE_BANK_BRANCH + " ps" + " INNER JOIN " + TABLE_LOCATION
                + " l ON ps." + LOCATION + " = l." + LOCATION;
        String where = "";

        if ("MICR".equals(action)) {
            where = " WHERE LOWER(" + MICR + ") = '" + branchName + "'";
        } else if ("IFSC".equals(action)) {
            where = " WHERE LOWER(" + IFSC + ") = '" + branchName + "'";
        } else if ("BRANCH".equals(action)) {
            String branch;
            String bank;
            if (branchName.contains("\n")) {
                branch = branchName.substring(0, branchName.indexOf("\n"));
                bank = branchName.substring(branchName.indexOf("\n") + 1, branchName.length())
                        .toLowerCase();
            } else {
                branch = branchName.substring(0, branchName.indexOf("<br\\>"));
                bank = branchName.substring(branchName.indexOf("<br\\>") + 1, branchName.length())
                        .toLowerCase();
            }
            where = " WHERE LOWER(" + NAME + ") = '" + branch + "' AND LOWER(" + BANK + ") = '"
                    + bank + "'";
        } else {
            select = select + " WHERE LOWER(REPLACE(l." + BANK + ",' ',''))" + " LIKE '%"
                    + bankName + "%'";
            if (stateName.trim().length() > 0) {
                where = " AND LOWER(REPLACE(l." + STATE + ",' ','')) LIKE '%" + stateName + "%'";
            }
            if (districtIn.trim().length() > 0) {
                where = where + " AND LOWER(REPLACE(l." + DISTRICT + ",' ','')) LIKE '%" +
                        districtIn + "%'";
            }
            if (branchName.trim().length() > 0) {
                where = where + " AND (LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + branchName
                        + "%' OR  LOWER(REPLACE(" + IFSC + ",' ','')) LIKE '%" + branchName
                        + "%' OR LOWER(REPLACE(" + MICR + ",' ','')) LIKE '%" + branchName + "%')";
            }
        }
        String selectQuery = select + where + " ORDER BY " + IFSC;
        Log.d(CLASS_NAME, selectQuery);

        return db.rawQuery(selectQuery, null);
    }

    public Cursor findFavIfscCodes(String ifsc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  l." + BANK + ", " + NAME + ", " + CITY + ", l." + STATE + ", l."
                + DISTRICT + ", " + ADDRESS + ", " + CONTACT + ", " + MICR + ", " + IFSC
                + " AS _id FROM " + TABLE_BANK_BRANCH + " ps INNER JOIN " + TABLE_LOCATION
                + " l ON ps." + LOCATION + " = l." + LOCATION + " WHERE " + IFSC + " IN (" + ifsc +
                ")";
        return db.rawQuery(select, null);
    }

    public Cursor getIFSCCodes(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  DISTINCT " + IFSC + " AS _id FROM " + TABLE_BANK_BRANCH + " WHERE "
                + IFSC + " LIKE '%" + queryTxt + "%' ORDER BY " + IFSC;
        return db.rawQuery(select, null);
    }

    public Cursor getMICRCodes(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  DISTINCT " + MICR + " AS _id FROM " + TABLE_BANK_BRANCH + " WHERE "
                + MICR + " LIKE '%" + queryTxt + "%' AND " + MICR + "!='0'" + " ORDER BY " + MICR;
        return db.rawQuery(select, null);
    }

    public Cursor getBranchNames(String queryTxt) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT  DISTINCT " + NAME + "|| "
                + (queryTxt.length() == 0 ? "'\n'" : "'<br\\>'") + " || l." + BANK + " AS _id FROM "
                + TABLE_BANK_BRANCH + " ps INNER JOIN " + TABLE_LOCATION + " l ON ps." + LOCATION
                + " = l." + LOCATION + " WHERE " + NAME + "<> '' AND " + NAME + " LIKE '%"
                + queryTxt + "%' " + "ORDER BY " + NAME;
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
