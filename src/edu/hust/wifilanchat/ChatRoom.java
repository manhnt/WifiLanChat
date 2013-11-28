package edu.hust.wifilanchat;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONAware;

public class ChatRoom implements JSONAware {
	private String roomName;
	private String serverName;
	private Member server;
	private int id;
	private boolean isMine = false;
	private boolean isJoining = false;
	
	private List<Member> members = new ArrayList<Member>();
	
	public ChatRoom(String name) {
		this.roomName = name;
	}
	
	public ChatRoom(String name, Member serv) {
		this.roomName = name;
		this.setServer(serv);
		this.serverName = serv.getName();
	}
	
	public ChatRoom(String name, Member serv, int id) {
		this.roomName = name;
		this.setServer(serv);
		this.serverName = serv.getName();
		this.id = id;
	}
	
	public static ChatRoom createNewRoom(String name) {
		ChatRoom r = new ChatRoom(name);
		r.setId(MyRoomsManager.getInstance().getNextID());
		r.setServer(UserPreferenceManager.getInstance().getMe());
		return r;
	}
	
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public Member getServer() {
		return server;
	}

	public void setServer(Member server) {
		this.server = server;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return (getRoomName());
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public void addMember(Member m) {
		members.add(m);
	}
	
	public void removeMember(Member m) {
		members.remove(m);
	}
	
	public void removeMember(int pos) {
		members.remove(pos);
	}
	
	public boolean isJoining() {
		return isJoining;
	}

	public void setJoining(boolean isJoining) {
		this.isJoining = isJoining;
	}

	/**
	 * Return a String in JSON format represents for this room
	 * {"id":"room id","name":"room name"}
	 */
	@Override
	public String toJSONString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"id\":");
		sb.append("\"" + getId() + "\",");
		sb.append("\"name\":");
		sb.append("\"" + getRoomName() + "\"");
		sb.append("}");
		return sb.toString();
	}
}
