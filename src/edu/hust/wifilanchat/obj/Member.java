package edu.hust.wifilanchat.obj;

public class Member {

	private String name;
	// TODO: add more necessary fields like IP address, etc.
	
	public Member(String pName) {
		this.name = pName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return (getName());
	}
}
