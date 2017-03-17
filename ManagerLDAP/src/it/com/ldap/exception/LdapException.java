package com.ifi.ldap.exception;

//import it.icbpi.mul.profilazioneCompany.domain.ProfilazioneCompanyException;

/**
 * WBob
 *
 * @author Mauro Polastri / Giovanni Giorgi / Luigi Verderi
 */
public class LdapException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private LdapError error;

	public LdapException(LdapError error) {
		super(error.getDescription());
		this.error = error;

	}


	/**
	 * GG was here
	 * @param error
	 * @param message
	 */
	public LdapException(LdapErrors error, Throwable m) {
		super(error.getDescription()+" wbob-caused-by:"+ m.getMessage(),m);
		this.error = new LdapError();
		this.error.setCode(error.getCode());
		this.error.setDescription(error.getDescription()+" wbob-caused-by:"+ m.getMessage());
	}

	public LdapException(LdapError error, Exception m) {
		super(error.getDescription()+" wbob-caused-by:"+ m.getMessage(),m);
		this.error = error;

	}

	public LdapException(int code, String string) {
		super();
		this.error = new LdapError();
		error.setCode(Integer.toString(code));
		error.setDescription(string);
	}

	public LdapException(LdapErrors error) {
		super();
		this.error = new LdapError();
		this.error.setCode(error.getCode());
		this.error.setDescription(error.getDescription());
	}

	public LdapError getError() {
		return error;
	}

	private void makeErrorInfo(int code, String message, LdapErrors systemBase,
			LdapErrors businessBase) {

		if (code >= 90000) {

			code = code - 90000 + Integer.decode(systemBase.getCode());
			message = systemBase.getDescription() + ": " + message;
		} else {

			code = code - 70000 + Integer.decode(businessBase.getCode());
			message = businessBase.getDescription() + ": " + message;
		}

		this.error = new LdapError();
		this.error.setCode(Integer.toString(code));
		this.error.setDescription(message);
	}


	// Example of external service error re-mapping for external service "ProfilazioneCompany".
//	public WbobOnlineException(ProfilazioneCompanyException libraryExc) {
//
//		super(libraryExc);
//		WbobOnlineErrors businessBase = WbobOnlineErrors.BASE_PROFILAZIONECOMPANY_BUSINESS;
//		WbobOnlineErrors systemBase = WbobOnlineErrors.BASE_PROFILAZIONECOMPANY_SYSTEM;
//
//		makeErrorInfo(libraryExc.getCode(), libraryExc.getMessage(), systemBase, businessBase);
//
//	}

	public LdapException(LdapErrors e, String addMessage) {
		this(e);
		this.error.setDescription(e.getDescription() + ": " + addMessage);
	}





}
