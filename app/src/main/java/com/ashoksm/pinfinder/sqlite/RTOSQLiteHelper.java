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

import com.ashoksm.pinfinder.DisplayRTOResultActivity;

public class RTOSQLiteHelper extends SQLiteOpenHelper {

	private DisplayRTOResultActivity context;
	private ProgressDialog mProgressDialog;

	// Logcat tag
	private static final String CLASS_NAME = RTOSQLiteHelper.class.getName();

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "ashoksm.rto";

	// Table Names
	private static final String TABLE_RTO = "rto_t";
	private static final String TABLE_STATE = "state_t";

	// Common column names
	public static final String CITY = "city";
	private static final String STATE = "state";
	public static final String STATE_NAME = "state_name";
	public static final String RTO_CODE = "rto_code";
	public static final String ID = "_id";

	// post_office_t table create statement
	private static final String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "(" + STATE + " INTEGER, "
			+ STATE_NAME + " TEXT, " + "PRIMARY KEY (" + STATE + "))";

	private static final String CREATE_STD_TABLE = "CREATE TABLE " + TABLE_RTO + "(" + STATE + " INTEGER, " + RTO_CODE
			+ " TEXT, " + CITY + " TEXT, " + "FOREIGN KEY(" + STATE + ") REFERENCES " + TABLE_STATE + "(" + STATE
			+ "), " + "PRIMARY KEY (" + STATE + "," + CITY + "," + RTO_CODE + "))";

	public RTOSQLiteHelper(DisplayRTOResultActivity contextIn) {
		super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
		context = contextIn;
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

		// insert states
		insertStates(db);

		// insert rtocodes
		insertRTOCodes(db);
		context.runOnUiThread(new Runnable() {
			public void run() {
				mProgressDialog.dismiss();
			}
		});
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
					BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

					while (insertReader.ready()) {
						String insertStmt = insertReader.readLine();
						if (insertStmt != null) {
							db.execSQL(insertStmt);
						}
					}
					insertReader.close();
				}
				final Double percentage = (i / (double) fileNames.length) * 100.00d;
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
	 * @param stateName stateName
	 * @param cityName cityName
	 * @return Cursor
	 */
	public Cursor findRTOCodes(final String stateName, final String cityName) {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT  s." + STATE_NAME + ", " + CITY + ", " + RTO_CODE + " AS _id FROM " + TABLE_RTO + " st"
				+ " INNER JOIN " + TABLE_STATE + " s ON st." + STATE + " = s." + STATE;
		String where = "";

		if (stateName.trim().length() > 0) {
			where = " WHERE LOWER(REPLACE(s." + STATE_NAME + ",' ','')) LIKE '%" + stateName + "%'";
		}
		if (cityName.trim().length() > 0 && where.trim().length() > 0) {
			where = where + " AND (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName
					+ "%' OR LOWER(REPLACE(st." + RTO_CODE + ",' ','')) LIKE '%" + cityName + "%')";
		} else if (cityName.trim().length() > 0) {
			where = " WHERE (LOWER(REPLACE(st." + CITY + ",' ','')) LIKE '%" + cityName + "%' OR LOWER(REPLACE(st."
					+ RTO_CODE + ",' ','')) LIKE '%" + cityName + "%')";
		}
		String orderBy = " ORDER BY 1, 3, 2";
		String selectQuery = select + where + orderBy;
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
