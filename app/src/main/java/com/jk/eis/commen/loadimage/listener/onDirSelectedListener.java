package com.jk.eis.commen.loadimage.listener;

import com.jk.eis.commen.loadimage.bean.FolderBean;

/*************************************
 * 处理点击popupwindow点击事件，切换文件夹
 * @author Ji Kai
 *
 *************************************/
public interface onDirSelectedListener {

	/**
	 * @param folderBean
	 */
	public void onSelected(FolderBean folderBean);
}
