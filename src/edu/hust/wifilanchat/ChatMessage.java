package edu.hust.wifilanchat;

public class ChatMessage {
	private String content;
	private Member sender;
	private boolean isLeft;
	
	public ChatMessage(String pCont, Member pSender, boolean pLeft) {
		content = pCont;
		sender = pSender;
		isLeft = pLeft;
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
	
	public boolean isLeft() {
		return isLeft;
	}
}
