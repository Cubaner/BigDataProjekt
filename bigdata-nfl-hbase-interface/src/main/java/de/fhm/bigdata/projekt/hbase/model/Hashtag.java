package de.fhm.bigdata.projekt.hbase.model;

public class Hashtag {

	
	private String name;
	private int counter;
	
	
	public Hashtag(String name, int counter) {
		this.name = name;
		this.counter = counter;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
}