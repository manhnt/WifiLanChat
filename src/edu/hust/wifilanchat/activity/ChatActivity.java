package edu.hust.wifilanchat.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.hust.wifilanchat.ChatMessage;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.adapter.BubbleAdapter;

public class ChatActivity extends Activity {

	private Button sendButton;
	private EditText editMsgBox;
	ListView msgList = null;
	
	ArrayList<ChatMessage> listMsg = new ArrayList<ChatMessage>();
//	MessageArrayAdapter msgAdapter = null;
	BubbleAdapter bubbleAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		
		sendButton = (Button) findViewById(R.id.send_button);
		editMsgBox = (EditText) findViewById(R.id.edit_message);
		msgList = (ListView) findViewById(R.id.list_message);
		msgList.setDivider(null);
		msgList.setDividerHeight(0);
		
//		msgAdapter = new MessageArrayAdapter(this, listMsg);
		bubbleAdapter = new BubbleAdapter(this, listMsg);
		
		listMsg.add(new ChatMessage("Hello there!", new Member("Alex"), true));
		listMsg.add(new ChatMessage("Hi there!", new Member("Phil"), false));
		listMsg.add(new ChatMessage("What's up?", new Member("Alex"), true));
		listMsg.add(new ChatMessage("Nothing's up.", new Member("Cam"), false));
		listMsg.add(new ChatMessage("My name is ABC", new Member("ABC"), true));
		listMsg.add(new ChatMessage("Nice to meet you", new Member("ABC"), true));
		listMsg.add(new ChatMessage("Welcome!", new Member("XYZ"), false));
		listMsg.add(new ChatMessage("Bye", new Member("Alex"), true));
		
		msgList.setAdapter(bubbleAdapter);
		msgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String s = editMsgBox.getText().toString().trim();
				if (!s.equals("")) {
					ChatMessage m = new ChatMessage(s, new Member("Me"), true);
					bubbleAdapter.add(m);
				}
			}
		});
	}
}
