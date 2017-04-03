package com.jk.eis.modules.map.listener;

public interface InformShipSegListenr {
	
	/**
	 * 根据船舶零件的ID号去查询信息
	 * @param id
	 */
	public void querySegInfo(int id);

}