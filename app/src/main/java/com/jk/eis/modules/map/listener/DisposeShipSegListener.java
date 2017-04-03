package com.jk.eis.modules.map.listener;

public interface DisposeShipSegListener {
	
	/**
	 * 将对应id的船舶分段放入手指坐标x,y对应的生产场地
	 * @param id, groupId, x, y
	 */
	public void putUnitIn(int id, int groupId, int x, int y);
}
