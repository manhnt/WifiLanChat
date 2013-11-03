package edu.hust.wifilanchat.activity;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.networking.DefaultDiscovery;
import edu.hust.wifilanchat.networking.HostBean;
import edu.hust.wifilanchat.networking.NetInfo;

public class HomeScreenActivity extends Activity {

	public static final String DEBUG_TAG = "WiFiLANChat";
	
	public static final int CHANGE_WIFI_SETTING = 0;
	
	Button viewChatRoomListBtn = null;
	Button viewPeopleBtn = null;
	Button settingBtn = null;
	private ProgressDialog pDialog;
	private boolean initialTaskDone = false;
//	private boolean setupWifiDone = false;
//	private ArrayList<Member> memberList = new ArrayList<Member>();
	private List<HostBean> hosts = null;
	
	private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;
	private NetInfo netinfo;
	private AbstractDiscovery mDiscoveryTask = null;
	
	public static SharedPreferences prefs = null;
	public final static int SCAN_PORT_RESULT = 1;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (pDialog != null)
				pDialog.dismiss();
			viewChatRoomListBtn.setEnabled(true);
			viewPeopleBtn.setEnabled(true);
			settingBtn.setEnabled(true);
			initialTaskDone = true;
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		Log.i(DEBUG_TAG, "onCreate");
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		hosts = new ArrayList<HostBean>();
		netinfo = new NetInfo(getApplicationContext());
		setInfo();
		
		createGUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(DEBUG_TAG, "onStart");
		setupWifiConnection();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(DEBUG_TAG, "onResume");
		
		if (isWifiConnected() && (!initialTaskDone)) {
			mDiscoveryTask = new DefaultDiscovery(this);
			mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
			mDiscoveryTask.execute();
			Log.i(DEBUG_TAG, "Executing Discovery Task");
			
			/*InitialTask task = new InitialTask();
			task.execute();
			Log.i(DEBUG_TAG, "Executed AsyncTask");*/
			initialTaskDone = true;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(DEBUG_TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(DEBUG_TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(DEBUG_TAG, "onDestroy");
	}
	
	private void setupWifiConnection() {
		Log.i(DEBUG_TAG, "setupWifiConnection");
		
		boolean isWifiConn = this.isWifiConnected();
		
		Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
		
		if (!isWifiConn) {
			AlertDialog.Builder b = new AlertDialog.Builder(HomeScreenActivity.this);
			b.setCancelable(true);
			b.setTitle("Wi-Fi is not connected");
			b.setMessage("Do you want to set up a Wi-Fi connection first?");
			b.setPositiveButton("Set up", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
					startActivity(i);
				}
			});
			b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog.Builder b = new AlertDialog.Builder(HomeScreenActivity.this);
					b.setCancelable(false);
					b.setMessage("You must connect to a Wi-Fi network");
					b.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HomeScreenActivity.this.finish();
						}
					});
					b.create().show();
				}
			});
			AlertDialog dl = b.create();
			dl.show();
		}
	}

	
	private void setInfo() {
	    // Get ip information
	    long network_ip = NetInfo.getUnsignedLongFromIp(netinfo.ip);
	    // Detected IP
	    int shift = (32 - netinfo.cidr);
	    if (netinfo.cidr < 31) {
	    	network_start = (network_ip >> shift << shift) + 1;
	        network_end = (network_start | ((1 << shift) - 1)) - 1;
	    } else {
	        network_start = (network_ip >> shift << shift);
	        network_end = (network_start | ((1 << shift) - 1));
	    }
	    
	
	}

	private boolean isWifiConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		boolean isWifiConn = networkInfo.isConnected();
		return isWifiConn;
	}


	private void createGUI() {
		viewChatRoomListBtn = (Button) findViewById(R.id.view_chat_rooms_btn);
		viewPeopleBtn = (Button) findViewById(R.id.view_people_btn);
		settingBtn = (Button) findViewById(R.id.settings_btn);
	
		viewChatRoomListBtn.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						RoomsListActivity.class);
				startActivity(intent);
			}
		});
	
		viewPeopleBtn.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						PeopleListActivity.class);
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


	private class InitialTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(HomeScreenActivity.this);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.setTitle("Processing");
			pDialog.setMessage("Scanning...");
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
//			memberList = discoverDevices();
			introduceSelf();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessage(msg);
		}
		
