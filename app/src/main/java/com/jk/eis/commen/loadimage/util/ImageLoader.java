package com.jk.eis.commen.loadimage.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.view.ViewGroup.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * 后台的轮询线程不断访问任务队列，如果任务队列中有加载图片的任务(Runnable)，就通过Hanndler发送消息给线程池
 * 取出一个子线程，根据调度方式，从任务队列中取出一个任务去获取图片 而图片的加载是通过UIHandler将图片发送给UI线程完成图片的显示
 */
public class ImageLoader {
	
	private static ImageLoader instance;
	/*
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	/*
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEFAUL_THREAD_COUNT = 1;
	/*
	 * 队列调度方式
	 */
	private Type mType = Type.LIFO;
	/*
	 * 实现TaskQueue
	 */
	private LinkedList<Runnable> mTaskQueue;
	/*
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Semaphore mSemaphorePoolThread;
	/*
	 * 给线程中messageueuee发送消息
	 */
	private Handler mPoolThreadHandler;
	//避免空指针错误，先保证初始化
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	/*
	 * UI线程的Handler
	 */
	private Handler mUIHandler;

	public enum Type {
		FIFO, LIFO;
	}

	/*
	 * 构造方法加入线程数，调度方式为参数 在init()方法中进行所有的初始化操作
	 */
	private ImageLoader(int ThreadCount, Type type) {
		init(ThreadCount, type);
	}

