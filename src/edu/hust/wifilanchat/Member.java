package edu.hust.wifilanchat;

import java.net.InetAddress;

public class Member {

	private String name;
	// TODO: add more necessary fields like IP address, etc.
	private InetAddress ipAddr;
	
	public Member(String pName) {
		this.name = pName;
		this.ipAddr = null;
	}
	
	public Member(String pName, InetAddress pAddr) {
		this.name = pName;
		this.ipAddr = pAddr;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public InetAddress getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(InetAddress ipAddr) {
		this.ipAddr = ipAddr;
	}
	@Override
	public String toString() {
		return (getName());
	}
}
