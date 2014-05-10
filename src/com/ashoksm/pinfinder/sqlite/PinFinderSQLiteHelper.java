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
	private static final String CLASS_NAME = PinFinderSQLiteHelper.class.getName();

	// Database Version
	private static final int DATABASE_VERSION = 9;

	// Database Name
	private static final String DATABASE_NAME = "ashoksm.pinfinder";

	// Table Names
	private static final String TABLE_POST_OFFICE = "post_office_t";
	private static final String TABLE_STATE = "state_t";
	private static final String TABLE_DISTRICT = "district_t";
	private static final String TABLE_LOCATION = "location_t";

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
	private static final String STATE_NAME = "state_name";
	private static final String DISTRICT_NAME = "district_name";
	private static final String LOCATION_NAME = "location_name";

	// post_office_t table create statement
	private static final String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "(" + STATE + " INTEGER, "
			+ STATE_NAME + " TEXT, " + "PRIMARY KEY (" + STATE + "))";

	private static final String CREATE_DISTRICT_TABLE = "CREATE TABLE " + TABLE_DISTRICT + "(" + STATE + " INTEGER, "
			+ DISTRICT + " INTEGER, " + DISTRICT_NAME + " TEXT, " + "FOREIGN KEY(" + STATE + ") REFERENCES "
			+ TABLE_STATE + "(" + STATE + "), " + "PRIMARY KEY (" + STATE + ", " + DISTRICT + "))";

	private static final String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "(" + LOCATION
			+ " INTEGER, " + LOCATION_NAME + " TEXT, " + "PRIMARY KEY (" + LOCATION + "))";

	private static final String CREATE_PINCODE_TABLE = "CREATE TABLE " + TABLE_POST_OFFICE + "(" + NAME + " TEXT,"
			+ PIN_CODE + " INTEGER, " + DISTRICT + " INTEGER, " + STATE + " INTEGER, " + STATUS + " TEXT, "
			+ SUB_OFFICE + " TEXT, " + HEAD_OFFICE + " TEXT, " + LOCATION + " INTEGER, " + TELEPHONE + " TEXT, "
			+ "FOREIGN KEY(" + STATE + ") REFERENCES " + TABLE_STATE + "(" + STATE + "), " + "FOREIGN KEY(" + DISTRICT
			+ ") REFERENCES " + TABLE_DISTRICT + "(" + DISTRICT + "), " + "FOREIGN KEY(" + LOCATION + ") REFERENCES "
			+ TABLE_LOCATION + "(" + LOCATION + "), " + "PRIMARY KEY (" + NAME + "," + PIN_CODE + "," + DISTRICT + ","
			+ STATE + "))";

	public PinFinderSQLiteHelper(Context contextIn) {
		super(contextIn, DATABASE_NAME, null, DATABASE_VERSION);
		context = contextIn;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		Log.d(CLASS_NAME, CREATE_STATE_TABLE);
		db.execSQL(CREATE_STATE_TABLE);

		Log.d(CLASS_NAME, CREATE_DISTRICT_TABLE);
		db.execSQL(CREATE_DISTRICT_TABLE);

		Log.d(CLASS_NAME, CREATE_LOCATION_TABLE);
		db.execSQL(CREATE_LOCATION_TABLE);

		Log.d(CLASS_NAME, CREATE_PINCODE_TABLE);
		db.execSQL(CREATE_PINCODE_TABLE);

		// insert state
		insertStates(db);

		// insert districts
		insertDistricts(db);

		// insert locations
		insertLocations(db);

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

		// create new tables
		onCreate(db);
	}

	private void insertPincodes(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			String[] fileNames = context.getAssets().list("sql");
			for (String name : fileNames) {
				if (name.endsWith(".sql") && !name.equals("states.sql") && !name.equals("district.sql")
						&& !name.equals("locations.sql")) {
					// Open the resource
					InputStream insertsStream = context.getAssets().open("sql/" + name);
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
			InputStream insertsStream = context.getAssets().open("sql/states.sql");
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

	private void insertDistricts(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			// Open the resource
			InputStream insertsStream = context.getAssets().open("sql/district.sql");
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
			InputStream insertsStream = context.getAssets().open("sql/locations.sql");
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
	 * @return List<Office>
	 */
	public List<Office> findMatchingOffices(final String stateName, final String districtIn, final String nameOrCode) {
		List<Office> offices = new ArrayList<Office>();
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT  " + NAME + ", " + PIN_CODE + ", " + STATUS + ", " + SUB_OFFICE + ", "
				+ HEAD_OFFICE + ", l." + LOCATION_NAME + ", d." + DISTRICT_NAME + ", s." + STATE_NAME + ", " + TELEPHONE + " FROM "
				+ TABLE_POST_OFFICE + " ps"
				+ " INNER JOIN " + TABLE_STATE + " s ON ps."+  STATE + " = s." + STATE
				+ " INNER JOIN " + TABLE_DISTRICT + " d ON ps."+  DISTRICT + " = d." + DISTRICT + " AND d." + STATE + " = s." + STATE 
				+ " INNER JOIN " + TABLE_LOCATION + " l ON ps."+  LOCATION + " = l." + LOCATION;
		String where = "";

		if (stateName.trim().length() > 0) {
			where = " WHERE LOWER(REPLACE(s." + STATE_NAME + ",' ','')) LIKE '%" + stateName + "%'";
		}
		if (districtIn.trim().length() > 0) {
			if (where.length() > 0) {
				where = where + " AND LOWER(REPLACE(d." + DISTRICT_NAME + ",' ','')) LIKE '%" + districtIn + "%'";
			} else {
				where = " WHERE LOWER(REPLACE(d." + DISTRICT_NAME + ",' ','')) LIKE '%" + districtIn + "%'";
			}
		}
		if (nameOrCode.trim().length() > 0) {
			if (where.length() > 0) {
				where = where + " AND (LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + nameOrCode + "%' OR LOWER("
						+ PIN_CODE + ") LIKE '%" + nameOrCode + "%')";
			} else {
				where = " WHERE LOWER(REPLACE(" + NAME + ",' ','')) LIKE '%" + nameOrCode + "%' OR LOWER(" + PIN_CODE
						+ ") LIKE '%" + nameOrCode + "%'";
			}
		}
		String selectQuery = select + where;
		Log.d(CLASS_NAME, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Office office = new Office();
				office.setOfficeName(c.getString(c.getColumnIndex(NAME)).trim());
				office.setPinCode(c.getString(c.getColumnIndex(PIN_CODE)).trim());
				office.setStatus(c.getString(c.getColumnIndex(STATUS)).trim());
				office.setSuboffice(c.getString(c.getColumnIndex(SUB_OFFICE)).trim());
				office.setHeadoffice(c.getString(c.getColumnIndex(HEAD_OFFICE)).trim());
				office.setLocation(c.getString(c.getColumnIndex(LOCATION_NAME)).trim());
				office.setDistrict(c.getString(c.getColumnIndex(DISTRICT_NAME)).trim());
				office.setStateName(c.getString(c.getColumnIndex(STATE_NAME)).trim());
				office.setTelephone(c.getString(c.getColumnIndex(TELEPHONE)).trim());
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
