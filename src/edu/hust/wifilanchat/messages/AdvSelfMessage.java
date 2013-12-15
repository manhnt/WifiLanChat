package edu.hust.wifilanchat.messages;

import org.json.simple.JSONArray;

import edu.hust.wifilanchat.MyRoomsManager;
import edu.hust.wifilanchat.UserPreferenceManager;

public class AdvSelfMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private String myNickName;
//	private String roomListStr;
//	private String jsonContent;
	
	public AdvSelfMessage() {
		super(MessageType.ADV_SELF);
		myNickName = UserPreferenceManager.getInstance().getNickName();
		this.setSender(UserPreferenceManager.getInstance().getMe());
		createJSONContent();
	}

	private void createJSONContent() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"nickname\":");
		sb.append("\"" + myNickName + "\"");
		sb.append(",");
		sb.append("\"room\":");
		sb.append(getJSONRoomArray());
		sb.append("}");
		
		setContent(sb.toString());
	}
	
	private String getJSONRoomArray() {
		return JSONArray.toJSONString(MyRoomsManager.getInstance().getMyRoomList());
	}
}