/*
		private void discoverDevices() {
			// TODO: tim kiem tat ca cac thiet bi co cai dat phan mem trong mang
			// simulate taking sometime to scan
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		private ArrayList<Member> discoverDevices() {
			// TODO: tim kiem tat ca cac thiet bi co cai dat phan mem trong mang
			// simulate taking sometime to scan
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ArrayList<Member> mList = new ArrayList<Member>();
			InetAddress addr;
			try {
				addr = InetAddress.getByName("192.168.0.10");
				mList.add(new Member("Member 1", addr));
				addr = InetAddress.getByName("192.168.0.11");
				mList.add(new Member("Member 2", addr));
				addr = InetAddress.getByName("192.168.0.12");
				mList.add(new Member("Member 3", addr));
				addr = InetAddress.getByName("192.168.0.13");
				mList.add(new Member("Member 4", addr));
				addr = InetAddress.getByName("192.168.0.14");
				mList.add(new Member("Member 5", addr));
				addr = InetAddress.getByName("192.168.0.15");
				mList.add(new Member("Member 6", addr));
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			return mList;
		}
		private void introduceSelf() {
			
		}
		
	}
	
	public abstract class AbstractDiscovery extends AsyncTask<Void, HostBean, Void> {
	    final protected WeakReference<HomeScreenActivity> mDiscover;

	    protected long ip;
	    protected long start = 0;
	    protected long end = 0;
	    protected long size = 0;

	    public AbstractDiscovery(HomeScreenActivity discover) {
	        mDiscover = new WeakReference<HomeScreenActivity>(discover);
	    }

	    public void setNetwork(long ip, long start, long end) {
	        this.ip = ip;
	        this.start = start;
	        this.end = end;
	    }

	    abstract protected Void doInBackground(Void... params);

	    @Override
	    protected void onPreExecute() {
	    	pDialog = new ProgressDialog(HomeScreenActivity.this);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.setTitle("Processing");
			pDialog.setMessage("Scanning...");
			pDialog.show();
			
	        size = (int) (end - start + 1);
	        if (mDiscover != null) {
	            final HomeScreenActivity discover = mDiscover.get();
	            if (discover != null) {
	                discover.setProgress(0);
	            }
	        }
	    }

	    @Override
	    protected void onProgressUpdate(HostBean... host) {
	        if (mDiscover != null) {
	            final HomeScreenActivity discover = mDiscover.get();
	            if (discover != null) {
	                if (!isCancelled()) {
	                    if (host[0] != null) {
	                        discover.addHost(host[0]);
	                    }
	                    
	                }

	            }
	        }
	    }

	    @Override
	    protected void onPostExecute(Void unused) {
	        if (mDiscover != null) {
	            final HomeScreenActivity discover = mDiscover.get();
	            if (discover != null) {
	                
	                discover.makeToast("Discovery Finished!");
	                discover.stopDiscovering();
	                Iterator k = hosts.iterator();
	                for(int i=0;i<hosts.size();i++)
	                	Toast.makeText(getApplicationContext(), k.next().toString(), Toast.LENGTH_SHORT).show();
	            }
	        }
	        Message msg = mHandler.obtainMessage();
			mHandler.sendMessage(msg);
	    }

	    @Override
	    protected void onCancelled() {
	        if (mDiscover != null) {
	            final HomeScreenActivity discover = mDiscover.get();
	            if (discover != null) {
	                discover.makeToast("Discovery Canceled!");
	                discover.stopDiscovering();
	            }
	        }
	        super.onCancelled();
	    }
		
	}

	public void makeToast(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	public void addHost(HostBean hostBean) {
        //host.position = hosts.size();
        hosts.add(hostBean);
        //Toast.makeText(getApplicationContext(), host.toString(), Toast.LENGTH_SHORT).show();
        
    }

	public void stopDiscovering() {
		mDiscoveryTask.cancel(true);
		mDiscoveryTask = null;
	}
}
