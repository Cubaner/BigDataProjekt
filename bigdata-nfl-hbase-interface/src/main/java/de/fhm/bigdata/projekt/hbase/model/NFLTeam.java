package de.fhm.bigdata.projekt.hbase.model;

public class NFLTeam {

	private String teamName;
	private int rank;
	
	
	public NFLTeam(String teamName, int rank) {
		this.teamName = teamName;
		this.rank = rank;
	}


	public String getTeamName() {
		return teamName;
	}


	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


	public int getRank() {
		return rank;
	}


	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
	
}
