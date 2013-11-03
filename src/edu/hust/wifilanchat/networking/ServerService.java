package edu.hust.wifilanchat.networking;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {

	private ServerSocket serverSocket;
	private static final int SERVER_PORT = 45678;
	private static final String DEBUG_TAG = "ServerService";
	
	private IBinder mBinder = new LocalServerBinder();
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(DEBUG_TAG, "onCreate");
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.e(DEBUG_TAG, "Cannot open server socket");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalServerBinder extends Binder {
		ServerService getService() {
			return ServerService.this;
		}
	}
}
