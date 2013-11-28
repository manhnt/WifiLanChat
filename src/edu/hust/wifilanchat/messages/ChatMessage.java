package edu.hust.wifilanchat.messages;

import edu.hust.wifilanchat.Member;

public class ChatMessage extends NetworkMessage {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private boolean inLeftHand;
	
	public ChatMessage() {
		super(MessageType.TEXT_MESSAGE);
	}
	
	public ChatMessage(String pContent, Member pSender, boolean pLeft) {
		super(pContent, pSender, MessageType.TEXT_MESSAGE);
		inLeftHand  = pLeft;
	}

	public boolean isLeft() {
		return inLeftHand;
	}

	public void setIsLeft(boolean inLeftHand) {
		this.inLeftHand = inLeftHand;
	}
	
}
