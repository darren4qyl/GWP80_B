package com.printshare.bluetooth;

import com.printshare.services.BluetoothPrintService;

import android.content.Context;
import android.os.Message;

public abstract class PrintFile {
	private BluetoothPrintService mBluetoothPrintService;
	public void PrintFileStart(Context context,int type)
	{
//		mBluetoothPrintService=new BluetoothPrintService(context);
	}
	public abstract void updataCmdMessage(Message msg);
}
