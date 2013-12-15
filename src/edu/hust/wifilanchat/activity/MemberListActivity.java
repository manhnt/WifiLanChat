package edu.hust.wifilanchat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MemberManager;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.adapter.PeopleRowAdapter;

public class MemberListActivity extends ListActivity {

	List<Member> members = MemberManager.getInstance().getMemberList();
	PeopleRowAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_list);

		if (!members.isEmpty())
			adapter = new PeopleRowAdapter(this, members);
		else {
			members = new ArrayList<Member>();
			adapter = new PeopleRowAdapter(this, members);
			Member p1 = new Member("<Empty>");
			members.add(p1);
		}
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mem_list_activity, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh_mem_list:
			// TODO: gui ban tin yeu cau cap nhat danh sach member
			adapter.notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
