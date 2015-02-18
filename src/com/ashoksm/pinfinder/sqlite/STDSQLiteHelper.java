package com.ashoksm.pinfinder.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ashoksm.pinfinder.DisplaySTDResultActivity;

public class STDSQLiteHelper extends SQLiteOpenHelper {

	private DisplaySTDResultActivity context;
	private ProgressDialog mProgressDialog;

	// Logcat tag
	private static final String CLASS_NAME = STDSQLiteHelper.class.getName();

	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "ashoksm.std";

	// Table Names
	private static final String TABLE_STD = "std_t";
	private static final String TABLE_STATE = "state_t";

	// Common column names
	public static final String CITY = "city";
	private static final String STATE = "state";
	public static final String STATE_NAME = "state_name";
	public static final String STD_CODE = "std_code";
	public static final String ID = "_id";

	// post_office_t table create statement
	private static final String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "(" + STATE + " INTEGER, "
			+ STATE_NAME + " TEXT, " + "PRIMARY KEY (" + STATE + "))";

	private static final String CREATE_STD_TABLE = "CREATE TABLE " + TABLE_STD + "(" + STATE + " INTEGER, " + CITY
			+ " TEXT, " + STD_CODE + " TEXT, " + "FOREIGN KEY(" + STATE + ") REFERENCES " + TABLE_STATE + "(" + STATE
			+ "), " + "PRIMARY KEY (" + STATE + "," + CITY + "," + STD_CODE + "))";

	public STDSQLiteHelper(DisplaySTDResultActivity displaySTDResultActivityIn) {
		super(displaySTDResultActivityIn, DATABASE_NAME, null, DATABASE_VERSION);
		context = displaySTDResultActivityIn;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				mProgressDialog = new ProgressDialog(context);
				mProgressDialog.setMessage("Initializing Database..");
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
			}
		});
		// crate tables
		Log.d(CLASS_NAME, CREATE_STATE_TABLE);
		db.execSQL(CREATE_STATE_TABLE);

		Log.d(CLASS_NAME, CREATE_STD_TABLE);
		db.execSQL(CREATE_STD_TABLE);

		// insert locations
		insertStates(db);

		// insert pincodes
		insertSTDCodes(db);

		context.runOnUiThread(new Runnable() {
			public void run() {
				mProgressDialog.dismiss();
			}
		});
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);

		// create new tables
		onCreate(db);
	}

	private void insertSTDCodes(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			double i = 1.00d;
			String[] fileNames = context.getAssets().list("sql/std");
			for (String name : fileNames) {
				if (name.endsWith(".sql")) {
					// Open the resource
					InputStream insertsStream = context.getAssets().open("sql/std/" + name);
					BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

					while (insertReader.ready()) {
						String insertStmt = insertReader.readLine();
						if (insertStmt != null) {
							db.execSQL(insertStmt);
						}
					}
					insertReader.close();
				}
				final Double percentage = (i / new Double(fileNames.length)) * 100.00d;
				context.runOnUiThread(new Runnable() {
					public void run() {
						mProgressDialog.setProgress(percentage.intValue());
					}
				});
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
	 * @param stateName
	 * @param districtIn
	 * @param nameOrCode
	 * @return Cursor
	 */
	public Cursor findIfscCodes(final String stateName, final String cityName) {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT  s." + STATE_NAME + ", " + CITY + ", " + STD_CODE + " AS _id FROM " + TABLE_STD + " st"
				+ " INNER JOIN " + TABLE_STATE + " s ON st." + STATE + " = s." + STATE;
		String where = "";

		if (stateName.trim().length() > 0) {
			where = " WHERE LOWER(REPLACE(s." + STATE_NAME + ",' ','')) LIKE '%" + stateName + "%'";
		}
		if (cityName.trim().length() > 0 && where.trim().length() > 0) {
			where = where + " AND (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName
					+ "%' OR LOWER(REPLACE(st." + STD_CODE + ",' ','')) LIKE '%" + cityName + "%')";
		} else if (cityName.trim().length() > 0) {
			where = " WHERE (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName + "%' OR LOWER(REPLACE(st."
					+ STD_CODE + ",' ','')) LIKE '%" + cityName + "%')";
		}
		String selectQuery = select + where;
		Log.d(CLASS_NAME, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		return c;
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}
