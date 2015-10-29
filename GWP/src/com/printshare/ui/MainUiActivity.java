package com.printshare.ui;

import java.io.Serializable;
import java.util.List;

import org.androidannotations.annotations.ViewById;

import com.printshare.PrintApplication;
import com.printshare.R;
import com.printshare.test.FragmentTest;
import com.printshare.ui.SidebarAdapter.SidebarEntry;
import com.printshare.ui.browser.FileBrowserFragment;
import com.printshare.util.WaitDialog;
import com.printshare.widget.HackyDrawerLayout;
import com.printsharelib.file.FileWrapper;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewDebug.ExportedProperty;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainUiActivity extends BaseAppActivity implements
		OnItemClickListener {
	private final static String TAG = "com.printshare.ui/MainUiActivity";
	private HackyDrawerLayout mDrawerLayout;
	private ListView mListView;
	private SidebarAdapter mSidebarAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mCurrentFragment;
	private int mFocusedPrior = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/* Enable the indeterminate progress feature */
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		InitBaseAppActivity();

		mDrawerLayout = (HackyDrawerLayout) findViewById(R.id.root_container);
		mListView = (ListView) findViewById(R.id.sidelist);
		mListView.setFooterDividersEnabled(true);
		mSidebarAdapter = new SidebarAdapter(this);
		mListView.setAdapter(mSidebarAdapter);

		/*
		 * Set up the sidebar click listener no need to invalidate menu for now
		 */
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

				// if
				// (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder)
				// instanceof BaseUIFragment)
				// ((BaseUIFragment)
				// getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder)).setReadyToDisplay(true);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// TODO Auto-generated method stub
				super.onDrawerSlide(drawerView, slideOffset);
			}

		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		prepareActionBar();
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// mDrawerLayout.openDrawer(mListView);
		mListView
				.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) this);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {// 4
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	private void prepareActionBar() {// 3
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {// 12
		/*
		 * Note: on Android 3.0+ with an action bar this method is called while
		 * the view is created. This can happen any time after onCreate.
		 */
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.print_share_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Fragment current = getSupportFragmentManager().findFragmentById(
				R.id.fragment_placeholder);
		if (current instanceof FileBrowserFragment) {
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.print_share_menu_sub_browser, menu);
		} else if (current instanceof FragmentHome) {
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.print_share_menu, menu);
		} else if (current instanceof FragmentTest) {
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.directory_view_file, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fragment current = getSupportFragmentManager().findFragmentById(
				R.id.fragment_placeholder);
		switch (item.getItemId()) {

		case android.R.id.home:
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			break;
		case R.id.psm_menu_addprinter:
			WaitDialog.getInstance().OpenLoadDialog(this, "正在加载中……");
			List<FileWrapper> fileWrapper = ((FileBrowserFragment) current).mAdapter
					.getSelectPrintFileList();
			Log.e(TAG, "size:" + fileWrapper.size());
			if(fileWrapper.size()>0)
			{
			Intent intent = new Intent(this, PreviewPrinter.class);
			Bundle bundle = new Bundle();
			bundle.putString("style", "list");
			bundle.putSerializable("content", (Serializable) fileWrapper);
			intent.putExtras(bundle);
			startActivity(intent);
			
			}
			break;
		}
		mDrawerLayout.closeDrawer(mListView);
		return super.onOptionsItemSelected(item);
	}

	public Fragment fetchFragment(String id) {
		// Save the previous fragment in case an error happens after.
		String prevFragmentId = null;
		Log.e(TAG, "fetch child start---------");
		// Set the current fragment.
		mSidebarAdapter.setCurrentFragment(id);

		if (mFragments.containsKey(id) && mFragments.get(id) != null) {
			return mFragments.get(id);
		}

		Fragment f = super.fetchFragment(id);
		if (f == null) {
			mSidebarAdapter.setCurrentFragment(prevFragmentId);
			throw new IllegalArgumentException("Wrong fragment id.");
		}
		Log.e(TAG, "fetch child end---------");
		return f;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Filter for LG devices, see
		// https://code.google.com/p/android/issues/detail?id=78154
		if ((keyCode == KeyEvent.KEYCODE_MENU) && (Build.VERSION.SDK_INT <= 16)
				&& (Build.MANUFACTURER.compareTo("LGE") == 0)) {
			return true;
		}
		/*
		 * if (mFocusedPrior == 0) setMenuFocusDown(true, 0);
		 */
		if (getCurrentFocus() != null)
			mFocusedPrior = getCurrentFocus().getId();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		SidebarAdapter.SidebarEntry entry = (SidebarEntry) mListView
				.getItemAtPosition(position);
		Fragment current = getSupportFragmentManager().findFragmentById(
				R.id.fragment_placeholder);

		if (current == null
				|| (entry != null && current.getTag().equals(entry.id))) { /*
																			 * Already
																			 * selected
																			 */
			if (mFocusedPrior != 0)
				requestFocusOnSearch();
			mDrawerLayout.closeDrawer(mListView);
			return;
		}

		// This should not happen
		if (entry == null || entry.id == null)
			return;

		if (entry.type == SidebarEntry.TYPE_FRAGMENT) {

			/* Switch the fragment */
			Fragment fragment = getFragment(entry.id);
			if (fragment instanceof BaseUIFragment)
				((BaseUIFragment) fragment).setReadyToDisplay(true);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fragment_placeholder, fragment, entry.id);
			ft.addToBackStack(mCurrentFragment);
			ft.commit();
			mCurrentFragment = entry.id;
			mSidebarAdapter.setCurrentFragment(mCurrentFragment);

			if (mFocusedPrior != 0)
				requestFocusOnSearch();
		}else if(entry.type == SidebarEntry.TYPE_ACTION)
		{
			if(entry.id.equals(FRAGMENT_MENU_ID_PREFERENCES))
	        {
	        	startActivityForResult(new Intent(this, PreferencesActivity.class), ACTIVITY_RESULT_PREFERENCES);
	        }
		}
		mDrawerLayout.closeDrawer(mListView);
	}

	private Fragment getFragment(String id)// 7
	{
		Fragment frag = getSupportFragmentManager().findFragmentByTag(id);
		if (frag != null)
			return frag;
		return fetchFragment(id);
	}

	protected void onResumeFragments() {
		// TODO Auto-generated method stub
		super.onResumeFragments();
		// Figure out if currently-loaded fragment is a top-level fragment.
		Fragment current = getSupportFragmentManager().findFragmentById(
				R.id.fragment_placeholder);
		boolean found = (current == null)
				|| SidebarAdapter.sidebarFragments.contains(current.getTag());

		/**
		 * Restore the last view.
		 * 
		 * Replace: - null fragments (freshly opened Activity) - Wrong fragment
		 * open AND currently displayed fragment is a top-level fragment
		 * 
		 * Do not replace: - Non-sidebar fragments. It will try to remove() the
		 * currently displayed fragment (i.e. tracks) and replace it with a
		 * blank screen. (stuck menu bug)
		 */
		if (current == null
				|| (!current.getTag().equals(mCurrentFragment) && found)) {
			Log.d(TAG, "Reloading displayed fragment");
			if (mCurrentFragment == null)
				mCurrentFragment = FRAGMENT_MENU_ID_HOME;
			if (!SidebarAdapter.sidebarFragments.contains(mCurrentFragment)) {
				Log.d(TAG, "Unknown fragment \"" + mCurrentFragment
						+ "\", resetting to video");
				mCurrentFragment = FRAGMENT_MENU_ID_HOME;
			}
			Fragment ff = getFragment(mCurrentFragment);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fragment_placeholder, ff, mCurrentFragment);
			ft.commit();
		}
	}

	private void requestFocusOnSearch() {
		View search = findViewById(R.id.ml_menu_search);
		if (search != null)
			search.requestFocus();
	}
}
