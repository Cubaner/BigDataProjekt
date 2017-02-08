package de.fhm.bigdata.projekt.hbase.model;

import java.util.Comparator;

public class Hashtag {

	
	private String name;
	private Integer counter;
	
	
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