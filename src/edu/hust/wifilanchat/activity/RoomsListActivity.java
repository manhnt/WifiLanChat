package edu.hust.wifilanchat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.obj.ChatRoom;

public class RoomsListActivity extends Activity {

	ListView roomsList = null;
	Button createRoomBtn = null;

	List<ChatRoom> rooms = new ArrayList<ChatRoom>();

	ArrayAdapter<ChatRoom> adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_rooms_list);

		createRoomBtn = (Button) findViewById(R.id.create_room_btn);
		roomsList = (ListView) findViewById(R.id.list_chat_rooms);

		adapter = new ArrayAdapter<ChatRoom>(this, android.R.layout.simple_list_item_1, rooms);
		adapter.add(new ChatRoom("Room 1"));
		adapter.add(new ChatRoom("Room 2"));
		adapter.add(new ChatRoom("ManhNt's Room"));
		roomsList.setAdapter(adapter);
		adapter.add(new ChatRoom("Hello Chat"));
		adapter.add(new ChatRoom("Room 3"));
		
		roomsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(RoomsListActivity.this, ChatActivity.class);
				startActivity(intent);
			}
		});
	}
}
