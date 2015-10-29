/**
 * 
 */
package com.printshare.ui;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.circlemenu.CircleLayout;
import com.circlemenu.CircleLayout.OnCircleItemClickListener;
import com.circlemenu.CircleLayout.OnCircleItemSelectedListener;
import com.image.FixLinearLayout;
import com.image.ImageBrowse;
import com.image.ImageBrowse.ListViewAdapter;
import com.image.ImageBrowse.RefreshCirclemenu;
import com.image.ImageBrowse.ViewParameter;
import com.image.util.Combination;
import com.image.util.ImageViewOperation;
import com.printshare.R;
import com.printshare.bluetooth.DeviceListActivity;
import com.printshare.services.BluetoothPrintService;
import com.printshare.services.USBPrintService;
import com.printshare.test.FragmentTest;
import com.printshare.ui.browser.FileBrowserFragment;
import com.printshare.util.BitmapOperationUtil;
import com.printshare.util.Tools;
import com.printshare.util.WaitDialog;
import com.printshare.widget.VerticalProgressBar;
import com.printsharelib.file.FileWrapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;

/**
 * @author over
 * 
 */
public class PreviewPrinter extends AppCompatActivity implements
		OnTouchListener, OnClickListener {
	private static final int REQUEST_ENABLE_BT = 0;
	private String DEFAULT_DEVICE_NAME = "";
	private String PRINTER_DEVICE = "printer_device";
	ImageBrowse hListView;
	ListViewAdapter hListViewAdapter;
	ImageViewOperation previewImg;
	View olderSelectView = null;
	FixLinearLayout property;
	List<TextView> textViews = new ArrayList<TextView>();
	List<Integer> values = new ArrayList<Integer>();
	ImageView imageView;
	private TextView mStatusLabel = null;

	private CircleLayout circleMenu;
	private FrameLayout main_circle_layout_FrameLayout = null;
	/** 缩小动画 **/
	Animation mLitteAnimation = null;
	/** 放大动画 **/
	Animation mBigAnimation = null;
	public static final int MESSAGE_CIRCLE_MENU_CLICK = 1;
	public static final int MESSAGE_CIRCLE_MENU_MOVE = 2;
	private int circle_menu_status = 0;
	/** 获取button控件 */
	private RelativeLayout btnClick = null;
	/** 获取X,Y */
	private int lastX, lastY;
	private int menu_left = 0;
	private int menu_right = 0;
	private int menu_top = 0;
	private int menu_bottom = 0;
	private int menu_init = 0;
	/** 获取容器尺寸 */
	private DisplayMetrics dm;
	/** 获取宽度 */
	private int screenWidth = 0;
	/** 获取高 */
	private int screenHeight = 0;
	private final int INIT_NUM = 0;
	private final int INTI_HEIGHT_NUM = 0;

	private BitmapOperationUtil mBitmapOperationUtil = null;
	PowerManager pm = null;
	PowerManager.WakeLock wl = null;
	private BluetoothPrintService mBluetoothPrintService;
	private Toolbar mToolbar = null;
	/**
	 * 0:没有添加默认设备，必须要添加默认设备 1:未连接状态 2:已连接 3:打印中 4:打印完成
	 */
	private int mDeviceJobStatus = 0;

	private View printProcessView = null;
	private ImageView mPrintProgressImage;
	Animation animation;
	LinearInterpolator lir;
	private static PopupWindow popupWindow = null;
	VerticalProgressBar mPrintPercentage;
	private TextView mPrintProcessTextShow = null;
	private Button btnPrintProcessCancel = null;
	private TextView mPrintProcessPercentage = null;
	private USBPrintService mUsbPrintService = null;

	
	private String[] mFileList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_printer_bitmap);
		WaitDialog.getInstance().OpenLoadDialog(this, "正在加载中……");
		dm = getResources().getDisplayMetrics();
		// 获取容器的宽、高
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - INTI_HEIGHT_NUM;
		circleMenu = (CircleLayout) findViewById(R.id.main_circle_layout);
		circleMenu
				.setCircleOnItemSelectedListener(onCircleItemSelectedListener);
		circleMenu.setCircleOnItemClickListener(onCircleItemClickListener);
		main_circle_layout_FrameLayout = (FrameLayout) findViewById(R.id.main_circle_layout_FrameLayout);
		mLitteAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bluetooth_circle_menu_reduce);
		mBigAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bluetooth_circle_menu_amplify);
		mBitmapOperationUtil = BitmapOperationUtil.getInstance(this);
		btnClick = (RelativeLayout) findViewById(R.id.btn);
		// 滑动的监听事件
		btnClick.setOnTouchListener(this);

		// 点击的事件
		btnClick.setOnClickListener(this);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"PrintPreView");
		wl.acquire();
		mStatusLabel = (TextView) findViewById(R.pp.textview_label);
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		mToolbar.setTitle(getResources().getString(
				R.string.preview_printer_menu_title));
		mToolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		setSupportActionBar(mToolbar);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LayoutInflater layoutInflater = (LayoutInflater) PreviewPrinter.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				printProcessView = layoutInflater.inflate(
						R.layout.print_process, null);
				mPrintProgressImage = (ImageView) printProcessView
						.findViewById(R.id.search_progress);
				mPrintPercentage = (VerticalProgressBar) printProcessView
						.findViewById(R.id.circle_progress_percentage);
				lir = new LinearInterpolator();
				mPrintProcessTextShow = (TextView) printProcessView
						.findViewById(R.id.PrintProcessShowText);
				mPrintProcessPercentage = (TextView) printProcessView
						.findViewById(R.id.percentage_text);
				btnPrintProcessCancel = (Button) printProcessView
						.findViewById(R.id.btnCancelPrint);
				animation = AnimationUtils.loadAnimation(PreviewPrinter.this,
						R.anim.print_progress_anim);
				btnPrintProcessCancel.setOnClickListener(PreviewPrinter.this);
			}
		}).start();
	}

	private void CreatePopupWindow() {
		popupWindow = new PopupWindow(printProcessView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(mToolbar, Gravity.CENTER, 0, 0);
		animation.setInterpolator(lir);
		mPrintProgressImage.startAnimation(animation);
	}

	private void setCircleMenuLocation(View v, int l, int t, int r, int b) {
		// Log.e("darren",
		// "**************************left:"+l+"top"+t+"rirht"+r+"botton:"+b);
		int width = main_circle_layout_FrameLayout.getWidth();
		int height = main_circle_layout_FrameLayout.getHeight();
		int left = l + (r - l) / 2 - width / 2;
		int top = t + (b - t) / 2 - height / 2;
		int right = l + (r - l) / 2 + width / 2;
		int bottom = t + (b - t) / 2 + height / 2;
		// Log.e("darren",
		// "----------------------left:"+left+"top"+top+"rirht"+right+"botton:"+bottom);
		if (main_circle_layout_FrameLayout.getVisibility() == View.VISIBLE)
			main_circle_layout_FrameLayout.layout(left, top, right, bottom);
		// main_circle_layout_FrameLayout.setX(left);
		// main_circle_layout_FrameLayout.setY(top);
		v.layout(l, t, r, b);
		// v.setX(l);
		// v.setY(t);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		main_circle_layout_FrameLayout.setVisibility(View.INVISIBLE);
		menu_init = 0;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				// TODO Auto-generated method stub
				String val = Tools.getDataSharedPreferences(
						PreviewPrinter.this, PRINTER_DEVICE, "type");
				DEFAULT_DEVICE_NAME = Tools.getDataSharedPreferences(
						PreviewPrinter.this, PRINTER_DEVICE, "devices_name");
				if (val.equals("bluetooth")) {
					mBluetoothPrintService = new BluetoothPrintService(PreviewPrinter.this,
							mBluetoothHandler);
					mDefaultDeviceHandler.obtainMessage(1).sendToTarget();
				} else if (val.equals("usb")) {
					mUsbPrintService = USBPrintService.getInstance(
							PreviewPrinter.this, mUsbHandler);
					mDefaultDeviceHandler.obtainMessage(2).sendToTarget();
				} else {
					mDefaultDeviceHandler.obtainMessage(3).sendToTarget();
				}
				Looper.loop();
			}
		}).start();
	}

	private Handler mDefaultDeviceHandler = new Handler() {
		private Drawable drawable;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				mDeviceJobStatus = 1;
				drawable = getResources().getDrawable(R.drawable.bluetooth_min);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mStatusLabel.setCompoundDrawables(drawable, null, null, null);
				mStatusLabel.setText(DEFAULT_DEVICE_NAME + ": 设备未连接");
				break;
			case 2:
				mDeviceJobStatus = 1;
				drawable = getResources().getDrawable(R.drawable.usb_min);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mStatusLabel.setCompoundDrawables(drawable, null, null, null);
				mStatusLabel.setText(DEFAULT_DEVICE_NAME + ": 设备未连接");
				break;
			case 3:
				mDeviceJobStatus = 0;
				drawable = getResources().getDrawable(
						R.drawable.no_default_device);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mStatusLabel.setCompoundDrawables(drawable, null, null, null);
				mStatusLabel.setText(" 没有设置默认设备");
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		wl.release();
		super.onDestroy();

	}

	public void initUI() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String style = bundle.getString("style", "list");// or property
		final List<FileWrapper> fileWrapper = (List<FileWrapper>) bundle
				.getSerializable("content");
		hListView = (ImageBrowse) findViewById(R.pp.horizon_listview);
		hListView.setRefreshCirclemenu(new RefreshCirclemenu() {

			@Override
			public void onImageBrowseRefreshCirclemenu() {
				// TODO Auto-generated method stub
				main_circle_layout_FrameLayout.setVisibility(View.INVISIBLE);
				menu_init = 0;
			}
		});
		previewImg = (ImageViewOperation) findViewById(R.pp.image_preview);
		property = (FixLinearLayout) findViewById(R.pp.fix_grid_layout_property);
		imageView = (ImageView) findViewById(R.pp.image);
		if (style.equals("list")) {
			if (fileWrapper != null && fileWrapper.size() > 0) {
				String[] titles = new String[fileWrapper.size()];
				mFileList = new String[fileWrapper.size()];
				for (int i = 0; i < fileWrapper.size(); i++) {
					titles[i] = fileWrapper.get(i).getTitle();
					mFileList[i] = Uri.decode(fileWrapper.get(i).getUri()
							.getEncodedPath().toString());
				}

				hListViewAdapter = hListView.new ListViewAdapter(
						getApplicationContext(), titles, mFileList);
				hListView.setAdapter(hListViewAdapter);
				hListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// if(olderSelectView == null){
						// olderSelectView = view;
						// }else{
						// olderSelectView.setSelected(false);
						// olderSelectView = null;
						// }
						// olderSelectView = view;
						// view.setSelected(true);
						previewImg.setImageBitmap(mBitmapOperationUtil
								.getPrintPreviewBitmap(PreviewPrinter.this,
										mFileList[position]));

						hListViewAdapter.setSelectIndex(position);
						hListViewAdapter.notifyDataSetChanged();
						main_circle_layout_FrameLayout
								.setVisibility(View.INVISIBLE);
						menu_init = 0;
					}
				});
			}

		} else {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			int screenHeigh = dm.heightPixels;
			String[] propertyVal = {};
			for (int i = 0; i < propertyVal.length; i++) {
				TextView textView = new TextView(this);
				textView.setText(propertyVal[i]);
				textView.setTextColor(0xFFFFFFFF);
				textView.setLayoutParams(new LayoutParams(-2, -2));
				textView.setSingleLine();
				textView.setPadding(20, 10, 20, 10);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18);
				textView.setBackgroundResource(R.drawable.btn_erase_pressed);
				int width = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);

				int height = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);

				textView.measure(width, height);

				width = textView.getMeasuredWidth();
				textViews.add(textView);
				values.add(width);
				// property.addChildView(textView,/*(int)
				// getCharacterWidth(i,18)*/width,screenWidth);

			}
			while (textViews.size() > 0) {
				Combination combination = new Combination();
				int[] arrays = new int[values.size()];
				for (int i = 0; i < values.size(); i++) {
					arrays[i] = values.get(i);
				}
				combination.combination(arrays, screenWidth);
				List<Integer> integers = combination.getListRow();
				int id = 0;
				for (Integer integer : integers) {
					property.addChildView(textViews.get(integer - id),/*
																	 * (int)
																	 * getCharacterWidth
																	 * (i,18)
																	 */
							values.get(integer - id), screenWidth);
					textViews.remove(integer - id);
					values.remove(integer - id);
					id++;
				}
			}
		}
		ViewTreeObserver vto2 = imageView.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				RelativeLayout.LayoutParams paramTV = new RelativeLayout.LayoutParams(
						0, 0);
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);

				paramTV = new RelativeLayout.LayoutParams(imageView.getWidth(),
						imageView.getHeight());
				previewImg.setLayoutParams(paramTV);
				previewImg.setImageRect(imageView.getWidth(),
						imageView.getHeight());

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Bitmap bmp = mBitmapOperationUtil
								.getPrintPreviewBitmap(
										PreviewPrinter.this,
										Uri.decode(fileWrapper.get(0).getUri()
												.getEncodedPath().toString()));
						mUpdataImageHandler.obtainMessage(1, bmp)
								.sendToTarget();
					}
				}).start();
			}
		});
		// hListView.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// if(olderSelected != null){
		// olderSelected.setSelected(false); //��һ��ѡ�е�View�ָ�ԭ����
		// }
		// olderSelected = view;
		// view.setSelected(true);
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

	}

	private Handler mUpdataImageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bitmap bmp = (Bitmap) msg.obj;
			Matrix currentMatrix = new Matrix();
			float scale = (float) imageView.getWidth() / bmp.getWidth();
			float scale2 = (float) imageView.getHeight() / bmp.getHeight();
			if (scale > scale2)
				scale = scale2;
			currentMatrix.postScale(scale, scale);
			previewImg.setImageBitmap(bmp);// 设置控件图片
			previewImg.setImageMatrix(currentMatrix);
			previewImg.center(true, true, true);
			WaitDialog.getInstance().DismissDialog();
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * Note: on Android 3.0+ with an action bar this method is called while
		 * the view is created. This can happen any time after onCreate.
		 */
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.print_bitmap_preview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.pbpm_connect:
			if (mDeviceJobStatus < 1) {
				return super.onOptionsItemSelected(item);
			}
			String str = item.getTitle().toString();
			if (str.equals(getResources().getString(
					R.string.preview_printer_menu_connect_title))) {
				item.setTitle(getResources().getString(
						R.string.preview_printer_menu_connect_title_no));
				String val = Tools.getDataSharedPreferences(this,
						PRINTER_DEVICE, "type");
				if (val.equals("bluetooth")) {
					mBluetoothPrintService.connectDevice(Tools
							.getDataSharedPreferences(this, PRINTER_DEVICE,
									"buletooth_address"));
					DEFAULT_DEVICE_NAME = Tools.getDataSharedPreferences(this,
							PRINTER_DEVICE, "devices_name");
				} else if (val.equals("usb")) {
					mUsbPrintService.getUsbDevices();
					mUsbPrintService.OpenDevice();
				}

			} else if (str.equals(getResources().getString(
					R.string.preview_printer_menu_connect_title_no))) {
				item.setTitle(getResources().getString(
						R.string.preview_printer_menu_connect_title));
				String val = Tools.getDataSharedPreferences(this,
						PRINTER_DEVICE, "type");
				if (val.equals("bluetooth")) {
					mDeviceJobStatus = 1;
					mBluetoothPrintService.StopPrint();
					DEFAULT_DEVICE_NAME = Tools.getDataSharedPreferences(this,
							PRINTER_DEVICE, "devices_name");
					mStatusLabel
					.setText(DEFAULT_DEVICE_NAME
							+ ":未连接"
							);
				} else if (val.equals("usb")) {
					mDeviceJobStatus = 1;
					mUsbPrintService.stopSendData();
					mStatusLabel
					.setText(DEFAULT_DEVICE_NAME
							+ ":未连接"
							);
				}
			}

			break;
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public float getCharacterWidth(String text, float size) {
		if (null == text || "".equals(text))
			return 0;
		float width = 0;
		Paint paint = new Paint();
		paint.setTextSize(size);
		float text_width = paint.measureText(text);// 得到总体长度
		width = text_width / text.length();// 每一个字符的长度
		return text_width;
	}

	OnCircleItemSelectedListener onCircleItemSelectedListener = new OnCircleItemSelectedListener() {

		@Override
		public void onCircleItemSelected(View view, int position, long id,
				String name) {
			// TODO Auto-generated method stub

		}
	};
	OnCircleItemClickListener onCircleItemClickListener = new OnCircleItemClickListener() {

		@Override
		public void onCircleItemClick(View view, int position, long id,
				String name) {
			// TODO Auto-generated method stub

			if (view.getId() == R.id.laybtn_fangda) {
			} else if (view.getId() == R.id.laybtn_suoxiao) {
			} else if (view.getId() == R.id.laybtn_xiangshangfanye) {
			} else if (view.getId() == R.id.laybtn_xiangxiafanye) {
			} else if (view.getId() == R.id.laybtn_printparametersetting) {
			} else if (view.getId() == R.id.laybtn_printselectdefaultdevice) {
			} else if (view.getId() == R.id.laybtn_quanbudayin) {
				if (mDeviceJobStatus < 2) {
					Toast.makeText(
							getApplicationContext(),
							getResources()
									.getString(
											R.string.preview_printer_view_hint_print_before),
							Toast.LENGTH_SHORT).show();

					return;
				}
				String val = Tools.getDataSharedPreferences(PreviewPrinter.this,
						PRINTER_DEVICE, "type");
				if (val.equals("bluetooth")) {
					mBluetoothPrintService.StartPrint(mFileList);
				} else if (val.equals("usb")) {
					mUsbPrintService.sendData(mFileList);
				}
			} else if (view.getId() == R.id.laybtn_dayindangqianye) {
				if (mDeviceJobStatus < 2) {
					Toast.makeText(
							getApplicationContext(),
							getResources()
									.getString(
											R.string.preview_printer_view_hint_print_before),
							Toast.LENGTH_SHORT).show();

					return;
				}
				String val = Tools.getDataSharedPreferences(PreviewPrinter.this,
						PRINTER_DEVICE, "type");
				String[] file=new String[1];
				file[0]=mFileList[hListViewAdapter.getSelectIndex()];
				if (val.equals("bluetooth")) {
					mBluetoothPrintService.StartPrint(file);
				} else if (val.equals("usb")) {
					mUsbPrintService.sendData(file);
				}
			}
		}
	};
	Integer iPrintPageTatol = 0;
	private Timer m_timer = null;

	class MyTimerTask extends TimerTask {
		public void run() {
			try {
				mUsbHandler.obtainMessage(USBPrintService.UsbMESSAGE_KILL_POPUPWINDOW).sendToTarget();
				m_timer.cancel();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	Handler mUsbHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case USBPrintService.UsbGetDevices:
				mStatusLabel.setText(DEFAULT_DEVICE_NAME+":"+getResources().getString(
						R.string.preview_printer_usb_get_device_fd));
				break;
			case USBPrintService.UsbRequestPermission:
				mStatusLabel.setText(DEFAULT_DEVICE_NAME+":"+getResources().getString(
						R.string.preview_printer_usb_request_device_permissions));
				break;
			case USBPrintService.UsbAcquicePermission:
				mStatusLabel.setText(DEFAULT_DEVICE_NAME+":"+getResources().getString(
						R.string.preview_printer_usb_acquice_device_permissions));
				break;
			case USBPrintService.UsbMESSAGE_PRINT_OPEN:
				mStatusLabel.setText(DEFAULT_DEVICE_NAME+":"+getResources().getString(
						R.string.preview_printer_usb_device_open));
				mDeviceJobStatus = 2;
				break;
			case USBPrintService.UsbMESSAGE_PRINT_OPEN_FAILE:
				mStatusLabel.setText(DEFAULT_DEVICE_NAME+":"+getResources().getString(
						R.string.preview_printer_usb_device_open));
				mDeviceJobStatus = 1;
				break;
			case USBPrintService.UsbMESSAGE_PRINT_PREPARE:
				if (popupWindow == null) {
					CreatePopupWindow();
				}
				iPrintPageTatol = msg.arg1;
				mPrintProcessTextShow.setText("打印机开始准备打印。。。。");
				mPrintPercentage.setProgress(0);
				mPrintProcessPercentage.setText("00%");
				break;
			case USBPrintService.UsbMESSAGE_PRINT_START:
				int printPercentage = msg.arg1;
				mPrintProcessTextShow.setText("总共打印" + iPrintPageTatol
						+ "张，当前打印第" + (msg.arg2+1) + "张；" + "打印中。。。。。");
				mPrintPercentage.setProgress(printPercentage);
				mPrintProcessPercentage.setText(printPercentage + "%");
				break;
			case USBPrintService.UsbMESSAGE_PRINT_DONE:
				mPrintProcessTextShow.setText("打印完成！");
				mPrintPercentage.setProgress(100);
				mPrintProcessPercentage.setText("100%");
				MyTimerTask task = new MyTimerTask();
				m_timer = new Timer(true);
				m_timer.schedule(task, 1000);
				break;
			case USBPrintService.UsbMESSAGE_KILL_POPUPWINDOW:
				if (popupWindow != null) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				break;
			default:
				break;
			}
		}

	};
	Handler mBluetoothHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case BluetoothPrintService.BluetoothAdapterNotAvailable:
				mStatusLabel
						.setText(getResources()
								.getString(
										R.string.preview_printer_bluetooth_default_device_invalid));
				break;
			case BluetoothPrintService.BluetoothAdapterStartActivityForResult:
				mStatusLabel.setText(getResources().getString(
						R.string.preview_printer_bluetooth_open));
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				break;
			case BluetoothPrintService.MESSAGE_PRINT_PREPARE:
				if (popupWindow == null) {
					CreatePopupWindow();
				}
				iPrintPageTatol = msg.arg1;
				mPrintProcessTextShow.setText("打印机开始准备打印。。。。");
				mPrintPercentage.setProgress(0);
				mPrintProcessPercentage.setText("00%");

				break;
			case BluetoothPrintService.MESSAGE_PRINT_START:
				int printPercentage = msg.arg1;
				mPrintProcessTextShow.setText("总共打印" + iPrintPageTatol
						+ "张，当前打印第" + (msg.arg2+1) + "张；" + "打印中。。。。。");
				mPrintPercentage.setProgress(printPercentage);
				mPrintProcessPercentage.setText(printPercentage + "%");

				break;
			case BluetoothPrintService.MESSAGE_PRINT_DONE:
				mPrintProcessTextShow.setText("打印完成！");
				mPrintPercentage.setProgress(100);
				mPrintProcessPercentage.setText("100%");
				MyTimerTask task = new MyTimerTask();
				m_timer = new Timer(true);
				m_timer.schedule(task, 1000);
				break;
			case BluetoothPrintService.BluetoothServiceNone:

				break;
			case BluetoothPrintService.BluetoothServiceConnectRequest:

				break;
			case BluetoothPrintService.BluetoothServiceConnecting:
				mStatusLabel
						.setText(DEFAULT_DEVICE_NAME
								+ ":"
								+ getResources()
										.getString(
												R.string.preview_printer_bluetooth_socket_connecting));
				break;
			case BluetoothPrintService.BluetoothServiceConnectDeviceName:

				break;
			case BluetoothPrintService.BluetoothServiceConnected:
				mDeviceJobStatus = 2;
				mStatusLabel
						.setText(DEFAULT_DEVICE_NAME
								+ ":"
								+ getResources()
										.getString(
												R.string.preview_printer_bluetooth_socket_connect_success));
				break;
			case BluetoothPrintService.BluetoothServiceConnectFailed:
				mStatusLabel
						.setText(DEFAULT_DEVICE_NAME
								+ ":"
								+ getResources()
										.getString(
												R.string.preview_printer_bluetooth_socket_connect_failed));
				mToolbar.getMenu()
						.getItem(0)
						.setTitle(
								getResources()
										.getString(
												R.string.preview_printer_menu_connect_title));
				break;
			case BluetoothPrintService.BluetoothServiceLost:
				mStatusLabel
						.setText(DEFAULT_DEVICE_NAME
								+ ":"
								+ getResources()
										.getString(
												R.string.preview_printer_bluetooth_socket_lose));
				mToolbar.getMenu()
						.getItem(0)
						.setTitle(
								getResources()
										.getString(
												R.string.preview_printer_menu_connect_title));
				break;
			default:
				break;
			}
		}

	};

	public void updataCmdMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnClick) {
			if (circle_menu_status != MESSAGE_CIRCLE_MENU_CLICK)
				return;
			if (menu_init == 0) {
				menu_left = v.getLeft();
				menu_right = v.getRight();
				menu_top = v.getTop();
				menu_bottom = v.getBottom();
				menu_init = 1;
			}
			v.layout(menu_left, menu_top, menu_right, menu_bottom);
			if (main_circle_layout_FrameLayout.getVisibility() != View.VISIBLE) {
				main_circle_layout_FrameLayout.setVisibility(View.VISIBLE);
				main_circle_layout_FrameLayout.startAnimation(mBigAnimation);

			} else {
				main_circle_layout_FrameLayout.startAnimation(mLitteAnimation);
				main_circle_layout_FrameLayout.setVisibility(View.INVISIBLE);
			}
			setCircleMenuLocation(v, menu_left, menu_top, menu_right,
					menu_bottom);
			Toast.makeText(PreviewPrinter.this, "功能按钮", Toast.LENGTH_SHORT)
					.show();
		} else if (v == btnPrintProcessCancel) {
			mDeviceJobStatus = 1;
			String val = Tools.getDataSharedPreferences(PreviewPrinter.this,
					PRINTER_DEVICE, "type");
			if (val.equals("bluetooth")) {
				mBluetoothPrintService.StopPrint();
			} else if (val.equals("usb")) {
				mUsbPrintService.stopSendData();
			}
			if (popupWindow != null) {
				popupWindow.dismiss();
				popupWindow = null;
			}

		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean falg = false;
		circle_menu_status = MESSAGE_CIRCLE_MENU_CLICK;
		int ea = event.getAction();
		switch (ea) {
		case MotionEvent.ACTION_DOWN:
			// 执行此操作 初始化 X,Y
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			circle_menu_status = MESSAGE_CIRCLE_MENU_MOVE;
			// getRawX()和getRawY()获得的是相对屏幕的位置
			int pawX = (int) event.getRawX();
			int pawY = (int) event.getRawY();
			if (pawX != lastX || pawY != lastY) {
				int dx = pawX - lastX;
				int dy = pawY - lastY;
				// 得到最终的 上下 左右的坐标
				int left = v.getLeft() + dx;
				int top = v.getTop() + dy;
				int right = v.getRight() + dx;
				int bottom = v.getBottom() + dy;
				// 不能超过左边
				if (left < INIT_NUM) {
					left = INIT_NUM;
					right = left + v.getWidth();
				}
				// 不能超过右边
				if (right > screenWidth) {
					right = screenWidth;
					left = right - v.getWidth();
				}
				// 限制 最上的位置
				if (top < INIT_NUM) {
					top = INIT_NUM;
					bottom = top + v.getHeight();
				}
				// 限制最下的位置
				if (bottom > screenHeight) {
					bottom = screenHeight;
					top = bottom - v.getHeight();
				}
				// 写入控件的位置
				menu_left = left;
				menu_top = top;
				menu_right = right;
				menu_bottom = bottom;
				menu_init = 1;
				setCircleMenuLocation(v, left, top, right, bottom);
				// 重新获取控件的位置
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				falg = true;
			} else {
				falg = false;
			}
		}
		return falg;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			mStatusLabel.setText(getResources().getString(
					R.string.preview_printer_bluetooth_open_success));
			if (mBluetoothPrintService != null)
				mBluetoothPrintService.setBluetoothService(this);
			break;
		default:
			break;
		}
	}
}
