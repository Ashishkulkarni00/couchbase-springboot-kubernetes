package com.srijan.eis.mock.model;



public class AuthData {
	
	
	String sessionKey;
	String userId;
	//String password;
	//String authKey;
	
	public AuthData() {
		
	}
	
	public AuthData(String sessionId, String userId, String password, String authKey) {
		this.sessionKey = sessionId;
		this.userId = userId;
		//this.password = password;
		//this.authKey = authKey;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionId) {
		this.sessionKey = sessionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/*public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}*/
	
	
}
