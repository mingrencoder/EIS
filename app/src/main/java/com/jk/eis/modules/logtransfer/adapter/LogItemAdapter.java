package com.jk.eis.modules.logtransfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.eis.R;
import com.jk.eis.modules.logtransfer.bean.LogItem;

import java.util.List;

public class LogItemAdapter extends BaseAdapter {

	private List<LogItem> mList;
	private LayoutInflater mInflater;


	public LogItemAdapter(Context context, List<LogItem> mList) {
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
			convertView = mInflater.inflate(R.layout.log_item, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.id_log_photo);
			holder.name = (TextView) convertView.findViewById(R.id.id_log_name);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		LogItem bean = mList.get(position);
		holder.imageView.setBackgroundResource(bean.getPhotoId());
		holder.name.setText(bean.getName());
		return convertView;
	}
	
	class ViewHolder{
		public ImageView imageView;
		public TextView name;
	}

}
