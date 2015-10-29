/*****************************************************************************
 * SidebarAdapter.java
 *****************************************************************************
 * Copyright © 2012-2013 GWP authors 
 * Copyright © 2012-2013 Edward Wang
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
 *****************************************************************************/
package com.printshare.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.printshare.BuildConfig;
import com.printshare.PrintApplication;
import com.printshare.R;
import com.printsharelib.database.PrintDataBase;
import com.printshare.util.Util;

public class SidebarAdapter extends BaseAdapter {
    public final static String TAG = "printshare/SidebarAdapter";

    public static class SidebarEntry {
        public static final  int TYPE_FRAGMENT = 0;
        public static final  int TYPE_ACTION = 1;
        public static final  int TYPE_SECONDARY_FRAGMENT = 2;

        public static final String ID_MENU_OPEN = "open";
        public static final String ID_AUDIO = "audio";
        public static final String ID_NETWORK = "network";
        public static final String ID_DIRECTORIES = "directories";
        public static final String ID_HISTORY = "history";
        public static final String ID_MRL = "mrl";
        public static final String ID_ABOUT = "about";

        String id;
        String name;
        int attributeID;
        int type;

        public SidebarEntry(String id, int name, int attributeID, int type) {
            this.id = id;
            this.name = PrintApplication.getAppContext().getString(name);
            this.attributeID = attributeID;
            this.type = type;
        }
    }

    private Context mContext;
    private LayoutInflater mInflater;
    static final List<SidebarEntry> entries;
    public static final List<String> sidebarFragments;
    private HashMap<String, Fragment> mFragments;
    private String mCurrentFragmentId;

    static {
    	sidebarFragments = new ArrayList<String>();
        entries = new ArrayList<SidebarEntry>();
        entries.add(new SidebarEntry(BaseAppActivity.FRAGMENT_MENU_ID_HOME, R.string.menu_home, R.attr.ic_menu_home, SidebarEntry.TYPE_FRAGMENT));
        entries.add(new SidebarEntry(BaseAppActivity.FRAGMENT_MENU_ID_OPEN_LOCAL, R.string.menu_open_local, R.attr.ic_menu_open_local, SidebarEntry.TYPE_FRAGMENT));
        entries.add(new SidebarEntry(BaseAppActivity.FRAGMENT_MENU_ID_PREFERENCES, R.string.preferences, R.attr.ic_menu_preferences, SidebarEntry.TYPE_ACTION));
        for(SidebarEntry e : entries) {
            sidebarFragments.add(e.id);
        }
    }

    public SidebarAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFragments = new HashMap<String, Fragment>(entries.size());
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position; // The SidebarEntry list is unique
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        SidebarEntry sidebarEntry = entries.get(position);

        /* If view not created */
        if(v == null) {
            v = mInflater.inflate(R.layout.sidebar_item, parent, false);
        }
        TextView textView = (TextView)v;
        textView.setText(sidebarEntry.name);
        Drawable img = PrintApplication.getAppResources().getDrawable(
                Util.getResourceFromAttribute(mContext, sidebarEntry.attributeID));
        if (img != null) {
            int dp_32 = Util.convertDpToPx(32);
            img.setBounds(0, 0, dp_32, dp_32);
            textView.setCompoundDrawables(img, null, null, null);
        }
        // Set in selected the current item.
        if (TextUtils.equals(mCurrentFragmentId,sidebarEntry.id)) {
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }

        return v;
    }

//    public Fragment fetchFragment(String id) {
//        // Save the previous fragment in case an error happens after.
//        String prevFragmentId = mCurrentFragmentId;
//
//        // Set the current fragment.
//        setCurrentFragment(id);
//
//        if(mFragments.containsKey(id) && mFragments.get(id) != null) {
//            return mFragments.get(id);
//        }
//
//        Fragment f;
//        if(id.equals(SidebarEntry.ID_MENU_OPEN)) {
//            f = new FragmentOpenLocalDir();
//        }
//        else {
//            mCurrentFragmentId = prevFragmentId; // Restore the current fragment id.
//            throw new IllegalArgumentException("Wrong fragment id.");
//        }
//        f.setRetainInstance(true);
//        mFragments.put(id, f);
//        return f;
//    }

    public void setCurrentFragment(String id) {
        mCurrentFragmentId = id;
        this.notifyDataSetChanged();
    }

    /**
     * When Android has automatically recreated a fragment from the bundle state,
     * use this function to 'restore' the recreated fragment into this sidebar
     * adapter to prevent it from trying to create the same fragment again.
     *
     * @param id ID of the fragment
     * @param f The fragment itself
     */
    public void restoreFragment(String id, Fragment f) {
        if(f == null) {
            Log.e(TAG, "Can't set null fragment for " + id + "!");
            return;
        }
        mFragments.put(id, f);
        setCurrentFragment(id);
        // if Android added it, it's been implicitly added already...
    }
}
