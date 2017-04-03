package com.jk.eis.push.org.androidpn.client.IQ;

import org.jivesoftware.smack.packet.IQ;

public class DeliverConfirmIQ extends IQ {

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	private String uuid;
	
	@Override
	public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        //命名空间
        buf.append("<").append("notification").append(" xmlns=\"").append(
                "androidpn:iq:deliverconfirm").append("\">");
        if (uuid != null) {
            buf.append("<uuid>").append(uuid).append("</uuid>");
        }
        buf.append("</").append("notification").append("> ");
        return buf.toString();
	}

}
