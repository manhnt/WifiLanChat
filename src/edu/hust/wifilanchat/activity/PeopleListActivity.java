package edu.hust.wifilanchat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MemberManager;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.adapter.PeopleRowAdapter;

public class PeopleListActivity extends ListActivity {

	List<Member> people = MemberManager.getInstance().getMemberList();
	PeopleRowAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_list);
		
		/*HostBean host = new HostBean(); host.setIpAddress("192.168.0.102");
		Member m = new Member("Manh", host);
		MemberManager.getInstance().addMember(m);*/

		if (!people.isEmpty())
			adapter = new PeopleRowAdapter(this, people);
		else {
			people = new ArrayList<Member>();
			adapter = new PeopleRowAdapter(this, people);
			Member p1 = new Member("Manh");
			people.add(p1);
		}
		setListAdapter(adapter);
	}
}
