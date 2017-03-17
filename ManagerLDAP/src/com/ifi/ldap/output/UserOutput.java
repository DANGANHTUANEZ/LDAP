package com.ifi.ldap.output;

public class UserOutput {
	private String result;
	private String errorCode;
	private String errorMessage;

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

	public UserOutput() {
	}

	public UserOutput(String result, String errorCode, String errorMessage) {
		this.result = result;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
