package com.sapient.footballleague.exception;

public enum ErrorCode {
	LEAGUE_SERVICE_NO_DATA_FOUND("LEAGUE_SERVICE_01","No league found "),
	INVALID_COUNTRY_NAME("LEAGUE_SERVICE_02","Invalid Country Name "),
	INVALID_LEAGUE_NAME("LEAGUE_SERVICE_03","Invalid League Name"),
	NO_STANDINGS_DATA_FOUND("LEAGUE_SERVICE_04","No standings data found for given league name, country name and team name");
	private final String code;
	
	private final String messgae;

	public String getCode() {
		return code;
	}

	public String getMessgae() {
		return messgae;
	}

	private ErrorCode(String code, String messgae) {
		this.code = code;
		this.messgae = messgae;
	}
	
	

}
