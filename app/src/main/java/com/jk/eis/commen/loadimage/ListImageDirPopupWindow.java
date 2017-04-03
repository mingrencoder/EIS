package com.jk.eis.commen.loadimage;

import java.util.List;

import com.jk.eis.R;
import com.jk.eis.commen.loadimage.bean.FolderBean;
import com.jk.eis.commen.loadimage.listener.onDirSelectedListener;
import com.jk.eis.commen.loadimage.util.ImageLoader;
import com.jk.eis.commen.loadimage.util.ImageLoader.Type;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class ListImageDirPopupWindow extends PopupWindow {
	
	private int mWidth;
	private int mHeight;
	private View mConvertView;
	private ListView mListView;
	private List<FolderBean> mDatas;
	
	//持有一个切换点击事件对象
	private onDirSelectedListener mListener;
	
	public void setmListener(onDirSelectedListener mListener) {
		this.mListener = mListener;
	}

	public ListImageDirPopupWindow(Context context, List<FolderBean> datas){
		
		calSize(context);
			
		mConvertView = LayoutInflater.from(context).inflate(R.layout.popupwindow, null);
		mDatas = datas;
		
		setContentView(mConvertView);
		setWidth(mWidth);
		setHeight(mHeight);
		
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);//在外面可以点击
		setBackgroundDrawable(new BitmapDrawable());//点击后可以消失
		
		setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
					dismiss();
					return true;
				}
				return false;
			}
		});
		
		initViews(context);
		initEvent();
	}

	
	private void initViews(Context context) {
		mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
		mListView.setAdapter(new ListDirAdapter(context, mDatas));
	}
	
	/**
	 * 初始化点击事件
	 * 为了解耦，设置itemOnClick事件后设置一个接口进行回调，如果其他的Activity想去执行动作点击该listview，可以通过监听接口的方式
	 * 这里的好处——>可以让任意其他的Activity(不一定是MainActivity)去初始化这个window监听其回调函数
	 */
	private void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mListener != null){
					//将这个FolderBean传出去，供外面的Activity调用
					mListener.onSelected(mDatas.get(position));
				}
			}
		});
		
	}


	/**
	 * 计算popupWindow的宽高
	 * @param context
	 */
	private void calSize(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		
		//设置高度为屏幕高度0.8，宽度为屏幕宽度
		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels*0.8);
	}
	
	/**
	 * 设置一个适配器
	 * 这里直接继承一个ArrayAdapter，泛型是FolderBean，因为这个popupwindow就是包含了很多FolderBean，不需要自己再定义
	 */
	private class ListDirAdapter extends ArrayAdapter<FolderBean>{

		private LayoutInflater mInflater;
		private List<FolderBean> mDatas;
		
		public ListDirAdapter(Context context, List<FolderBean> mDatas) {
			super(context,0,mDatas);
			this.mDatas = mDatas;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.popupwindow_item, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.id_list_dir_item_image);
				holder.mDirName = (TextView) convertView.findViewById(R.id.id_list_dir_item_name);
				holder.mDirCount = (TextView) convertView.findViewById(R.id.id_list_dir_item_count);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			FolderBean bean = mDatas.get(position);
			//重置
			holder.imageView.setImageResource(R.drawable.pictures_no);
			
			//设置图片
			ImageLoader.getInstance(3, Type.LIFO).loadImage(bean.getFirstImgPath(), holder.imageView);
			holder.mDirCount.setText(bean.getCount()+"");
			holder.mDirName.setText(bean.getName());
			
			return convertView;
		}
		//对应布局中的三个控件
		class ViewHolder{
			public ImageView imageView;
			public TextView mDirName;
			public TextView mDirCount;
		}
		
	}
}
