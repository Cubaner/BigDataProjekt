package de.fhm.bigdata.projekt.hbase.model;

import java.util.Comparator;

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
	
	public static Comparator<Hashtag> getHashtagByCounter()
	{   
	 Comparator<Hashtag> comp = new Comparator<Hashtag>(){
	     @Override
	     public int compare(Hashtag h1, Hashtag h2)
	     {
	         return h1.counter.compareTo(h2.counter);
	     }        
	 };
	 return comp;
	}  
	
}