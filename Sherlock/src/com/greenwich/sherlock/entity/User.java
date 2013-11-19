package com.greenwich.sherlock.entity;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "user";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PHOTO_PATH = "photo";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_HEIGHT = "height";
	public static final String COLUMN_AGE_FROM = "age_from";
	public static final String COLUMN_AGE_TO = "age_to";
	public static final String COLUMN_HAIR_COLOR = "hair_color";
	public static final String COLUMN_COMMENT = "comment";
	
	private int id;
	private String photoPath;
	private String username;
	private String gender;
	private int height;
	private int ageFrom;
	private int ageTo;
	private String hairColor;
	private String comment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getAgeFrom() {
		return ageFrom;
	}

	public void setAgeFrom(int ageFrom) {
		this.ageFrom = ageFrom;
	}

	public int getAgeTo() {
		return ageTo;
	}

	public void setAgeTo(int ageTo) {
		this.ageTo = ageTo;
	}

	public String getHairColor() {
		return hairColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
