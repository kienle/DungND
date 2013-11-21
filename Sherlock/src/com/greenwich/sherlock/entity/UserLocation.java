package com.greenwich.sherlock.entity;

import java.io.Serializable;

public class UserLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "user_location";
	public static final String ID = "_id";
	public static final String USER_ID = "user_id";
	public static final String TIME = "time";
	public static final String ADDRESS = "address";
	public static final String NOTE = "note";

	private int id;
	private int userId;
	private String time;
	private String address;
	private String note;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
