package com.jk.eis.modules.map.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.eis.R;
import com.jk.eis.modules.map.listener.DragGridListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DragAdapter extends BaseAdapter implements DragGridListener{
	private List<HashMap<String, Object>> list;
	private LayoutInflater mInflater;
	private Context context;
	private int mHidePosition = -1;
	
	public DragAdapter(Context context, List<HashMap<String, Object>> list){
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.grid_item, null);
		ImageView mImageView = (ImageView) convertView.findViewById(R.id.item_image);
		TextView mTextView = (TextView) convertView.findViewById(R.id.item_text);
		
		/*
		 * 设置imageView
		 * 将list的值传入的是assets的文件名称
		 */
		AssetManager asm=context.getAssets();
		InputStream is = null;
		try {
			is = asm.open(list.get(position).get("item_image")+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap=BitmapFactory.decodeStream(is);
		mImageView.setImageBitmap(bitmap);
		
		//mImageView.setImageResource((Integer) list.get(position).get("item_image"));
		mTextView.setText((CharSequence) list.get(position).get("item_text"));
		
		if(position == mHidePosition){
			convertView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	

	@Override
	public void reorderItems(int oldPosition, int newPosition) {
		HashMap<String, Object> temp = list.get(oldPosition);
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				Collections.swap(list, i, i+1);
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				Collections.swap(list, i, i-1);
			}
		}
		
		list.set(newPosition, temp);
	}

	@Override
	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition; 
		notifyDataSetChanged();
	}


}