	public static ImageLoader getInstance() {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader(DEFAUL_THREAD_COUNT, Type.LIFO);
				}
			}
		}
		return instance;
	}
	
	public static ImageLoader getInstance(int threadCount, Type type)
	{
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader(threadCount, type);
				}
			}
		}
		return instance;
	}
	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		// 1、初始化后台轮询线程
		/*
		 * 对实现用户操作UI控件必须充分流畅作了优化，这里excute(getTask())是一个耗时操作
		 */
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// 这个Handler主要用于发送消息到这个线程
				mPoolThreadHandler = new Handler() {
					public void handleMessage(Message msg) {
						// 线程池去执行取出的任务
						mThreadPool.execute(getTask());
						
						/*
						 * 作了优化
						 * 总体流程是：Task->TaskQueue->通知后台线程->把Task放到内部任务队列
						 * 修改前：每次TaskQueue新加一个任务Task，就会在线程池拿一个线程去执行，这样TaskQueue始终就有一个Task
						 * 			不能体现任务队列的概念
						 * 修改后：从线程池中拿一个线程去执行任务过程的同时，TaskQueue会有很多Task加进来，
						 * 			在当前任务执行后，再去拿一个线程去执行TaskQueue中的任务
						 * 比如信号量为3，在执行第四个方法的时候，就会阻塞住。而在LoadImage执行完毕后，就会释放一个任务
						 */
						try {
							mSemaphorePoolThread.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				
				//释放一个信号量，这里是防止mPoolThreadHandler报空指针异常
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		// 2、获取应用的最大可用内存和缓存大小
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;

		// 3、初始化缓存对象
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			/**
			 * 覆写这个方法用于测量每个Bitmap的值
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 计算方法为每一行的字节数*高度
				return value.getRowBytes() * value.getHeight();
			}
		};

		// 4、初始化线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		//5、初始化信号量
		mSemaphorePoolThread = new Semaphore(threadCount);
	}

	/**
	 * 根据type从任务队列取出一个方法
	 * 
	 * @return Runnable
	 */
	private Runnable getTask() {
		if (mType == Type.LIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.FIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}


	/**
	 * 根据path为imageView设置图片 核心方法
	 * @param path:当前图片路径
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				/**
				 * 获取图片，并为ImageView回调设置图片
				 * 
				 * 在当前执行该handleMessage方法时，所获得回调的Bitmap不一定是当前传入的path和imageView，(
				 * 传入的一直在变化)
				 * 因此需要再设置一个ImageBeanHolder，来保证当前的path,imageView和Bitmap一致
				 */
				@Override
				public void handleMessage(Message msg) {
					// 获得图片并设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					ImageView img = holder.imageView;
					// 将最初loadImage方法时传入的ImageView对象与回调的对象的路径进行比对，并设置图片
					if (img.getTag().toString().equals(path)) {
						img.setImageBitmap(bm);
					}

				}
			};
		}
		/*
		 * 根据url从lrucache中寻找bm，如果没有则去创建
		 */
		Bitmap bm = getBitmapFromLrucache(path);
		// bm不为空，将Message的obj设置为当前的信息一致的ImgBeanHolder对象进行回调
		if (bm != null) {
			//回调
			refreshBitmap(path, imageView, bm);
		} else {
			// bm为空，创建一个Task，并添加到TaskQueue中
			addTask(new Runnable() {
				/**
				 * 加载图片 1、获得图片需要显示的大小 2、压缩图片（使用Options） 3、加载图片且放入Lrucache中
				 */
				@Override
				public void run() {
					ImageSize imageSize = getImageViewSize(imageView);

					Bitmap bm = decodeSampledBitmapFromPath(path, imageSize);
					
					if(bm!=null){
						addBitmapToLrucache(path, bm);
						
						//回调
						refreshBitmap(path, imageView, bm);
					}
					
					mSemaphorePoolThread.release();
				}

				
			});
		}
	}
	
	/**
	 * 回调
	 * @param path
	 * @param imageView
	 * @param bm
	 */
	private void refreshBitmap(final String path, final ImageView imageView, Bitmap bm) {
		Message message = Message.obtain();
		message.obj = new ImgBeanHolder(bm, path, imageView);
		mUIHandler.sendMessage(message);
	}
	
	/**
	 * 将图片传入到缓存中
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLrucache(String path, Bitmap bm) {
		if (getBitmapFromLrucache(path) != null) {
			if (bm != null) {
				mLruCache.put(path, bm);
			}
		}

	}

	/**
	 * 根据图片需要显示的宽高对图片进行压缩
	 * 
	 * @param path
	 * @param imageSize
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, ImageSize imageSize) {
		/*
		 * 得到实际的宽和高
		 */
		Options options = new Options();
		// 获得图片的宽和高，并不把图片加载到内存中
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			return null;
		}
		// 获得一个inSampleSize
		options.inSampleSize = calculateInsampleSize(options, imageSize);

		// 使用获取到的inSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据实际宽高和需求的宽高做对比，获得一个inSampleSize
	 */
	private int calculateInsampleSize(Options options, ImageSize imageSize) {
		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;// 默认为1

		if (width > imageSize.width || height > imageSize.height) {
			// 宽高比例
			int widthRadio = Math.round(width * 1.0f / imageSize.width);
			int heightRadio = Math.round(height * 1.0f / imageSize.height);

			// 为节省内存，图片压缩越小越好
			inSampleSize = Math.max(widthRadio, heightRadio);
		}

		return inSampleSize;
	}

	/**
	 * 根据ImageView获得适当的宽高
	 * 
	 * @param imageView
	 * @return
	 */
	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize size = new ImageSize();
		// 获取屏幕宽度
		DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams params = imageView.getLayoutParams();
		/*
		 * 压缩后的宽度
		 */
		int width = imageView.getWidth(); // 获得imageView实际的宽度
		// 这里第一次判断是判断imageView刚刚new出来还未被加进布局中来
		if (width <= 0) {
			width = params.width;
		}
		// 设置了Wapcontent或fillParent
		if (width <= 0) {
			//width = imageView.getMaxWidth();// 检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0) {
			width = metrics.widthPixels;// 设置成屏幕宽度
		}
		/*
		 * 压缩后的高度
		 */
		int height = imageView.getHeight(); // 获得imageView实际的宽度
		// 这里第一次判断是判断imageView刚刚new出来还未被加进布局中来
		if (height <= 0) {
			height = params.height;
		}
		// 设置了Wapcontent或fillParent
		if (height <= 0) {
			//height = imageView.getMaxHeight();// 检查最大值
			height = getImageViewFieldValue(imageView, "mMaxHeight");
		}
		if (height <= 0) {
			height = metrics.heightPixels;// 设置成屏幕宽度
		}

		size.width = width;
		size.height = height;
		return size;
	}

	// 返回ImageView的大小，需要加一个含有宽度和高度的包装类ImageSize
	private class ImageSize {
		int width;
		int height;
	}

	private synchronized void addTask(Runnable runnable) {
		// 随便传一个值就好，发送通知以后，就会调用mPoolThreadHandler的handleMessage方法
		/*
		 * 注意，这里mPoolThreadHandler是并发执行，所以必须要判断其是否已经初始化
		 * 这里可以用java给的一个工具，Semaphore
		 */
		try {
			if(mPoolThreadHandler == null){
				mSemaphorePoolThreadHandler.acquire();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mTaskQueue.add(runnable);
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	private Bitmap getBitmapFromLrucache(String path) {
		return mLruCache.get(path);
	}

	private class ImgBeanHolder {
		public ImgBeanHolder(Bitmap bitmap, String path, ImageView imageView) {
			this.bitmap = bitmap;
			this.path = path;
			this.imageView = imageView;
		}

		Bitmap bitmap;
		String path;
		ImageView imageView;
	}

	/**
	 * 针对上面imageView.getMaxHeight()方法只能最低支持到API16,
	 * 通过反射机制来实现同样的功能
	 */
	public static int getImageViewFieldValue(Object o, String fieldName){
		int value = 0;
		try {
			Field f = ImageView.class.getField(fieldName);
			int fieldValue = f.getInt(o);
			if(fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
				value = fieldValue;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
}
