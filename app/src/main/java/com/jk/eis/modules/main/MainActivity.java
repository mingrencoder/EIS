package com.jk.eis.modules.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.greendao.User;
import com.jk.eis.modules.logtransfer.fragment.TransferFragment;
import com.jk.eis.modules.map.fragment.MapFragment;
import com.jk.eis.modules.personalInfo.fragment.PersonalFragment;
import com.jk.eis.modules.task.fragment.TaskFragment;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	private RadioGroup group;
	private FragmentManager mFragManager;
	private FragmentTransaction mFragTransaction;

	//从服务器接收到的user
	private User user;

	//数据库操作工具类
	MapDaoHelper helper;
	
	// 来标识是否退出
	private static boolean isExit = false;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		helper = MapDaoHelper.getInstance(MainActivity.this);

		Fresco.initialize(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        initDB();
		initViews();

		group.setOnCheckedChangeListener(this);
	}

	private void initDB() {
		/**
		 * 可以考虑这里若连接服务器，则同步一次，再initdata();
		 */
		/************************************************
		 * 同步数据库 逻辑再想想
		 */
		if(helper.getMaster() == null){
			helper.initdata();
		}
		//从登录界面接受到的intent
		Intent intent = this.getIntent();
		user=(User)intent.getSerializableExtra("user");

		//这没必要，以后删了
        helper.addUser(user);

		/*//将username在会话中保存
		SharedPreferences preferences = getSharedPreferences("username", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		String username = user.getUsername();
		editor.putString("name", username);
		editor.commit();*/
        /************************************************
         * 带更新用户信息
         */
	}

	private void initViews() {
		mFragManager = getFragmentManager();
		group = (RadioGroup) findViewById(R.id.radioGroup);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.id_main_recent:
			break;
		case R.id.id_main_task:
			TaskFragment mTaskFragment = new TaskFragment();
			loadFragment(mTaskFragment);
			break;
		case R.id.id_main_transfer:
			TransferFragment mTransferFragment = new TransferFragment(user.getUsername());
			loadFragment(mTransferFragment);
			break;
		case R.id.id_main_map:
			/***************************************************************************
			 * 这里先做判断是否联网 
			 * 1、若联网，更新本地数据库数据 
			 * 2、若不联网，读取本地数据库数据 
			 * 3、此处加入progress显示
			 */
			MapFragment mMapFragment = new MapFragment();
			loadFragment(mMapFragment);
			break;
		case R.id.id_main_me:
			PersonalFragment mPersonalFragment = new PersonalFragment();
			loadFragment(mPersonalFragment);
			break;

		default:
			break;
		}
	}

	private void loadFragment(Fragment fragment) {
		mFragTransaction = mFragManager.beginTransaction();
		if (fragment.isAdded()) {
			mFragTransaction.add(R.id.frame, fragment);
		} else {
			mFragTransaction.replace(R.id.frame, fragment);
		}
		// mFragTransaction.addToBackStack(null);
		mFragTransaction.commit();
	}

	/**
	 * 按两次返回键退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}
}
