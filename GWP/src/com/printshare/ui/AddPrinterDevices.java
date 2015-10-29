package com.printshare.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat.Field;
import java.util.ArrayList;
import java.util.List;

import com.print.operationBMP;
import com.printshare.R;
import com.printshare.bluetooth.DeviceListActivity;
import com.printshare.dbtable.PrinterDevicesTable;
import com.printshare.dbtable.PrinterDevicesTable.PrinterDevicesTableData;
import com.printshare.services.BluetoothPrintService;
import com.printshare.services.USBPrintService;
import com.printshare.util.SelectPopupWindow;
import com.printshare.util.Tools;
import com.printsharelib.listview3d.util.ListView3D;
import com.printsharelib.util.BitmapUtil.PrintBitmap;
import com.printsharelib.util.BitmapUtil;
import com.printsharelib.util.XmlFileParse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable.ConstantState;
import android.hardware.usb.UsbDevice;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddPrinterDevices extends Activity implements OnClickListener,
		OnItemClickListener {
	private final String Tag = "ListView3D";
	private final int BLUETOOTH_DEVICE=1;
	private final int USB_DEVICE=2;
	private String PRINTER_DEVICE="printer_device";
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQUEST_ENABLE_BT_DEFAULT_DEVICE = 4;
	private Toolbar mToolbar = null;
	private Button mBtnAddDevices = null;
	private PrinterDevicesTable mPrinterDevicesTable;
	private ListView3D mListView3D;
	private List<PrinterDevicesTableData> mDevicesData = new ArrayList<PrinterDevicesTable.PrinterDevicesTableData>();
	private CustomAdapter mCustomAdapter;
	private SelectPopupWindow mWindow;
	private String[] mItemVlues = { "设置为默认设备", "修改设备名称", "删除设备" };
	private String[] mAddDevicesType = { "usb 设备 ", "Bluetooth 设备" };
	private LayoutInflater mInflater;
	private BluetoothPrintService mBluetoothPrintService=null;
	private USBPrintService mUsbPrintService=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printer_add_devices);
		mInflater = LayoutInflater.from(this);
		mPrinterDevicesTable = new PrinterDevicesTable();
		mDevicesData = mPrinterDevicesTable.read(AddPrinterDevices.this);
		mBtnAddDevices = (Button) findViewById(R.id.button);
		mListView3D = (ListView3D) findViewById(R.id.my_listview);
		mListView3D.setDynamics(new SimpleDynamics(0.9f, 0.6f));
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		mToolbar.setTitle(getResources().getString(
				R.string.printer_add_devices_toolbar_text));
		mToolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mBtnAddDevices.setOnClickListener(this);
		mCustomAdapter = new CustomAdapter(this, mDevicesData);
		mListView3D.setAdapter(mCustomAdapter);
		mListView3D.setOnItemClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBtnAddDevices) {
			mWindow = new SelectPopupWindow(AddPrinterDevices.this,
					mAddDevicesType.length) {

				@Override
				public View ListviewConvertView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					if (convertView == null) {
						convertView = mInflater.inflate(
								R.layout.layout_add_devices_type, null);
						TextView textView = new TextView(AddPrinterDevices.this);
						textView.setText(mAddDevicesType[1]);
						textView.setTextColor(0xFFFFFFFF);
						textView.setLayoutParams(new LayoutParams(-2, -2));
						textView.setSingleLine();
						textView.setGravity(Gravity.CENTER);
						textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
								getResources().getDimension(R.dimen.text_size));
						int width = View.MeasureSpec.makeMeasureSpec(0,
								View.MeasureSpec.UNSPECIFIED);

						int height = View.MeasureSpec.makeMeasureSpec(0,
								View.MeasureSpec.UNSPECIFIED);

						textView.measure(width, height);

						width = textView.getMeasuredWidth();
						TextView textView1 = (TextView) convertView
								.findViewById(R.id.ladt_showtext);
						textView1
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, -2));
					}
					ImageView imageView = (ImageView) convertView
							.findViewById(R.id.ladt_image);
					TextView textView = (TextView) convertView
							.findViewById(R.id.ladt_showtext);
					if (position == 1) {
						imageView
								.setBackgroundResource(R.drawable.bluetooth_min);
					} else if (position == 0) {
						imageView.setBackgroundResource(R.drawable.usb_min);
					}
					textView.setText(mAddDevicesType[position]);
					return convertView;
				}

				@Override
				public void ListviewOnItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub
					if (position == 0) {
						mUsbPrintService=USBPrintService.getInstance(AddPrinterDevices.this, null);
						final UsbDevice usbDevice=mUsbPrintService.getUsbDevices();
						if(usbDevice!=null)
						{
							final EditText mEditTextName = new EditText(
									AddPrinterDevices.this);
							final AlertDialog dialog = new AlertDialog.Builder(
									AddPrinterDevices.this).setTitle("请输入")
									.setIcon(android.R.drawable.ic_dialog_info)
									.setView(mEditTextName).setPositiveButton("确定", null)
									.setNegativeButton("取消", null).show();
							dialog.getButton(AlertDialog.BUTTON_POSITIVE)
									.setOnClickListener(new OnClickListener() {

										@SuppressLint("NewApi")
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											boolean result = mPrinterDevicesTable
													.isExistData(AddPrinterDevices.this,
															mEditTextName.getText()
																	.toString());
											if (result) {
												Toast.makeText(AddPrinterDevices.this,
														"设备名称冲突,请重新输入。。。。",
														Toast.LENGTH_LONG).show();
												return;
											} else {
												long ret = mPrinterDevicesTable.InsertData(
														AddPrinterDevices.this,
														mEditTextName.getText().toString(),
														USB_DEVICE, usbDevice.getVendorId(),usbDevice.getProductId(),usbDevice.getDeviceName(),null,null);
												if (ret != -1) {
													mDevicesData.clear();
													mDevicesData = mPrinterDevicesTable
															.read(AddPrinterDevices.this);
													mListView3D.destroyDrawingCache();
													mListView3D = (ListView3D) findViewById(R.id.my_listview);
													mListView3D
															.setDynamics(new SimpleDynamics(
																	0.9f, 0.6f));
													mCustomAdapter = new CustomAdapter(
															AddPrinterDevices.this, mDevicesData);
													mListView3D.setAdapter(mCustomAdapter);
													mListView3D
															.setOnItemClickListener(AddPrinterDevices.this);
												}
												dialog.dismiss();
											}
										}
									});
						}else
						{
							String val=Tools.getDataSharedPreferences(AddPrinterDevices.this, "devices", "root");
							if(val.equals(""))
							{
								boolean ret=XmlFileParse.getInstance(AddPrinterDevices.this).getOpearionPermissions();
								if(ret)
								{
									Tools.saveDataSharedPreferences(AddPrinterDevices.this, "devices", "root", "root");
									new  AlertDialog.Builder(AddPrinterDevices.this)    
					                .setTitle("提示" )  
					                .setMessage("已获得usb host权限，请重启手机。。。" )  
					                .setPositiveButton("确定" ,  null )  
					                .show();
								}else
								{
									new  AlertDialog.Builder(AddPrinterDevices.this)    
						                .setTitle("提示" )  
						                .setMessage("此操作需要root权限，请先将系统用root工具root，(king root等)" )  
						                .setPositiveButton("确定" ,  null )  
						                .show();
								}
							}else
							{
								Toast.makeText(AddPrinterDevices.this, "重新插入usb设备后，重试。。。。。", Toast.LENGTH_LONG).show();
							}
							
						}
