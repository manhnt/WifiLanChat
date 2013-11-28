package edu.hust.wifilanchat.messages;

import edu.hust.wifilanchat.ChatRoom;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.UserPreferenceManager;

public class RequestJoinMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private Member server;
	private int roomID;
	
	public RequestJoinMessage(ChatRoom room, Member server) {
		super(MessageType.REQ_JOIN_ROOM);
		this.setServer(server);
		this.roomID = room.getId();
	}

	public RequestJoinMessage(ChatRoom room) {
		this(room, room.getServer());
	}

	public Member getServer() {
		return server;
	}

	public void setServer(Member server) {
		this.server = server;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
}
