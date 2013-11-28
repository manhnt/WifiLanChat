package edu.hust.wifilanchat.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.R;

public class PeopleRowAdapter extends ArrayAdapter<Member> {
	
	private Activity context;
	private List<Member> data;
	
	public PeopleRowAdapter(Activity a, List<Member> people) {
		super(a, R.layout.people_row, people);
		context = a;
		data = people;
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
