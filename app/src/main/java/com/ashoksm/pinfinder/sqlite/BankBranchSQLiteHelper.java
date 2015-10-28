package com.ashoksm.pinfinder.sqlite;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ashoksm.pinfinder.DisplayBankBranchResultActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BankBranchSQLiteHelper extends SQLiteOpenHelper {

    private DisplayBankBranchResultActivity context;
    private ProgressDialog mProgressDialog;
    // Logcat tag
    private static final String CLASS_NAME = BankBranchSQLiteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 7;

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
    private static final String BANK = "bank";
    public static final String IFSC = "ifsc";
    public static final String MICR = "micr";
    public static final String ID = "_id";

    // post_office_t table create statement
    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "(" + LOCATION
            + " INTEGER, " + BANK + " TEXT, " + STATE + " TEXT, " + DISTRICT + " TEXT, " + "PRIMARY KEY (" + LOCATION
            + ", " + BANK + ", " + STATE + ", " + DISTRICT + "))";

    private static final String CREATE_BANK_BRANCH_TABLE = "CREATE TABLE " + TABLE_BANK_BRANCH + "(" + NAME + " TEXT,"
            + CITY + " TEXT, " + ADDRESS + " TEXT, " + CONTACT + " TEXT, " + MICR + " INTEGER, " + IFSC + " TEXT, "
            + LOCATION + " INTEGER, " + "FOREIGN KEY(" + LOCATION + ") REFERENCES " + TABLE_LOCATION + "(" + LOCATION
            + "), " + "PRIMARY KEY (" + NAME + "," + IFSC + "," + LOCATION + "))";

    public BankBranchSQLiteHelper(DisplayBankBranchResultActivity contextIn) {
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
                    BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

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
                    BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

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
    public Cursor findIfscCodes(final String stateName, final String districtIn, final String bankName,
                                final String branchName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT  " + NAME + ", " + CITY + ", l." + STATE + ", l." + DISTRICT + ", " + ADDRESS + ", "
                + CONTACT + "," + MICR + ", " + IFSC + " AS _id FROM " + TABLE_BANK_BRANCH + " ps" + " INNER JOIN "
                + TABLE_LOCATION + " l ON ps." + LOCATION + " = l." + LOCATION + " WHERE LOWER(REPLACE(l." + BANK
                + ",' ',''))" + " LIKE '%" + bankName + "%'";
        String where = "";

        if (stateName.trim().length() > 0) {
            where = " AND LOWER(REPLACE(l." + STATE + ",' ','')) LIKE '%" + stateName + "%'";
        }
        if (districtIn.trim().length() > 0) {
            where = where + " AND LOWER(REPLACE(l." + DISTRICT + ",' ','')) LIKE '%" + districtIn + "%'";
        }
        if (branchName.trim().length() > 0) {
            where = where + " AND (LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + branchName + "%' OR  LOWER(REPLACE("
                    + IFSC + ",' ','')) LIKE '%" + branchName + "%' OR LOWER(REPLACE(" + MICR + ",' ','')) LIKE '%"
                    + branchName + "%')";
        }
        String selectQuery = select + where;
        Log.d(CLASS_NAME, selectQuery);

        return db.rawQuery(selectQuery, null);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
