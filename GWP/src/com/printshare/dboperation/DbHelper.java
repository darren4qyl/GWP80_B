package com.printshare.dboperation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.printshare.dbtable.PrinterDevicesTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Xml.Encoding;

public class DbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "print.db";
	private PrinterDevicesTable mPrinterDevicesTable=null; 

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		mPrinterDevicesTable=new PrinterDevicesTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(mPrinterDevicesTable.getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(mPrinterDevicesTable.getDropTable());
		onCreate(db);
	}

	public static class OperationDB{
		private static OperationDB instance = null;
		private DbHelper dbHelper=null;
		public SQLiteDatabase db=null;
		private Context mContext=null;
		public OperationDB(Context context)
		{
			this.mContext=context;
		}
		public static OperationDB getIntance(Context context) {
			if (instance == null)
				instance = new OperationDB(context);
			return instance;
		}
		public void OpenDB()
		{
			if(dbHelper==null||db==null)
			{
				dbHelper=new DbHelper(mContext);
				db=dbHelper.getWritableDatabase();
			}
		}
		public void CloseDB()
		{
			db.close();
			dbHelper.close();
			db=null;
			dbHelper=null;
		}
	}
	/*public static class DB_Table {
		public String TEXT_TYPE = " TEXT";
		public String INTEGER_TYPE = " INTEGER";
		public String BLOB_TYPE = " BLOB";
		public String COMMA_SEP = ",";
	}

	public static class DB_Table_VideoManager extends DB_Table {
		private String TABLE_NAME = "table_videoManager";
		private String Current_select = "current_select";
		private String Current_select_Name = "current_select_name";
		private static DB_Table_VideoManager instance = null;

		public static DB_Table_VideoManager getIntance() {
			if (instance == null)
				instance = new DB_Table_VideoManager();
			return instance;
		}

		*//**
		 * 生成创建数据库中video管理界面table
		 * 
		 * @return Transact-SQL的语句
		 *//*
		public String getCreateSql() {
			String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY,"
					+ Current_select + INTEGER_TYPE + COMMA_SEP
					+ Current_select_Name + BLOB_TYPE + " )";
			return SQL_CREATE_ENTRIES;
		}

		public String getDropTable() {
			String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
			return SQL_DELETE_ENTRIES;
		}

		public long InsertData(int id, String name) {
			if(name==null)
				return 0;
			try {
				String strGBK = URLEncoder.encode(name, "UTF-8");

				String sql = "REPLACE INTO table_videoManager (_id,current_select,current_select_name) VALUES (1,"
						+ id + ", '" + strGBK + "' )";
				db.execSQL(sql);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public int delete() {
			return db.delete(TABLE_NAME, null, null);
		}

		public int updata(int id, String name) {
			ContentValues values = new ContentValues();
			values.put(Current_select, id);
			values.put(Current_select_Name, name);
			String[] selectionArgs = { "1" };
			return db.update(TABLE_NAME, values, BaseColumns._ID + " like ?",
					selectionArgs);
		}

		public List<DB_Table_VideoManagerData> read() {
			String[] projection = { BaseColumns._ID, Current_select,
					Current_select_Name };
			String sortOrder = Current_select + " ASC";
			Cursor cursor = db.query(TABLE_NAME, // The table to query
					projection, // The columns to return
					null, // The columns for the WHERE clause
					null, // The values for the WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);
			List<DB_Table_VideoManagerData> result = new ArrayList<DB_Table_VideoManagerData>();
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(cursor.getLong(2));
					String test = "";
					try {
						test = URLDecoder.decode(cursor.getString(2), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DB_Table_VideoManagerData entry = new DB_Table_VideoManagerData(
							cursor.getInt(0), cursor.getInt(1),
							test);
					result.add(entry);
					cursor.moveToNext();
				}
			}
			cursor.close();
			return result;
		}

		public class DB_Table_VideoManagerData {
			public int id;
			public int select_id;
			public String Name;

			public DB_Table_VideoManagerData(int id, int select_id, String name) {
				this.id = id;
				this.select_id = select_id;
				this.Name = name;
			}
		}
	}

	public static class DB_Table_VideoList extends DB_Table {
		private String TABLE_NAME = "table_videoList";
		private String file_path = "file_path";
		private String Duration = "duration";
		private String position = "position";
		private String image_big = "image_big";
		private String image_small = "image_small";
		private String storage_type = "storage_type";
		private static DB_Table_VideoList instance = null;

		public static DB_Table_VideoList getIntance() {
			if (instance == null)
				instance = new DB_Table_VideoList();
			return instance;
		}

		*//**
		 * 生成创建数据库中video文件列表table
		 * 
		 * @return Transact-SQL的语句
		 *//*
		public String getCreateSql() {
			String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY," + file_path
					+ BLOB_TYPE + " UNIQUE " + COMMA_SEP + Duration
					+ INTEGER_TYPE + COMMA_SEP + position + INTEGER_TYPE
					+ COMMA_SEP + image_big + BLOB_TYPE + COMMA_SEP
					+ image_small + BLOB_TYPE + COMMA_SEP + storage_type
					+ INTEGER_TYPE + " )";
			return SQL_CREATE_ENTRIES;
		}

		public String getDropTable() {
			String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
			return SQL_DELETE_ENTRIES;
		}

		public long insert(String path, long duration, long position,
				byte[] pic_big, byte[] pic_small, int type) {
			try {
				path = URLEncoder.encode(path, "UTF-8");

				String sql = "REPLACE INTO table_videoList (file_path,duration,position,image_big,image_small,storage_type) VALUES ('"
						+ path
						+ "', "
						+ duration
						+ ","
						+ position
						+ ", null , null ," + type + ")";
				try {
					db.execSQL(sql);
					ContentValues values = new ContentValues();
					values.put(image_big, pic_big);
					values.put(image_small, pic_small);
					db.update(TABLE_NAME, values, file_path + " like ?",
							new String[] { path });
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return 0;
		}

		public int delete(String whereClause, String[] whereArgs) {
			return db.delete(TABLE_NAME, whereClause, whereArgs);
		}

		public int updataDuration(String path, long Duration) {
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ContentValues values = new ContentValues();
			values.put(this.Duration, Duration);
			String[] selectionArgs = { path };
			return db.update(TABLE_NAME, values, file_path + " like ?",
					selectionArgs);
		}

		public int updata(String path, long position) {
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block  
				e.printStackTrace();
			}
			ContentValues values = new ContentValues();
			values.put(this.position, position);
			String[] selectionArgs = { path  };
			return db.update(TABLE_NAME, values, file_path + " like ?",
					selectionArgs);
		}

		public List<DB_Table_VideoListData> read(String path) {
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] projection = { BaseColumns._ID, file_path, Duration,
					position, image_big, image_small, storage_type };
			String sortOrder = file_path + " ASC";
			String selection = file_path + " like ?";
			String[] selectionArgs = { path };
//			Cursor cursor=db.rawQuery("select "+ BaseColumns._ID+","+file_path+","+Duration+","+position+","+image_big+","+image_small+","+storage_type+" from "+TABLE_NAME +" where "+file_path+" = '"+path+"' ", null);
			Cursor cursor = db.query(TABLE_NAME, // The table to query
					projection, // The columns to return
					path == null ? null : selection, // The columns for the
														// WHERE clause
					path == null ? null : selectionArgs, // The values for the
															// WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);
			List<DB_Table_VideoListData> result = new ArrayList<DB_Table_VideoListData>();
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(cursor.getLong(2));
					String test = "";
					try {
						test = URLDecoder.decode(cursor.getString(1), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DB_Table_VideoListData entry = new DB_Table_VideoListData(
							cursor.getInt(0), test,
							cursor.getLong(2), cursor.getLong(3),
							cursor.getBlob(4), cursor.getBlob(5),
							cursor.getInt(6));
					result.add(entry);
					cursor.moveToNext();
				}
			}
			cursor.close();
			return result;
		}

		public long readPosition(String path) {
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] projection = { BaseColumns._ID, file_path, Duration,
					position, storage_type };
			String sortOrder = file_path + " ASC";
			String selection = file_path + " like ?";
			String[] selectionArgs = { path };
//			Cursor cursor=db.rawQuery("select "+ BaseColumns._ID+","+file_path+","+Duration+","+position+","+storage_type+" from "+TABLE_NAME +" where "+file_path+" = '"+path+"' ", null);
			Cursor cursor = db.query(TABLE_NAME, // The table to query
					projection, // The columns to return
					path == null ? null : selection, // The columns for the
														// WHERE clause
					path == null ? null : selectionArgs, // The values for the
															// WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);
			long result = 0;
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					if (cursor.getLong(2) > cursor.getLong(3)) {
						result = cursor.getLong(3);
					} else {
						result = 0;
					}
					cursor.moveToNext();
				}
			}
			cursor.close();
			return result;
		}

		public boolean isExist(String path) {
			boolean ret = false;
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] projection = { BaseColumns._ID, file_path };
			String sortOrder = file_path + " ASC";
			String selection = file_path + " like ?";
			String[] selectionArgs = { path };
//			Cursor cursor=db.rawQuery("select "+ BaseColumns._ID+","+file_path+" from "+TABLE_NAME +" where "+file_path+" = '"+path+"' ", null);
			Cursor cursor = db.query(TABLE_NAME, // The table to query
					projection, // The columns to return
					selection, // The columns for the WHERE clause
					selectionArgs, // The values for the WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);
			int count = cursor.getCount();
			if (count > 0) {
				ret = true;
			}
			cursor.close();
			return ret;
		}

		public class DB_Table_VideoListData {
			public int id;
			public String file_path;
			public long Duration;
			public long position;
			public byte[] image_big;
			public byte[] image_small;
			public int storage_type;// 0 表示内部存储 1 表示外部存储

			public DB_Table_VideoListData(int id, String path, long duration,
					long position, byte[] image_big, byte[] image_small,
					int type) {
				this.id = id;
				this.file_path = path;
				this.Duration = duration;
				this.position = position;
				this.image_big = image_big;
				this.image_small = image_small;
				this.storage_type = type;
			}
		}
	}*/

}
