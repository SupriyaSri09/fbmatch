package com.sapient.footballleague;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.sapient.footballleague.pojo.CustomErrorResponse;
import com.sapient.footballleague.pojo.LeagueStandingsReponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FootballLeagueControllerTest {
	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();
	
	@Test
	public void testGetTeamstandings() throws Exception {
	        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
	        ResponseEntity<CustomErrorResponse> response = restTemplate.exchange(
	          createURLWithPort("/league/standings/team?countryName=France&teamName=pqr&leagueName=Ligue 2"),
	          HttpMethod.GET, entity,  CustomErrorResponse.class);
	        CustomErrorResponse errorResponse = response.getBody();
	       assertTrue(errorResponse.getStatus()==404);
	       assertTrue(errorResponse.getError().
	    		   equalsIgnoreCase("No standings data found for given league name, country name and team name"));
	}
	
	@Test
	public void testWithInvalidCountry() throws Exception {
	        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
	        ResponseEntity<CustomErrorResponse> response = restTemplate.exchange(
	          createURLWithPort("/league/standings/team?countryName=abc&teamName=pqr&leagueName=Ligue 2"),
	          HttpMethod.GET, entity,  CustomErrorResponse.class);
	        CustomErrorResponse errorResponse = response.getBody();
	       assertTrue(errorResponse.getStatus()==404);
	       assertTrue(errorResponse.getError().
	    		   equalsIgnoreCase("Invalid Country Name "));
	}

	@Test
	public void testWithInvalidTeamName() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<LeagueStandingsReponse> response = restTemplate.exchange(
				createURLWithPort("/league/standings/team?countryName=France&teamName=Lorient&leagueName=Ligue 2"),
				HttpMethod.GET, entity, LeagueStandingsReponse.class);
		LeagueStandingsReponse leagueStandingsReponse = response.getBody();
		assertTrue(leagueStandingsReponse.getCountry_name().equalsIgnoreCase("France"));
		assertTrue(leagueStandingsReponse.getLeague_name().equalsIgnoreCase("Ligue 2"));
		assertTrue(leagueStandingsReponse.getTeam_name().equalsIgnoreCase("Lorient"));

	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
