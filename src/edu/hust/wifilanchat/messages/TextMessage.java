package edu.hust.wifilanchat.messages;

import edu.hust.wifilanchat.Member;

public class TextMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
//	private int whichRoomID = 0;
	
	public TextMessage(String msg, Member sender) {
		super(null, sender, MessageType.TEXT_MESSAGE);
		createJSONContent(msg);
	}
	
	/*public TextMessage(String msg, Member sender, int inRoomID) {
		super(null, sender, MessageType.TEXT_MESSAGE);
		this.whichRoomID = inRoomID;
		createJSONContent(msg);
	}*/
	
	private void createJSONContent(String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"nickname\":\"" + this.getSenderName() +"\"");
		sb.append(",");
		sb.append("\"text\":\"" + msg + "\"");
		sb.append("}");
		setContent(sb.toString());
	}
/*
	public int getWhichRoomID() {
		return whichRoomID;
	}

	public void setWhichRoomID(int whichRoomID) {
		this.whichRoomID = whichRoomID;
	}
*/
}
