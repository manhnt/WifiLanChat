package edu.hust.wifilanchat;

import java.util.ArrayList;
import java.util.List;

public class MemberManager {
	private List<Member> memberList = new ArrayList<Member>();
	
	/* The single instance of this class */
	private static MemberManager instance = new MemberManager();
	
	private MemberManager() {
		
	}
	
	public static MemberManager getInstance() {
		return instance;
	}
	
	public List<Member> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Member> memberList) {
		this.memberList = memberList;
	}

	public void addMember(Member m) {
		memberList.add(m);
	}
	
	public void removeMember(Member m) {
		memberList.remove(m);
	}
	
	public void removeMember(int pos) {
		memberList.remove(pos);
	}
	
	public Member getMember(int pos) {
		return memberList.get(pos);
	}
	
	public int getMemberCount() {
		return memberList.size();
	}
	
}
