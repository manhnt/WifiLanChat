package edu.hust.wifilanchat.activity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.MemberManager;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.UserPreferenceManager;
import edu.hust.wifilanchat.messages.AdvSelfMessage;
import edu.hust.wifilanchat.messages.MessageType;
import edu.hust.wifilanchat.messages.NetworkMessage;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("nickname")) {
			String newNick = sharedPreferences.getString("nickname", UserPreferenceManager.getInstance().getNickName());
			UserPreferenceManager.getInstance().setNickName(newNick);
			// Gui lai ban tin AdvSelf de thong bao toi cac may khac ve nickname moi cua minh
			List<Member> memList = MemberManager.getInstance().getMemberList();
			Iterator<Member> iter = memList.iterator();
			while (iter.hasNext()) {
				Member m = iter.next();
				AdvNewNickName r = new AdvNewNickName(m);
				new Thread(r).start();
			}
		}
	}
	
	private class AdvNewNickName implements Runnable {
		private NetworkMessage asm = new NetworkMessage(MessageType.ADV_NEW_NAME);
		private Member targetMem;
		
		private static final int SO_TIMEOUT = 5000; // Socket timeout = 5s
		private static final int SERVER_PORT = 25125;
		
		public AdvNewNickName(Member tMem) {
			this.targetMem = tMem;
		}
		
		@Override
		public void run() {
			// tao socket, ket noi den server
			Socket s = new Socket();
			InetAddress addr = targetMem.getHost().getInetAddress();
			try {
				s.connect(new InetSocketAddress(addr, SERVER_PORT), SO_TIMEOUT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				oos.flush();
				oos.writeObject(asm);
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
