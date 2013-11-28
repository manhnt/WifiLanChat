package edu.hust.wifilanchat.activity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.hust.wifilanchat.ChatRoom;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MemberManager;
import edu.hust.wifilanchat.MyRoomsManager;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.RoomManager;
import edu.hust.wifilanchat.ServerService;
import edu.hust.wifilanchat.UserPreferenceManager;
import edu.hust.wifilanchat.messages.AdvNewRoomMessage;
import edu.hust.wifilanchat.messages.AdvRoomMessage;
import edu.hust.wifilanchat.messages.AllowJoinMessage;
import edu.hust.wifilanchat.messages.ChatMessage;
import edu.hust.wifilanchat.messages.JSONTag;
import edu.hust.wifilanchat.messages.MessageType;
import edu.hust.wifilanchat.messages.NetworkMessage;
import edu.hust.wifilanchat.messages.RequestJoinMessage;
import edu.hust.wifilanchat.messages.TextMessage;
import edu.hust.wifilanchat.networking.HostBean;

public class RoomsListActivity extends Activity {
	private static final String TAG = "WiFiLANChat";

	private static final int SO_TIMEOUT = 5000; // Socket timeout = 5s
	private static final int SERVER_PORT = 25125;

	ListView roomsList = null;
	Button createRoomBtn = null;
	LocalBroadcastManager broadcaster;
	
	List<ChatRoom> rooms = RoomManager.getInstance().getRoomList();
	
