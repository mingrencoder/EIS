package com.jk.eis.commen.okhttp;

import com.jk.eis.commen.okhttp.listener.DisposeDataListener;

/**
 * @author vision
 * @function 这个类是真正来处理响应的，这里封装响应回调和字节码对象
 */
public class DisposeDataHandle
{
	public DisposeDataListener mListener = null;
	public Class<?> mClass = null;
	//保存文件路径
	public String mSource = null;

	public DisposeDataHandle(DisposeDataListener listener)
	{
		this.mListener = listener;
	}

	public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz)
	{
		this.mListener = listener;
		this.mClass = clazz;
	}

	public DisposeDataHandle(DisposeDataListener listener, String source)
	{
		this.mListener = listener;
		this.mSource = source;
	}
}