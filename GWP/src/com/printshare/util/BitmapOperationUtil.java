package com.printshare.util;

import com.printsharelib.util.BitmapCache;
import com.printsharelib.util.BitmapUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BitmapOperationUtil {
	private static BitmapOperationUtil mInstance;
	private BitmapCache mBitmapCache;
	private BitmapUtil mBitmapUtil;
	public BitmapOperationUtil(Context context)
	{
		mBitmapCache=BitmapCache.getInstance(context);
		mBitmapUtil=BitmapUtil.getInstance();
	}
	public static BitmapOperationUtil getInstance(Context context)
	{
		if(mInstance==null)
			mInstance=new BitmapOperationUtil(context);
		return mInstance;
	}
	public Bitmap getPrintPreviewBitmap(Context context,String filePath)
	{
		String strExternalPath=Environment.getExternalStorageDirectory().getPath();
		String strPath=filePath.substring(strExternalPath.length()+1);
		Bitmap resultBitmap=mBitmapCache.getBitmapFromMemCache(strPath);
		if(resultBitmap==null)
		{
			setParameter(context);
			resultBitmap=mBitmapUtil.getPre(context, filePath);
			mBitmapCache.addBitmapToMemCache(strPath, resultBitmap);
		}
		return resultBitmap;
	}
	private void setParameter(Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		boolean test=settings.getBoolean("printer_print_padding", false);
		if(test)
		{
			double left=Double.parseDouble(settings.getString("printer_print_padding_left", "0").toString());
			double top=Double.parseDouble(settings.getString("printer_print_padding_top", "0").toString());
			double right=Double.parseDouble(settings.getString("printer_print_padding_right", "0").toString());
			double bottom=Double.parseDouble(settings.getString("printer_print_padding_bottom", "0").toString());
			mBitmapUtil.setPadding(left, right, top, bottom);
		}else
			mBitmapUtil.setPadding();
		test=settings.getBoolean("printer_print_bookbinding", false);
		if(test)
		{
			double bookbindingLineWidth=Double.parseDouble(settings.getString("printer_print_bookbinding_line", "0").toString());
			int bookbindingLinePosition=Integer.parseInt(settings.getString("printer_print_bookbinding_line_position", "1").toString());
			mBitmapUtil.setBookbindingLine(bookbindingLineWidth, bookbindingLinePosition);
		}
		else
			mBitmapUtil.setBookbindingLine();
		int picProperty=Integer.parseInt(settings.getString("printer_print_picture_property", "1").toString());
		mBitmapUtil.setPicParameter(picProperty);
	}
}
