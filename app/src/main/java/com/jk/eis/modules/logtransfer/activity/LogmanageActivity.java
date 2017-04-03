package com.jk.eis.modules.logtransfer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.modules.logtransfer.adapter.LogItemAdapter;
import com.jk.eis.modules.logtransfer.bean.LogItem;

import java.util.ArrayList;
import java.util.List;

public class LogmanageActivity extends Activity {

	MapDaoHelper helper;

	private ListView mListViewLog;
	private List<LogItem> mLogItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logmanage);
		
		helper = MapDaoHelper.getInstance(this);

		initViews();
		initData();
	}

	private void initViews() {
		mListViewLog = (ListView) findViewById(R.id.id_logmine_listview);
		mLogItemList = new ArrayList<LogItem>();
	}

	private void initData() {
		LogItem logItem1 = new LogItem(R.drawable.log_day,"2017年1月23日日报");
		LogItem logItem2 = new LogItem(R.drawable.log_week,"2017年1月第三次周报");
		LogItem logItem3 = new LogItem(R.drawable.log_day,"2017年1月22日日报");
		LogItem logItem4 = new LogItem(R.drawable.log_year,"2016年年度总结");
		LogItem logItem5 = new LogItem(R.drawable.log_emergency,"XX部门紧急事故");
		LogItem logItem6 = new LogItem(R.drawable.log_day,"2017年1月21日日报");
		LogItem logItem7 = new LogItem(R.drawable.log_day,"2017年1月20日日报");
		LogItem logItem8 = new LogItem(R.drawable.log_day,"2017年1月19日日报");
		mLogItemList.add(logItem1);
		mLogItemList.add(logItem2);
		mLogItemList.add(logItem3);
		mLogItemList.add(logItem4);
		mLogItemList.add(logItem5);
		mLogItemList.add(logItem6);
		mLogItemList.add(logItem7);
		mLogItemList.add(logItem8);
		mListViewLog.setAdapter(new LogItemAdapter(LogmanageActivity.this, mLogItemList));

	}

	public void doClick(View view) {
		switch (view.getId()) {
			case R.id.id_logmine_search_btn:

				LayoutInflater inflater = LayoutInflater.from(LogmanageActivity.this);
				LinearLayout groupPollingAddress = (LinearLayout) inflater.inflate(R.layout.dialog_loginfo, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(LogmanageActivity.this,3);
				builder.setTitle("日报信息");
				builder.setIcon(R.drawable.log_day);
				// 一定要在这里面定义
				TextView name = (TextView) groupPollingAddress.findViewById(R.id.id_logday_name);
				TextView finished = (TextView) groupPollingAddress.findViewById(R.id.id_logday_finished);
				TextView unfinished = (TextView) groupPollingAddress.findViewById(R.id.id_logday_unfinished);
				TextView remark = (TextView) groupPollingAddress.findViewById(R.id.id_logday_remark);
				name.setText("2017年1月23日日报");
				finished.setText("已完成工作：xxxxxxxxx");
				unfinished.setText("未完成工作：xxxxxxxxx");
				remark.setText("备注信息： xxxxxxxxx");

				builder.setView(groupPollingAddress);
				AlertDialog dialog = builder.create();
				dialog.show();
				break;

			default:
				break;
		}
	}
}
