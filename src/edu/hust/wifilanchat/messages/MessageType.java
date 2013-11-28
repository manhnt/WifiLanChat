package edu.hust.wifilanchat.messages;

public enum MessageType {
	HELLO_WORLD,	// Hello World and request update member list
	ADV_SELF,	// Advertise self information
	ADV_ROOM,	// Advertise information about rooms which I'm serving
	ADV_NEW_ROOM,	// Advertise information about a new room I've just created
	REQ_UPDATE_ROOMS,	// Request to update room list
	REQ_JOIN_ROOM,	// Request permission to join a chat room
	ALLOW_FOR_JOIN,	// Allow a member to join my chat room
	NOT_ALLOW_FOR_JOIN,	// Do not allow a member to join my chat room
	LEAVE_ROOM,	// Inform the server that I'm leaving your room
	NEW_ROOM_MEM,	// The server informs all the members in its room about new member
	MEM_LEFT_ROOM,	// The server informs members in its room that one member is leaving.
	GO_OFFLINE,	// I'm going to disappear
	TEXT_MESSAGE,	// Normal text message for chatting.
	START_CHAT,		// Indicates starting chat session
	ACK	// Just ACK
}