	ArrayAdapter<ChatRoom> adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_rooms_list);

		createRoomBtn = (Button) findViewById(R.id.create_room_btn);
		roomsList = (ListView) findViewById(R.id.list_chat_rooms);

		adapter = new ArrayAdapter<ChatRoom>(this, android.R.layout.simple_list_item_1, rooms);
		roomsList.setAdapter(adapter);
		roomsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ChatRoom room = rooms.get(position);
				// send REQ_JOIN_ROOM message to room's master
				if (!room.isMine()) {
//					RequestJoinRoom req = new RequestJoinRoom(room, room.getServer());
					ClientChatConnection req = new ClientChatConnection(room, room.getServer(), broadcaster);
					new Thread(req).start();
					Log.i(TAG, "Started RequestJoinRoom thread");
				} else {
					// Chuyen sang ChatActivity vao phong cua minh
					room.setJoining(true);
					Intent i = new Intent(RoomsListActivity.this, ChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("server", room.getServer());
					bundle.putBoolean("ismine", true);
					bundle.putInt("roomid", room.getId());
					i.putExtras(bundle);
					startActivity(i);
				}
			}
		});
		
		createRoomBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createRoomDialog();
			}
		});
		
		broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.room_list, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			// send REQ_UPDATE_ROOMS message
			sendRequestUpdateMessage();
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void sendRequestUpdateMessage() {
		NetworkMessage msg = new NetworkMessage(MessageType.REQ_UPDATE_ROOMS);
		msg.setSender(UserPreferenceManager.getInstance().getMe());
		List<Member> members = MemberManager.getInstance().getMemberList();
		for (Member member : members) {
			new Thread(new RequestUpdateRoom(msg, member)).start();
		}
	}
	
	protected void createRoomDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage("Create new room");
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.create_room_dialog, null);
		final EditText edit = (EditText) v.findViewById(R.id.room_name);
		b.setView(v);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String str = edit.getText().toString();
				String trim = str.trim();
				if (!trim.equals(""))
					createNewRoom(str);
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		b.setCancelable(true);
		AlertDialog d = b.create();
		d.show();
	}
	protected void createNewRoom(String str) {
		Log.i(TAG, "creating new room with name " + str);
		
		ChatRoom r = ChatRoom.createNewRoom(str);
		r.setMine(true); r.setJoining(true);
		// Add this room to MyRoomsManager and RoomManager
		MyRoomsManager.getInstance().addRoom(r);
		RoomManager.getInstance().addRoom(r);
		adapter.notifyDataSetChanged();
		// Gui ban tin ADV_ROOM toi cac may khac
		advertiseNewRoom(r);
	}
	
	private void advertiseNewRoom(ChatRoom r) {
		Log.i(TAG, "Advertising new room");
		AdvNewRoomMessage msg = new AdvNewRoomMessage(r);
		msg.setSender(UserPreferenceManager.getInstance().getMe());
		List<Member> members = MemberManager.getInstance().getMemberList();
		if (members.isEmpty()) {
			Log.i(TAG, "Member list is empty.");
			return;
		}
		
		for (int i = 0; i < members.size(); i++) {
			Member member = members.get(i);
			AdvNewRoom adv = new AdvNewRoom(msg, member);
			new Thread(adv).start();
		}
	}

	private class AdvNewRoom implements Runnable {
		
		// private String serverAddr;
		private Member toMember;
		private NetworkMessage msgToSend;

		/*
		 * public AdvNewRoom(NetworkMessage msg, String addr) { serverAddr =
		 * addr; msgToSend = msg; }
		 */
		public AdvNewRoom(NetworkMessage msg, Member toMem) {
			this.toMember = toMem;
			msgToSend = msg;
		}

		@Override
		public void run() {
			Log.i(TAG, "Advertise new room to member " + toMember.getName());
			
			Socket s = new Socket();
			try {
				InetAddress addr = toMember.getHost().getInetAddress();
				s.connect(new InetSocketAddress(addr, SERVER_PORT), SO_TIMEOUT);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						s.getOutputStream());
				oos.flush();
				// ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				oos.writeObject(msgToSend);
				oos.close();
			} catch (IOException e) {
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

	}
	
	private class RequestUpdateRoom implements Runnable {
		private Member toMember;
		private NetworkMessage msgToSend;

		public RequestUpdateRoom(NetworkMessage msg, Member member) {
			msgToSend = msg;
			toMember = member;
		}

		@Override
		public void run() {
			Log.i(TAG, "Sending request update to " + toMember.getName());

			Socket s = new Socket();
			try {
				InetAddress addr = toMember.getHost().getInetAddress();
				s.connect(new InetSocketAddress(addr, SERVER_PORT), SO_TIMEOUT);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				oos.flush();
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				// Send request
				oos.writeObject(msgToSend);
				// Read response
				try {
					NetworkMessage response = (NetworkMessage)ois.readObject();
					if (response.getType() == MessageType.ADV_ROOM) {
						parseResponse(response);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				finally {
					oos.close();
					ois.close();
				}
			} catch (IOException e) {
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

		private void parseResponse(NetworkMessage message) {
			AdvRoomMessage msg = (AdvRoomMessage) message;
			Member mem = msg.getSender();
			String jsonContent = msg.getContent();
			Log.i(TAG, "Parsing response of RequestUpdateRoom: " + jsonContent);
			
			// Remove old rooms from room list before update room list
			removeOldRoom();
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(jsonContent);
				JSONArray rooms = jsonObj.getJSONArray(JSONTag.TAG_ROOM);
				for (int i = 0; i < rooms.length(); i++) {
					JSONObject r = rooms.getJSONObject(i);
					int id = r.getInt(JSONTag.TAG_ID);
					String name = r.getString(JSONTag.TAG_NAME);
					// TODO: kiem tra xem phong nay da co chua. Neu co thi khong update
					RoomManager.getInstance().addRoom(new ChatRoom(name, mem, id));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
/*	
	private class RequestJoinRoom implements Runnable {
			ChatRoom room;
			Member server;
			RequestJoinMessage msg;
			public RequestJoinRoom(ChatRoom room, Member server) {
				this.room = room;
				this.server = server;
				this.msg = new RequestJoinMessage(room, server);
				this.msg.setSender(UserPreferenceManager.getInstance().getMe());
			}
			
			@Override
			public void run() {
				Socket s = new Socket();
				try {
					InetAddress addr = server.getHost().getInetAddress();
					s.connect(new InetSocketAddress(addr, SERVER_PORT), SO_TIMEOUT);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					oos.flush();
					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
					// Send request
					oos.writeObject(msg);
	//				oos.flush();
					Log.i(TAG, "Sent RequestJoinMessage to member " + server.getName());
					// Read response
					try {
						Log.i(TAG, "Trying to read response for REQ_JOIN_ROOM from server");
						NetworkMessage response = (NetworkMessage) ois.readObject();
						Log.i(TAG, "Got it! The response for REQ_JOIN_ROOM.");
						if (response != null && response.getType() == MessageType.ALLOW_FOR_JOIN) {
							AllowJoinMessage allow = (AllowJoinMessage) response;
							Log.i(TAG, "Start parsing AllowJoinMessage response from server");
							// TODO: phan tich list member trong room va chuyen activity sang ChatActivity
							// TODO: tao ket noi chat voi server
							parseResponse(allow);
							Log.i(TAG, "RequestJoinRoom.parseRespone() done.");
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									room.setJoining(true);
									Intent i = new Intent(RoomsListActivity.this, ChatActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("server", room.getServer());
									bundle.putBoolean("ismine", false);
									bundle.putInt("roomid", room.getId());
									i.putExtras(bundle);
									startActivity(i);
								}
							});
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					finally {
						oos.close();
						ois.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
			private void parseResponse(AllowJoinMessage allow) {
				String jsonContent = allow.getContent();
				Log.i(TAG, "Content of AllowJoinMessage: " + jsonContent);
				try {
					JSONObject jsonObj = new JSONObject(jsonContent);
					JSONArray inRoomMems = jsonObj.getJSONArray(JSONTag.TAG_MEMBER);
					for (int i = 0; i < inRoomMems.length(); i++) {
						JSONObject o = inRoomMems.getJSONObject(i);
						String name = o.getString(JSONTag.TAG_NAME);
						String ip = o.getString(JSONTag.TAG_ID);
						HostBean host = new HostBean(); host.setIpAddress(ip);
						Member mem = new Member(name, host);
						room.addMember(mem);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	*/
	/**
	 * Remove all the rooms which is not mine from RoomManager room list
	 */
	private void removeOldRoom() {
		List<ChatRoom> rooms = RoomManager.getInstance().getRoomList();
		Iterator<ChatRoom> i = rooms.iterator();
		while (i.hasNext()) {
			ChatRoom r = i.next();
			if (!r.isMine()) {
				i.remove();
			}
		}
	}
	
	public class ClientChatConnection implements Runnable {
		private static final String TAG = "WiFiLANChat";
		private static final int SERVER_PORT = 25125;

		ChatRoom room;
		Member server;
		private Socket conn = new Socket();
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		private boolean connecting = true;
		protected LocalBroadcastManager broadcaster;
		
		public ClientChatConnection(ChatRoom room, Member server, LocalBroadcastManager bManager) {
			this.room = room;
			this.server = server;
			this.broadcaster = bManager;
		}

		@Override
		public void run() {
			try {
				conn.connect(new InetSocketAddress(server.getHost()
						.getInetAddress(), SERVER_PORT));
				Log.i(TAG, "Connected to room's server");

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Cannot connect to server");
			}

			try {
				oos = new ObjectOutputStream(conn.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(conn.getInputStream());
				
				// Send request
				RequestJoinMessage msg = new RequestJoinMessage(room, server);
				msg.setSender(UserPreferenceManager.getInstance().getMe());
				oos.writeObject(msg);
				Log.i(TAG, "Sent RequestJoinMessage to member " + server.getName());
				
				// Read response
				try {
					Log.i(TAG, "Trying to read response for REQ_JOIN_ROOM from server");
					NetworkMessage response = (NetworkMessage) ois.readObject();
					Log.i(TAG, "Got it! The response for REQ_JOIN_ROOM.");
					
					if (response != null && response.getType() == MessageType.ALLOW_FOR_JOIN) {
						AllowJoinMessage allow = (AllowJoinMessage) response;
						Log.i(TAG, "Start parsing AllowJoinMessage response from server");
						// Phan tich list member trong room va chuyen activity
						parseResponse(allow);
						Log.i(TAG, "RequestJoinRoom.parseRespone() done.");
						// Chuyen activity
						startChatActivity();
						
						/* Wait for and read TextMessage from server */
						while (connecting) {
							Object objMsg = ois.readObject();
							if (objMsg instanceof TextMessage) {
								// Tin nhan text tu server gui den
								// Cap nhat len UI va forward cho cac member khac trong room tru client da gui
								TextMessage textMessage = (TextMessage) objMsg;
								String jsonContent = textMessage.getContent();
								Member sender = textMessage.getSender();
								Log.i(TAG, jsonContent);
								try {
									JSONObject jsonObj = new JSONObject(jsonContent);
//									String senderNick = jsonObj.getString(JSONTag.TAG_NICK);
									String text = jsonObj.getString(JSONTag.TAG_TEXT);
									ChatMessage c = new ChatMessage(text, sender, false);
									updateUI(c);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					oos.close();
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private void updateUI(ChatMessage c) {
			Intent i = new Intent(ServerService.UPDATE_MESSAGE_UI);
			Bundle b = new Bundle();
			b.putSerializable("message", c);
			i.putExtras(b);
			broadcaster.sendBroadcast(i);
		}

		private void startChatActivity() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					room.setJoining(true);
					Intent i = new Intent(RoomsListActivity.this, ChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("server", room.getServer());
					bundle.putBoolean("ismine", false);
					bundle.putInt("roomid", room.getId());
					i.putExtras(bundle);
					startActivity(i);
				}
			});
		}

		private void parseResponse(AllowJoinMessage allow) {
			String jsonContent = allow.getContent();
			Log.i(TAG, "Content of AllowJoinMessage: " + jsonContent);
			try {
				JSONObject jsonObj = new JSONObject(jsonContent);
				JSONArray inRoomMems = jsonObj.getJSONArray(JSONTag.TAG_MEMBER);
				for (int i = 0; i < inRoomMems.length(); i++) {
					JSONObject o = inRoomMems.getJSONObject(i);
					String name = o.getString(JSONTag.TAG_NAME);
					String ip = o.getString(JSONTag.TAG_ID);
					HostBean host = new HostBean();
					host.setIpAddress(ip);
					Member mem = new Member(name, host);
					room.addMember(mem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public boolean isConnecting() {
			return connecting;
		}

		public void setConnecting(boolean connecting) {
			this.connecting = connecting;
		}
	}

}

