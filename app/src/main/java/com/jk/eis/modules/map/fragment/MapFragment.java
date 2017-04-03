package com.jk.eis.modules.map.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jk.eis.R;
import com.jk.eis.commen.framework.DragGridView;
import com.jk.eis.commen.framework.FlowLayout;
import com.jk.eis.commen.greendao.Deposition;
import com.jk.eis.commen.greendao.Field;
import com.jk.eis.commen.greendao.MapDaoHelper;
import com.jk.eis.commen.greendao.SegType;
import com.jk.eis.commen.greendao.Segment;
import com.jk.eis.commen.utils.StringUtils;
import com.jk.eis.modules.map.adapter.DragAdapter;
import com.jk.eis.modules.map.listener.DisposeShipSegListener;
import com.jk.eis.modules.map.listener.InformFieldListener;
import com.jk.eis.modules.map.listener.InformShipSegListenr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment {

    private View view;

    /**
     * 制订一个命名规则，方便解析
     */
    private LinkedHashMap<Integer, String> mShipUnitVals1;
    private LinkedHashMap<Integer, String> mShipUnitVals2;
    private LinkedHashMap<Integer, String> mShipUnitVals3;
    private LinkedHashMap<Integer, String> mShipUnitVals4;
    private LinkedHashMap<Integer, String> mShipSegListChild;

    private List<LinkedHashMap<Integer, String>> mShipSegList;
    private List<HashMap<String, Object>> mFieldsList;
    /**
     * 数据库查询数据
     */
    private List<Field> mFieldList;
    private List<Segment> mSegmentList;
    private List<SegType> mSegTypeList;


    private ImageView mImgSeg;

    //对话框属性
    private TextView mInfoSegName;
    private TextView mInfoSegDetail;
    private TextView mInfoSegLocation;

    private TextView mConfirmSegName;
    private TextView mConfirmFiledName;
    private EditText mConfirmSegCount;
    private EditText mSegBeginDate;
    private EditText mSegEndDate;
    private Button mBtnConfirm;
    private Button mBtnCancel;

    private TextView mFieldName;
    private TextView mFieldType;

    private ListView depositionListView;
    private ArrayAdapter<String> arr_adapter;

    //两个布局
    private FlowLayout mFlowlayout;
    private DragGridView mDragGridView;

    //适配器
    private DragAdapter mDragAdapter;

    MapDaoHelper helper;

    /**
     * 创建Fragment时会回调，只会调用一次
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        helper = MapDaoHelper.getInstance(getActivity());
        if (view == null) {
            view = inflater.inflate(R.layout.map, container, false);
            initViews(view);
            initData();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;

    }

    private void initViews(View view) {
        mFieldsList = new ArrayList<HashMap<String, Object>>();
        mShipSegList = new ArrayList<LinkedHashMap<Integer, String>>();
        mShipUnitVals1 = new LinkedHashMap<Integer, String>();
        mShipUnitVals2 = new LinkedHashMap<Integer, String>();
        mShipUnitVals3 = new LinkedHashMap<Integer, String>();
        mShipUnitVals4 = new LinkedHashMap<Integer, String>();

        mFlowlayout = (FlowLayout) view.findViewById(R.id.id_flowlayout);
        mDragGridView = (DragGridView) view.findViewById(R.id.dragGridView);
        mDragAdapter = new DragAdapter(getActivity(), mFieldsList);

    }

    private void initData() {

        /***************************************************************************
         * 暂时写死的
         */
        //initShipUnitList();
        //initFielList();

        //helper.initdata();
        mFieldList = helper.getFieldAll();
        mSegmentList = helper.getSegmentAll();
        mSegTypeList = helper.getSegTypeAll();

        /**
         * 读取场地信息
         */
        for (int i = 0; i < mFieldList.size(); i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            for (int j = 0; j < mSegTypeList.size(); j++) {
                if (mFieldList.get(i).getType() == mSegTypeList.get(j).getId()) {
                    itemHashMap.put("item_image", mSegTypeList.get(j).getTypeNum());
                    itemHashMap.put("item_text", mFieldList.get(i).getName());
                }
            }

            //同一场地可多次放置
            /*if(mFieldList.get(i).getSeg_id() != null){
				itemHashMap.put("item_image", "type0.png");
				itemHashMap.put("item_text", mFieldList.get(i).getName());
			}*/
            mFieldsList.add(itemHashMap);
        }
        /**
         * 读取分段信息
         */
        HashSet<Integer> types = helper.getSegmentTypes();
        //分组index
        int index = 1;
        for (int type : types) {
            LinkedHashMap<Integer, String> mShipUnitVals = new LinkedHashMap<Integer, String>();
            //第一个放置分组名
            mShipUnitVals.put(index, helper.getSegType(type).getDescription());
            for (int i = 1; i <= mSegmentList.size(); i++) {
                if (mSegmentList.get(i - 1).getType() == type) {
                    mShipUnitVals.put(new Long(mSegmentList.get(i - 1).getId()).intValue(), mSegmentList.get(i - 1).getName());
                }
            }
            index++;
            mShipSegList.add(mShipUnitVals);
        }

        initFlowLayout();
        initGridView();

    }

