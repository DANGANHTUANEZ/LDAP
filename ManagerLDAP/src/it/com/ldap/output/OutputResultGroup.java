package it.com.ldap.output;

import it.com.ldap.entity.Group;

public class OutputResultGroup {
	private Group group;
	private String result;
	private String errorCode;
	private String errorMessage;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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

	public OutputResultGroup() {
	}

	public OutputResultGroup(Group group, String result, String errorCode, String errorMessage) {
		this.group = group;
		this.result = result;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
