package edu.hust.wifilanchat.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.messages.ChatMessage;

public class BubbleAdapter extends ArrayAdapter<ChatMessage> {
	private Context context;
	private ArrayList<ChatMessage> data;
	private LinearLayout outterWrapper = null;
	private LinearLayout innerWrapper = null;
	
	public BubbleAdapter(Context mContext, ArrayList<ChatMessage> pData) {
		super(mContext, R.layout.message_row, pData);
		context = mContext;
		data = pData;
	}
	
	@Override
	public void add(ChatMessage object) {
		data.add(object);
		this.notifyDataSetChanged();
//		super.add(object);
	}
	
	@Override
	public ChatMessage getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BubbleHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			row = inflater.inflate(R.layout.message_row, parent, false);
			holder = new BubbleHolder(row);
			row.setTag(holder);
		}
		else
			holder = (BubbleHolder) row.getTag();
		
		outterWrapper = (LinearLayout) row.findViewById(R.id.outter_wrapper);
		innerWrapper = (LinearLayout) row.findViewById(R.id.inner_wrapper);
		ChatMessage msg = data.get(position);
		
		holder.sender.setText(msg.getSenderName());
		holder.content.setText(msg.getContent());
		if (msg.isLeft()) {
			innerWrapper.setBackgroundResource(R.drawable.bubble_yellow);
			outterWrapper.setGravity(Gravity.LEFT);
		} else {
			innerWrapper.setBackgroundResource(R.drawable.bubble_green);
			outterWrapper.setGravity(Gravity.RIGHT);
		}
		
		return row;
	}
	
	static class BubbleHolder {
		TextView sender;
		TextView content;
		
		public BubbleHolder(View row) {
			sender = (TextView) row.findViewById(R.id.sender_name);
			content  = (TextView) row.findViewById(R.id.content);
		}
	}
}
