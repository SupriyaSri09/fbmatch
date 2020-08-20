package com.sapient.footballleague.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.footballleague.exception.ErrorCode;
import com.sapient.footballleague.exception.LeagueException;
import com.sapient.footballleague.exception.NoDataFoundException;
import com.sapient.footballleague.pojo.Country;
import com.sapient.footballleague.pojo.LeagueCountry;
import com.sapient.footballleague.pojo.LeagueStandingsReponse;
import com.sapient.footballleague.service.impl.service.FootballLeagueService;

/**
 * RestController which takes care of mapping request data to the defined request handler method.
 * It uses FootballLeagueService interface to get the relevant data from upstream web service.
 * @author suppraka
 *
 */
@RestController
@RequestMapping("/standings")
public class FootballLeagueController implements InitializingBean {

	private List<LeagueCountry> leagueCountryList = new ArrayList<LeagueCountry>();

	private static final Logger LOGGER = LoggerFactory.getLogger(FootballLeagueController.class);

	@Autowired
	FootballLeagueService footballLeagueServiceImpl;

	@Override
	public void afterPropertiesSet() throws Exception {
		loadData();

	}

	/**
	 * Loads country and country league data on bean initialization to improve performance.
	 * It ensures upstream web service is not invoked to get static data on each user request.
	 */
	private void loadData() {
		List<Country> countries = (List<Country>) footballLeagueServiceImpl.getCountries();
		countries.forEach(new Consumer<Country>() {
			@Override
			public void accept(Country country) {
				List<LeagueCountry> leagueList = (List<LeagueCountry>) footballLeagueServiceImpl
						.getLeagues(country.getCountry_id());
				if (CollectionUtils.isNotEmpty(leagueList))
					leagueCountryList.addAll(leagueList);

			}
		});
		LOGGER.info("List of Country and League :" + Arrays.toString(leagueCountryList.toArray()));
	}

	/**
	 * Get standing of a team in league football match based on country name, league
	 * name and team name.
	 * 
	 * @param countryName name of country.
	 * @param leagueName  Name of the league.
	 * @param teamName    Name of the team.
	 * @return standing of a team in league football match.
	 * @throws LeagueException
	 */
	@RequestMapping(value = "/team", method = RequestMethod.GET)
	public ResponseEntity<LeagueStandingsReponse> getTeamStanding(@RequestParam(required = true) String countryName,
			@RequestParam(required = true) String leagueName, @RequestParam(required = true) String teamName)
			throws LeagueException {
		LOGGER.info("Country Name: " + countryName + "League Name: " + leagueName + "Team Name: " + teamName);
		LOGGER.debug("League Country " + Arrays.toString(leagueCountryList.toArray()));
		validateData(countryName, leagueName);
		String leagueId = getLeagueId(leagueName, countryName);
		LOGGER.info("League Id is " + leagueId);
		List<LeagueStandingsReponse> response = footballLeagueServiceImpl.getStandings(leagueId);
		return new ResponseEntity<LeagueStandingsReponse>(getResult(response, leagueName, countryName, teamName),
				HttpStatus.OK);
	}

	/**
	 * @param leagueName
	 * @param countryName
	 * @return
	 */
	private String getLeagueId(final String leagueName, final String countryName) {

		return leagueCountryList.stream().filter(x -> x.getLeague_name().equalsIgnoreCase(leagueName))
				.filter(x -> x.getCountry_name().equalsIgnoreCase(countryName)).findFirst().get().getLeague_id();

	}

	/**
	 * Filters the list to get the standings of user input team.
	 * @param standingsList
	 * @param leagueName
	 * @param countryName
	 * @param teamName
	 * @return response object that contains the team details with standings.
	 * @throws NoDataFoundException
	 */
	private LeagueStandingsReponse getResult(List<LeagueStandingsReponse> standingsList, final String leagueName,
			final String countryName, final String teamName) throws NoDataFoundException {
		long count = standingsList.stream().filter(x -> x.getLeague_name().equalsIgnoreCase(leagueName))
				.filter(x -> x.getCountry_name().equalsIgnoreCase(countryName))
				.filter(x -> x.getTeam_name().equalsIgnoreCase(teamName)).count();
		LOGGER.info("Total number of teams" + count);
		if (count > 0) {
			return standingsList.stream().filter(x -> x.getLeague_name().equalsIgnoreCase(leagueName))
					.filter(x -> x.getCountry_name().equalsIgnoreCase(countryName))
					.filter(x -> x.getTeam_name().equalsIgnoreCase(teamName)).findFirst().get();
		} else {
			throw new NoDataFoundException(ErrorCode.NO_STANDINGS_DATA_FOUND);
		}

	}

	/**
	 * Validates user input data.
	 * @param countryName input country name.
	 * @param leagueName input league name.
	 * @throws LeagueException
	 */
	private void validateData(final String countryName, final String leagueName) throws LeagueException {
		if (leagueCountryList.stream().filter(x -> x.getCountry_name().equalsIgnoreCase(countryName)).count() == 0) {
			throw new NoDataFoundException(ErrorCode.INVALID_COUNTRY_NAME);
		}
		if (leagueCountryList.stream().filter(x -> x.getLeague_name().equalsIgnoreCase(leagueName)).count() == 0) {
			throw new NoDataFoundException(ErrorCode.INVALID_LEAGUE_NAME);
		}

	}

}
