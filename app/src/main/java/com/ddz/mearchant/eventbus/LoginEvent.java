package com.ddz.mearchant.eventbus;
/**
 */
public class LoginEvent {
	private String message;
	private String title;
	private int tage;
	public LoginEvent() {
		super();
	}

	public LoginEvent(int tage) {
		super();
		this.tage = tage;
	}

	public LoginEvent(String message, String title) {
		super();
		this.message = message;
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTage() {
		return tage;
	}
	public void setTage(int tage) {
		this.tage = tage;
	}
	
	

	
	
	
	
}
