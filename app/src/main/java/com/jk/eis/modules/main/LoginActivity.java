package com.jk.eis.modules.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.User;
import com.jk.eis.commen.okhttp.CommonOkHttpClient;
import com.jk.eis.commen.okhttp.DisposeDataHandle;
import com.jk.eis.commen.okhttp.listener.DisposeDataListener;
import com.jk.eis.commen.okhttp.request.CommonRequest;
import com.jk.eis.commen.okhttp.request.RequestParams;
import com.jk.eis.commen.utils.Constants;
import com.jk.eis.push.org.androidpn.client.ServiceManager;

public class LoginActivity extends Activity {

	private Button mBtnLogin;
	private Button mBtnRegister;
	private Button mBtnForgot;
	private EditText mUsername;
	private EditText mPassword;

	private ServiceManager serviceManager;

	//测试用
	private Button btnOffline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		initViews();
	}

	public void doClick(View v) {
		switch (v.getId()) {
			case R.id.id_login_signin:
				String username = mUsername.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				login(username, password);
				break;
		/*case R.id.id_login_register:
			Intent intent2 = new Intent();
			break;
		case R.id.id_login_forgot:

			break;*/
			case R.id.id_login_signin_offline:
				Log.i("tag", "登录成功!");
				Intent intent = new Intent();
				if ("".equals(mUsername.getText().toString()) || "".equals(mPassword.getText().toString())) {
					Toast.makeText(getApplicationContext(), "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
					return;
				}
				intent.putExtra(Constants.USER_USERNAME, mUsername.getText().toString().trim());
				intent.putExtra(Constants.USER_PASSWORD, mPassword.getText().toString().trim());
				intent.putExtra(Constants.USER_NAME, "王小明");
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	private void login(final String username, String password) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);
		CommonOkHttpClient.post(
				CommonRequest.createPostRequest("http://" + Constants.SERVER_IP + ":8080/EISServer/login/signin", params),
				new DisposeDataHandle(new DisposeDataListener() {

					@Override
					public void onSuccess(Object responseObj) {
						Log.i("tag", "登录成功!");
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", ((User) responseObj));
						intent.putExtras(bundle);

						//将username在会话中保存
						SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME_CUSTOM, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						String username = ((User) responseObj).getUsername();
						String password = ((User) responseObj).getPassword();
						editor.putString(Constants.XMPP_USERNAME, username);
						editor.putString(Constants.XMPP_PASSWORD, password);
						editor.commit();
						intent.setClass(LoginActivity.this, MainActivity.class);
						//开启服务
						serviceManager = new ServiceManager(LoginActivity.this);
						serviceManager.setNotificationIcon(R.drawable.notification);
						serviceManager.startService();
						startActivity(intent);
					}

					@Override
					public void onFailure(Object reasonObj) {
						Log.i("tag", "登录失败!");
						Toast.makeText(getApplicationContext(), "用户名密码错误！", Toast.LENGTH_SHORT).show();
					}
				}, User.class));
	}

	private void initViews() {
		mBtnLogin = (Button) findViewById(R.id.id_login_signin);
		//mBtnRegister = (Button) findViewById(R.id.id_login_register);
		//mBtnForgot = (Button) findViewById(R.id.id_login_forgot);
		mUsername = (EditText) findViewById(R.id.id_login_username);
		mPassword = (EditText) findViewById(R.id.id_login_password);

		btnOffline = (Button) findViewById(R.id.id_login_signin_offline);
	}
}
