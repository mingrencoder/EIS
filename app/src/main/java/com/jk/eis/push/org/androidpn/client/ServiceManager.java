/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jk.eis.push.org.androidpn.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.jk.eis.push.org.androidpn.client.IQ.SetAliasIQ;

import org.jivesoftware.smack.packet.IQ;

import java.util.Properties;

/** 
 * This class is to manage the notificatin service and to load the configuration.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class ServiceManager {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ServiceManager.class);

    private Context context;

    private SharedPreferences sharedPrefs;

    private Properties props;

    private String version = "0.5.0";

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

    private String callbackActivityPackageName;

    private String callbackActivityClassName;

    public ServiceManager(Context context) {
        this.context = context;

        if (context instanceof Activity) {
            Log.i(LOGTAG, "Callback Activity...");
            Activity callbackActivity = (Activity) context;
            callbackActivityPackageName = callbackActivity.getPackageName();
            callbackActivityClassName = callbackActivity.getClass().getName();
        }

        props = loadProperties();
        apiKey = props.getProperty("apiKey", "");
        xmppHost = props.getProperty("xmppHost", "127.0.0.1");
        xmppPort = props.getProperty("xmppPort", "5222");
        Log.i(LOGTAG, "apiKey=" + apiKey);
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

        sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.API_KEY, apiKey);
        editor.putString(Constants.VERSION, version);
        editor.putString(Constants.XMPP_HOST, xmppHost);
        editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME,
                callbackActivityPackageName);
        editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME,
                callbackActivityClassName);
        editor.commit();
        // Log.i(LOGTAG, "sharedPrefs=" + sharedPrefs.toString());
    }

    public void startService() {
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*Intent intent = NotificationService.getIntent();
                context.startService(intent);*/

                Intent mIntent = new Intent();
                mIntent.setAction("org.androidpn.client.NotificationService");//你定义的service的action
                mIntent.setPackage("com.jk.eis");//这里你需要设置你应用的包名
                context.startService(mIntent);
            }
        });
        serviceThread.start();
    }

    /**
     * 设置alias方法
     */
    public void setAlias(final String alias){
    	//通过sharedPrefs文件获得username
    	final String username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
    	if(TextUtils.isEmpty(username) || TextUtils.isEmpty(alias)){
    		return;
    	}
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//这里通过静态方法拿到NotificationService实例，等其创建出来，这里就有可以拿到了
		    	NotificationService notificationService = NotificationService.getNotificationService();
		    	XmppManager xmppManager = notificationService.getXmppManager();
		    	if(xmppManager != null){
		    		if(!xmppManager.isAuthenticated()){
		    			/*
		    			 * 使用同步机制，等待客户端服务器认证通过
		    			 */
		    			synchronized (xmppManager) {
							try {
								Log.i(LOGTAG, "等待验证");
								xmppManager.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
		    			Log.i(LOGTAG, "验证通过，发送别名IQ");
		            	SetAliasIQ iq = new SetAliasIQ();
		            	iq.setType(IQ.Type.SET);
		            	iq.setUsername(username);
		            	iq.setAlias(alias);
		            	xmppManager.getConnection().sendPacket(iq);
		    		}
		    	}
			}
		}).start();
    }
    
    public void stopService() {
        Intent mIntent = new Intent();
        mIntent.setAction("org.androidpn.client.NotificationService");
        mIntent.setPackage("com.jk.eis");
        context.stopService(mIntent);
    }

    private Properties  loadProperties() {

        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("androidpn", "raw",
                    context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
            // e.printStackTrace();
        }
        return props;
    }


    public void setNotificationIcon(int iconId) {
        Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.NOTIFICATION_ICON, iconId);
        editor.commit();
    }

    public static void viewNotificationSettings(Context context) {
        Intent intent = new Intent().setClass(context,
                NotificationSettingsActivity.class);
        context.startActivity(intent);
    }

}
