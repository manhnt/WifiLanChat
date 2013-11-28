package edu.hust.wifilanchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatThread implements Runnable {
	private static final int SERVER_PORT = 25125;
	private Member server;

	public ChatThread() {
		
	}

	public ChatThread(Member server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(server.getHost().getInetAddress(), SERVER_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
