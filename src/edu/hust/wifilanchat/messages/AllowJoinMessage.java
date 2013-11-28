package edu.hust.wifilanchat.messages;

import java.util.List;

import edu.hust.wifilanchat.ChatRoom;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MyRoomsManager;
import edu.hust.wifilanchat.UserPreferenceManager;

public class AllowJoinMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private transient ChatRoom which;
	private int roomID;
	
	public AllowJoinMessage(int id) {
		super(MessageType.ALLOW_FOR_JOIN);
		this.roomID = id;
		createJSONContent();
	}
	
	public AllowJoinMessage(ChatRoom r) {
		super(MessageType.ALLOW_FOR_JOIN);
		this.which = r;
		createJSONContent();
	}
	private void createJSONContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"member\":");
		sb.append(createMemberList());
		sb.append("}");
		super.setContent(sb.toString());
	}

	private String createMemberList() {
		ChatRoom r = MyRoomsManager.getInstance().getRoomById(roomID);
		List<Member> mems = r.getMembers();
		String str = org.json.simple.JSONArray.toJSONString(mems);
		return str;
	}
	/*private String createMemberJSONList() {
		List<Member> mems = which.getMembers();
		String str = org.json.simple.JSONArray.toJSONString(mems);
		return str;
	}*/
}
