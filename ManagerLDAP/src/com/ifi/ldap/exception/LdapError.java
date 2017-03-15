package com.ifi.ldap.exception;

public class LdapError {
	
	/** The code. */
	private String code;
	
	/** The message. */
	private String description;


	public LdapError(LdapErrors e) {
		super();
		this.code = e.getCode();
		this.description = e.getDescription();
	}
	
	public LdapError(LdapErrors e, String addMessage) {
		super();
		this.code = e.getCode();
		this.description = e.getDescription() + ": " + addMessage;
	}

	public LdapError() {
		
	}
	

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}


	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getDescription() {
		return description;
	}



}
