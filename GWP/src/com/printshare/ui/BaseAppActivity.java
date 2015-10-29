package com.printshare.ui;

import java.util.HashMap;

import org.androidannotations.annotations.ViewById;

import com.printshare.R;
import com.printshare.test.FragmentTest;
import com.printshare.ui.browser.FileBrowserFragment;
import com.printshare.widget.SlidingPaneLayout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

public class BaseAppActivity extends AppCompatActivity {
	public static final String TAG="com.printshare.ui/BaseAppActivity";
	public static final String FRAGMENT_MENU_ID_OPEN_LOCAL = "open_local";
	public static final String FRAGMENT_MENU_ID_OPEN_NETWORK = "open_network";
	public static final String FRAGMENT_MENU_ID_HOME = "home";
	public static final String FRAGMENT_MENU_ID_PREFERENCES = "preferences";
	protected static final int ACTIVITY_RESULT_PREFERENCES = 1;
	protected ActionBar mActionBar;
	protected ViewGroup mRootContainer;
	protected Toolbar mToolbar;
	protected SlidingPaneLayout mSlidingPane;
	protected HashMap<String, Fragment> mFragments;
	protected void InitBaseAppActivity() {
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
		mActionBar.collapseActionView();
		mActionBar.show();
		mFragments = new HashMap<String, Fragment>();
		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.pane);
		mSlidingPane.closePane();
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

    /**
     * �����activity�У�ʹ�ô˺�������fragment�������ʵ���ֱ�ӷ��ز�����ȥ������
     * @param id ��hashmap��key ֵ
     * @param isListFragment ��ǰ��fragment�Ƿ�Ϊlist
     * @return ��������ʵ��
     */
    public Fragment fetchFragment(String id) {

    	Log.e(TAG, "fetch parent start---------");
        if(mFragments.containsKey(id) && mFragments.get(id) != null) {
            return mFragments.get(id);
        }

        Fragment f;
        if(id.equals(FRAGMENT_MENU_ID_OPEN_LOCAL)) {
            f = new FileBrowserFragment();
        }else if(id.equals(FRAGMENT_MENU_ID_HOME))
        {
        	f = new FragmentHome();
        }else
        {
        	return null;
        }
        f.setRetainInstance(true);
        mFragments.put(id, f);
        return f;
    }
}
