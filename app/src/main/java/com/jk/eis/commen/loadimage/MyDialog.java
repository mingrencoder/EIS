package com.jk.eis.commen.loadimage;

import java.io.File;

import com.jk.eis.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MyDialog extends Dialog {
	private Button mDialogBtn;
	private ImageView mDialogImg;
	private String mImgPath;

	public MyDialog(Context context, String mImgPath) {
		super(context,R.style.CustomDialog);
		this.mImgPath = mImgPath;
		init();
	}

	private void init() {
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.loadimage_dialog_layout, null);
		mDialogBtn = (Button) mView.findViewById(R.id.id_dialog_btn_back);
		mDialogImg = (ImageView) mView.findViewById(R.id.id_dialog_image);
		File file = new File(mImgPath);
        if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(mImgPath);
                //将图片显示到ImageView中
                mDialogImg.setImageBitmap(bm);
        }
		super.setContentView(mView);
	}

	 @Override
	public void setContentView(int layoutResID) {
	}


	@Override
	public void setContentView(View view) {
	}

	/** 
     * 确定键监听器 
     * @param listener 
     */  
    public void setOnmListener(View.OnClickListener listener){  
    	mDialogBtn.setOnClickListener(listener);  
    }  
}
