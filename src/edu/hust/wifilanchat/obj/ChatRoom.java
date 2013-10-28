package edu.hust.wifilanchat.obj;

public class ChatRoom {
	private String roomName;

	public ChatRoom(String name) {
		this.roomName = name;
	}
	
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	@Override
	public String toString() {
		return (getRoomName());
	}
}
