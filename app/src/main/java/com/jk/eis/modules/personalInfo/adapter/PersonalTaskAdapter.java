package com.jk.eis.modules.personalInfo.adapter;

import java.util.List;

import com.jk.eis.R;
import com.jk.eis.modules.personalInfo.bean.ItemTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalTaskAdapter extends BaseAdapter {

	private List<ItemTask> mList;
	private LayoutInflater mInflater;

	
	public PersonalTaskAdapter(Context context, List<ItemTask> mList) {
		this.mList = mList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.personalinfo_item_task, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.id_personal_image_task);
			holder.name = (TextView) convertView.findViewById(R.id.id_personal_tv_task);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		ItemTask bean = mList.get(position);
		holder.imageView.setBackgroundResource(bean.getPhotoId());
		holder.name.setText(bean.getName());
		return convertView;
	}
	
	class ViewHolder{
		public ImageView imageView;
		public TextView name;
	}

}
