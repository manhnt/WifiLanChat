package edu.hust.wifilanchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.hust.wifilanchat.messages.AdvRoomMessage;
import edu.hust.wifilanchat.messages.AdvSelfMessage;
import edu.hust.wifilanchat.messages.AllowJoinMessage;
import edu.hust.wifilanchat.messages.ChatMessage;
import edu.hust.wifilanchat.messages.JSONTag;
import edu.hust.wifilanchat.messages.MessageType;
import edu.hust.wifilanchat.messages.NetworkMessage;
import edu.hust.wifilanchat.messages.RequestJoinMessage;
import edu.hust.wifilanchat.messages.TextMessage;

public class ServerThread implements Runnable {

	private String DEBUG_TAG = "ServerThread";
	
	private ServerSocket serverSocket = null;
	private static final int SERVER_PORT = 25125;
	private boolean listening = true;
	private int clientCount = 0;
	private boolean openServerOK = false;
	
	private ServerService service;
	
	public ServerThread(ServerService service) {
		this.service = service;
	}
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			openServerOK = true;
			Log.i(DEBUG_TAG, "Open port successfully!");
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "Could not open server port");
			e.printStackTrace();
		}
		
		while (openServerOK && listening) {
			try {
				Socket s = serverSocket.accept();
				clientCount++;
				Log.d(DEBUG_TAG, "Spawning another thread for new client");
				Runnable r = new ClientHandlder(s, service);
				Thread t = new Thread(r);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getClientCount() {
		return clientCount;
	}
}

class ClientHandlder implements Runnable {
	
	private static final String TAG = "WiFiLANChat";
	private boolean done = false;
	private Socket incoming;
	private static ServerService service;
	
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	
	public ClientHandlder(Socket socket, ServerService service) {
		incoming = socket;
		ClientHandlder.service = service;
	}
	
	@Override
	public void run() {
		Log.d("ClientHandler", "Client has been accepted");
		
		try {
			InputStream is = incoming.getInputStream();
			OutputStream os = incoming.getOutputStream();
//			Scanner input = new Scanner(is);
//			PrintWriter output = new PrintWriter(os);
			oos = new ObjectOutputStream(os);
			oos.flush();
			ois = new ObjectInputStream(is);
						
			// keep listening and handle client's message
			while (!done) {
				try {
					NetworkMessage netMsg = (NetworkMessage) ois.readObject();
					handleMessage(netMsg);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(NetworkMessage msg) {
		MessageType type = msg.getType();
		switch (type) {
		case HELLO_WORLD: // HELLO_WORLD message from client
			Member member = msg.getSender();
			MemberManager manager = MemberManager.getInstance();
			synchronized (manager) {
				// If this member has been in my member list, remove him firstly.
				removeOldIfDuplicate(member);
				// Then add him again into the list, in case of he has changed his nickname.
				MemberManager.getInstance().addMember(member);
				Log.i(TAG, "Added one new member: " + member.getName());
			}
			// send back ADV_SELF message
			NetworkMessage response = new AdvSelfMessage();
			response.setSender(UserPreferenceManager.getInstance().getMe());
			try {
				oos.writeObject(response);
				oos.flush();
				Log.i(TAG, "Sent AdvSelfMessage response to client");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;

		case ADV_NEW_ROOM:
			// Update room list
			Member roomMaster = msg.getSender();
//			RoomManager rManager = RoomManager.getInstance();
			String content = msg.getContent();
			try {
				JSONObject jsonObj = new JSONObject(content);
				int roomId = Integer.valueOf(jsonObj.getString(JSONTag.TAG_ID));
				String roomName = jsonObj.getString(JSONTag.TAG_NAME);
				RoomManager.getInstance().addRoom(new ChatRoom(roomName, roomMaster, roomId));
				Log.i(TAG, "New room from " + roomMaster.getName() + ", name: " + roomName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case ADV_NEW_NAME:
			// TODO: phan tich 
			Member memWithNewName = msg.getSender();
			MemberManager manager1 = MemberManager.getInstance();
			synchronized (manager1) {
				// If this member has been in my member list, remove him firstly.
				removeOldIfDuplicate(memWithNewName);
				// Then add him again into the list, in case of he has changed his nickname.
				MemberManager.getInstance().addMember(memWithNewName);
				Log.i(TAG, "Added one new member: " + memWithNewName.getName());
			}
			
			break;
			
		case REQ_JOIN_ROOM:
			// send back ALLOW_FOR_JOIN message
			// TODO: tao ket noi voi client
			Log.i(TAG, "Received REQ_JOIN_ROOM from client.");
			RequestJoinMessage rq = (RequestJoinMessage) msg;
			int which = rq.getRoomID(); // which room
			ChatRoom room = MyRoomsManager.getInstance().getRoomById(which);
			if (room != null) {
				Log.i(TAG, "roomID = " + which + ", room name: " + room.toString());
				/* Get the sender then add him to member list of this room */
				Member m = msg.getSender();
				m.setStream(oos);
				room.addMember(m);
				
				/* Send back AllowJoinMessage */
				NetworkMessage allow = new AllowJoinMessage(which);
				allow.setSender(UserPreferenceManager.getInstance().getMe());
				try {
					Log.i(TAG, "Trying to send AllowJoinMessage to client");
					oos.writeObject(allow);
					Log.i(TAG, "Sent AllowJoinMessage to client.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Log.i(TAG, "room == null");
					oos.writeObject(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
			
		case TEXT_MESSAGE:
			// Tin nhan text tu client gui den
			// Cap nhat len UI va forward cho cac member khac trong room tru client da gui
			TextMessage textMessage = (TextMessage) msg;
			String jsonContent = msg.getContent();
			Member sender = msg.getSender();
			Log.i(TAG, jsonContent);
			try {
				JSONObject jsonObj = new JSONObject(jsonContent);
//				String senderNick = jsonObj.getString(JSONTag.TAG_NICK);
				String text = jsonObj.getString(JSONTag.TAG_TEXT);
				ChatMessage c = new ChatMessage(text, sender, false);
				updateUI(c);
				forwardMessage(textMessage);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case START_CHAT:
			// TODO:
//			Member m = msg.getSender();
			
			break;
		
		case REQ_UPDATE_ROOMS:
			// Gui ban tin AdvRoomMessage toi client
			AdvRoomMessage arMsg = new AdvRoomMessage();
			arMsg.setSender(UserPreferenceManager.getInstance().getMe());
			try {
				oos.writeObject(arMsg);
				Log.i(TAG, "Sent AdvRoomMessage to client");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Error in send AdvRoomMessage");
			}
			break;
			
		default:
			Log.d(TAG, "Receive message of type " + type.toString());
			break;
		}
	}

	private void removeOldIfDuplicate(Member member) {
		String ip = member.getHost().getIpAddress();
		Iterator<Member> i = MemberManager.getInstance().getMemberList().iterator();
		while (i.hasNext()) {
			Member m = i.next();
			if (m.getHost().getIpAddress().equals(ip)){
				i.remove();
				Log.w(TAG, "Duplicated member " + m.getName() + "! Removed");
			}
		}
	}

	private void updateUI(ChatMessage c) {
		service.updateNewMessageToUI(c);
	}

	private void forwardMessage(TextMessage textMessage) {
		String senderIp = textMessage.getSender().getHost().getIpAddress();
		List<Member> mems = MemberManager.getInstance().getMemberList();
		for (int i = 0; i < mems.size(); i++ ) {
			Member mem = mems.get(i);
			String ip = mem.getHost().getIpAddress();
			if (!ip.equals(senderIp)) {
				// TODO: forward den tung member trong room
			}
		}
	}
}