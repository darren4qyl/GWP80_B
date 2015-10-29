package com.printshare.ui;

import com.printshare.R;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FragmentHome extends BaseUIFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		View view = inflater.inflate(R.layout.fragment_index, container, false);
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.fragment_index.layout);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width/ 3 * 2, height / 3 * 2);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.setLayoutParams(params);
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void display() {
		if (getActivity() != null)
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mReadyToDisplay = true;
					focusHelper(false);
				}
			});
	}

	private void focusHelper(boolean idIsEmpty) {
		View parent = getView();
		if (getActivity() == null || !(getActivity() instanceof MainUiActivity))
			return;
		MainUiActivity activity = (MainUiActivity) getActivity();
		// activity.setMenuFocusDown(idIsEmpty, android.R.id.list);
		// activity.setSearchAsFocusDown(idIsEmpty, parent,
		// android.R.id.list);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.print_share_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	
}
