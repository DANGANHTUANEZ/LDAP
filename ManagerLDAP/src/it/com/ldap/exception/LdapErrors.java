package com.ifi.ldap.exception;

/**
 * The Enum WbobOnlineErrors.
 *
 * @author sviluppo soa
 */
public enum LdapErrors {

	/** The auth message invalid. */
	VOUCHER_NOT_FOUND("70000", "Voucher not found"),
	
	/*Products not found*/ 
	NO_PROD_FOUND("70001", "Products not found"),
	
	/**Dossier not found*/ 
	DOSSIER_NOT_FOUND("70002", "Dossier not found"),
	
	EMPTY_RESULT_SET("70004", "Empty result set"),
	
	/**Banklist not found*/ 
	BANKLIST_NOT_FOUND("70014", "Banklist not found"),
	
	STATUS_NOT_UPDATE("70003", "Status not update"),
	
	SSO_ERROR("70005", "Business Fault in SSO Service"),
	
	OTP_ERROR("70006", "Business Fault in OTP Service"),
	
	FEQ_ERROR("70007", "Business Fault in FEQ Service"),
	
	SERVICE_UTILITY_ERROR("70009", "Business Fault in SERVICE UTILITY Service"),
	
	/**Banklist not found*/ 
	FILENET_ERROR("70010", "Filenet error"),
	
	/**Banklist not found*/ 
	FILENET_EMPTY_RESPONSE("70011", "Filenet empty response"),
	
	SERVER_FE_MANAGER_ERROR("70012", "Error from ServerFeManager"),
	
	PROXY_SERVICE_ERROR("70013", "Error from ProxyService"),
	
	SIGN_SERVICE_ERROR("70050", "Sign Service Failed"),
	
	MES_OTP_ERROR("70015", "Error from MES Otp"),
	
	SENDEMAIL_SERVICE_BUSINESS_ERROR("70016", "Business Error from Service utility"),
	
	BASE_PROFILAZIONECOMPANY_BUSINESS("70100", "Profile error"),
	
	MUS_TENTATVI_ACCESS_ERROR("70016", "Error from MUS Tentativi Accesso"),
	
	TRANSFER_NOT_FOUND("70030", "Transfer not found"),
	
	CA_ERROR("70007", "CA_ERROR"),
	
	TELEPHONE_NUMBER_IS_NULL("70077", "Telephone number is null"),
	
	CARTASI_CONTRACT_IS_NULL("70078", "CartaSi contract is null"),
	
	CO_TAX_CODE_IS_NULL("70079", "Co Tax Code is null"),
	
	MODULE_IS_NOT_A_PDF("70080", "Module is not a pdf"),
	
	/** Max attempts exceeded error */
	OTP_ATTEMPTS_EXCEEDED("70017", "Max attempts exceeded error"),
	
	/** The system error. */
	GENERIC_SYSTEM_ERROR("90000", "Generic system error"),

	/** The system error. */
	GENERIC_DB_ERROR("90001", "Generic data base error"),
	
	/** The system error. */
	GENERIC_NETWORK_ERROR("90002", "Generic network error"),
	
	SENDEMAIL_SERVICE_SYSTEM_ERROR("90003", "System Error from Service utility"),
	
	SENDSMS_SERVICE_ERROR_SYSTEM("90004", "Errore da servizio Ubiquity"),
	
	/** The system error. */
	GENERIC_CLIENT_AXIS_ERROR("90005", "Generation axis client error"),
	
	/** The base profilazionecompany system. */
	BASE_PROFILAZIONECOMPANY_SYSTEM("90100", "Profile error");


	/** The code. */
	private final String code;

	/** The message. */
	private final String description;

	/**
	 * Instantiates a new TDS piattaforma int errors.
	 * 
	 * @param code the code
	 * @param description the description
	 */
	private LdapErrors(String code, String description) {
		this.code = code;
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
