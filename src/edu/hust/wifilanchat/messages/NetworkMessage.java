package edu.hust.wifilanchat.messages;

import java.io.Serializable;

import edu.hust.wifilanchat.Member;

public class NetworkMessage implements Serializable {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	private String content;
	private Member sender;
	private MessageType type;

	public NetworkMessage() {

	}

	public NetworkMessage(MessageType t) {
		this.type = t;
	}
	public NetworkMessage(String pContent, Member pSender, MessageType t) {
		content = pContent;
		sender = pSender;
		type = t;
	}

	public NetworkMessage(String pContent, MessageType t) {
		content = pContent;
		type = t;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Member getSender() {
		return sender;
	}

	public void setSender(Member sender) {
		this.sender = sender;
	}

	public String getSenderName() {
		return sender.getName();
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String toJSONString() {
		// TODO: implement this
		return null;
	}
}
