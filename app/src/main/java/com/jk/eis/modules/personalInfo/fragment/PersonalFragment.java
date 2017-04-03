package com.jk.eis.modules.personalInfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.greendao.User;
import com.jk.eis.commen.utils.Constants;
import com.jk.eis.modules.personalInfo.adapter.PersonalInfoAdapter;
import com.jk.eis.modules.personalInfo.adapter.PersonalTaskAdapter;
import com.jk.eis.modules.personalInfo.bean.ItemInfo;
import com.jk.eis.modules.personalInfo.bean.ItemTask;

import java.util.ArrayList;
import java.util.List;

public class PersonalFragment extends Fragment {
	
	private View view;
	
	private ListView mListViewInfo;
	private ListView mListViewTask;
	private List<ItemInfo> mItemInfoList;
	private List<ItemTask> mItemTaskList;
	
	private TextView mPersonalName;
	private TextView mPersonalUsername;

	private String username;


	SharedPreferences preferences;

	MapDaoHelper helper;
	
	public PersonalFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		helper = MapDaoHelper.getInstance(getActivity());

		preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME_CUSTOM, Context.MODE_PRIVATE);
		username = preferences.getString(Constants.XMPP_USERNAME, "");
		//加载Fresco
		Fresco.initialize(getActivity());
		if (view == null) {
			view = inflater.inflate(R.layout.personalinfo, container, false);
			initViews();
			initData();
			initEvent();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}


	private void initViews() {

		mListViewInfo = (ListView) view.findViewById(R.id.id_personal_listview_info);
		mListViewTask = (ListView) view.findViewById(R.id.id_personal_listview_task);
		mItemInfoList = new ArrayList<ItemInfo>();
		mItemTaskList = new ArrayList<ItemTask>();


	}
	
	private void initData() {
		User user = helper.getUserByUsername(username);
		if(user != null){
			Uri uri = Uri.parse("http://" + Constants.SERVER_IP + ":8080/EISServer/images/userphoto/" + username + "/1.png");
			mItemInfoList.add(new ItemInfo(uri, "姓名："+user.getName(), "账号："+user.getUsername()));
		}
		mListViewInfo.setAdapter(new PersonalInfoAdapter(getActivity(),mItemInfoList));
		
		mItemTaskList.add(new ItemTask(R.drawable.tasksend, "发出任务"));
		mItemTaskList.add(new ItemTask(R.drawable.taskreceive, "接受任务"));
		mListViewTask.setFooterDividersEnabled(true);
		mListViewTask.setHeaderDividersEnabled(true);
		mListViewTask.setAdapter(new PersonalTaskAdapter(getActivity(), mItemTaskList));
	}

	private void initEvent() {

		//fresco
	}

	/**
	 * 当Fragment被添加到Activity时候会回调这个方法，并且只调用一次
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	/**
	 * 当Fragment所在的Activty启动完成后调用
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 启动Fragment
	 * 
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * 恢复Fragment时会被回调，调用onStart（）方法后面一定会调用onResume()方法
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 暂停Fragment
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
	}

	/**
	 * 停止Fragment
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * 销毁Fragment所包含的View组件时
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * 销毁Fragment时会被回调
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Fragment从Activity中删除时会回调该方法，并且这个方法只会调用一次
	 */
	@Override
	public void onDetach() {
		super.onDetach();
	}
}
