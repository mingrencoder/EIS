package com.jk.eis.commen.loadimage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jk.eis.R;
import com.jk.eis.commen.loadimage.bean.FolderBean;
import com.jk.eis.commen.loadimage.listener.onDirSelectedListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.provider.ContactsContract.Contacts;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoadImageActivity extends Activity {

	private GridView mGridView;
	//当前文件夹下的没完整路径的文件名
	private List<String> mImgs;
	private ImageAdapter mImgAdapter;
	//popupwindow
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	//底部区域，可以弹出popupwindow
	private RelativeLayout mBottomLy;
	private TextView mDirName, mDirCount;
	private ProgressDialog progressDialog;
	//当前文件夹
	private File mCurrentDir;
	//文件夹最多的图片数量
	private int mMaxCount;
	//所有文件夹
	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();

	private Button mBtnfinish;
	
	private static final int DATA_LOADED = 0x110;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == DATA_LOADED){
				progressDialog.dismiss();
				//绑定数据到View中
				data2View();
				//初始化popupwindow
				initPopupWindow();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadimage_main);

		initView();
		initDatas();
		initEvent();
	}

	protected void initPopupWindow() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
		//设置画面点亮事件
		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		mListImageDirPopupWindow.setmListener(new onDirSelectedListener() {
			
			@Override
			public void onSelected(FolderBean folderBean) {
				mCurrentDir = new File(folderBean.getDir());
				data2View();
				mListImageDirPopupWindow.dismiss();
			}
		});
	}

	/**
	 * 当点开popupwindow时，内容区域会变亮
	 */
	protected void lightOn() {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		getWindow().setAttributes(params);
	}
	/**
	 * 当点开popupwindow时，内容区域会变暗
	 */
	protected void lightOff() {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 0.3f;
		getWindow().setAttributes(params);
	}

	protected void data2View() {
		if(mCurrentDir == null){
			Toast.makeText(LoadImageActivity.this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
			return;
		} else{
			//利用Filter判断
			List<String> mImgsList = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".jpeg")
							|| name.endsWith(".jpg")
							|| name.endsWith(".png")){
						return true;
					}
					return false;
				}
			}));
			//初始化ImageAdapter
			mImgAdapter = new ImageAdapter(LoadImageActivity.this, mImgsList, mCurrentDir.getAbsolutePath());
			mGridView.setAdapter(mImgAdapter);
			mDirCount.setText(mImgsList.size()+"");
			mDirName.setText(mCurrentDir.getName());
		}
		
	}

	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				lightOff();
			}
		});
		mBtnfinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				ArrayList<String> list = new ArrayList<String>(mImgAdapter.getmSelectedImg());
				data.putExtra("paths", list);
				setResult(1, data);
				finish();
			}
		});
	}

	/**
	 * 开启一个线程，获得图片，更新ImageView控件
	 */
	private void initDatas() {
		if ((Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED))) {
			Toast.makeText(this, "当前存储卡不可用!", Toast.LENGTH_SHORT).show();
		}

		progressDialog = ProgressDialog.show(this, null, "正在加载……");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver resolver = getContentResolver();

				Cursor c = resolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				//这里存储的是所有扫描过的文件夹
				Set<String> mDirPaths =  new HashSet<String>();
				
				if(c!=null){
					while(c.moveToNext()){
						//当前图片的路径
						String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
						File parentFile = new File(path).getParentFile();
						//有时候ContentProvider存在某些图片，但是找不到它的父路径
						if(parentFile == null){
							continue;
						}
						
						String dirPath = parentFile.getAbsolutePath();
						/*
						 * 这里需要扫描文件夹下面所有的图片，而文件夹下面的图片的parentFile都是一样的，会导致重复遍历。
						 * 所以这里设置了一个Set<String>
						 */
						
						FolderBean folderBean = null;
						
						if(mDirPaths.contains(dirPath)){
							//如果包含的话，则代表这个文件夹已经扫描过
							continue;
						} else{
							mDirPaths.add(dirPath);
							folderBean = new FolderBean();
							folderBean.setDir(dirPath);
							folderBean.setFirstImgPath(path);
						}
						
						//有些文件特殊，找不到
						if(parentFile.list() == null){
							continue;
						}
						
						int picCount = parentFile.list(new FilenameFilter() {
							/**
							 * 因为最终要得到的是图片的数量，所以判断一下遍历的文件是否是图片
							 */
							@Override
							public boolean accept(File dir, String name) {
								if(name.endsWith(".jpeg")
										|| name.endsWith(".jpg")
										|| name.endsWith(".png")){
									return true;
								}
								return false;
							}
						}).length;
						folderBean.setCount(picCount);
						mFolderBeans.add(folderBean);
						
						//给GridView 得到图片数量最多的文件夹
						if(picCount > mMaxCount){
							mMaxCount = picCount;
							mCurrentDir = parentFile;
						}
					}
				}
				c.close();
				//通知Handler扫描完成
				mHandler.sendEmptyMessage(DATA_LOADED);
			}
		}).start();

	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.gridView);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mDirName = (TextView) findViewById(R.id.id_dir_name);
		mDirCount = (TextView) findViewById(R.id.id_dir_count);
		mBtnfinish = (Button) findViewById(R.id.id_loadImg_confirm);
	}
}
