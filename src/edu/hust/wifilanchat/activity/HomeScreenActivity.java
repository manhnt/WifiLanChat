package edu.hust.wifilanchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.hust.wifilanchat.R;

public class HomeScreenActivity extends Activity {

	public static final int CHANGE_WIFI_SETTING = 0;
	Button viewChatRoomListBtn = null;
	Button viewPeopleBtn = null;
	Button settingBtn = null;
	
	private ProgressDialog pDialog;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (pDialog != null)
				pDialog.dismiss();
			viewChatRoomListBtn.setEnabled(true);
			viewPeopleBtn.setEnabled(true);
			settingBtn.setEnabled(true);
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		viewChatRoomListBtn = (Button) findViewById(R.id.view_chat_rooms_btn);
		viewPeopleBtn = (Button) findViewById(R.id.view_people_btn);
		settingBtn = (Button) findViewById(R.id.settings_btn);
		
		viewChatRoomListBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RoomsListActivity.class);
				startActivity(intent);
			}
		});
		
		viewPeopleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PeopleListActivity.class);
				startActivity(intent);
			}
		});
		
		settingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		viewChatRoomListBtn.setEnabled(false);
		viewPeopleBtn.setEnabled(false);
		settingBtn.setEnabled(false);
		DemoInitialTask task = new DemoInitialTask();
		task.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

	private class DemoInitialTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			// TODO: display progress dialog for 5 seconds
			pDialog = new ProgressDialog(HomeScreenActivity.this);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.setTitle("Processing");
			pDialog.setMessage("Scanning...");
			pDialog.show();
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessage(msg);
		}
	}
	
	private class InitialTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO: display progress dialog for 5 seconds
			pDialog = new ProgressDialog(HomeScreenActivity.this);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.setTitle("Processing");
			pDialog.setMessage("Scanning...");
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			checkWifiConnection();
			scanDevices();
			introduceSelf();
			return null;
		}

		private void checkWifiConnection() {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			boolean isWifiConnected = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
			if (!isWifiConnected) {
				Log.i("WifiLanChat", "wifi is not connected");
//				Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
				Intent i = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
				startActivity(i);
			}
		}

		private void scanDevices() {
			// TODO Auto-generated method stub
			
		}

		private void introduceSelf() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
