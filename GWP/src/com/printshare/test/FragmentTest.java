package com.printshare.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.printshare.R;
import com.printshare.ui.BaseUIFragment;

public class FragmentTest extends BaseUIFragment {

	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	public FragmentTest() {
		// TODO Auto-generated constructor stub
	}
	
	public Fragment CreateFragment()
	{
		return new FragmentTest();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		getActivity().supportInvalidateOptionsMenu();
		super.onStart();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.testmain, container, false);
		return v;
	}
	
}
