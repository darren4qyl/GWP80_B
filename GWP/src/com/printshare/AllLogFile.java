package com.printshare;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class AllLogFile {
	private Context mContext;
	private static AllLogFile mInstance;
	public AllLogFile(Context context)
	{
		mContext=context;
	}
	public static AllLogFile getInstance(Context context)
	{
		if(mInstance==null)
			mInstance=new AllLogFile(context);
		return mInstance;
	}
	public void OpenLog()
	{
		File destDir = new File("/storage/sdcard0/GWP80/Log");
		  if (!destDir.exists()) {
		   destDir.mkdirs();
		  }
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Integer> prePSID=getProcessInfo("logcat");
				int psid=getPSID();
				if(psid==0||!matcherVal(psid,prePSID))
				{
					List<Integer> prePSID1=getProcessInfo("com.printshare");
					List<String> commandList = new ArrayList<String>();
					commandList.add("logcat");
					commandList.add("-f");
					commandList.add("/storage/sdcard0/GWP80/Log/print.txt");  
					commandList.add("-v");
					commandList.add("time");
					commandList.add("*:V");
					commandList.add("&");  
					try {
						Process pro = Runtime.getRuntime().exec(
								commandList.toArray(new String[commandList
										.size()]));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<Integer> resultPSID=getProcessInfo("logcat");
					List<Integer> otherPSID=matcherOtherVal(prePSID,resultPSID);
					for (Integer id : otherPSID) {
						Log.e("darren", "---------------"+id);
					}
					if(otherPSID.size()>0)
						setPSID(otherPSID.get(0));
				}
				
			}
		}).start();
	}
	class StreamConsumer extends Thread {
		InputStream is;
		List<String> list;

		StreamConsumer(InputStream is) {
			this.is = is;
		}

		StreamConsumer(InputStream is, List<String> list) {
			this.is = is;
			this.list = list;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (list != null) {
						list.add(line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	public List<Integer> getProcessInfo(String str) {   
		Process pro1;
		List<String> orgProcList = new ArrayList<String>();
		List<Integer> orgProcPid = new ArrayList<Integer>();

		try {
			List<String> commandList1 = new ArrayList<String>();
			commandList1.add("ps");
			commandList1.add("|");
			commandList1.add("grep");
			commandList1.add(str);
			pro1 = Runtime.getRuntime().exec(
					commandList1.toArray(new String[commandList1.size()]));
			StreamConsumer outputConsumer = new StreamConsumer(
					pro1.getInputStream(), orgProcList);
			outputConsumer.start();
			try {
				if (pro1.waitFor() == 0) {

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (String xxVideoInfo : orgProcList) {
				String[] temp = null;
				temp = xxVideoInfo.split("\\s{1,}");

				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(temp[1].toString());
				if (isNum.matches()) {
					orgProcPid.add(Integer.parseInt(temp[1].toString()));
				}

			}
			InputStream in1 = pro1.getInputStream();
			BufferedReader read1 = new BufferedReader(
					new InputStreamReader(in1));
			String result1 = read1.readLine();
			result1 = read1.readLine();
			System.out.println("INFO:" + result1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return orgProcPid;
	}

	private int getPSID() {
		int psID = 0;
		SharedPreferences sp = mContext.getSharedPreferences("Logcat", mContext.MODE_PRIVATE);
		if (sp != null)
			psID=sp.getInt("ID", 0);
		return psID;
	}

	private void setPSID(int id) {
		SharedPreferences sp = mContext.getSharedPreferences("Logcat", mContext.MODE_PRIVATE);
		if (sp != null) {
			Editor editor = sp.edit();
			editor.putInt("ID", id);
			editor.commit();
		}
	}
	private boolean matcherVal(int id,List<Integer> integers) {
		boolean val=false;
		for(Integer integer:integers)
		{
			if(integer==id)
			{
				val=true;
				List<String> commandList1 = new ArrayList<String>();
				commandList1.add("kill");
				commandList1.add("-9");
				commandList1.add("id");
				try {
					Process pro1 = Runtime.getRuntime().exec(
							commandList1.toArray(new String[commandList1.size()]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file = new File("/storage/sdcard0/GWP80/Log/print.txt");
				if(file.length()>10000000)
					file.delete();
				break;
			}
		}
		return val;
	}
	private List<Integer> matcherOtherVal(List<Integer> preIntegers,List<Integer> currIntegers) {
		boolean val=false;
		for(Integer integer:preIntegers)
		{
			currIntegers.remove(integer);
		}
		return currIntegers;
	}
}
