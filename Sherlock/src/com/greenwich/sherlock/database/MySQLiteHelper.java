package com.greenwich.sherlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.greenwich.sherlock.entity.User;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sherlock.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation album table sql statement
	private static final String CREATE_ALBUM = "create table " + User.TABLE_NAME
			+ "(" + User.COLUMN_ID + " integer primary key autoincrement, "
			+ User.COLUMN_PHOTO_PATH + " text not null, "
			+ User.COLUMN_USERNAME + " text not null, "
			+ User.COLUMN_GENDER + " int not null, "
			+ User.COLUMN_HEIGHT + " int not null, "
			+ User.COLUMN_AGE_FROM + " int not null, "
			+ User.COLUMN_AGE_TO + " int not null, "
			+ User.COLUMN_HAIR_COLOR + " text not null, "
			+ User.COLUMN_COMMENT + " int not null);";
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_ALBUM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
		onCreate(db);
	}

}