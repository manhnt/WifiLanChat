package edu.hust.wifilanchat;

public class UserPreferenceManager {

	private String nickName;
	private Member me;
	private String myIpAddr;
	
	private static UserPreferenceManager instance = new UserPreferenceManager();
	
	private UserPreferenceManager() {
		
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
		me.setName(nickName);
	}
	
	public static UserPreferenceManager getInstance() {
		return instance;
	}

	public Member getMe() {
		return me;
	}

	public void setMe(Member me) {
		this.me = me;
	}

	public String getMyIpAddr() {
		return myIpAddr;
	}

	public void setMyIpAddr(String myIpAddr) {
		this.myIpAddr = myIpAddr;
	}

}
