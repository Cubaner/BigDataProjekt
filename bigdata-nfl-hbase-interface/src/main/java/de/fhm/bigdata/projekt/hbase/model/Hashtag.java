package de.fhm.bigdata.projekt.hbase.model;

public class Hashtag {

	
	private String name;
	private String counter;
	
	
	public Hashtag(String name, String counter) {
		this.name = name;
		this.counter = counter;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
	
}