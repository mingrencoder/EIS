package com.jk.eis.commen.framework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ToggleButton;

import com.jk.eis.modules.map.listener.DisposeShipSegListener;
import com.jk.eis.modules.map.listener.DragGridListener;
import com.jk.eis.modules.map.listener.InformShipSegListenr;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mStatusHeight = getStatusHeight(context); //获取状态栏的高度
	}

	public FlowLayout(Context context) {
		super(context, null);
	}
	
	/**
	 * 包含测量模式+测量值
	 * 测量模式：1、exactly:	100dp, match_parent, fill_parent
	 * 			2、at_most:	wrap_content
	 * 			3、unspcified
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//精确模式
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modelWidth = MeasureSpec.getMode(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modelHeight = MeasureSpec.getMode(heightMeasureSpec);
		
		//wrap_content 就需要进行精确定义长度和宽度，即宽和高的最大值
		int width = 0;
		int height = 0;
		
		//记录当前行的宽度和高度
		int lineWidth = 0;
		int lineHeight = 0;
		
		//得到内部元素个数
		int cCount = getChildCount();
		
		for(int i=0; i<cCount; i++){
			View child = getChildAt(i);
		
			//测量内部元素的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();//这里为什么是MarginLayoutParams是由generate方法决定的
			//子View占据的宽高
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			
			//换行
			if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
				//总宽度取最大值，当前宽度为子View宽度
				width = Math.max(width, childWidth);
				lineWidth = childWidth;
				//总高度连加即可，当前高度为子View的高度
				height += lineHeight;
				lineHeight = childHeight;
			}//不换行 
			else{
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			//最后一个元素无论如何也要执行最终的高宽赋值
			if (i == cCount - 1)
			{
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}
		
		//判断模式，进行最终的宽高设定
		setMeasuredDimension(
				modelWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
				modelHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop()+ getPaddingBottom()//
		);
		
	}

	//存储所有的View，相当于一个二维集合
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	
	//每一行的高度
	private List<Integer> mLineHeight = new ArrayList<Integer>();
	
	/**
	 * 实现设置View的所有位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mAllViews.clear();
		mLineHeight.clear();

		// 当前ViewGroup的宽度
		int width = getWidth();

		int lineWidth = 0;
		int lineHeight = 0;

		List<View> lineViews = new ArrayList<View>();

		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++)
		{
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 如果需要换行
			if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight())
			{
				// 记录LineHeight
				mLineHeight.add(lineHeight);
				// 记录当前行的Views
				mAllViews.add(lineViews);

				// 重置我们的行宽和行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				// 重置我们的View集合
				lineViews = new ArrayList<View>();
			}
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			lineViews.add(child);

		}// for end
			// 处理最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);

		// 设置子View的位置

		int left = getPaddingLeft();
		int top = getPaddingTop();

		// 行数
		int lineNum = mAllViews.size();

		for (int i = 0; i < lineNum; i++)
		{
			// 当前行的所有的View
			lineViews = mAllViews.get(i);
			lineHeight = mLineHeight.get(i);

			for (int j = 0; j < lineViews.size(); j++)
			{
				View child = lineViews.get(j);
				// 判断child的状态
				if (child.getVisibility() == View.GONE)
				{
					continue;
				}

				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();

				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();

				// 为子View进行布局
				child.layout(lc, tc, rc, bc);

				left += child.getMeasuredWidth() + lp.leftMargin
						+ lp.rightMargin;
			}
			left = getPaddingLeft() ; 
			top += lineHeight ; 
		}
		
		mDragElement = getChildAt(0);
		if (mDragElement != null) {
			mDragElement.setVisibility(View.INVISIBLE);//隐藏该item
		}
	}
	
	/**
	 * 与当前viewGroup对应的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	/**
	 * 避免切换fragment未清除的镜像对象仍保留
	 */
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mDragImageView != null){
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}
	
	
	

    public static final int INVALID_POSITION = -1;

     //覆写abList方法
	 private Rect mTouchFrame;
	 
	//DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
	private long dragResponseMS = 1000;
	


	//是否可以拖拽，默认不可以
	private boolean isDrag = false;

	//按钮是否被按下
	private boolean isDown = false;

	//刚开始被按下的坐标
	private int mDownX;
	private int mDownY;
	//移动的坐标
	private int moveX;
	private int moveY;

	//当前的的position
	private int mDragPosition;
	//为了记录正在移动的position，设置一个初始值
	private int mInitPosition;

	//刚开始拖拽的item对应的View
	private View mStartDragItemView = null;
	
	//拖拽图片
	private View mDragElement = null;

	//用于拖拽的镜像，这里直接用一个ImageView
	private ImageView mDragImageView;
	

	//震动器
	private Vibrator mVibrator;
	
	private WindowManager mWindowManager;

	//item镜像的布局参数
	private WindowManager.LayoutParams mWindowLayoutParams;
	
	

	//我们拖拽的item对应的Bitmap
	private Bitmap mDragBitmap;
	
	//DragGridView距离屏幕顶部的偏移量
	private int mOffset2Top;
	
	//DragGridView距离屏幕左边的偏移量
	private int mOffset2Left;
	
	//状态栏的高度
	private int mStatusHeight; 
	
	//DragGridView自动滚动的速度
	private static final int speed = 20;
	
	//重排数据接口
	private DragGridListener mDragGridListener;
	//展示数据接口
	private InformShipSegListenr mQueryShipUnitListenr;
	//布置分段接口
	private DisposeShipSegListener mDisposeShipSegListener;
	
	public void setDragGridAdapter(ListAdapter adapter) {
		
		if(adapter instanceof DragGridListener){
			mDragGridListener = (DragGridListener) adapter;
		}else{
			throw new IllegalStateException("the adapter must be implements DragGridAdapter");
		}
	}
	
	public void setQueryShipUnitListenr(InformShipSegListenr mQueryShipUnitListenr){
		this.mQueryShipUnitListenr = mQueryShipUnitListenr;
	}
	

	public void setmDisposeShipSegListener(DisposeShipSegListener mDisposeShipSegListener) {
		this.mDisposeShipSegListener = mDisposeShipSegListener;
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
			if(mQueryShipUnitListenr != null){
				mQueryShipUnitListenr.querySegInfo(getChildAt(mDragPosition).getId());
				for (int i=1 ;i<getChildCount()-1; i++) {
	        		ToggleButton child = (ToggleButton) getChildAt(i);
	        		child.setEnabled(true);
		        }
		        isDrag = false; 
				isDown = false;
				mInitPosition = 0;
				removeDragImage();
			}
		}
	};

	/**
	 * 创建拖动的镜像————下面若停止拖拽，会再次移出镜像
	 * @param bitmap 
	 * @param downX
	 * 			按下的点相对父控件的X坐标
	 * @param downY
	 * 			按下的点相对父控件的X坐标
	 */
	private void createDragImage(Bitmap bitmap, int downX , int downY){
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
		mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowLayoutParams.x = (int) mDragElement.getTranslationX();
		mWindowLayoutParams.y = (int) mDragElement.getTranslationY() + mOffset2Top - getStatusHeight(getContext());
		mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  
	                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
		  
		mDragImageView = new ImageView(getContext());  
		mDragImageView.setImageBitmap(bitmap);  
		mWindowManager.addView(mDragImageView, mWindowLayoutParams);  
	}
	
	/**
	 * 覆写方法
	 * @param x
	 * @param y
	 * @return
	 */
	public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }
        final int count = getChildCount();
        for (int i = count - 1; i >= 1; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return i;
                }
            }
        }
        return INVALID_POSITION;
    }
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
			mStartDragItemView = getChildAt(mDragPosition);
			final int count = getChildCount();
			//当前是目录或者imageview
			if(!(mStartDragItemView instanceof ToggleButton)){
				if(mDragPosition == count-1){
			        isDrag = false; 
					isDown = false;
					removeDragImage();
				}
				break;
			}
			if(!isDown){
				//使用Handler延迟dragResponseMS执行mLongClickRunnable
				mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
				
		        for (int i=1 ;i<count-1; i++) {//减一是有个back
		        	if(i != mDragPosition){
		        		ToggleButton child = (ToggleButton) getChildAt(i);
		        		child.setEnabled(false);
		        	}
		        }
		        isDown = true;
				
				
		        if(mDragPosition == AdapterView.INVALID_POSITION){
					return super.dispatchTouchEvent(ev);
				}
		        
		        //保持着该选定分段的位置，返回或再次点击销毁
		        mInitPosition = mDragPosition;
		        
				
				//根据position获取该item所对应的View
				mStartDragItemView = getChildAt(mDragPosition);
				
				//按下点到button的上，左边距离
				//mPoint2ItemTop = mDownY - mDragElement.getTop();
				//mPoint2ItemLeft = mDownX - mDragElement.getLeft();
				
				mOffset2Top = (int) (ev.getRawY() - mDownY);
				mOffset2Left = (int) (ev.getRawX() - mDownX);
				
		        isDrag = true; //设置可以拖拽
				
				//开启mDragItemView绘图缓存
				mDragElement.setDrawingCacheEnabled(true);
				//获取mDragItemView在缓存中的Bitmap对象
				mDragBitmap = Bitmap.createBitmap(mDragElement.getDrawingCache());
				//这一步很关键，释放绘图缓存，避免出现重复的镜像
				mDragElement.destroyDrawingCache();
				//根据我们按下的点显示item镜像
				createDragImage(mDragBitmap, mDownX, mDownY);
				break;
			}
			
			if(isDown && mDragPosition == mInitPosition){
				for (int i=1 ;i<count-1; i++) {
	        		ToggleButton child = (ToggleButton) getChildAt(i);
	        		child.setEnabled(true);
		        }
		        isDrag = false; 
				isDown = false;
				mInitPosition = 0;
				removeDragImage();
				break;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			int moveX = (int)ev.getX();
			int moveY = (int) ev.getY();
			
			//如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
			if(!isTouchInItem(mStartDragItemView, moveX, moveY)){
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			break;
		case MotionEvent.ACTION_UP:
			mHandler.removeCallbacks(mLongClickRunnable);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	
	/**
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
	
	
	/*******************************************
	 * 核心————执行拖拽动作开始
	 * 
	 ******************************************/
	
	/**
	 * 当初始触碰item有效，则进行第二步的动作，在onTouchEvent函数中实现所有的拖动动作
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(isDrag){
			switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				mVibrator.vibrate(50); //震动一下
				break;
			case MotionEvent.ACTION_MOVE:
				moveX = (int) ev.getX();
				moveY = (int) ev.getY();
				
				//拖动item
				onDragItem(moveX, moveY);
				break;
			case MotionEvent.ACTION_UP:
				//停止拖拽
				onStopDrag(moveX, moveY);
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
	 * @param moveX
	 * @param moveY
	 */
	private void onDragItem(int moveX, int moveY){
		mWindowLayoutParams.x = moveX - mDragElement.getWidth()/2 + mOffset2Left;
		mWindowLayoutParams.y = moveY - mDragElement.getHeight()/2 + mOffset2Top - mStatusHeight;
		mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置
	}

	/**
	 * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
	 * @param moveY 
	 * @param moveX 
	 */
	private void onStopDrag(int moveX, int moveY){
		//mDragAdapter.setHideItem(-1);
		if(mDisposeShipSegListener != null){
			mDisposeShipSegListener.putUnitIn(getChildAt(mInitPosition).getId(), getChildAt(getChildCount()-1).getId(), moveX, moveY-getHeight());
		}
		removeDragImage();
		//清除掉镜像对象后，重新再定义一个镜像
		createDragImage(mDragBitmap, mDownX, mDownY);
	}
	
	
	/**
	 * 从界面上面移动拖动镜像
	 */
	private void removeDragImage(){
		if(mDragImageView != null){
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}
	
	
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
