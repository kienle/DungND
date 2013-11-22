package com.greenwich.sherlock.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.greenwich.sherlock.entity.UserLocation;

public class UserLocationDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public UserLocationDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertUserLocation(UserLocation userLocation) {
		return database.insert(UserLocation.TABLE_NAME, null, getUserLocationContentValues(userLocation));
	}

	public long updateUserLocation(UserLocation userLocation) {
		return database.update(UserLocation.TABLE_NAME, getUserLocationContentValues(userLocation), UserLocation.ID + "=" + userLocation.getId(), null);
	}
	
	public long deleteUserLocation(int userId) {
		return database.delete(UserLocation.TABLE_NAME, UserLocation.USER_ID + " = " + userId, null);
	}

	public List<UserLocation> getAllLocationByUser(int userId) {
		List<UserLocation> userLocations = new ArrayList<UserLocation>();

		Cursor cursor = database.query(UserLocation.TABLE_NAME, null, UserLocation.USER_ID + "=" + userId, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserLocation user = cursorToComment(cursor);
			userLocations.add(user);
			cursor.moveToNext();
		}

		// make sure to close the cursor
		cursor.close();
		return userLocations;
	}

	private ContentValues getUserLocationContentValues(UserLocation userLocation) {
		ContentValues values = new ContentValues();
		values.put(UserLocation.USER_ID, userLocation.getUserId());
		values.put(UserLocation.TIME, userLocation.getTime());
		values.put(UserLocation.ADDRESS, userLocation.getAddress());
		values.put(UserLocation.NOTE, userLocation.getNote());
		
		return values;
	}
	
	private UserLocation cursorToComment(Cursor cursor) {
		UserLocation userLocation = new UserLocation();
		userLocation.setId(cursor.getInt(cursor.getColumnIndex(UserLocation.ID)));
		userLocation.setUserId(cursor.getInt(cursor.getColumnIndex(UserLocation.USER_ID)));
		userLocation.setTime(cursor.getString(cursor.getColumnIndex(UserLocation.TIME)));
		userLocation.setAddress(cursor.getString(cursor.getColumnIndex(UserLocation.ADDRESS)));
		userLocation.setNote(cursor.getString(cursor.getColumnIndex(UserLocation.NOTE)));
		return userLocation;
	}
}
