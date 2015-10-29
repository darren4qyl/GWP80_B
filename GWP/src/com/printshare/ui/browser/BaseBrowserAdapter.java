/**
 * **************************************************************************
 * BaseBrowserAdapter.java
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

import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.printshare.PrintApplication;
import com.printshare.R;
import com.printshare.util.CustomDirectories;
import com.printsharelib.database.PrintDataBase;
import com.printsharelib.file.FileWrapper;
import com.printsharelib.util.FileComparators;

public class BaseBrowserAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	protected static final String TAG = "GWP/BaseBrowserAdapter";

	private static final int TYPE_MEDIA = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_STORAGE = 2;

	protected int FOLDER_RES_ID = R.drawable.ic_menu_folder;

	ArrayList<FileWrapper> mMediaList = new ArrayList<FileWrapper>();
	BaseBrowserFragment fragment;
	PrintDataBase mDbManager;
	LinkedList<String> mMediaDirsLocation;
	List<String> mCustomDirsLocation;
	private List<FileWrapper> mSelectPrintFileList;
	String mEmptyDirectoryString;

	private boolean isMoreSelect = false;

	public BaseBrowserAdapter(BaseBrowserFragment fragment) {
		this.fragment = fragment;
		mEmptyDirectoryString = PrintApplication.getAppResources().getString(
				R.string.directory_empty);
		mSelectPrintFileList=new ArrayList<FileWrapper>();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		RecyclerView.ViewHolder vh;
		View v;
		if (viewType == TYPE_MEDIA) {
			v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.directory_view_item, parent, false);
			vh = new FileViewHolder(v);
		} else {
			v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.browser_item_separator, parent, false);
			vh = new SeparatorViewHolder(v);
		}
		return vh;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder,
			int position) {
		int viewType = getItemViewType(position);
		if (viewType == TYPE_MEDIA) {
			onBindMediaViewHolder(holder, position);
		} else {
			SeparatorViewHolder vh = (SeparatorViewHolder) holder;
			vh.title.setText(getItem(position).toString());
		}
	}

	private void onBindMediaViewHolder(final RecyclerView.ViewHolder holder,
			int position) {
		final FileViewHolder vh = (FileViewHolder) holder;
		final FileWrapper file = (FileWrapper) getItem(position);
		boolean hasContextMenu = (file.getType() == FileWrapper.TYPE_BITMAP
				|| file.getType() == FileWrapper.TYPE_FILE || file.getType() == FileWrapper.TYPE_DIR);

		vh.checkBox.setVisibility(View.GONE);
		vh.title.setText(file.getTitle());
		

		vh.icon.setImageResource(getIconResId(file));
		vh.more.setVisibility(hasContextMenu ? View.VISIBLE : View.GONE);
		if (!TextUtils.isEmpty(file.getDescription())) {

			vh.text.setVisibility(View.VISIBLE);
			vh.text.setText(file.getDescription());
		} else {
			if (isMoreSelect)
			{
				vh.checkBox.setOnCheckedChangeListener(null);
				vh.checkBox.setChecked(false);
				
				for (FileWrapper filetest:mSelectPrintFileList) {
					if(filetest.equals(file))
					{
						vh.checkBox.setChecked(true);
						break;
					}
				}
					
				vh.checkBox.setVisibility(View.VISIBLE);
				vh.more.setVisibility(View.GONE);
				vh.checkBox.setOnCheckedChangeListener(changeListener);
			}
			vh.text.setVisibility(View.INVISIBLE);
		}
		vh.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isMoreSelect)
				{
					if(vh.checkBox.isChecked())
						vh.checkBox.setChecked(false);
					else
						vh.checkBox.setChecked(true);
					return;
				}
				FileWrapper mw = (FileWrapper) getItem(holder
						.getAdapterPosition());

				if (mw.getType() == FileWrapper.TYPE_DIR)
					fragment.browse(mw, holder.getAdapterPosition(), true);
				else if (mw.getType() == FileWrapper.TYPE_BITMAP) {
					Toast.makeText(fragment.getActivity(), "Ԥ��ͼƬ",
							Toast.LENGTH_LONG).show();
					// Util.openMedia(v.getContext(), mw);
				} else if (mw.getType() == FileWrapper.TYPE_FILE) {
					Toast.makeText(fragment.getActivity(), "Ԥ��pdf",
							Toast.LENGTH_LONG).show();
					// int position = 0;
					// LinkedList<FileWrapper> mediaLocations = new
					// LinkedList<FileWrapper>();
					// FileWrapper mediaItem;
					// for (Object item : mMediaList)
					// if (item instanceof FileWrapper) {
					// mediaItem = (FileWrapper) item;
					// if (mediaItem.getType() == FileWrapper.TYPE_VIDEO ||
					// mediaItem.getType() == FileWrapper.TYPE_AUDIO) {
					// mediaLocations.add(mediaItem);
					// if (mediaItem.equals(mw))
					// position = mediaLocations.size() - 1;
					// }
					// }
					// Util.openList(v.getContext(), mediaLocations, position);
				} else {
					Toast.makeText(fragment.getActivity(), "�ļ���ʽ��֧��",
							Toast.LENGTH_LONG).show();
					// Util.openStream(v.getContext(), mw.getLocation());
				}
			}
		});
		if (hasContextMenu) {
			vh.more.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fragment.onPopupMenu(vh.more, holder.getAdapterPosition());
				}
			});
			vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					isMoreSelect=!isMoreSelect;
					notifyDataSetChanged();
					mSelectPrintFileList.clear();
					return true;
				}
			});
		}
		vh.checkBox.setTag(file);
		vh.checkBox.setOnCheckedChangeListener(changeListener);
		
	}
	OnCheckedChangeListener changeListener=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			String path="";
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
				File pathFile = Environment.getExternalStorageDirectory();
				path=pathFile.getAbsolutePath();
			} 
			
			FileWrapper fileWrapper=(FileWrapper) buttonView.getTag();
			
			if(isChecked)
			{
				mSelectPrintFileList.add(fileWrapper);
			}else
			{
				mSelectPrintFileList.remove(fileWrapper);
			}
		}
	};
	@Override
	public int getItemCount() {
		return mMediaList.size();
	}
	public List<FileWrapper> getSelectPrintFileList()
	{
		return mSelectPrintFileList;
	}
	
	public class FileViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public CheckBox checkBox;
		public TextView text;
		public ImageView icon;
		public ImageView more;

		public FileViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.title);
			icon = (ImageView) v.findViewById(R.id.dvi_icon);
			text = (TextView) v.findViewById(R.id.text);

			more = (ImageView) v.findViewById(R.id.item_more);
			checkBox = (CheckBox) v.findViewById(R.id.browser_checkbox);
		}
	}

	public static class SeparatorViewHolder extends RecyclerView.ViewHolder {
		public TextView title;

		public SeparatorViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.separator_title);
		}
	}

	public static class Storage {
		Uri uri;
		String name;
		String description;

		public Storage(Uri uri) {
			this.uri = uri;
			name = uri.getLastPathSegment();
		}

		public String getName() {
			return Uri.decode(name);
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public Uri getUri() {
			return uri;
		}
	}

	public void clear() {
		mMediaList.clear();
		notifyDataSetChanged();
	}

	public boolean isEmpty() {
		return mMediaList.isEmpty();
	}

	public void addItem(FileWrapper file, boolean notify, boolean top) {
		addItem1(file, notify, top);
	}

	public void addItem1(Object item, boolean notify, boolean top) {
		int position = top ? 0 : mMediaList.size();
		if (item instanceof FileWrapper
				&& ((FileWrapper) item).getTitle().startsWith("."))
			return;
		else if (item instanceof FileWrapper) {
			mMediaList.add(position, (FileWrapper) item);
		}

		if (notify)
			notifyItemInserted(position);
	}

	public void setDescription(int position, String description) {
		Object item = getItem(position);
		if (item instanceof FileWrapper)
			((FileWrapper) item).setDescription(description);
		else if (item instanceof Storage)
			((Storage) item).setDescription(description);
		else
			return;
		notifyItemChanged(position);
	}

	public void updateMediaDirs() {
		if (mDbManager == null)
			mDbManager = PrintDataBase.getInstance(fragment.getActivity());
		if (mMediaDirsLocation == null)
			mMediaDirsLocation = new LinkedList<String>();
		else
			mMediaDirsLocation.clear();
		List<File> mediaDirs = mDbManager.getMediaDirs();
		for (File dir : mediaDirs) {
			mMediaDirsLocation.add(dir.getPath());
		}
		mCustomDirsLocation = Arrays.asList(CustomDirectories
				.getCustomDirectories());
	}

	public void addAll(ArrayList<FileWrapper> mediaList) {
		mMediaList.clear();
		for (FileWrapper mw : mediaList)
			mMediaList.add(mw);
		sortList(mMediaList);
	}

	public void removeItem(int position, boolean notify) {
		mMediaList.remove(position);
		if (notify) {
			notifyItemRemoved(position);
		}
	}

	public Object getItem(int position) {
		return mMediaList.get(position);
	}

	public int getItemViewType(int position) {
		if (getItem(position) instanceof FileWrapper)
			return TYPE_MEDIA;
		else if (getItem(position) instanceof Storage)
			return TYPE_STORAGE;
		else
			return TYPE_SEPARATOR;
	}
	public void sortList()
	{
		sortList(mMediaList);
	}
	public ArrayList<FileWrapper> sortList( ArrayList<FileWrapper> arrayList) {
		ArrayList<FileWrapper> files = new ArrayList<FileWrapper>(), dirs = new ArrayList<FileWrapper>();
		for (Object item : arrayList) {
			if (item instanceof FileWrapper) {
				FileWrapper media = (FileWrapper) item;
				if (media.getType() == FileWrapper.TYPE_DIR)
					dirs.add(media);
				else
					files.add(media);
			}
		}
		if (dirs.isEmpty() && files.isEmpty())
			return null;
		arrayList.clear();
		if (!dirs.isEmpty()) {
			Collections.sort(dirs, FileComparators.byName);
			arrayList.addAll(dirs);
		}
		if (!files.isEmpty()) {
			Collections.sort(files, FileComparators.byName);
			arrayList.addAll(files);
		}
		notifyDataSetChanged();
		return arrayList;
	}

	protected int getIconResId(FileWrapper file) {
		switch (file.getType()) {
		case FileWrapper.TYPE_BITMAP:
			return R.drawable.ic_browser_bitmap_normal;
		case FileWrapper.TYPE_DIR:
			return FOLDER_RES_ID;
		case FileWrapper.TYPE_FILE:
			return R.drawable.ic_browser_file_normal;
		default:
			return R.drawable.ic_browser_unknown_normal;
		}
	}
}
