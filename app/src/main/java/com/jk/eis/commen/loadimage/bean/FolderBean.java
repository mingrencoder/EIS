package com.jk.eis.commen.loadimage.bean;

public class FolderBean {

	/*
	 * 当前文件夹路径
	 * 第一张图片路径
	 * 文件夹名称
	 * 文件夹图片的数量
	 */
	private String dir;
	private String firstImgPath;
	private String name;
	private int count;
	
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
		//设置路径的时候同时将name也设置好了
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}
	public String getFirstImgPath() {
		return firstImgPath;
	}
	public void setFirstImgPath(String firstImgPath) {
		this.firstImgPath = firstImgPath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	
}
