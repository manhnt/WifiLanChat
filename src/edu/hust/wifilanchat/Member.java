package edu.hust.wifilanchat;

import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.simple.JSONAware;

import edu.hust.wifilanchat.networking.HostBean;

public class Member implements Serializable, JSONAware {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private HostBean host;
	private transient ObjectOutputStream stream = null;
	
	public Member(String pName) {
		this.name = pName;
	}
	
	public Member(String pName, HostBean pHost) {
		this.name = pName;
		this.host = pHost;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public HostBean getHost() {
		return host;
	}

	public void setHost(HostBean host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return (getName());
	}

	@Override
	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"" + getName() + "\"");
		sb.append(",");
		sb.append("\"ip\":\"" + getHost().getIpAddress() + "\"");
		sb.append("}");
		return sb.toString();
	}

	public ObjectOutputStream getStream() {
		return stream;
	}

	public void setStream(ObjectOutputStream stream) {
		this.stream = stream;
	}
}
