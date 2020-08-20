package com.sapient.footballleague.service.impl.service;

import java.util.List;

import com.sapient.footballleague.pojo.Country;
import com.sapient.footballleague.pojo.LeagueCountry;
import com.sapient.footballleague.pojo.LeagueStandingsReponse;

public interface FootballLeagueService {
	
	public List<LeagueStandingsReponse> getStandings(String leagueId);
	
	public List<Country> getCountries();
	
	public List<LeagueCountry> getLeagues(final String countryId) ;

}
