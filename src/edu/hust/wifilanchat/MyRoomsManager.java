package edu.hust.wifilanchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;

public class MyRoomsManager {

	private static MyRoomsManager instance = new MyRoomsManager();
	private List<ChatRoom> myRooms = new ArrayList<ChatRoom>();
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, ChatRoom> map = new HashMap<Integer, ChatRoom>();
	
	private int nextID;
	
	private MyRoomsManager() {
		nextID = 0;
	}

	public static MyRoomsManager getInstance() {
		return instance;
	}
	
	public List<ChatRoom> getMyRoomList() {
		return myRooms;
	}
	
	public void addRoom(ChatRoom r) {
		myRooms.add(r);
		map.put(r.getId(), r);
	}
	
	public void removeRoom(ChatRoom r) {
		myRooms.remove(r);
	}
	
	public int getRoomCount() {
		return myRooms.size();
	}

	public int getNextID() {
		return nextID++;
	}
	
	public ChatRoom getRoomById(int id) {
		return (ChatRoom) map.get(Integer.valueOf(id));
	}

	/*public void setNextID(int nextID) {
		this.nextID = nextID;
	}
	*/
}
