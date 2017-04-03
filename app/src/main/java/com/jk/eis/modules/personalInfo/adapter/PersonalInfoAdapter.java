package com.jk.eis.modules.personalInfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jk.eis.R;
import com.jk.eis.modules.personalInfo.bean.ItemInfo;

import java.util.List;

public class PersonalInfoAdapter extends BaseAdapter {

	private List<ItemInfo> mList;
	private LayoutInflater mInflater;

	
	public PersonalInfoAdapter(Context context, List<ItemInfo> mList) {
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
			convertView = mInflater.inflate(R.layout.personalinfo_item_info, null);
			holder.draweeView = (SimpleDraweeView) convertView.findViewById(R.id.id_personal_image_photo);
			holder.name = (TextView) convertView.findViewById(R.id.id_personal_tv_name);
			holder.username = (TextView) convertView.findViewById(R.id.id_personal_tv_username);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		ItemInfo bean = mList.get(position);
		holder.draweeView.setImageURI(bean.getUri());
		holder.name.setText(bean.getName());
		holder.username.setText(bean.getUsername());
		return convertView;
	}
	
	class ViewHolder{
		public SimpleDraweeView draweeView;
		public TextView name;
		public TextView username;
	}

}
