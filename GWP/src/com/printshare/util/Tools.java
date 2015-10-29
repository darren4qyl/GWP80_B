package com.printshare.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Tools {
	public static void saveDataSharedPreferences(Context context,String name,String key,String value)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(name, context.MODE_WORLD_WRITEABLE).edit();
        editor.putString(key, value);
        editor.commit();
	}
	public static String getDataSharedPreferences(Context context,String name,String key)
	{
		SharedPreferences read = context.getSharedPreferences(name, context.MODE_WORLD_READABLE);
		String value = read.getString(key, "");
        return value;
	}
}
