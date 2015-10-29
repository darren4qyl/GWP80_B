package com.printshare.ui.browser;

import java.io.File;

import android.net.Uri;

import com.printshare.util.Util;
import com.printsharelib.file.FileWrapper;
import com.printsharelib.util.AndroidUtil;

public class FileBrowser {
	private EventListener mEventListener;
	private boolean mAlive;

	public FileBrowser(EventListener listener) {
		mEventListener = listener;
		mAlive = true;
	}

	private synchronized void reset() {

	}

	public synchronized void changeEventListener(EventListener eventListener) {
		reset();
		mEventListener = eventListener;
	}

	/**
	 * Browse to the specified local path starting with '/'.
	 * 
	 * @param path
	 */
	public synchronized void browse(String path) {
		browse1(path);
	}

	/**
	 * Browse to the specified uri.
	 * 
	 * @param uri
	 */
	public synchronized void browse(Uri uri) {
		browse1(uri.getPath());
	}

	/**
	 * Browse to the specified media.
	 * 
	 * @param media
	 *            Can be a media returned by MediaBrowser.
	 */
	public synchronized void browse1(String path) {
		/*
		 * media can be associated with a medialist, so increment ref count in
		 * order to don't clean it with the medialist
		 */
		File fileIO = new File(path);
		File[] subFile = fileIO.listFiles();
		for (File fileList : subFile) {
			if (fileList.isDirectory()&&!fileList.isHidden()) {
				FileWrapper file=new FileWrapper(AndroidUtil.PathToUri(fileList.getAbsolutePath()));
				file.setTitle(fileList.getName());
				file.setType(FileWrapper.TYPE_DIR);
				file.setFileName(fileList.getName());
				mEventListener.onMediaAdded(0, file);
			} else if(!fileList.isHidden()) {
				FileWrapper file=new FileWrapper(AndroidUtil.PathToUri(fileList.getAbsolutePath()));
				file.setTitle(fileList.getAbsolutePath());
				file.setFileName(fileList.getName());
				mEventListener.onMediaAdded(0, file);
			}
		}
		mEventListener.onBrowseEnd();
	}

	/**
	 * Listener called when medias are added or removed.
	 */
	public interface EventListener {
		/**
		 * Received when a new media is added.
		 * 
		 * @param index
		 * @param media
		 */
		public void onMediaAdded(int index, FileWrapper file);

		/**
		 * Received when a media is removed (Happens only when you discover
		 * networks)
		 * 
		 * @param index
		 * @param media
		 *            Released media, but cached attributes are still available
		 *            (like media.getMrl())
		 */
		public void onMediaRemoved(int index, FileWrapper file);

		/**
		 * Called when browse ended. It won't be called when you discover
		 * networks
		 */
		public void onBrowseEnd();
	}
}
