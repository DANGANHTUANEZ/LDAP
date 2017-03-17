package com.ifi.ldap.output;

import com.ifi.ldap.input.User;

public class OutputResult {
	private User user;
	private String result;
	private String errorCode;
	private String errorMessage;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public OutputResult() {
	}

	public OutputResult(User user, String result, String errorCode, String errorMessage) {
		this.user = user;
		this.result = result;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
