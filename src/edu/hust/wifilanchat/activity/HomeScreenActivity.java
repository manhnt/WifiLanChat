package edu.hust.wifilanchat.activity;

import java.lang.ref.WeakReference;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.hust.wifilanchat.Member;
import edu.hust.wifilanchat.R;
import edu.hust.wifilanchat.SayHelloThread;
import edu.hust.wifilanchat.ServerService;
import edu.hust.wifilanchat.UserPreferenceManager;
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
	
	private boolean discoveryDone = false;
	private boolean isDiscovering = false;
	
	private List<HostBean> hosts = null;
	private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;
	private NetInfo netinfo;
	private AbstractDiscovery mDiscoveryTask = null;
	private final int DISCOVERY_DONE = 1;
	
	public static SharedPreferences prefs = null;
	public final static int SCAN_PORT_RESULT = 1;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int arg1 = msg.arg1;
			if (arg1 == DISCOVERY_DONE) {
				/* If devices discovery task has finished */
				if (pDialog != null)
					pDialog.dismiss();
				viewChatRoomListBtn.setEnabled(true);
				viewPeopleBtn.setEnabled(true);
				settingBtn.setEnabled(true);
				discoveryDone = true;
				isDiscovering = false;
				
				for (HostBean h : hosts) {
					/* Gui ban tin HELLO_WORLD toi tat ca cac host */
					Log.d(DEBUG_TAG, "Found host: " + h.getIpAddress());
					SayHelloThread s = new SayHelloThread(h);
					new Thread(s).start();
				}
			}
		};
	};
	
	UserPreferenceManager uManager = UserPreferenceManager.getInstance();
//	RoomManager rManager = RoomManager.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		Log.i(DEBUG_TAG, "onCreate");
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		createGUI();
		
		/* Then open server for listening */
		startListeningServer();
		
		/* Set default user's nickname */
		uManager.setNickName(android.os.Build.MODEL);
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
		findMyIP();
		
		hosts = new ArrayList<HostBean>();
		netinfo = new NetInfo(getApplicationContext());
		setNetInfo();
		
		if ((!discoveryDone) && (!isDiscovering) && (isWifiConnected())) {
			mDiscoveryTask = new DefaultDiscovery(this);
			mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
			mDiscoveryTask.execute();
			isDiscovering = true;
			Log.i(DEBUG_TAG, "Executing Discovery Task");
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("discoveryDone", discoveryDone);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		discoveryDone = savedInstanceState.getBoolean("discoveryDone");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_screen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rescan:
			// Perform discovery task again
			discoveryDone = false;
			mDiscoveryTask = new DefaultDiscovery(this);
			mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
			mDiscoveryTask.execute();
			isDiscovering = true; discoveryDone = false;
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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

	private void setNetInfo() {
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

	private void findMyIP() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		uManager.setMyIpAddr(ip);
		Log.i(DEBUG_TAG, ip);
		createMemberMe(ip);
	}

	private String intToIp(int i) {
		int d = (i >> 24) & 0xFF;
		int c = (i >> 16) & 0xFF;
		int b = (i >> 8) & 0xFF;
		int a = i & 0xFF;
		String myIP = a + "." + b + "." + c + "." + d;
		return myIP;
	}

	private void createMemberMe(String ipAddr) {
		HostBean meHost = new HostBean();
		String s = uManager.getNickName();
		meHost.setUsername(android.os.Build.MODEL);
		meHost.setIpAddress(ipAddr);
		Member me = new Member(s, meHost);
		uManager.setMe(me);
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
						MemberListActivity.class);
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


	/* Start server service */
	private void startListeningServer() {
		Intent service = new Intent(this, ServerService.class);
		startService(service);
		// TODO: bind service ?
//		bindService(service, conn, flags);
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
	                Iterator<HostBean> k = hosts.iterator();
	                if (hosts.size() == 0) {
	                	Toast.makeText(getApplicationContext(), "No host", Toast.LENGTH_SHORT)
	                	.show();
	                }
	                else {
	                	for(int i=0;i<hosts.size();i++)
	                	Toast.makeText(getApplicationContext(), k.next().toString(), Toast.LENGTH_SHORT)
	                	.show();
	                }
	            }
	        }
	        
	        discoveryDone = true;
	        Message msg = mHandler.obtainMessage();
	        msg.arg1 = DISCOVERY_DONE;
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
		if (!hostBean.getIpAddress().equals(uManager.getMyIpAddr()))
			hosts.add(hostBean);
        //Toast.makeText(getApplicationContext(), host.toString(), Toast.LENGTH_SHORT).show();
        
    }

	public void stopDiscovering() {
		mDiscoveryTask.cancel(true);
		mDiscoveryTask = null;
	}
}

