package com.jk.eis.commen.framework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.jk.eis.modules.map.listener.DragGridListener;
import com.jk.eis.modules.map.listener.InformFieldListener;

/**
 * @blog http://blog.csdn.net/xiaanming 
 * 
 * @author xiaanming
 *
 */
public class DragGridView extends GridView{
	/**
	 * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
	 */
	private long dragResponseMS = 1000;
	
	/**
	 * 是否可以拖拽，默认不可以
	 */
	private boolean isDrag = false;
	
	private int mDownX;
	private int mDownY;
	private int moveX;
	private int moveY;
	/**
	 * 初始的position
	 */
	private int mDragPosition;
	
	/**
	 * 刚开始拖拽的item对应的View
	 */
	private View mStartDragItemView = null;
	
	/**
	 * 震动器
	 */
	private Vibrator mVibrator;
	
	private WindowManager mWindowManager;

	/**
	 * 按下的点到所在item的上边缘的距离
	 */
	private int mPoint2ItemTop ; 
	
	/**
	 * 按下的点到所在item的左边缘的距离
	 */
	private int mPoint2ItemLeft;
	
	/**
	 * DragGridView距离屏幕顶部的偏移量
	 */
	private int mOffset2Top;
	
	/**
	 * DragGridView距离屏幕左边的偏移量
	 */
	private int mOffset2Left;
	
	/**
	 * 状态栏的高度
	 */
	private int mStatusHeight; 
	
	/**
	 * DragGridView自动向下滚动的边界值
	 */
	private int mDownScrollBorder;
	
	/**
	 * DragGridView自动向上滚动的边界值
	 */
	private int mUpScrollBorder;
	
	/**
	 * DragGridView自动滚动的速度
	 */
	private static final int speed = 20;
	
	//重排数据接口
	private DragGridListener mDragAdapter;
	//展示数据接口
	private InformFieldListener mInformFieldListener;
	private int mNumColumns;
	private int mColumnWidth;
	private boolean mNumColumnsSet;
	private int mHorizontalSpacing;


	public void setmInformFieldListener(InformFieldListener mInformFieldListener) {
		this.mInformFieldListener = mInformFieldListener;
	}

	/*******************************************
	 * 构造方法
	 * @param context
	 ******************************************/
	public DragGridView(Context context) {
		this(context, null);
	}
	
	public DragGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mStatusHeight = getStatusHeight(context); //获取状态栏的高度
		
		if(!mNumColumnsSet){
			mNumColumns = AUTO_FIT;
		}
		
	}
	
	/*******************************************
	 * 覆写方法
	 * 
	 ******************************************/
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		
		if(adapter instanceof DragGridListener){
			mDragAdapter = (DragGridListener) adapter;
		}else{
			throw new IllegalStateException("the adapter must be implements DragGridAdapter");
		}
	}
	

	@Override
	public void setNumColumns(int numColumns) {
		super.setNumColumns(numColumns);
		mNumColumnsSet = true;
		this.mNumColumns = numColumns;
	}
	
	
	@Override
	public void setColumnWidth(int columnWidth) {
	    super.setColumnWidth(columnWidth);
	    mColumnWidth = columnWidth;
	}
	
	
    @Override
	public void setHorizontalSpacing(int horizontalSpacing) {
		super.setHorizontalSpacing(horizontalSpacing);
		this.mHorizontalSpacing = horizontalSpacing;
	}
    

    /**
     * 若设置为AUTO_FIT，计算有多少列
     */
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                        - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        } 

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	
	/*******************************************
	 * 各个变量的初始化开始
	 * 
	 ******************************************/
	
	/**
	 * 设置响应拖拽的毫秒数，默认是1000毫秒
	 * @param dragResponseMS
	 */
	public void setDragResponseMS(long dragResponseMS) {
		this.dragResponseMS = dragResponseMS;
	}

	private Handler mHandler = new Handler();
	
	//用来处理是否为长按的Runnable
	private Runnable mLongClickRunnable = new Runnable() {
		
		@Override
		public void run() {
//			if (mInformFieldListener != null) {
				mInformFieldListener.queryFieldInfo(mDragPosition);
//			}
			mVibrator.vibrate(50); //震动一下
		}
	};

	/**
	 * 初始化参数
	 * 由于dispatchTouchEvent是先于onTouchEvent，因此这里可以获取到手指刚刚触碰屏幕时，对应得到的有效item
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) ev.getX();
			mDownY = (int) ev.getY();
			
			//根据按下的X,Y坐标获取所点击item的position
			mDragPosition = pointToPosition(mDownX, mDownY);
			
			Log.i("mdp", mDragPosition+"");
			if(mDragPosition == AdapterView.INVALID_POSITION){
				return super.dispatchTouchEvent(ev);
			}
			
			//使用Handler延迟dragResponseMS执行mLongClickRunnable
			mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
			
			//根据position获取该item所对应的View
			mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
			Log.i("mStartDragItemView", mDragPosition +"");
			//下面这几个距离大家可以参考我的博客上面的图来理解下
			mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
			mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
			
			mOffset2Top = (int) (ev.getRawY() - mDownY);
			mOffset2Left = (int) (ev.getRawX() - mDownX);
			
			//获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
			mDownScrollBorder = getHeight() / 5;
			//获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
			mUpScrollBorder = getHeight() * 4/5;
			
			break;
		case MotionEvent.ACTION_UP:
			mHandler.removeCallbacks(mLongClickRunnable);
			mHandler.removeCallbacks(mScrollRunnable);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	
	/**
	 * 是否点击在GridView的item上面，配合上面判断初始触碰item的函数
	 * @param dragView
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouchInItem(View dragView, int x, int y){
		if(dragView == null){
			return false;
		}
		int leftOffset = dragView.getLeft();
		int topOffset = dragView.getTop();
		if(x < leftOffset || x > leftOffset + dragView.getWidth()){
			return false;
		}
		
		if(y < topOffset || y > topOffset + dragView.getHeight()){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
	 * 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
	 * 否则不进行滚动
	 */
	private Runnable mScrollRunnable = new Runnable() {
		
		@Override
		public void run() {
			int scrollY;
			if(getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1){
				mHandler.removeCallbacks(mScrollRunnable);
			}
			
			if(moveY > mUpScrollBorder){
				 scrollY = speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else if(moveY < mDownScrollBorder){
				scrollY = -speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else{
				scrollY = 0;
				mHandler.removeCallbacks(mScrollRunnable);
			}
			
			smoothScrollBy(scrollY, 10);
		}
	};
	
	
	/*******************************************
	 * 其他重要方法
	 * 
	 ******************************************/
	
	/**
	 * 获取状态栏的高度
	 * @param context
	 * @return
	 */
	private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return statusHeight;
    }
	
}