/*	private void initShipUnitList() {
		mShipUnitVals1.put(1, "船舶种类");
		for (int i = 1; i <= 5; i++) {
			mShipUnitVals1.put(Integer.parseInt("1" + i), "类型" + i);
		}
		mShipUnitVals2.put(2, "重要组件");
		for (int i = 1; i <= 4; i++) {
			mShipUnitVals2.put(Integer.parseInt("2" + i), "重要组件" + i);
		}
		mShipUnitVals3.put(3, "一般组件");
		for (int i = 1; i <= 8; i++) {
			mShipUnitVals3.put(Integer.parseInt("3" + i), "一般组件" + i);
		}
		mShipUnitVals4.put(4, "特殊组件");
		for (int i = 1; i <= 12; i++) {
			mShipUnitVals4.put(Integer.parseInt("4" + i), "特殊组件" + i);
		}

		mShipSegList.add(mShipUnitVals1);
		mShipSegList.add(mShipUnitVals2);
		mShipSegList.add(mShipUnitVals3);
		mShipSegList.add(mShipUnitVals4);
	}*/


/*	private void initFielList() {

		*/

    /***************************************************************************
     * 得到数据，判断场地是否被占用，放置不同背景图片
     *//*
		for (int i = 0; i < 38; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image", R.drawable.area);
			itemHashMap.put("item_text", "拖拽" + Integer.toString(i));
			mFieldsList.add(itemHashMap);
		}
		
	}*/
    private void initFlowLayout() {
        final MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mFlowlayout.removeAllViews();
        mImgSeg = new ImageView(getActivity());
        mImgSeg.setBackgroundResource(R.drawable.boat);
        mFlowlayout.addView(mImgSeg, params);
        for (int i = 0; i < mShipSegList.size(); i++) {
            // 一行数据，代表一组船舶部件分类
            mShipSegListChild = mShipSegList.get(i);
            Button button = new Button(getActivity());
            button.setId(i);
            // 第一个数据就设为分组名字
            button.setText(mShipSegListChild.get((i + 1)));
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mFlowlayout.removeAllViews();
                    mImgSeg = new ImageView(getActivity());
                    mImgSeg.setBackgroundResource(R.drawable.boat);
                    mFlowlayout.addView(mImgSeg, params);

                    LinkedHashMap<Integer, String> s = mShipSegList.get(v.getId());
                    Iterator<Entry<Integer, String>> it = s.entrySet().iterator();
                    // 除去第一个名称元素，并把第一个元素id赋给back
                    int mGroupId = it.next().getKey();
                    s.remove(s.get(mGroupId));
                    while (it.hasNext()) {
                        Entry entry = (Entry) it.next();
                        ToggleButton mtbtn = new ToggleButton(getActivity());
                        mtbtn.setId((Integer) entry.getKey());
                        mtbtn.setText((String) entry.getValue());
                        mtbtn.setTextOn(entry.getValue() + "(已选中)");
                        mtbtn.setTextOff((String) entry.getValue());
                        mFlowlayout.addView(mtbtn, params);
                    }
                    Button mBackBtn = new Button(getActivity());
                    mBackBtn.setId(mGroupId);
                    mBackBtn.setText("返回");
                    mBackBtn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            initFlowLayout();
                        }
                    });
                    mFlowlayout.addView(mBackBtn, params);
                }
            });
            mFlowlayout.addView(button, params);
        }

        //没用的
        //mFlowlayout.setDragGridAdapter(mDragAdapter);
        mFlowlayout.setQueryShipUnitListenr(new InformShipSegListenr() {

            @Override
            public void querySegInfo(int unitId) {
                // 假设这里是通过unitId获得了部件详细数据
                Log.i("tag", unitId + "");
                String name = helper.getSegmentById(unitId).getName();
                String des = helper.getSegmentById(unitId).getDescription();
                List<Field> flist = helper.getFieldListBySegId(unitId);
                String ff = "";
                for (Field f : flist) {
                    ff += f.getName() + " ";
                }
                String[] mUnitInfo = new String[]{name, des, ff};
                showInfoDialog(mUnitInfo);
            }

        });
        mFlowlayout.setmDisposeShipSegListener(new DisposeShipSegListener() {

            @Override
            public void putUnitIn(int id, int groupId, int x, int y) {

                int areaNum = mDragGridView.pointToPosition(x, y);
                if (areaNum == -1) {
                    Toast.makeText(getActivity(), "请在目标区域内选择", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("tag", id + "");
                Log.i("tag", groupId + "");
                Log.i("tag", areaNum + "");
                /***************************************************************************
                 * 根据场地field类型判断是否可以放置
                 */

                if (!(String.valueOf(id).substring(0, 1).equals(String.valueOf(mFieldList.get(areaNum).getType())))) {
                    Toast.makeText(getActivity(), "场地类型不符，请挑选合适场地", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDisposeDialog(id, groupId, areaNum);
            }
        });
    }

    private void initGridView() {

        mDragGridView.setAdapter(mDragAdapter);

        mDragGridView.setmInformFieldListener(new InformFieldListener() {
            @Override
            public void queryFieldInfo(int id) {
                Log.i(TAG, "field: " + id);
                Field field = mFieldList.get(id);
                if (field != null) {
                    showDeposition(field);
                }
            }
        });
    }

    /**
     * 展示分段详细信息
     *
     * @param mUnitInfo
     */
    private void showInfoDialog(String[] mUnitInfo) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout groupPollingAddress = (LinearLayout) inflater.inflate(R.layout.dialog_shipseg, null);
        Builder builder = new Builder(getActivity(),3);
        builder.setTitle("船舶分段详情");
        builder.setIcon(R.drawable.unit);
        // 一定要在这里面定义
        mInfoSegName = (TextView) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_tv_name);
        mInfoSegDetail = (TextView) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_tv_detail);
        mInfoSegLocation = (TextView) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_tv_location);
        mInfoSegName.setText("名称：" + mUnitInfo[0]);
        mInfoSegDetail.setText("作用：" + mUnitInfo[1]);
        if (!(mUnitInfo[2].equals("") || mUnitInfo[2].equals(null))) {
            mInfoSegLocation.setText("位置：" + mUnitInfo[2]);
        } else {
            mInfoSegLocation.setText("位置：暂未被分配");
        }
        builder.setView(groupPollingAddress);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 展示场地存储详情
     * @param field
     */
    private void showDeposition(Field field) {
        String name = field.getName();
        String type = helper.getSegType(field.getType()).getDescription();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout groupPollingAddress = (LinearLayout) inflater.inflate(R.layout.dialog_deposition, null);
        Builder builder = new Builder(getActivity(),3);
        builder.setTitle("堆放详情");
        builder.setIcon(R.drawable.unit);
        // 一定要在这里面定义
        mFieldName = (TextView) groupPollingAddress.findViewById(R.id.id_Deposition_Dialog_tv_name_Dialog_tv_name);
        mFieldType = (TextView) groupPollingAddress.findViewById(R.id.id_Deposition_Dialog_tv_type);
        mFieldName.setText("名称：" + name);
        mFieldType.setText("类型：" + type);

        depositionListView = (ListView) groupPollingAddress.findViewById(R.id.id_Deposition_Dialog_listview);
        List<Deposition> depositionList = helper.getDepositionByField(field);
        if (depositionList.size() == 0) {
            String[] arr_data = {"暂无分段放置"};
            //设置堆放详情
            arr_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arr_data);
            depositionListView.setAdapter(arr_adapter);
        } else {
            String[] arr_data = new String[depositionList.size()];
            for (int i = 0; i < depositionList.size(); i++) {
                Deposition deposition = depositionList.get(i);
                Segment segment = helper.getSegmentById(deposition.getSegment_id().intValue());
                String begin = deposition.getBegin_date().toString();
                String end = deposition.getEnd_date().toString();
                begin = begin.substring(0, 4) + "年" + begin.substring(4, 6) + "月" + begin.substring(6) + "日";
                end = end.substring(0, 4) + "年" + end.substring(4, 6) + "月" + end.substring(6) + "日";

                arr_data[i] = begin + "到" + end + "-" + segment.getName();
                //设置堆放详情
                arr_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arr_data);
                depositionListView.setAdapter(arr_adapter);
            }
        }
        builder.setView(groupPollingAddress);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * 放置分段处理窗口
     *
     * @param id
     * @param groupId
     * @param areaNum
     */
    private void showDisposeDialog(final int id, final int groupId, final int areaNum) {

        /**
         * 根据id和areaName值查到的数据
         */
        String segName = mShipSegList.get(groupId - 1).get(id);
        String areaName = (String) mFieldsList.get(areaNum).get("item_text");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout groupPollingAddress = (RelativeLayout) inflater.inflate(R.layout.dialog_confirm, null);
        Builder builder = new Builder(getActivity(),3);
        builder.setTitle("位置分配");
        builder.setIcon(R.drawable.unit);
        // 一定要在这里面初始化
        mConfirmSegName = (TextView) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_confirm_name_tv);
        mConfirmFiledName = (TextView) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_field_name_tv);
        mConfirmSegCount = (EditText) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_confirm_count);
        mSegBeginDate = (EditText) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_confirm_beginDate);
        mSegEndDate = (EditText) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_confirm_endDate);
        mBtnConfirm = (Button) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_btn_confirm);
        mBtnCancel = (Button) groupPollingAddress.findViewById(R.id.id_ShipSeg_Dialog_btn_cancel);
        // 设置监听器
        mSegBeginDate.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDlg(mSegBeginDate);
                    return true;
                }
                return false;
            }
        });
        mSegBeginDate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg(mSegBeginDate);
                }
            }
        });
        mSegEndDate.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDlg(mSegEndDate);
                    return true;
                }
                return false;
            }
        });
        mSegEndDate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg(mSegEndDate);
                }
            }
        });

        mConfirmSegName.setText("分段名称：" + segName);
        mConfirmFiledName.setText("区域名称：" + areaName);

        builder.setView(groupPollingAddress);
        final AlertDialog dialog = builder.create();
        mBtnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String count = mConfirmSegCount.getText().toString().trim();
                String beginDate = mSegBeginDate.getText().toString().trim();
                String endDate = mSegEndDate.getText().toString().trim();

                /***************************************************************************
                 * 判断参数是否有效更新数据库
                 */
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher isNum = pattern.matcher(count);
                if (!isNum.matches()) {
                    Toast.makeText(getActivity(), "请输入正确的数字！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (beginDate == null || beginDate.equals("") || endDate == null || endDate.equals("")) {
                    Toast.makeText(getActivity(), "请输入起始日期！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                int cNum = 0;
                if (StringUtils.isNumber(count)) {
                    cNum = Integer.parseInt(count);
                }
                int begin = formatDate(beginDate);
                int end = formatDate(endDate);

                if (begin > end) {
                    Toast.makeText(getActivity(), "结束日期必须在起始日期之后！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                Segment segment = new Segment((long) id);
                Field field = mFieldList.get(areaNum);
                //判断输入区间是否和已有分配日期区间冲突
                List<Deposition> depositionList = helper.getDepositionByField(field);
                for (int i = 0; i < depositionList.size(); i++) {
                    if (begin > depositionList.get(i).getEnd_date()) {
                        continue;
                    } else if (end < depositionList.get(i).getBegin_date()) {
                        continue;
                    } else {
                        Toast.makeText(getActivity(), "该区域当前时间段已被占用", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                }

                if (!(begin == 0 || end == 0 || cNum == 0) || segment == null || field == null) {
                    try {
                        helper.assignSegmentToField(segment, field, cNum, begin, end);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "分配失败，请检查输入参数", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    Toast.makeText(getActivity(), "分配成功", Toast.LENGTH_SHORT).show();
                }
                mDragGridView.setAdapter(mDragAdapter);

                dialog.dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                return;
            }
        });
        dialog.show();

    }

    private int formatDate(String s) {
        String m = s.substring(s.indexOf("-") + 1, s.lastIndexOf("-"));
        String d = s.substring(s.lastIndexOf("-") + 1);
        if (m.length() == 1) {
            m = "0" + m;
        }
        if (d.length() == 1) {
            d = "0" + d;
        }
        s = s.substring(0, s.indexOf("-")) + m + d;
        if (StringUtils.isNumber(s)) {
            int ss = Integer.parseInt(s);
            return ss;
        }
        return 0;
    }

    protected void showDatePickDlg(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /**
     * 当Fragment被添加到Activity时候会回调这个方法，并且只调用一次
     */
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

    }

    /**
     * 当Fragment所在的Activty启动完成后调用
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 启动Fragment
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 恢复Fragment时会被回调，调用onStart（）方法后面一定会调用onResume()方法
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 暂停Fragment
     */
    @Override
    public void onPause() {
        super.onPause();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
    }

    /**
     * 停止Fragment
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 销毁Fragment所包含的View组件时
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 销毁Fragment时会被回调
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Fragment从Activity中删除时会回调该方法，并且这个方法只会调用一次
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
