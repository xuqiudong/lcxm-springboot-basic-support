package cn.xuqiudong.basic.third.login.model;

import java.io.Serializable;

/**
 * 通过username获取token时候的sign原本的数据结构
 * @author VIC.xu
 * @since 2025-09-04
 *
 */
public class TokenSignModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * appId
	 */
	private String appId;

	/**
	 * 时间戳
	 */
	private long timestamp;

	public TokenSignModel() {
	}

	public TokenSignModel(String username, String appId) {
		this.username = username;
		this.appId = appId;
		this.timestamp = System.currentTimeMillis();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}





}
