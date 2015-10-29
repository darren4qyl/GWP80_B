package com.printshare.test;

import com.printshare.R;
import com.printshare.util.BitmapOperationUtil;
import com.printsharelib.util.BitmapUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class TestActivity extends Activity {
	private ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testxml);
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		BitmapUtil bitmapUtil=BitmapUtil.getInstance();
		String strFile = "/storage/sdcard0/test.png";
		BitmapOperationUtil util=BitmapOperationUtil.getInstance(this);
		mImageView.setImageBitmap(util.getPrintPreviewBitmap(this,strFile));
	}
}
