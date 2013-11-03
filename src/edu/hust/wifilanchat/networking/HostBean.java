/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

// Inspired by http://connectbot.googlecode.com/svn/trunk/connectbot/src/org/connectbot/bean/HostBean.java
package edu.hust.wifilanchat.networking;

//import info.lamatricexiste.network.ActivityMain;


public class HostBean {
	public static final String EXTRA = "com.example.mydiscover.extra";
	
    private int id;
	private String ipAddress = null;
    private String username = null;
    public String hardwareAddress = NetInfo.NOMAC;
//    private boolean isAlive = false;
    
    public HostBean(){
    	
    }
    
    public HostBean (int id, String ip, String user, boolean alive){
    	this.id = id;
//    	this.isAlive = alive;
    	this.ipAddress = ip;
    	this.username = user;
    }
    
    public HostBean (int id, String ip, String user){
    	this.id = id;
    	this.ipAddress = ip;
    	this.username = user;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public void setID(int id){
    	this.id = id;
    }
    
    public String getIpAddress(){
    	return this.ipAddress;
    }
    
    public void setIpAddress(String ip){
    	this.ipAddress = ip;
    }
    
    public String getUsername(){
    	return this.username;
    }
    
    public void setUsername(String user){
    	this.username = user;
    }
    
    public String toString(){
    	return ipAddress;
    }
}
