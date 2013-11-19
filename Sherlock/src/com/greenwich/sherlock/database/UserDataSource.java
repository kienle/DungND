package com.greenwich.sherlock.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.greenwich.sherlock.entity.User;

public class UserDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public UserDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertUser(User user) {
		return database.insert(User.TABLE_NAME, null, getUserContentValues(user));
	}

	public long updateUser(User user) {
		return database.update(User.TABLE_NAME, getUserContentValues(user), null, null);
	}
	
	public void deleteUser(int userId) {
		database.delete(User.TABLE_NAME, User.COLUMN_ID + " = " + userId, null);
	}

	public List<User> getAllUser() {
		List<User> users = new ArrayList<User>();

		Cursor cursor = database.query(User.TABLE_NAME, null, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User user = cursorToComment(cursor);
			users.add(user);
			cursor.moveToNext();
		}

		// make sure to close the cursor
		cursor.close();
		return users;
	}

	private ContentValues getUserContentValues(User user) {
		ContentValues values = new ContentValues();
		values.put(User.COLUMN_PHOTO_PATH, user.getPhotoPath());
		values.put(User.COLUMN_USERNAME, user.getUsername());
		values.put(User.COLUMN_GENDER, user.getGender());
		values.put(User.COLUMN_HEIGHT, user.getHeight());
		values.put(User.COLUMN_AGE_FROM, user.getAgeFrom());
		values.put(User.COLUMN_AGE_TO, user.getAgeTo());
		values.put(User.COLUMN_HAIR_COLOR, user.getHairColor());
		values.put(User.COLUMN_COMMENT, user.getComment());
		
		return values;
	}
	
	private User cursorToComment(Cursor cursor) {
		User user = new User();
		user.setId(cursor.getInt(cursor.getColumnIndex(User.COLUMN_ID)));
		user.setPhotoPath(cursor.getString(cursor.getColumnIndex(User.COLUMN_PHOTO_PATH)));
		user.setUsername(cursor.getString(cursor.getColumnIndex(User.COLUMN_USERNAME)));
		user.setGender(cursor.getString(cursor.getColumnIndex(User.COLUMN_GENDER)));
		user.setHeight(cursor.getInt(cursor.getColumnIndex(User.COLUMN_HEIGHT)));
		user.setAgeFrom(cursor.getInt(cursor.getColumnIndex(User.COLUMN_AGE_FROM)));
		user.setAgeTo(cursor.getInt(cursor.getColumnIndex(User.COLUMN_AGE_TO)));
		user.setHairColor(cursor.getString(cursor.getColumnIndex(User.COLUMN_HAIR_COLOR)));
		user.setComment(cursor.getString(cursor.getColumnIndex(User.COLUMN_COMMENT)));
		return user;
	}
}