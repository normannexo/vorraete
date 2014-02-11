package com.example.vorraete;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {
	
	public String name;
	public String date;
	public Date lastModified;
	public int id;
	public boolean kaufen = false;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public Article(String name) {
		this.name = name;
		
	}
	
	public Article (String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public Article (String name, int id, boolean kaufen) {
		this.name = name;
		this.id = id;
		this.kaufen = kaufen;
	}
	

	public Article (String name, int id, boolean kaufen, Date lastm) {
		this.name = name;
		this.id = id;
		this.kaufen = kaufen;
		this.lastModified = lastm;
	}

	
	@Override
	public String toString() {
		return "Name: "  + name + ", Id:" +  id;
	}
}
