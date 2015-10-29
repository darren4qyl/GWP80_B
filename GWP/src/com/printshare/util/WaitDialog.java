package com.printshare.util;

import com.printshare.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class WaitDialog{
	private static Dialog mDialog=null;
	private static WaitDialog mWaitDialog=null;
	public static WaitDialog getInstance()
	{
		if(mWaitDialog==null)
			mWaitDialog=new WaitDialog();
		return mWaitDialog;
	}
	public void OpenLoadDialog(Context context, String msg)
	{
		if(mDialog==null)
			mDialog=createLoadingDialog(context, msg);
		mDialog.show();
		
	}
	public void DismissDialog()
	{
		if(mDialog!=null)
			mDialog.dismiss();
		mDialog=null;
	}
	private Dialog createLoadingDialog(Context context, String msg) {  
		  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.view_loading_dialog, null); 
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view); 
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView); 
   
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.wait_dialog_loading_animation);  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);
  
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
  
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;  
  
    }
}
