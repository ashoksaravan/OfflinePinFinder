package com.ashoksm.pinfinder.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ashoksm.pinfinder.to.Office;

public class PinFinderSQLiteHelper extends SQLiteOpenHelper {

	private Context context;

	// Logcat tag
	private static final String LOG = PinFinderSQLiteHelper.class.getName();

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "ashoksm.pinfinder";

	// Table Names
	private static final String TABLE_POST_OFFICE = "post_office_t";

	// Common column names
	private static final String NAME = "name";
	private static final String PIN_CODE = "pin_code";
	private static final String STATUS = "status";
	private static final String SUB_OFFICE = "sub_office";
	private static final String HEAD_OFFICE = "head_office";
	private static final String LOCATION = "location";
	private static final String DISTRICT = "district";
	private static final String STATE = "state";
	private static final String TELEPHONE = "telephone";

	// post_office_t table create statement
	private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_POST_OFFICE + "(" + NAME + " TEXT,"
			+ PIN_CODE + " INTEGER," + STATUS + " TEXT," + SUB_OFFICE + " TEXT," + HEAD_OFFICE + " TEXT," + LOCATION
			+ " TEXT," + DISTRICT + " TEXT," + STATE + " TEXT," + TELEPHONE + " TEXT" + ")";

	public PinFinderSQLiteHelper(Context contextIn) {
		super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
		context = contextIn;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		Log.d(LOG, CREATE_TABLE_TODO);
		db.execSQL(CREATE_TABLE_TODO);
		// insert data
		insertFromFile(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_OFFICE);

		// create new tables
		onCreate(db);
	}

	private void insertFromFile(SQLiteDatabase db) {
		try {
			String[] fileNames = context.getAssets().list("");
			for (String name : fileNames) {
				if (name.endsWith(".sql")) {
					// Open the resource
					InputStream insertsStream = context.getAssets().open(name);
					BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

					while (insertReader.ready()) {
						String insertStmt = insertReader.readLine();
						if (insertStmt != null) {
							db.execSQL(insertStmt);
						}
					}
					insertReader.close();
				}
			}

		} catch (IOException ioEx) {
			Log.e(LOG, ioEx.getMessage());
		}

	}

	/**
	 * get single todo
	 */
	public List<Office> findMatchingOffices(final String stateName, final String districtIn, final String nameOrCode) {
		List<Office> offices = new ArrayList<Office>();
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT  " + NAME + ", " + PIN_CODE + ", " + STATUS + ", " + SUB_OFFICE + ", " + HEAD_OFFICE
				+ ", " + LOCATION + ", " + DISTRICT + ", " + STATE + ", " + TELEPHONE + " FROM " + TABLE_POST_OFFICE;
		String where = "";

		if (stateName.trim().length() > 0) {
			where = " WHERE LOWER(" + STATE + ") LIKE '%" + stateName + "%'";
		}
		if (districtIn.trim().length() > 0) {
			if (where.length() > 0) {
				where = where + " AND LOWER(" + DISTRICT + ") LIKE '%" + districtIn + "%'";
			} else {
				where = " WHERE LOWER(" + DISTRICT + ") LIKE '%" + districtIn + "%'";
			}
		}
		if (nameOrCode.trim().length() > 0) {
			if (where.length() > 0) {
				where = where + " AND (LOWER(" + NAME + ") LIKE '%" + nameOrCode + "%' OR LOWER(" + PIN_CODE
						+ ") LIKE '%" + nameOrCode + "%')";
			} else {
				where = " WHERE (LOWER(" + NAME + ") LIKE '%" + nameOrCode + "%' OR LOWER(" + PIN_CODE + ") LIKE '%"
						+ nameOrCode + "%')";
			}
		}
		String selectQuery = select + where;
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Office office = new Office();
				office.setOfficeName(c.getString(c.getColumnIndex(NAME)));
				office.setPinCode(c.getString(c.getColumnIndex(PIN_CODE)));
				office.setStatus(c.getString(c.getColumnIndex(STATUS)));
				office.setSuboffice(c.getString(c.getColumnIndex(SUB_OFFICE)));
				office.setHeadoffice(c.getString(c.getColumnIndex(HEAD_OFFICE)));
				office.setLocation(c.getString(c.getColumnIndex(LOCATION)));
				office.setDistrict(c.getString(c.getColumnIndex(DISTRICT)));
				office.setStateName(c.getString(c.getColumnIndex(STATE)));
				office.setTelephone(c.getString(c.getColumnIndex(TELEPHONE)));
				// adding to office list
				offices.add(office);
			} while (c.moveToNext());
		}

		return offices;
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}
