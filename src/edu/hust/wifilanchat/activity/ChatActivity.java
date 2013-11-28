package edu.hust.wifilanchat.activity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.hust.wifilanchat.ChatRoom;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MyRoomsManager;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.ServerService;
import edu.hust.wifilanchat.UserPreferenceManager;
import edu.hust.wifilanchat.adapter.BubbleAdapter;
import edu.hust.wifilanchat.messages.ChatMessage;
import edu.hust.wifilanchat.messages.JSONTag;
import edu.hust.wifilanchat.messages.MessageType;
import edu.hust.wifilanchat.messages.NetworkMessage;
import edu.hust.wifilanchat.messages.TextMessage;

public class ChatActivity extends Activity {
	private static final String TAG = "WiFiLANChat";
	private static final int SERVER_PORT = 25125;
	
	private Button sendButton;
	private EditText editMsgBox;
	ListView msgList = null;
	
//	List<ChatRoom> rooms = RoomManager.getInstance().getCurrentJoiningRoom();
//	ChatRoom chatRoom = rooms.get(0);
	ChatRoom thisRoom = null;
	List<ChatRoom> myRoomList = MyRoomsManager.getInstance().getMyRoomList();
//	Member roomServer = chatRoom.getServer();
	
	boolean isServer = false;
	int roomID;
	Member roomServer = null;
	Member me = UserPreferenceManager.getInstance().getMe();
	ChatConnectionToServer connection;
	
	ArrayList<ChatMessage> listMsg = new ArrayList<ChatMessage>();
	BubbleAdapter bubbleAdapter = null;
	BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		Log.i(TAG, "ChatActivity.onCreate");
		
		if (!myRoomList.isEmpty()) thisRoom = myRoomList.get(0);
		
		createGUI();
		
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle b = intent.getExtras();
				ChatMessage c = (ChatMessage) b.getSerializable("message");
				bubbleAdapter.add(c);
			}
			
		};
		
		Intent intent = getIntent();
		Bundle intentExtras = intent.getExtras();
		isServer = intentExtras.getBoolean("ismine");
		roomID = intentExtras.getInt("roomid");
		roomServer = (Member) intentExtras.getSerializable("server");
		if (!isServer) {
			// I'm a client in this room. Just connect to server
			connection = new ChatConnectionToServer(roomServer);
			new Thread(connection).start();
		}
		else {
			// TODO: I'am the server of this room
			
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ServerService.UPDATE_MESSAGE_UI));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	
	private void createGUI() {
		sendButton = (Button) findViewById(R.id.send_button);
		editMsgBox = (EditText) findViewById(R.id.edit_message);
		msgList = (ListView) findViewById(R.id.list_message);
		msgList.setDivider(null);
		msgList.setDividerHeight(0);

		// msgAdapter = new MessageArrayAdapter(this, listMsg);
		bubbleAdapter = new BubbleAdapter(this, listMsg);

		msgList.setAdapter(bubbleAdapter);
		msgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = editMsgBox.getText().toString().trim();
				// Clear EditText content
				editMsgBox.setText("");
				if (!s.equals("")) {
					if (isServer) {
						// Send a TextMessage to all client member in this room
						TextMessage tmsg = new TextMessage(s, me);
						Iterator<Member> i = thisRoom.getMembers().iterator();
						while (i.hasNext()) {
							Member m = i.next();
							ObjectOutputStream oos = m.getStream();
							try {
								Log.i(TAG, "Start send TextMessage to member "
										+ m.getName());
								oos.writeObject(tmsg);
								Log.i(TAG, "Sent that TextMessage");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// Update a ChatMessage to adapter
						ChatMessage m = new ChatMessage(s, me, true);
						bubbleAdapter.add(m);
					} else {
						// Update a ChatMessage to adapter
						ChatMessage m = new ChatMessage(s, me, true);
						bubbleAdapter.add(m);
						// Send a TextMessage to the server of this room
						connection.sendMessage(s);
					}
				}
			}
		});
	}

	private class ChatConnectionToServer implements Runnable {
		private Member server;
		private Socket conn = new Socket();
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		private boolean connecting = true;
		
		public ChatConnectionToServer(Member server) {
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				conn.connect(new InetSocketAddress(server.getHost().getInetAddress(), SERVER_PORT));
				Log.i(TAG, "Connected to room's server");

				oos = new ObjectOutputStream(conn.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(conn.getInputStream());
				
				// Thong bao cho server bat dau ket noi cho chat session
				NetworkMessage startChatSs = new NetworkMessage(null, me, MessageType.START_CHAT);
				oos.writeObject(startChatSs);
				
				// Wait for input
				while (connecting) {
					try {
						Object obj = ois.readObject();
						if (obj instanceof TextMessage) {
							// TODO: phan tich ban tin, cap nhat giao dien
							TextMessage message = (TextMessage) obj;
							Log.i(TAG, "Received TextMessage from server");
							parseAndUpdateUI(message);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (IOException e) {
					Log.e(TAG, "Unable to close connection");
					e.printStackTrace();
				}
			}
		}

		private void parseAndUpdateUI(TextMessage message) {
			String jsonContent = message.getContent();
			Log.i(TAG, "Parsing content of TextMessage: " + jsonContent);
			Member sender = message.getSender();
			try {
				JSONObject jsonObj = new JSONObject(jsonContent);
				String senderNick = jsonObj.getString(JSONTag.TAG_NICK);
				String content = jsonObj.getString(JSONTag.TAG_TEXT);
				final ChatMessage msg = new ChatMessage(content, sender, false);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						bubbleAdapter.add(msg);
						bubbleAdapter.notifyDataSetChanged();
					}
				});
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		public void sendMessage(String str) {
			TextMessage msg = new TextMessage(str, me);
			
			try {
				oos.writeObject(msg);
			} catch (IOException e) {
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
