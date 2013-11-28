package edu.hust.wifilanchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import edu.hust.wifilanchat.messages.ChatMessage;

public class ServerService extends Service {

	private static final String DEBUG_TAG = "ServerService";
	public static final String UPDATE_MESSAGE_UI = "edu.hust.wifilanchat.ServerService.UPDATE_REQUEST";
	private IBinder mBinder = new LocalServerBinder();
	private LocalBroadcastManager broadcaster;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(DEBUG_TAG, "onCreate");
		broadcaster = LocalBroadcastManager.getInstance(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startListeningServer();
		
		return Service.START_STICKY;
	}
	
	private void startListeningServer() {
		Runnable r = new ServerThread(this);
		Thread t = new Thread(r);
		t.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void updateNewMessageToUI(ChatMessage c) {
		Intent intent = new Intent(UPDATE_MESSAGE_UI);
		if (c != null) {
			Bundle b = new Bundle();
			b.putSerializable("message", c);
			intent.putExtras(b);
			broadcaster.sendBroadcast(intent);
		}
	}
	
	public class LocalServerBinder extends Binder {
		ServerService getService() {
			return ServerService.this;
		}
	}
}
