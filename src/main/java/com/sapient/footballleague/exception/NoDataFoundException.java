package com.sapient.footballleague.exception;

public class NoDataFoundException extends LeagueException {
	/**
	 * Generated Serial version Id.
	 */
	private static final long serialVersionUID = 6152668818102407607L;
	public NoDataFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

}
