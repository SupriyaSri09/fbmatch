package com.sapient.footballleague.service.impl.service.impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sapient.footballleague.exception.ErrorCode;
import com.sapient.footballleague.exception.NoDataFoundException;
import com.sapient.footballleague.pojo.Country;
import com.sapient.footballleague.pojo.LeagueCountry;
import com.sapient.footballleague.pojo.LeagueStandingsReponse;
import com.sapient.footballleague.service.impl.service.FootballLeagueService;
import com.sapient.footballleague.util.LeagueConstant;

/**
 * WebService Client that contain all the operations to interact with external web service.
 * @author suppraka
 *
 */
@Service
public class FootballLeagueServiceImpl implements FootballLeagueService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FootballLeagueServiceImpl.class);
	
	private static final String ERROR = "error";
	
	private RestTemplate restTemplate;
	
	@Value("${standings.url}")
	private String standingsAPIurl;
	
	@Value("${country.url}")
	private String countryAPIurl;
	

	@Value("${league.url}")
	private String leagueAPIurl;
	
	@Autowired
	public FootballLeagueServiceImpl(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}
	
	/**
	 * Gets the list of countries included in the current subscription plan.
	 * @return List of countries.
	 */
	public List<Country> getCountries(){
		ResponseEntity<Country[]> response = restTemplate.getForEntity(countryAPIurl,Country[].class);
		Country[] countries = response.getBody();
		List<Country> countryList  = Arrays.asList(countries);
		LOGGER.info(countries.toString());
		return countryList;
	}
	

	/**
	 * Gets the list of supported competitions included in your current subscription plan for a particular country.
	 * @param countryId
	 * @return List of leagues.
	 */
	public List<LeagueCountry> getLeagues(final String countryId)  {
	  List<LeagueCountry> list = null;
		String url = UriComponentsBuilder.fromHttpUrl(leagueAPIurl).replaceQueryParam(LeagueConstant.COUNTRY_ID, countryId)
				.toUriString();
		LOGGER.debug("URL to get leagues information :"+url);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		JsonParser jsonParser = new JsonParser();
		JsonElement jelement = jsonParser.parse(response.getBody());
		JsonObject  jobject =null;
		
		 if (jelement instanceof JsonObject) {
			     jobject = jelement .getAsJsonObject();
			 } else if (jelement instanceof JsonArray) {
			    JsonArray  jarray = jelement.getAsJsonArray();
			    jobject = jarray.get(0).getAsJsonObject();
			    Gson gson = new Gson();
			    LeagueCountry[] larray =  gson.fromJson(response.getBody(), LeagueCountry[].class);
			    list = Arrays.asList(larray);
			 }
		try {
			generateErrorMessaageWhenNoData(jobject);
		} catch (NoDataFoundException e) {
			LOGGER.error("No leagues data found for countryId:"+countryId);
		}
		
		return list;
	}
	
	/**
	 *
	 */
	public List<LeagueStandingsReponse> getStandings(String leagueId) {
		String url = UriComponentsBuilder.fromHttpUrl(standingsAPIurl).replaceQueryParam(LeagueConstant.LEAGUE_ID, leagueId)
				.toUriString();
		LOGGER.info("URL to get standings:"+url);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		LOGGER.info("Response JSON for standings"+response.getBody());
		Gson gson = new Gson();
		LeagueStandingsReponse[] standingsResponseArray =  gson.fromJson(response.getBody(),
				LeagueStandingsReponse[].class);
		List<LeagueStandingsReponse> resultList = Arrays.asList(standingsResponseArray);
		return resultList;
	}

	
	/**
	 * Sends error alert to client in case response contains no data.
	 * @param response
	 * @throws NoDataFoundException
	 */
	public static void generateErrorMessaageWhenNoData(final JsonObject response) throws NoDataFoundException {
		if(null!=response && response.has(ERROR)) {
			throw new NoDataFoundException(ErrorCode.LEAGUE_SERVICE_NO_DATA_FOUND);
			
		}
		
	}

}
