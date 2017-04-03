package com.jk.eis.modules.logtransfer.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.utils.Constants;
import com.jk.eis.modules.logtransfer.activity.LogcheckActivity;
import com.jk.eis.modules.logtransfer.activity.LogmanageActivity;
import com.jk.eis.modules.logtransfer.activity.LoguploadActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class TransferFragment extends Fragment implements OnItemClickListener {

	private View view;

	private String username;
	
	MapDaoHelper helper;


	private GridView gridView;
	private SimpleAdapter simp_adapter;
	private List<Map<String, Object>> dataList;
	private int[] icon = {R.drawable.log_upload,R.drawable.log_check,R.drawable.log_mine};
	private String[] iconName = {"日志上传","日志审查","我的日志"};
	
	public TransferFragment(String username) {
		this.username = username;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		helper = MapDaoHelper.getInstance(getActivity());
		if (view == null) {
			view = inflater.inflate(R.layout.transfer, container, false);
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

		gridView = (GridView) view.findViewById(R.id.id_transfer_gridview);
		dataList = new ArrayList<Map<String,Object>>();
	}
	
	private void initData() {

		simp_adapter = new SimpleAdapter(getActivity(), getData(), R.layout.transfer_gridview_item, 
				new String[]{"icon","name"}, new int[]{R.id.id_transfer_gridview_image,R.id.id_transfer_gridview_tv});
		gridView.setAdapter(simp_adapter);
	}
	
	private void initEvent() {
		gridView.setOnItemClickListener(this);
		
	}

	private List<Map<String, Object>> getData() {
		for(int i=0; i<icon.length; i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("icon", icon[i]);
			map.put("name", iconName[i]);
			dataList.add(map);
		}
		return dataList;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			Intent intent1 = new Intent();
			intent1.setClass(getActivity(), LoguploadActivity.class);
            intent1.putExtra(Constants.USER_USERNAME, username);
			startActivity(intent1);
			break;
		case 1:
			Intent intent2 = new Intent();
			intent2.setClass(getActivity(), LogcheckActivity.class);
			startActivity(intent2);
			break;
		case 2:
			Intent intent3 = new Intent();
			intent3.setClass(getActivity(), LogmanageActivity.class);
			startActivity(intent3);
			break;

		default:
			break;
		}
		
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
