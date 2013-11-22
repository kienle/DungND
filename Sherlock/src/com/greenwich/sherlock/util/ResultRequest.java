package com.greenwich.sherlock.util;

public class ResultRequest {
	private int statusCode;
	private String stringResult;

	public ResultRequest(int statusCode, String stringResult) {
		this.statusCode = statusCode;
		this.stringResult = stringResult;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStringResult() {
		return stringResult;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

}
