package com.jk.eis.modules.personalInfo.bean;

import android.net.Uri;

public class ItemInfo {
	
	private Uri uri;
	private String name;
	private String username;

	public ItemInfo(Uri uri, String name, String username) {
		super();
		this.uri = uri;
		this.name = name;
		this.username = username;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
