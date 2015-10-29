package com.printshare.widget;



import com.printshare.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class VerticalProgressBar extends ProgressBar {

	private int viewWidth;
	private int viewHeight;

	public VerticalProgressBar(Context context) {
		super(context);
		initView(context);
	}

	public VerticalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public VerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

	}

	private void initView(Context context) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.circle_progress_bg);
		Bitmap bitmap = bitmapDrawable.getBitmap();
		viewWidth = bitmap.getWidth();
		viewHeight = bitmap.getHeight();
		bitmap.recycle();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldw, oldh);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// ��ת
		canvas.rotate(-90);
		canvas.translate(-this.getHeight(), 0);
		super.onDraw(canvas);
	}
}
