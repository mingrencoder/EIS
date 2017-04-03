package com.jk.eis.modules.logtransfer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.utils.Constants;
import com.jk.eis.modules.logtransfer.adapter.MyFragmentPagerAdapter;
import com.jk.eis.modules.logtransfer.fragment.FragmentDay;
import com.jk.eis.modules.logtransfer.fragment.FragmentEmergency;
import com.jk.eis.modules.logtransfer.fragment.FragmentMonth;
import com.jk.eis.modules.logtransfer.fragment.FragmentWeek;
import com.jk.eis.modules.logtransfer.fragment.FragmentYear;

import java.util.ArrayList;
import java.util.List;

public class LoguploadActivity extends FragmentActivity implements OnPageChangeListener{
	private String username;
	MapDaoHelper helper;

	private List<String> titleList;
	private List<Fragment> fragList;
	private FragmentDay fragmentDay;
	private FragmentWeek fragmentWeek;
	private FragmentMonth fragmentMonth;
	private FragmentYear fragmentYear;
	private FragmentEmergency fragmentEmergency;
	
	private PagerTabStrip tab;
	private ViewPager pager;
	private MyFragmentPagerAdapter fragAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logupload);
		
		Intent intent = getIntent();
        username = intent.getStringExtra(Constants.USER_USERNAME);
		helper = MapDaoHelper.getInstance(this);

		initViews();
		initData();
		initEvent();
		
	}

	private void initViews() {
		//设置Fragment数据源
		fragList = new ArrayList<Fragment>();
		//设置标题
		titleList = new ArrayList<String>();
		//初始化PagerTabStrip并设置属性
		tab = (PagerTabStrip) findViewById(R.id.tabstrip);
		//初始化ViewPager
		pager = (ViewPager) findViewById(R.id.viewPager);
		
		fragmentDay = new FragmentDay();
		fragmentWeek = new FragmentWeek();
		fragmentMonth = new FragmentMonth();
		fragmentYear = new FragmentYear();
		fragmentEmergency = new FragmentEmergency();
	}

	private void initData() {
        //向各个fragmen发送username
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		fragmentDay.setArguments(bundle);
		fragmentWeek.setArguments(bundle);
		fragmentMonth.setArguments(bundle);
		fragmentYear.setArguments(bundle);
		fragmentEmergency.setArguments(bundle);
		
		fragList.add(fragmentDay);
		fragList.add(fragmentWeek);
		fragList.add(fragmentMonth);
		fragList.add(fragmentYear);
		fragList.add(fragmentEmergency);
		
		titleList.add("日报");
		titleList.add("周报");
		titleList.add("月报");
		titleList.add("年报");
		titleList.add("突发事件");
		
		tab.setBackgroundColor(Color.LTGRAY);
		tab.setTextColor(Color.BLACK);
		//tab.setDrawFullUnderline(true);
		tab.setTabIndicatorColor(Color.TRANSPARENT);
	}

	private void initEvent() {
		fragAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragList,titleList);
		pager.setAdapter(fragAdapter);
		
		//加载页面切换监听器
		pager.setOnPageChangeListener(this);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		Toast.makeText(this, "当前是第" + (arg0+1) + "个页面", Toast.LENGTH_SHORT).show();;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
