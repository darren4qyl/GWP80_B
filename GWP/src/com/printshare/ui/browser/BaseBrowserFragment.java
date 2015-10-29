/**
 * **************************************************************************
 * BaseBrowserFragment.java
 * ****************************************************************************
 * Copyright © 2015 GWP authors 
 * Author: Geoffrey Métais
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * ***************************************************************************
 */
package com.printshare.ui.browser;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import com.printsharelib.file.FileWrapper;
import com.printsharelib.util.AndroidUtil;
import com.printshare.PrintApplication;
import com.printshare.R;
import com.printshare.interfaces.IRefreshable;
import com.printshare.ui.BaseUIFragment;
import com.printshare.ui.DividerItemDecoration;
import com.printshare.ui.MainUiActivity;
import com.printshare.util.Util;
import com.printshare.util.WeakHandler;
import com.printshare.widget.ContextMenuRecyclerView;
import com.printshare.widget.SwipeRefreshLayout;


public abstract class BaseBrowserFragment extends BaseUIFragment implements
		IRefreshable, FileBrowser.EventListener, SwipeRefreshLayout.OnRefreshListener {
	protected static final String TAG = "com.printshare.ui.browser/BaseBrowserFragment";
	public static String ROOT = "smb";
	public static final String KEY_MRL = "key_mrl";
	public static final String KEY_PRINT_FILE = "key_print_file";
	public static final String KEY_PRINT_FILE_LIST = "key_print_file_list";
	public static final String KEY_POSITION = "key_list";

	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected BrowserFragmentHandler mHandler;
	protected FileBrowser mFileBrowser;
	protected ContextMenuRecyclerView mRecyclerView;
	public BaseBrowserAdapter mAdapter;
	protected LinearLayoutManager mLayoutManager;
	protected TextView mEmptyView;
	public String mMrl;
	protected FileWrapper mCurrentFile;
	protected int mSavedPosition = -1, mFavorites = 0;
	public boolean mRoot;

	private SparseArray<ArrayList<FileWrapper>> mFileLists = new SparseArray<ArrayList<FileWrapper>>();
	private ArrayList<FileWrapper> fileList;
	public int mCurrentParsedPosition = 0;

	protected abstract Fragment createFragment();

	protected abstract void browseRoot();

	protected abstract String getCategoryTitle();

	public BaseBrowserFragment() {
		mHandler = new BrowserFragmentHandler(this);
		mAdapter = new BaseBrowserAdapter(this);
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if (bundle == null)
			bundle = getArguments();
		if (bundle != null) {
			fileList = bundle.getParcelableArrayList(KEY_PRINT_FILE_LIST);
			if (fileList != null)
				mAdapter.addAll(fileList);
			mCurrentFile = bundle.getParcelable(KEY_PRINT_FILE);
			if (mCurrentFile != null)
				mMrl = mCurrentFile.getLocation();
			else
				mMrl = bundle.getString(KEY_MRL);
			mSavedPosition = bundle.getInt(KEY_POSITION);
		} else if (getActivity().getIntent() != null) {
			mMrl = getActivity().getIntent().getDataString();
			getActivity().setIntent(null);
		}
	}

	protected int getLayoutId() {
		return R.layout.directory_browser;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(getLayoutId(), container, false);
		mRecyclerView = (ContextMenuRecyclerView) v
				.findViewById(R.id.network_list);
		mEmptyView = (TextView) v.findViewById(android.R.id.empty);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				PrintApplication.getAppContext(),
				DividerItemDecoration.VERTICAL_LIST));
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addOnScrollListener(mScrollListener);
		registerForContextMenu(mRecyclerView);

		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.swipeLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.orange700);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		final AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getTitle());
            getActivity().supportInvalidateOptionsMenu();
            display();
        }
	}

	public void onStop() {
		super.onStop();
		releaseBrowser();
	}

	private void releaseBrowser() {
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_MRL, mMrl);
		outState.putParcelable(KEY_PRINT_FILE, mCurrentFile);
		outState.putParcelableArrayList(KEY_PRINT_FILE_LIST, fileList);
		if (mRecyclerView != null) {
			outState.putInt(KEY_POSITION,
					mLayoutManager.findFirstCompletelyVisibleItemPosition());
		}
		super.onSaveInstanceState(outState);
	}

	public boolean isRootDirectory() {
		return mRoot;
	}

	public String getTitle() {
		if (mRoot)
			return getCategoryTitle();
		else
			return mCurrentFile != null ? mCurrentFile.getTitle() : mMrl;
	}

	@Override
	public void display() {
		if (!mReadyToDisplay) {
			mReadyToDisplay = true;
			update();
			return;
		}
		updateDisplay();
	}

	public void goBack() {
		if (!mRoot)
			getActivity().getSupportFragmentManager().popBackStack();
		else
			getActivity().finish();
	}

	public void browse(FileWrapper file, int position, boolean save) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		Fragment next = createFragment();
		Bundle args = new Bundle();
		
		
		ArrayList<FileWrapper> list = mFileLists != null ? mAdapter.sortList(mFileLists.get(position)) : null;
		if (list != null && !list.isEmpty())
			args.putParcelableArrayList(KEY_PRINT_FILE_LIST, list);
		args.putParcelable(KEY_PRINT_FILE, file);
		next.setArguments(args);
		ft.replace(R.id.fragment_placeholder, next, file.getLocation());
		if (save)
			ft.addToBackStack(mMrl);
		ft.commit();
	}



	@Override
	public void onRefresh() {
		mSavedPosition = mLayoutManager
				.findFirstCompletelyVisibleItemPosition();
		refresh();
	}

	RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			int topRowVerticalPosition = (recyclerView == null || recyclerView
					.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0)
					.getTop();
			mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
		}
	};

	/**
	 * Update views visibility and emptiness info
	 */
	protected void updateEmptyView() {
		if (mAdapter.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mRecyclerView.setVisibility(View.GONE);
			mSwipeRefreshLayout.setEnabled(false);
		} else if (mEmptyView.getVisibility() == View.VISIBLE) {
			mEmptyView.setVisibility(View.GONE);
			mRecyclerView.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setEnabled(true);
		}
	}

	protected void update() {
		update(false);
	}

	protected void update(boolean force) {
		if (mReadyToDisplay) {
			updateEmptyView();
			if (force || mAdapter.isEmpty()) {
				refresh();
			} else {
				updateDisplay();
			}
		}
	}

	protected void updateDisplay() {
		if (!mAdapter.isEmpty()) {
			if (mSavedPosition > 0) {
				mLayoutManager.scrollToPositionWithOffset(mSavedPosition, 0);
				mSavedPosition = 0;
			}
		}
		mAdapter.notifyDataSetChanged();
		mAdapter.sortList();
		parseSubDirectories();
		focusHelper();
	}
	@Override
	public void onMediaAdded(int index, FileWrapper media) {
		// TODO Auto-generated method stub
		if(!(media.getType()==FileWrapper.TYPE_BITMAP||media.getType()==FileWrapper.TYPE_FILE||media.getType()==FileWrapper.TYPE_DIR))
			return;
		mAdapter.addItem(media, mReadyToDisplay && mRoot, mRoot);
        if (mReadyToDisplay)
            updateEmptyView();
        if (mRoot)
            mHandler.sendEmptyMessage(BrowserFragmentHandler.MSG_HIDE_LOADING);
	}

	@Override
    public void onMediaRemoved(int index, FileWrapper media) {
        mAdapter.removeItem(index, mReadyToDisplay);
    }

    @Override
    public void onBrowseEnd() {
        releaseBrowser();
        mHandler.sendEmptyMessage(BrowserFragmentHandler.MSG_HIDE_LOADING);
        if (mReadyToDisplay)
            display();
    }
	@Override
	public void refresh() {
		mAdapter.clear();
		if (mFileBrowser == null)
			mFileBrowser = new FileBrowser(this);
		else
			mFileBrowser.changeEventListener(this);
		mCurrentParsedPosition = 0;
		if (mRoot)
			browseRoot();
		else
			mFileBrowser.browse(mCurrentFile != null ? mCurrentFile.getUri()
					: Uri.parse(mMrl));
		mHandler.sendEmptyMessageDelayed(
				BrowserFragmentHandler.MSG_SHOW_LOADING, 300);
	}

	protected static class BrowserFragmentHandler extends
			WeakHandler<BaseBrowserFragment> {

		public static final int MSG_SHOW_LOADING = 0;
		public static final int MSG_HIDE_LOADING = 1;

		public BrowserFragmentHandler(BaseBrowserFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseBrowserFragment fragment = getOwner();
			switch (msg.what) {
			case MSG_SHOW_LOADING:
				fragment.mSwipeRefreshLayout.setRefreshing(true);
				break;
			case MSG_HIDE_LOADING:
				removeMessages(MSG_SHOW_LOADING);
				fragment.mSwipeRefreshLayout.setRefreshing(false);
				break;
			}
		}
	}

	protected void focusHelper() {
		if (getActivity() == null || !(getActivity() instanceof MainUiActivity))
			return;
		boolean isEmpty = mAdapter.isEmpty();
		MainUiActivity main = (MainUiActivity) getActivity();
//		main.setMenuFocusDown(isEmpty, R.id.network_list);
//		main.setSearchAsFocusDown(isEmpty, getView(), R.id.network_list);
	}

	public void clear() {
		mAdapter.clear();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
		if (info != null)
			setContextMenu(getActivity().getMenuInflater(), menu, info.position);
	}

	protected void setContextMenu(MenuInflater inflater, Menu menu, int position) {
		FileWrapper mw = (FileWrapper) mAdapter.getItem(position);
		boolean canWrite = Util.canWrite(mw.getLocation());
		boolean isBitmap = mw.getType() == FileWrapper.TYPE_BITMAP;
		boolean isPrintFile = mw.getType() == FileWrapper.TYPE_FILE;
		if (isBitmap || isPrintFile) {
			inflater.inflate(R.menu.directory_view_file, menu);
			menu.findItem(R.id.directory_view_delete).setVisible(canWrite);
			menu.findItem(R.id.directory_view_info).setVisible(isBitmap);
			menu.findItem(R.id.directory_view_info).setVisible(isPrintFile);
		} else if (mw.getType() == FileWrapper.TYPE_DIR) {
			boolean isEmpty = mFileLists.get(position) == null
					|| mFileLists.get(position).isEmpty();
			if (canWrite || !isEmpty) {
				inflater.inflate(R.menu.directory_view_dir, menu);
//				menu.findItem(R.id.directory_view_print_folder).setVisible(
//						!isEmpty);
				menu.findItem(R.id.directory_view_delete).setVisible(canWrite);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item
				.getMenuInfo();
		if (info != null && handleContextItemSelected(item, info.position))
			return true;
		return super.onContextItemSelected(item);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onPopupMenu(View anchor, final int position) {
		if (!AndroidUtil.isHoneycombOrLater()) {
			// Call the "classic" context menu
			anchor.performLongClick();
			return;
		}
		PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
		setContextMenu(popupMenu.getMenuInflater(), popupMenu.getMenu(),
				position);

		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return handleContextItemSelected(item, position);
					}
				});
		popupMenu.show();
	}

	protected boolean handleContextItemSelected(MenuItem item, int position) {
		int id = item.getItemId();
		if (!(mAdapter.getItem(position) instanceof FileWrapper))
			return super.onContextItemSelected(item);
		FileWrapper mw = (FileWrapper) mAdapter.getItem(position);
		switch (id) {
		/*case R.id.directory_view_print_folder:
			mw.removeFlags(MediaWrapper.MEDIA_FORCE_AUDIO);
			Util.openMedia(getActivity(), mw);
			return true;*/
		case R.id.directory_view_print:
			/**
			 * ������ DarrenPrintShare ����
			 */
			Toast.makeText(getActivity(), "��ӡ��ʼ����", Toast.LENGTH_LONG).show();
			return true;
		case R.id.directory_view_delete:
			/*AlertDialog alertDialog = CommonDialogs.deleteMedia(mw.getType(),
					getActivity(), mw.getLocation(), new GWPRunnable() {
						@Override
						public void run(Object o) {
							refresh();
						}
					});
			alertDialog.show();*/
			Toast.makeText(getActivity(), "ɾ���ļ�", Toast.LENGTH_LONG).show();
			return true;
		case R.id.directory_view_info:
			/*Intent i = new Intent(getActivity(), SecondaryActivity.class);
			i.putExtra("fragment", "mediaInfo");
			i.putExtra("param", mw.getUri().toString());
			startActivity(i);*/
			/**
			 * �����ת��Ԥ�����棬Ԥ������Ĭ������������壬ֻ�д�����ȥ��ʾ���ص�������壬��ʾ��ϸ��Ϣ������
			 * һ����Ԥ���ܶ����Ƭ���������л�����
			 */
			return true;
		}
		return false;
	}

	protected void parseSubDirectories() {
		if (mCurrentParsedPosition == -1 || mAdapter.isEmpty())
			return;
		mFileLists.clear();
		if (mFileBrowser == null)
			mFileBrowser = new FileBrowser(mFoldersBrowserListener);
		else
			mFileBrowser.changeEventListener(mFoldersBrowserListener);
		mCurrentParsedPosition = 0;
		Object item;
		FileWrapper mw;
		while (mCurrentParsedPosition < mAdapter.getItemCount()) {
			item = mAdapter.getItem(mCurrentParsedPosition);
			if (item instanceof BaseBrowserAdapter.Storage) {
				mw = new FileWrapper(
						((BaseBrowserAdapter.Storage) item).getUri());
				mw.setType(FileWrapper.TYPE_DIR);
			} else if (item instanceof FileWrapper) {
				mw = (FileWrapper) item;
			} else
				mw = null;
			if (mw != null) {
				if (mw.getType() == FileWrapper.TYPE_DIR) {
					mFileBrowser.browse(mw.getUri());
					return;
				}
			}
			++mCurrentParsedPosition;
		}
	}

	private FileBrowser.EventListener mFoldersBrowserListener = new FileBrowser.EventListener() {
		ArrayList<FileWrapper> directories = new ArrayList<FileWrapper>();
		ArrayList<FileWrapper> files = new ArrayList<FileWrapper>();

		@Override
		public void onBrowseEnd() {
			if (mAdapter.isEmpty()) {
				mCurrentParsedPosition = -1;
				releaseBrowser();
				return;
			}
			String holderText = getDescription(directories.size(), files.size());
			FileWrapper mw = null;

			if (!TextUtils.equals(holderText, "")) {
				mAdapter.setDescription(mCurrentParsedPosition, holderText);
				directories.addAll(files);
				mFileLists.put(mCurrentParsedPosition,
						new ArrayList<FileWrapper>(directories));
			}
			while (++mCurrentParsedPosition < mAdapter.getItemCount()) { 
				if (mAdapter.getItem(mCurrentParsedPosition) instanceof FileWrapper) {
					mw = (FileWrapper) mAdapter
							.getItem(mCurrentParsedPosition);
					if (mw.getType() == FileWrapper.TYPE_DIR)
						break;
				} else if (mAdapter.getItem(mCurrentParsedPosition) instanceof BaseBrowserAdapter.Storage) {
					mw = new FileWrapper(
							((BaseBrowserAdapter.Storage) mAdapter
									.getItem(mCurrentParsedPosition)).getUri());
					break;
				} else
					mw = null;
			}

			if (mw != null) {
				if (mCurrentParsedPosition < mAdapter.getItemCount()) {
					directories.clear();
					files.clear();
					mFileBrowser.browse(mw.getUri());
				} else {
					mCurrentParsedPosition = -1;
					releaseBrowser();
				}
			} else
				releaseBrowser();
			directories.clear();
			files.clear();
		}

		private String getDescription(int folderCount, int mediaFileCount) {
			String holderText = "";
			if (folderCount > 0) {
				holderText += PrintApplication.getAppResources()
						.getQuantityString(R.plurals.subfolders_quantity,
								folderCount, folderCount);
				if (mediaFileCount > 0)
					holderText += ", ";
			}
			if (mediaFileCount > 0)
				holderText += PrintApplication.getAppResources()
						.getQuantityString(R.plurals.mediafiles_quantity,
								mediaFileCount, mediaFileCount);
			else if (folderCount == 0 && mediaFileCount == 0)
				holderText = getString(R.string.directory_empty);
			return holderText;
		}

		@Override
		public void onMediaAdded(int index, FileWrapper file) {
			// TODO Auto-generated method stub
			int type = file.getType();
			if (type == FileWrapper.TYPE_DIR)
				directories.add(file);
			else if (type == FileWrapper.TYPE_FILE||type == FileWrapper.TYPE_BITMAP)
				files.add(file);
		}

		@Override
		public void onMediaRemoved(int index, FileWrapper file) {
			// TODO Auto-generated method stub
			
		}
	};

	
}
