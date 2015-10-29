package com.printshare.ui;

import com.printshare.R;
import com.printshare.dbtable.PrinterDevicesTable.PrinterDevicesTableData;
import com.printshare.test.TestActivity;
import com.printshare.ui.AddPrinterDevices.SimpleDynamics;
import com.printsharelib.listview3d.util.ListView3D;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener,
		OnPreferenceChangeListener {
	private SharedPreferences mSP;
	private SwitchPreference mPrintPadding;
	private EditTextPreference mPrintPaddingLeft;
	private EditTextPreference mPrintPaddingTop;
	private EditTextPreference mPrintPaddingRight;
	private EditTextPreference mPrintPaddingBottom;
	private EditTextPreference mPrintBookbindingLine;
	private ListPreference mPrintBookbindingLinePosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		applyTheme();
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference printerAddDevPref = findPreference("printer_add_devices");
		printerAddDevPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						// Toast.makeText(PreferencesActivity.this, "添加打印设备",
						// Toast.LENGTH_LONG).show();
						Intent intent = new Intent(PreferencesActivity.this,
								AddPrinterDevices.class);
						startActivity(intent);

						return true;
					}
				});
		Preference printerDefaultDevPref = findPreference("printer_select_default_devices");
		printerDefaultDevPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						// Toast.makeText(PreferencesActivity.this, "添加打印设备",
						// Toast.LENGTH_LONG).show();
						/*Intent intent = new Intent(PreferencesActivity.this,
								TestActivity.class);
						startActivity(intent);*/
						Toast.makeText(PreferencesActivity.this, "暂时不支持此功能，设置默认打印机请到添加列表，这是默认设备。。。", Toast.LENGTH_LONG).show();
						return true;
					}
				});
		mPrintPadding = (SwitchPreference) findPreference("printer_print_padding");
		mSP = getPreferenceScreen().getSharedPreferences();
		mPrintPaddingLeft = (EditTextPreference) findPreference("printer_print_padding_left");
		mPrintPaddingLeft.setSummary("左边距为："
				+ mSP.getString("printer_print_padding_left", "0") + "mm");
		mPrintPaddingLeft.setOnPreferenceChangeListener(this);
		mPrintPaddingTop = (EditTextPreference) findPreference("printer_print_padding_top");
		mPrintPaddingTop.setSummary("上边距为："
				+ mSP.getString("printer_print_padding_top", "0") + "mm");
		mPrintPaddingTop.setOnPreferenceChangeListener(this);
		mPrintPaddingRight = (EditTextPreference) findPreference("printer_print_padding_right");
		mPrintPaddingRight.setSummary("右边距为："
				+ mSP.getString("printer_print_padding_right", "0") + "mm");
		mPrintPaddingRight.setOnPreferenceChangeListener(this);
		mPrintPaddingBottom = (EditTextPreference) findPreference("printer_print_padding_bottom");
		mPrintPaddingBottom.setSummary("下边距为："
				+ mSP.getString("printer_print_padding_bottom", "0") + "mm");
		mPrintPaddingBottom.setOnPreferenceChangeListener(this);

		mPrintBookbindingLine = (EditTextPreference) findPreference("printer_print_bookbinding_line");
		mPrintBookbindingLine.setSummary("装订线宽度："
				+ mSP.getString("printer_print_bookbinding_line", "0") + "mm");
		mPrintBookbindingLine.setOnPreferenceChangeListener(this);

		mPrintBookbindingLinePosition = (ListPreference) findPreference("printer_print_bookbinding_line_position");
		String strPosition="左边";
		switch (mSP.getString("printer_print_bookbinding_line_position", "1")) {
		case "1":
			strPosition="左边";
			break;
		case "2":
			strPosition="顶部";
			break;
		case "3":
			strPosition="右边";
			break;
		case "4":
			strPosition="底部";
			break;
		default:
			break;
		}
		mPrintBookbindingLinePosition.setSummary("装订线位置："
				+ strPosition);
		mPrintBookbindingLinePosition.setOnPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Toolbar bar;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			LinearLayout root = (LinearLayout) getListView().getParent()
					.getParent().getParent();
			bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar,
					root, false);
			root.addView(bar, 0); // insert at top
		} else {
			ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
			if (!(root.getChildAt(0) instanceof ListView))
				return;
			ListView content = (ListView) root.getChildAt(0);

			root.removeAllViews();

			bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar,
					root, false);
			root.addView(bar);

			int height;
			TypedValue tv = new TypedValue();
			if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
				height = TypedValue.complexToDimensionPixelSize(tv.data,
						getResources().getDisplayMetrics());
			} else {
				height = bar.getHeight();
			}

			content.setPadding(0, height, 0, 0);

			root.addView(content);
		}
		bar.setTitle(R.string.preferences);

		bar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference instanceof PreferenceScreen)
			setUpNestedScreen((PreferenceScreen) preference);
		try {
			if (preference != null && preference instanceof PreferenceScreen) {
				Dialog dialog = ((PreferenceScreen) preference).getDialog();
				if (dialog != null) {
					Window window = dialog.getWindow();
					if (window != null) {
						ConstantState state = this.getWindow().getDecorView()
								.findViewById(android.R.id.content)
								.getBackground().getConstantState();
						if (state != null)
							window.getDecorView().setBackgroundDrawable(
									state.newDrawable());
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		Preference pref = findPreference(key);
		if (pref instanceof EditTextPreference) {
			String title = pref.getSummary().toString();
			String str = title.substring(0, title.indexOf("：")) + "：";
			EditTextPreference etp = (EditTextPreference) pref;
			str += etp.getText() + "mm";
			pref.setSummary(str);
		}else if(pref == mPrintBookbindingLinePosition)
		{
			String strPosition="左边";
			switch (((ListPreference)pref).getValue().toString()) {
			case "1":
				strPosition="左边";
				break;
			case "2":
				strPosition="顶部";
				break;
			case "3":
				strPosition="右边";
				break;
			case "4":
				strPosition="底部";
				break;
			default:
				break;
			}
			mPrintBookbindingLinePosition.setSummary("装订线位置："+strPosition);
		}

	}

	private void applyTheme() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean enableBlackTheme = pref.getBoolean("enable_black_theme", false);
		if (enableBlackTheme) {
			setTheme(R.style.Theme_GWP);
		}
	}

	public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
		final Dialog dialog = preferenceScreen.getDialog();

		Toolbar bar;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			LinearLayout root = (LinearLayout) dialog.findViewById(
					android.R.id.list).getParent();
			bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar,
					root, false);
			root.addView(bar, 0); // insert at top
		} else {
			ViewGroup root = (ViewGroup) dialog
					.findViewById(android.R.id.content);
			ListView content = (ListView) root.getChildAt(0);

			root.removeAllViews();

			bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar,
					root, false);

			int height;
			TypedValue tv = new TypedValue();
			if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
				height = TypedValue.complexToDimensionPixelSize(tv.data,
						getResources().getDisplayMetrics());
			} else {
				height = bar.getHeight();
			}

			content.setPadding(0, height, 0, 0);

			root.addView(content);
			root.addView(bar);
		}

		bar.setTitle(preferenceScreen.getTitle());

		bar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if(preference==mPrintBookbindingLinePosition)
			return true;
		try {
			Integer.parseInt(newValue.toString().trim());
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("请输入整型数值，默认单位为mm；");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", null);
			builder.create().show();
		}
		return false;
	}

}
