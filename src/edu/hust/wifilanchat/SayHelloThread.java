package edu.hust.wifilanchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.hust.wifilanchat.messages.AdvSelfMessage;
import edu.hust.wifilanchat.messages.JSONTag;
import edu.hust.wifilanchat.messages.MessageType;
import edu.hust.wifilanchat.messages.NetworkMessage;
import edu.hust.wifilanchat.networking.HostBean;

public class SayHelloThread implements Runnable {
	private static final String TAG = "WiFiLANChat";
	
	private String serverAddr;
	private HostBean toHost;
	//TODO: choose a reasonable timeout value
	private static final int TIMEOUT = 5000;	// Socket timeout = 5s
	private static final int SERVER_PORT = 25125;
	
	UserPreferenceManager uManager = UserPreferenceManager.getInstance();
	
	public SayHelloThread(String addr) {
		serverAddr = addr;
	}

	public SayHelloThread(HostBean toHost) {
		this.toHost = toHost;
		Log.i(TAG, "Trying to say hello to host " + toHost.getIpAddress());
	}
	
	@Override
	public void run() {
		// TODO: tao socket, ket noi den server, gui ban tin Hello world
		// tao socket, ket noi den server
		Socket s = new Socket();

		try {
//			InetAddress addr = InetAddress.getByName(serverAddr);
			InetAddress addr = toHost.getInetAddress();
			s.connect(new InetSocketAddress(addr, SERVER_PORT), TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			
			String msg = uManager.getNickName();
			NetworkMessage hello = new NetworkMessage(msg, MessageType.HELLO_WORLD);
			hello.setSender(uManager.getMe());
			// gui ban tin hello world den server
			oos.writeObject(hello);
			
			try {
				Object obj = ois.readObject();
				if (obj instanceof AdvSelfMessage) {
					Log.i(TAG, "AdvSelf response from " + s.getInetAddress().toString());
					AdvSelfMessage fromServer = (AdvSelfMessage) obj;
					parseResponse(fromServer);
				}
				else Log.i(TAG, "Strange response from " + s.getInetAddress().toString() + ": " + obj.toString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				oos.close();
				ois.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			// Close socket
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Parse the response AdvSelfMessage from server
	 * @param fromServer
	 */
	private void parseResponse(AdvSelfMessage fromServer) {
		Member mem = fromServer.getSender();
		HostBean host = mem.getHost();
		String serverip = host.getIpAddress();
		Log.i(TAG, "There's response from server " + serverip + ". I'm parsing it right now");
		
		String content = fromServer.getContent();
		Log.i(TAG, content);
		MemberManager mManager = MemberManager.getInstance();
		synchronized (mManager) {
			mManager.addMember(mem);
		}
		try {
			JSONObject jsonObj = new JSONObject(content);
//			String serverNickname = jsonObj.getString(TAG_NICK);
			
			JSONArray rooms = jsonObj.getJSONArray(JSONTag.TAG_ROOM);
			for (int i = 0; i < rooms.length(); i++) {
				JSONObject o = rooms.getJSONObject(i);
				int id = Integer.valueOf(o.getString(JSONTag.TAG_ID));
				String roomName = o.getString(JSONTag.TAG_NAME);
				Log.i(TAG, roomName);
				RoomManager.getInstance().addRoom(new ChatRoom(roomName, mem, id));
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "Exception in parsing message content from server");
			e.printStackTrace();
		}
		
	}

}
