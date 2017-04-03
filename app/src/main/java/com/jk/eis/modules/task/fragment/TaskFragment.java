package com.jk.eis.modules.task.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.greendao.User;
import com.jk.eis.commen.model.UserCustom;
import com.jk.eis.commen.okhttp.CommonOkHttpClient;
import com.jk.eis.commen.okhttp.DisposeDataHandle;
import com.jk.eis.commen.okhttp.listener.DisposeDataListener;
import com.jk.eis.commen.okhttp.request.CommonRequest;
import com.jk.eis.commen.okhttp.request.RequestParams;
import com.jk.eis.commen.utils.Constants;
import com.jk.eis.modules.logtransfer.adapter.CheckUserInfoAdapter;
import com.jk.eis.modules.personalInfo.bean.ItemInfo;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TaskFragment extends Fragment {

	private View view;


	private ListView mListViewInfo;

	private String username;

	private List<ItemInfo> mItemInfoList;
	
	MapDaoHelper helper;

	public TaskFragment() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		helper = MapDaoHelper.getInstance(getActivity());

		Fresco.initialize(getActivity());

		if (view == null) {
			view = inflater.inflate(R.layout.task, container, false);
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
		mListViewInfo = (ListView) view.findViewById(R.id.id_task_listview);
		mItemInfoList = new ArrayList<ItemInfo>();
		
	}
	
	private void initData() {
		SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME_CUSTOM, Context.MODE_PRIVATE);
		username = preferences.getString(Constants.XMPP_USERNAME, "");

		RequestParams params = new RequestParams();
		params.put("username", username);
		CommonOkHttpClient.post(
				CommonRequest.createPostRequest("http://" + Constants.SERVER_IP + ":8080/EISServer/user/getFriends", params),
				new DisposeDataHandle(new DisposeDataListener() {

					@Override
					public void onSuccess(Object responseObj) {
						UserCustom userCustom = (UserCustom) responseObj;

						for (User u : userCustom.getList()) {
							if (u != null) {
								//String path = "http://" + Constants.SERVER_IP + ":8080/EISServer/user/getUserPhoto?username=";
								String path = "http://" + Constants.SERVER_IP + ":8080/EISServer/images/userphoto/";
								Uri uri = Uri.parse(path + u.getUsername() + "/1.png");
								Log.i(TAG, path + u.getUsername());
								mItemInfoList.add(new ItemInfo(uri, "姓名：" + u.getName(), "账号：" + u.getUsername()));
							}
						}
						mListViewInfo.setAdapter(new CheckUserInfoAdapter(getActivity(), mItemInfoList));
					}

					@Override
					public void onFailure(Object reasonObj) {
						Toast.makeText(getActivity(), "获取用户列表失败！", Toast.LENGTH_SHORT).show();
					}
				}, UserCustom.class));
		
	}
	
	private void initEvent() {
		
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
