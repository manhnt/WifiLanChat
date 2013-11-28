package edu.hust.wifilanchat;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

	private List<ChatRoom> roomList = new ArrayList<ChatRoom>();
	
	/* The single instance of this class */
	private static RoomManager instance = new RoomManager();
	
	private RoomManager() {
		
	}
	
	public static RoomManager getInstance() {
		return instance;
	}
	
	public List<ChatRoom> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<ChatRoom> roomList) {
		this.roomList = roomList;
	}

	public void addRoom(ChatRoom r) {
		roomList.add(r);
	}
	
	public void removeRoom(int pos) {
		roomList.remove(pos);
	}
	
	public void removeRoom(ChatRoom r) {
		roomList.remove(r);
	}
	
	public ChatRoom getRoom(int pos) {
		return roomList.get(pos);
	}
	
	public int getRoomCount() {
		return roomList.size();
	}
	
	public List<ChatRoom> getCurrentJoiningRoom() {
		List<ChatRoom> list = new ArrayList<ChatRoom>();
		for (ChatRoom chatRoom : roomList) {
			if (chatRoom.isJoining()) list.add(chatRoom);
		}
		return list;
	}
}
