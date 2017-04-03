package com.jk.eis.modules.logtransfer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class LogcheckActivity extends Activity {

    MapDaoHelper helper;

    private ListView mListViewInfo;

    private String username;

    private List<ItemInfo> mItemInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logcheck);

        helper = MapDaoHelper.getInstance(this);
        //加载Fresco
        Fresco.initialize(this);

        initViews();
        initData();
        initEvent();
    }

    private void initViews() {
        mListViewInfo = (ListView) findViewById(R.id.id_logcheck_listview);
        mItemInfoList = new ArrayList<ItemInfo>();
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME_CUSTOM, Context.MODE_PRIVATE);
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
                        mListViewInfo.setAdapter(new CheckUserInfoAdapter(LogcheckActivity.this, mItemInfoList));
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        Toast.makeText(LogcheckActivity.this, "获取用户列表失败！", Toast.LENGTH_SHORT).show();
                    }
                }, UserCustom.class));
    }

    private void initEvent() {

    }
}
