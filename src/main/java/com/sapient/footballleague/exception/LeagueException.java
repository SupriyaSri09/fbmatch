package com.sapient.footballleague.exception;

public class LeagueException extends Exception {
	
	/**
	 * Generated trnsient Id.
	 */
	private static final long serialVersionUID = -1836149803078886187L;
	public final ErrorCode errorCode;

	public LeagueException(ErrorCode errorCode) {
		super(errorCode.getMessgae());
		this.errorCode = errorCode;
	}
	
	

}
