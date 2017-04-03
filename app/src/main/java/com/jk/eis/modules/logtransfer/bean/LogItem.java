package com.jk.eis.modules.logtransfer.bean;

public class LogItem {

	private int photoId;
	private String name;

	public LogItem(int photoId, String name) {
		super();
		this.photoId = photoId;
		this.name = name;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
