package com.printshare.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.printshare.R;

public abstract class SelectPopupWindow extends PopupWindow implements OnClickListener,OnItemClickListener{
	private int mLayoutId=0;
	private View mMenuView;  
	private Button mBtnCancel;
	private ListView mListview;
	private SelectPopupwindowAdapte mAdapte;
	public SelectPopupWindow(Activity context,int count)
	{
		super(context);
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mMenuView = inflater.inflate(R.layout.select_popupwindow, null); 
        mBtnCancel=(Button)mMenuView.findViewById(R.id.sp_btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mListview=(ListView)mMenuView.findViewById(R.id.sp_listview);
        mAdapte=new SelectPopupwindowAdapte(count);
        mListview.setAdapter(mAdapte);
        mListview.setOnItemClickListener(this);
        this.setContentView(mMenuView);  
        this.setWidth(LayoutParams.WRAP_CONTENT);  
        this.setHeight(LayoutParams.WRAP_CONTENT); 
        this.setFocusable(true);
        this.setAnimationStyle(R.style.anim_menu_bottombar);
        ColorDrawable dw = new ColorDrawable(0xb0000000);  
//        this.setBackgroundDrawable(dw);
	}
	public void setLayout(int id)
	{
		this.mLayoutId=id;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(mBtnCancel==v)
		{
			dismiss();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ListviewOnItemClick(parent, view, position, id);
	}

	public class SelectPopupwindowAdapte extends BaseAdapter
	{
		private int count=0;
		public SelectPopupwindowAdapte(int count)
		{
			this.count=count;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return ListviewConvertView(position, convertView, parent);
		}
		
	}
	public abstract View ListviewConvertView(int position, View convertView, ViewGroup parent);
	public abstract void ListviewOnItemClick(AdapterView<?> parent, View view, int position,
			long id);
}
