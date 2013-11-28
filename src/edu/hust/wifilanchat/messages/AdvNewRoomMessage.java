package edu.hust.wifilanchat.messages;

import edu.hust.wifilanchat.ChatRoom;

public class AdvNewRoomMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	public AdvNewRoomMessage(ChatRoom newRoom) {
		super(MessageType.ADV_NEW_ROOM);
		createJSONContent(newRoom);
	}

	private void createJSONContent(ChatRoom r) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"id\":\"" + r.getId() + "\",");
		sb.append("\"name\":\"" + r.getRoomName() + "\"");
		sb.append("}");
		setContent(sb.toString());
	}

}
