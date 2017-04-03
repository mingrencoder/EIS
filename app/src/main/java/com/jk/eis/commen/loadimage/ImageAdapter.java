package com.jk.eis.commen.loadimage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jk.eis.R;
import com.jk.eis.commen.loadimage.util.ImageLoader;
import com.jk.eis.commen.loadimage.util.ImageLoader.Type;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * GridView的图片适配器
 * @author Ji Kai
 *
 */
public class ImageAdapter extends BaseAdapter {

	//用来得到被选取的路径
	public static Set<String> getmSelectedImg() {
		return mSelectedImg;
	}

	private Context context;
	private LayoutInflater mInflater;
	private List<String> mImgPaths;
	private String mDirPath;

	private int mScreenWitdh;
	
	//为了保证选择图片的记录可以保存，这里使用static修饰已经选择的图片
	private static Set<String> mSelectedImg = new HashSet<String>();
	
	public ImageAdapter(Context context, List<String> mImgPaths, String mDirPath) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mImgPaths = mImgPaths;
		this.mDirPath = mDirPath;
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
	}

	@Override
	public int getCount() {
		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.loadimage_item, parent, false);
			holder.mImg = (ImageView) convertView.findViewById(R.id.imageView);
			holder.mSelect = (ImageButton) convertView.findViewById(R.id.imageButton);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		//为view中的各个控件重置状态
		holder.mImg.setBackgroundResource(R.drawable.pictures_no);
		holder.mImg.setColorFilter(null);
		holder.mSelect.setImageResource(R.drawable.picture_unselected);
		
		//优化，先为ImageView设置最大宽度，如果另外再设置，这里就不会有影响，不会再起效果
		holder.mImg.setMaxWidth(mScreenWitdh/3);
		
		//实现图片加载
		final String mImgPath = mDirPath + "/" + mImgPaths.get(position);
		ImageLoader.getInstance(3, Type.LIFO).loadImage(mImgPath, holder.mImg);
		//这种就是原始的方式，坏处是没有压缩，非常卡顿
		//holder.mImg.setImageBitmap(BitmapFactory.decodeFile(mImgPath));
		holder.mSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//已被选择，注意这里的逻辑，如果已经选择了，再点击就是反选了，所以要remove
				if(mSelectedImg.contains(mImgPath)){
					mSelectedImg.remove(mImgPath);
					holder.mImg.setColorFilter(null);
					holder.mSelect.setImageResource(R.drawable.picture_unselected);
				} else{
					mSelectedImg.add(mImgPath);//设置状态
					holder.mImg.setColorFilter(Color.parseColor("#77000000"));
					holder.mSelect.setImageResource(R.drawable.pictures_selected);
				}
				
			}
		});
		
		holder.mImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final MyDialog dialog = new MyDialog(context, mImgPath);
				dialog.setOnmListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImg.contains(mImgPath))
		{
			holder.mImg.setColorFilter(Color.parseColor("#77000000"));
			holder.mSelect.setImageResource(R.drawable.pictures_selected);
		}
		
		
		return convertView;
	}

	class ViewHolder{
		public ImageView mImg;
		public ImageButton mSelect;
	}

}
