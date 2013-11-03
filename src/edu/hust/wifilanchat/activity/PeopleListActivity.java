package edu.hust.wifilanchat.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.adapter.PeopleRowAdapter;

public class PeopleListActivity extends ListActivity {

	ArrayList<Member> people = new ArrayList<Member>();
	PeopleRowAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_list);
		
		adapter = new PeopleRowAdapter(this, people);
		Member p1 = new Member("Ng The Manh");
		Member p2 = new Member("Michael Jackson");
		Member p3 = new Member("Batman");
		Member p4 = new Member("Bruce Wayne");
		adapter.add(p1); adapter.add(p2); adapter.add(p3); adapter.add(p4);
		
		setListAdapter(adapter);
	}
}
