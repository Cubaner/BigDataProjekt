package de.fhm.bigdata.projekt.rest;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.fhm.bigdata.projekt.hbase.HBaseConnectionManager;
import de.fhm.bigdata.projekt.hbase.model.*;

@Path("nfl")
public class JerseyService {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");
	private final String REPO_ALLE = "ALLE";

	@GET
	@Path("/getTeamstatictics")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ArrayList<NFLTeam> getAllTeamStatistics() {
		
		HBaseConnectionManager connMan = new HBaseConnectionManager();
		/*
		List<String> repos = connMan.getNflTeams();
		Repo[] resultArr = new Repo[repos.size()+1];
		resultArr[0] = new Repo(REPO_ALLE);
		int j = 0;
		for (int i = 1; i < resultArr.length; i++) {
			resultArr[i] = new Repo(repos.get(j));
			j++;
		}
		*/
		connMan.getTopHasttags();
		return createTestTeams();
	}
	
	private ArrayList<NFLTeam> createTestTeams() {
		ArrayList<NFLTeam> test = new ArrayList<NFLTeam>();
		test.add(new NFLTeam("GreenBayPackers", 1));
		test.add(new NFLTeam("ArizonaCardinals", 2));
		
		return test;		
	}
}
