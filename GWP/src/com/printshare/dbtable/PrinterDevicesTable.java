package com.printshare.dbtable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.printshare.dboperation.DbHelper.OperationDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class PrinterDevicesTable extends DB_Table {
	private String TABLE_NAME = "table_printer_devices";
	private String designation = "designation";
	private String type = "type";
	private String bluetooth_address = "bluetooth_address";
	private String vendor = "vendor";
	private String ProdID = "ProdID";
	private String Rev = "Rev";
	private String SerialNumber = "SerialNumber";
	private String Product = "Product";
	private OperationDB mOperationDB=null;
	public String getCreateSql() {
		String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY,"
				+ designation + TEXT_TYPE +" UNIQUE "+ COMMA_SEP
				+ bluetooth_address + TEXT_TYPE + COMMA_SEP
				+ vendor + INTEGER_TYPE + COMMA_SEP
				+ ProdID + INTEGER_TYPE + COMMA_SEP
				+ Rev + TEXT_TYPE + COMMA_SEP
				+ SerialNumber + TEXT_TYPE + COMMA_SEP
				+ Product + TEXT_TYPE + COMMA_SEP
				+ type + INTEGER_TYPE + " )";
		return SQL_CREATE_ENTRIES;
	}
	public String getDropTable() {
		String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
		return SQL_DELETE_ENTRIES;
	}
	public long InsertData(Context context,String designation, int type,String bluetooth_address) {
		return InsertData(context, designation, type, bluetooth_address, null, null, null, null, null);
	}
	public long InsertData(Context context,String designation, int type,Integer vendor,Integer ProdID,String Rev,String SerialNumber,String Product) {
		return InsertData(context, designation, type, null, vendor, ProdID, Rev, SerialNumber, Product);
	}
	public long InsertData(Context context,String designation, int type,String bluetooth_address,Integer vendor,Integer ProdID,String Rev,String SerialNumber,String Product) {
		long result=0;
		mOperationDB = new OperationDB(context);
		mOperationDB.OpenDB();
		if(type==1)
		{
			ContentValues values = new ContentValues();
			values.put("designation", designation);
			values.put("bluetooth_address", bluetooth_address);
			values.put("type", type);
			result=mOperationDB.db.insert(TABLE_NAME, null, values);
		}else if(type==2)
		{
			ContentValues values = new ContentValues();
			values.put("designation", designation);
			values.put("vendor", vendor);
			values.put("ProdID", ProdID);
			values.put("Rev", Rev);
			values.put("SerialNumber", SerialNumber);
			values.put("Product", Product);
			values.put("type", type);
			result=mOperationDB.db.insert(TABLE_NAME, null, values);
		}
		mOperationDB.CloseDB();
		Log.e("darren", "------------"+result);
		return result;
	}
	public List<PrinterDevicesTableData> read(Context context) {
		mOperationDB = new OperationDB(context);
		mOperationDB.OpenDB();
		String[] projection = { BaseColumns._ID, designation,
				type,bluetooth_address,vendor,ProdID,Rev,SerialNumber,Product };
		String sortOrder = BaseColumns._ID + " desc";
		Cursor cursor = mOperationDB.db.query(TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);
		List<PrinterDevicesTableData> result = new ArrayList<PrinterDevicesTableData>();
		int count = cursor.getCount();
		if (count > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(cursor.getLong(2));
				PrinterDevicesTableData entry = new PrinterDevicesTableData(
						cursor.getInt(0), cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
				result.add(entry);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mOperationDB.CloseDB();
		return result;
	}
	public boolean isExistData(Context context,String name)
	{
		boolean ret=false;
		mOperationDB = new OperationDB(context);
		mOperationDB.OpenDB();
		String[] projection = { BaseColumns._ID };
		String selection = designation + " = ?";
		String[] selectionArgs = { name.toString() };
		Cursor cursor = mOperationDB.db.query(TABLE_NAME, // The table to query
				projection, // The columns to return
				selection, // The columns for the WHERE clause
				selectionArgs, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);
		int count = cursor.getCount();
		if (count > 0) {
			ret=true;
		}
		mOperationDB.CloseDB();
		return ret;
	}
	public int delete(Context context, Integer id) {
		int ret=-1;
		mOperationDB = new OperationDB(context);
		mOperationDB.OpenDB();
		String selection = BaseColumns._ID + " = ?";
		String[] selectionArgs = { id.toString() };
		ret=mOperationDB.db.delete(TABLE_NAME, selection, selectionArgs);
		mOperationDB.CloseDB();
		return ret;
	}

	public int updata(Context context,Integer id, String name) {
		int ret=-1;
		mOperationDB = new OperationDB(context);
		mOperationDB.OpenDB();
		ContentValues values = new ContentValues();
		values.put(designation, name);
		String[] selectionArgs = { id.toString() };
		try {
			ret=mOperationDB.db.update(TABLE_NAME, values, BaseColumns._ID + " = ?",
					selectionArgs);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mOperationDB.CloseDB();
		return ret;
		
	}
	public class PrinterDevicesTableData{
		public int id;
		public String designation;
		public int type;
		public String bluetooth_address;
		public int vendor;
		public int ProdID;
		public String Rev;
		public String SerialNumber;
		public String Product;
		public PrinterDevicesTableData(int id,String designation, int type,String bluetooth_address,int vendor,int ProdID,String Rev,String SerialNumber,String Product)
		{
			this.id=id;
			this.designation=designation;
			this.type=type;
			this.bluetooth_address=bluetooth_address;
			this.vendor=vendor;
			this.ProdID=ProdID;
			this.Rev=Rev;
			this.SerialNumber=SerialNumber;
			this.Product=Product;
		}
	}
}
