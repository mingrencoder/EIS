package com.jk.eis.modules.logtransfer.fragment;

import com.jk.eis.R;
import com.jk.eis.commen.greendao.MapDaoHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentEmergency extends Fragment {

	MapDaoHelper helper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		helper = MapDaoHelper.getInstance(getActivity());
		return inflater.inflate(R.layout.transfer_log_emergency, container, false);
	}
}
