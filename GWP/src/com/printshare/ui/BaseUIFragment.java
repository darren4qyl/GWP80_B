package com.printshare.ui;

import android.support.v4.app.Fragment;


public abstract class BaseUIFragment extends Fragment {
	public boolean mRoot;
	protected volatile boolean mReadyToDisplay = true;
	public void goBack(){
        if (!mRoot)
            getActivity().getSupportFragmentManager().popBackStack();
        else
            getActivity().finish();
    }
	 public void setReadyToDisplay(boolean ready) {
	        if (ready && !mReadyToDisplay)
	            display();
	        else
	            mReadyToDisplay = ready;
	    }
	 public abstract void display();
	 
}
