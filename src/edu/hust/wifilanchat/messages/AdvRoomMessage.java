package edu.hust.wifilanchat.messages;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import edu.hust.wifilanchat.ChatRoom;
import edu.hust.wifilanchat.MyRoomsManager;

public class AdvRoomMessage extends NetworkMessage {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	private transient List<ChatRoom> roomList = new ArrayList<ChatRoom>();
	
	public AdvRoomMessage() {
		super(MessageType.ADV_ROOM);
		createJSONContent();
	}

//	public AdvRoomMessage(String pContent, Member pSender, MessageType t) {
//		super(pContent, pSender, t);
//		createJSONContent();
//	}
//
//	public AdvRoomMessage(Member pSender, MessageType t) {
//		super(null, pSender, t);
//		createJSONContent();
//	}
	
	public void addRoom(ChatRoom pRoom) {
		roomList.add(pRoom);
	}
	
	private void createJSONContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"room\":");
		sb.append(getJSONRoomArray());
		sb.append("}");
		
		setContent(sb.toString());
	}
	private String getJSONRoomArray() {
		return JSONArray.toJSONString(MyRoomsManager.getInstance().getMyRoomList());
	}
}
