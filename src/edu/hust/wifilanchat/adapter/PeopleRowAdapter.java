package edu.hust.wifilanchat.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.obj.Member;

public class PeopleRowAdapter extends ArrayAdapter<Member> {
	
	private Activity context;
	private ArrayList<Member> data;
	
	public PeopleRowAdapter(Activity a, ArrayList<Member> list) {
		super(a, R.layout.people_row, list);
		context = a;
		data = list;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PeopleRowHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();
			row = inflater.inflate(R.layout.people_row, null);
			
			holder = new PeopleRowHolder(row);
			row.setTag(holder);
		}
		else
			holder = (PeopleRowHolder) row.getTag();
		
		holder.name.setText(data.get(position).toString());
		return row;
	}
	
	static class PeopleRowHolder {
		TextView name;
		
		public PeopleRowHolder(View row) {
			name = (TextView) row.findViewById(R.id.person_name);
		}
	}
}