//						boolean ret=XmlFileParse.getInstance(AddPrinterDevices.this).getOpearionPermissions();
//						if(ret)
//						{
//							Toast.makeText(AddPrinterDevices.this, "已获得", Toast.LENGTH_LONG).show();
//						}
						/*String [] mFilePath={"/storage/sdcard0/test.png"};
						BitmapUtil mBitmapUtil = BitmapUtil.getInstance();
						PrintBitmap printBitmap = mBitmapUtil.getPrinteBitmap(AddPrinterDevices.this,
								mFilePath[0]);
						int width=printBitmap.bitmap.getWidth();
						int height=printBitmap.bitmap.getHeight();
						Bitmap testb=Bitmap.createBitmap(width, height, Config.ARGB_8888);
						boolean isBlankLine = true;
						for (int i = 0; i < height; i++) {
							int[] pixels = new int[width];
							int index = 0;
							printBitmap.bitmap.getPixels(pixels, 0, width, 0, i, width,
									1);
							operationBMP.getInstance().bmpOperation(pixels, 1, width);
							testb.setPixels(pixels, 0,width , 0, i, width, 1);
							int[] binaryPixels = new int[(width + 7) / 8]; // 通过位图的大小创建像素点数组
							for (int j2 = 0; j2 < width;) {
								if (pixels[j2] != 255) {
									binaryPixels[index] = (byte) ((binaryPixels[index] | 1 << (7 - j2 % 8) & 0xff));
									isBlankLine = false;
								}
								j2++;
								if (j2 % 8 == 0) {
									index++;
								}
							}
							if(isBlankLine)
							{
								testb.setPixels(binaryPixels, 0,(width + 7) / 8 , 0, i, width, 1);
							}else
							{
								testb.setPixels(binaryPixels, 0,(width + 7) / 8 , 0, i, width, 1);
							}
							
						}*/
					} else if (position == 1) {
						// 蓝牙设备
						BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
								.getDefaultAdapter();
						if (mBluetoothAdapter == null) {
							Toast.makeText(AddPrinterDevices.this,
									"Bluetooth is not available",
									Toast.LENGTH_LONG).show();
						} else {
							if (!mBluetoothAdapter.isEnabled()) {
								Intent enableIntent = new Intent(
										BluetoothAdapter.ACTION_REQUEST_ENABLE);
								startActivityForResult(enableIntent,
										REQUEST_ENABLE_BT);
								// Otherwise, setup the chat session
							} else {
								Intent serverIntent = new Intent(
										AddPrinterDevices.this,
										DeviceListActivity.class);
								startActivityForResult(serverIntent,
										REQUEST_CONNECT_DEVICE_SECURE);
							}
						}

					}
					this.dismiss();
				}
			};
			mWindow.showAtLocation(
					AddPrinterDevices.this.findViewById(R.id.pad_main),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			/*
			 * long ret =
			 * mPrinterDevicesTable.InsertData(AddPrinterDevices.this, "usb" +
			 * i, 2, null, 5, 6, "444", "4444", "3333333"); if (ret != -1) {
			 * mDevicesData.clear(); mDevicesData = mPrinterDevicesTable
			 * .read(AddPrinterDevices.this); mListView3D.destroyDrawingCache();
			 * mListView3D = (ListView3D) findViewById(R.id.my_listview);
			 * mListView3D.setDynamics(new SimpleDynamics(0.9f, 0.6f));
			 * mCustomAdapter = new CustomAdapter(this, mDevicesData);
			 * mListView3D.setAdapter(mCustomAdapter);
			 * mListView3D.setOnItemClickListener(this); }
			 */

		}
	}
	private void saveBitmap(Bitmap bitmap, String name) {
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ name);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			fout.flush();
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class CustomAdapter extends BaseAdapter {

		private List<PrinterDevicesTableData> mData;
		private LayoutInflater mInflater;
		private int mClickItemIndex = 0;

		public CustomAdapter(Context context, List<PrinterDevicesTableData> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
		}

		public List<PrinterDevicesTableData> getData() {
			return mData;
		}

		public void setData(List<PrinterDevicesTableData> mData) {
			this.mData.clear();
			this.mData.addAll(mData);
		}

		public int getClickItemIndex() {
			return mClickItemIndex;
		}

		public void setClickItemIndex(int mClickItemIndex) {
			this.mClickItemIndex = mClickItemIndex;
		}

		@Override
		public int getCount() {
			if (mData == null || mData.size() <= 0) {
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			if (mData == null || mData.size() <= 0 || position < 0
					|| position >= mData.size()) {
				return null;
			}
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.printer_add_devices_list_item, null);
			}
			if (position < mData.size()) {
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.iv_photo);
				TextView name = (TextView) convertView
						.findViewById(R.id.tv_name);
				name.setText(((PrinterDevicesTableData) getItem(position)).designation);
				TextView description = (TextView) convertView
						.findViewById(R.id.tv_name_description);
				if (((PrinterDevicesTableData) getItem(position)).type == 1) {
					description
							.setText(((PrinterDevicesTableData) getItem(position)).bluetooth_address);
					imageView.setBackgroundResource(R.drawable.bluetooth);
				} else if (((PrinterDevicesTableData) getItem(position)).type == 2) {
					description
							.setText(((PrinterDevicesTableData) getItem(position)).Rev);
					imageView.setBackgroundResource(R.drawable.usb);
				}
				TextView infomation = (TextView) convertView
						.findViewById(R.id.tv_showinfomation);

				if (mClickItemIndex == position) {
					infomation.setVisibility(View.VISIBLE);
					Log.e(Tag, "--------------- getView");
				} else {
					infomation.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

	}

	class SimpleDynamics extends com.printsharelib.listview3d.util.Dynamics {

		private float mFrictionFactor;
		private float mSnapToFactor;

		public SimpleDynamics(final float frictionFactor,
				final float snapToFactor) {
			mFrictionFactor = frictionFactor;
			mSnapToFactor = snapToFactor;
		}

		@Override
		protected void onUpdate(final int dt) {
			mVelocity += getDistanceToLimit() * mSnapToFactor;

			// 速度 * 时间间隔 = 间隔时间内位移
			mPosition += mVelocity * dt / 1000;

			// 减速， 供下次onUpdate使用
			mVelocity *= mFrictionFactor;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mCustomAdapter.setClickItemIndex(position);
		mListView3D.ref();
		mWindow = new SelectPopupWindow(AddPrinterDevices.this,
				mItemVlues.length) {
			@Override
			public void ListviewOnItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1) {
					final EditText mEditText = new EditText(
							AddPrinterDevices.this);
					new AlertDialog.Builder(AddPrinterDevices.this)
							.setTitle("请输入")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(mEditText)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											int position = mCustomAdapter
													.getClickItemIndex();
											PrinterDevicesTableData obj = (PrinterDevicesTableData) mCustomAdapter
													.getItem(position);
											int result = mPrinterDevicesTable
													.updata(AddPrinterDevices.this,
															obj.id, mEditText
																	.getText()
																	.toString());
											if (result == -1) {
												Toast.makeText(
														AddPrinterDevices.this,
														"设备名称冲突。。。。",
														Toast.LENGTH_LONG)
														.show();
											} else {
												mDevicesData.clear();
												mDevicesData = mPrinterDevicesTable
														.read(AddPrinterDevices.this);
												mCustomAdapter
														.setData(mDevicesData);
												mListView3D.ref();
											}
										}
									}).setNegativeButton("取消", null).show();
				} else if (position == 2) {
					AlertDialog.Builder builder = new Builder(
							AddPrinterDevices.this);
					builder.setMessage("确认删除吗？");
					builder.setTitle("提示");
					builder.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									int position = mCustomAdapter
											.getClickItemIndex();
									PrinterDevicesTableData obj = (PrinterDevicesTableData) mCustomAdapter
											.getItem(position);
									mPrinterDevicesTable.delete(
											AddPrinterDevices.this, obj.id);
									mListView3D.refAdd(position);
									mDevicesData.clear();
									mDevicesData = mPrinterDevicesTable
											.read(AddPrinterDevices.this);
									mListView3D.destroyDrawingCache();
									mListView3D = (ListView3D) findViewById(R.id.my_listview);
									mListView3D.setDynamics(new SimpleDynamics(
											0.9f, 0.6f));
									mCustomAdapter = new CustomAdapter(
											AddPrinterDevices.this,
											mDevicesData);
									mListView3D.setAdapter(mCustomAdapter);
									mListView3D
											.setOnItemClickListener(AddPrinterDevices.this);
									dialog.dismiss();
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				}else if(position==0)
				{
					int devPosition = mCustomAdapter
							.getClickItemIndex();
					PrinterDevicesTableData obj = (PrinterDevicesTableData) mCustomAdapter
							.getItem(devPosition);
					if(obj.type==BLUETOOTH_DEVICE)
					{
						mBluetoothPrintService = new BluetoothPrintService(AddPrinterDevices.this,mDefaultDeviceHandler);
						mBluetoothPrintService.connectDevice(obj.bluetooth_address);
					}else if(obj.type==USB_DEVICE)
					{ 
						mUsbPrintService=USBPrintService.getInstance(AddPrinterDevices.this, null);
						final UsbDevice usbDevice=mUsbPrintService.getUsbDevices();
						if(usbDevice!=null)
						{
							Tools.saveDataSharedPreferences(AddPrinterDevices.this, PRINTER_DEVICE, "type", "usb");
							Tools.saveDataSharedPreferences(AddPrinterDevices.this, PRINTER_DEVICE, "devices_name", obj.designation);
						}
					}
					
				}

				this.dismiss();
			}

			@Override
			public View ListviewConvertView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.layout_add_devices_item, null);
				}
				TextView textView = (TextView) convertView
						.findViewById(R.id.ladi_showtext);
				textView.setText(mItemVlues[position]);
				return convertView;
			}
		};
		mWindow.showAtLocation(
				AddPrinterDevices.this.findViewById(R.id.pad_main),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			if (resultCode == Activity.RESULT_OK) {
				final String device_address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				final EditText mEditTextName = new EditText(
						AddPrinterDevices.this);
				final AlertDialog dialog = new AlertDialog.Builder(
						AddPrinterDevices.this).setTitle("请输入")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(mEditTextName).setPositiveButton("确定", null)
						.setNegativeButton("取消", null).show();
				dialog.getButton(AlertDialog.BUTTON_POSITIVE)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								boolean result = mPrinterDevicesTable
										.isExistData(AddPrinterDevices.this,
												mEditTextName.getText()
														.toString());
								if (result) {
									Toast.makeText(AddPrinterDevices.this,
											"设备名称冲突,请重新输入。。。。",
											Toast.LENGTH_LONG).show();
									return;
								} else {
									long ret = mPrinterDevicesTable.InsertData(
											AddPrinterDevices.this,
											mEditTextName.getText().toString(),
											BLUETOOTH_DEVICE, device_address);
									if (ret != -1) {
										mDevicesData.clear();
										mDevicesData = mPrinterDevicesTable
												.read(AddPrinterDevices.this);
										mListView3D.destroyDrawingCache();
										mListView3D = (ListView3D) findViewById(R.id.my_listview);
										mListView3D
												.setDynamics(new SimpleDynamics(
														0.9f, 0.6f));
										mCustomAdapter = new CustomAdapter(
												AddPrinterDevices.this, mDevicesData);
										mListView3D.setAdapter(mCustomAdapter);
										mListView3D
												.setOnItemClickListener(AddPrinterDevices.this);
									}
									dialog.dismiss();
								}
							}
						});
			}
			break;
		case REQUEST_ENABLE_BT:
			Intent serverIntent = new Intent(AddPrinterDevices.this,
					DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			break;
		case REQUEST_ENABLE_BT_DEFAULT_DEVICE:
			int devPosition = mCustomAdapter
			.getClickItemIndex();
			PrinterDevicesTableData obj = (PrinterDevicesTableData) mCustomAdapter
			.getItem(devPosition);
			mBluetoothPrintService.connectDevice(obj.bluetooth_address);
			break;
		default:
			break;
		}
	}
	
	private Handler mDefaultDeviceHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case BluetoothPrintService.BluetoothServiceConnected:
				int devPosition = mCustomAdapter
				.getClickItemIndex();
				PrinterDevicesTableData obj = (PrinterDevicesTableData) mCustomAdapter
				.getItem(devPosition);
				Tools.saveDataSharedPreferences(AddPrinterDevices.this, PRINTER_DEVICE, "type", "bluetooth");
				Tools.saveDataSharedPreferences(AddPrinterDevices.this, PRINTER_DEVICE, "devices_name", obj.designation);
				Tools.saveDataSharedPreferences(AddPrinterDevices.this, PRINTER_DEVICE, "buletooth_address", obj.bluetooth_address);
				Toast.makeText(AddPrinterDevices.this,
						"设置默认设备成功。。。。",
						Toast.LENGTH_LONG).show();
				break;
			case BluetoothPrintService.BluetoothAdapterStartActivityForResult:
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT_DEFAULT_DEVICE);
				break;
			default:
				break;
			}
		}
		
	};
	
}
