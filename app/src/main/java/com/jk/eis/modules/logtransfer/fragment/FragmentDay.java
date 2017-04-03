package com.jk.eis.modules.logtransfer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.loadimage.LoadImageActivity;
import com.jk.eis.commen.model.Json;
import com.jk.eis.commen.okhttp.CommonOkHttpClient;
import com.jk.eis.commen.okhttp.DisposeDataHandle;
import com.jk.eis.commen.okhttp.listener.DisposeDataListener;
import com.jk.eis.commen.okhttp.request.CommonRequest;
import com.jk.eis.commen.okhttp.request.RequestParams;
import com.jk.eis.commen.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FragmentDay extends Fragment implements OnClickListener{
	private View view;
	private String username;
	MapDaoHelper helper;
	
	private TextView title;
	private TextView finished;
	private TextView unfinished;
	private TextView remark;
	private ImageView picsSlecte;//选取图片
	private EditText picsPaths;//图片路径
	private Button mBtnsubmitDay;
	private Button mBtnReset;

	private static String paths = "";
	private static String pathsName = "";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		helper = MapDaoHelper.getInstance(getActivity());
		if(!(getArguments().getString("username") == null || "".equals(getArguments().getString("username")))){
			username = getArguments().getString("username").toString();
		}
		view = inflater.inflate(R.layout.transfer_log_day, container, false);
		initViews();
		initData();
		initEvent();
		return view;
	}

	private void initViews() {
		title = (TextView) view.findViewById(R.id.id_day_et_title);
		finished = (TextView) view.findViewById(R.id.id_day_et_finished);
		unfinished = (TextView) view.findViewById(R.id.id_day_et_unfinished);
		remark = (TextView) view.findViewById(R.id.id_day_et_remark);
		picsSlecte = (ImageView) view.findViewById(R.id.id_day_imgview_pics);
		picsPaths = (EditText) view.findViewById(R.id.id_day_et_pics);
		mBtnsubmitDay = (Button) view.findViewById(R.id.id_transfer_day_btn_submit);
		mBtnReset = (Button) view.findViewById(R.id.id_transfer_day_btn_reset);
		
	}

	private void initData() {
		
	}

	private void initEvent() {
		picsSlecte.setOnClickListener(this);
		mBtnsubmitDay.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_day_imgview_pics:
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoadImageActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.id_transfer_day_btn_submit:
			send(title.getText().toString(), finished.getText().toString(), unfinished.getText().toString(),
					remark.getText().toString(), paths, username);
			break;
		case R.id.id_transfer_day_btn_reset:
			title.setText("");
			finished.setText("");
			unfinished.setText("");
			remark.setText("");
        	picsPaths.setText("");
        	paths = "";
        	pathsName = "";
			break;

		default:
			break;
		}
	}

	/**
	 * 向服务器发送日志
	 */
	private void send(final String title, final String finished, final String unfinished, final String remark,
			final String paths, final String username) {
		RequestParams params = new RequestParams();
		params.put("title", title);
		params.put("finished", finished);
		params.put("unfinished", unfinished);
		params.put("remark", remark);
		params.put("username", username);
		//params.put("paths", paths);
		//uploadPost(params);
		try {
			uploadFile(params);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void uploadPost(RequestParams params){
		CommonOkHttpClient.post(
				CommonRequest.createPostRequest("http://" + Constants.SERVER_IP + ":8080/EISServer/log/logday", params),
				new DisposeDataHandle(new DisposeDataListener() {

					@Override
					public void onSuccess(Object responseObj) {
						Toast.makeText(getActivity(), ((Json) responseObj).getMsg(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(Object reasonObj) {
						Toast.makeText(getActivity(), "上传失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
					}
			}));
	}

	private void uploadFile(RequestParams params) throws FileNotFoundException {
		String[] strPaths = paths.split(",");
        if(!("".equals(strPaths[0]))){
            for(int i=0; i<strPaths.length; i++){
                params.put(strPaths[i].substring(strPaths[i].lastIndexOf("/")), new File(strPaths[i]));
            }
        }

		CommonOkHttpClient.post(CommonRequest.createMultiPostRequest("http://" + Constants.SERVER_IP + ":8080/EISServer/log/logdayfile", params),
				new DisposeDataHandle(new DisposeDataListener() {

					@Override
					public void onSuccess(Object responseObj) {
						Toast.makeText(getActivity(), "上传图片成功", Toast.LENGTH_SHORT).show();
						title.setText("");
						finished.setText("");
						unfinished.setText("");
						remark.setText("");
						picsPaths.setText("");
						paths = "";
						pathsName = "";
					}

					@Override
					public void onFailure(Object reasonObj) {

					}
				}));
	}

	/**
	 * 图片选择地址回调
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 &&  resultCode == 1){
			paths = "";
			pathsName = "";
			ArrayList<String> list = data.getStringArrayListExtra("paths");
			for(String s : list){
				paths += s + ",";
				pathsName += s.substring(s.lastIndexOf("/")+1) + ",";
			}
            if(!("".equals(paths))){
            	picsPaths.setText(pathsName);
            } else{
            	picsPaths.setText("");
            }
		}
	}

}
