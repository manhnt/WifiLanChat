package edu.hust.wifilanchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;
import edu.hust.wifilanchat.messages.NetworkMessage;

public class SendMessageThread implements Runnable {
	private static final String TAG = "WiFiLANChat";
	
	private static final int SO_TIMEOUT = 5000;	// Socket timeout = 5s
	private static final int SERVER_PORT = 25125;
	
	private Member toMember;
	private NetworkMessage msgToSend;
	
	public SendMessageThread(NetworkMessage msg, Member toMem) {
		this.toMember = toMem;
		msgToSend = msg;
		Log.i(TAG, "Trying to say hello to host" + toMember.getName());
	}
	@Override
	public void run() {
		Socket s = new Socket();

		try {
			InetAddress addr = toMember.getHost().getInetAddress();
			s.connect(new InetSocketAddress(addr, SERVER_PORT), SO_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			
			oos.writeObject(msgToSend);
			// Read response from server
			try {
				NetworkMessage response = (NetworkMessage) ois.readObject();
				parseResponse(response);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				oos.close();
				ois.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			// Close socket
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
	private void parseResponse(NetworkMessage response) {
		switch (response.getType()) {
		case ACK:
			
			break;

		default:
			break;
		}
	}

}
