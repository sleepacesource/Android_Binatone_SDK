package com.binatonesdk.demo;

import com.binatonesdk.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;


public class DemoApp extends Application {
	
	public static final String APP_TAG = "BinatoneSdk";
	public static int USER_ID; //836408 MBP89SN-00003
	/**
	 * 0:女
	 * 1:男
	 */
	public static final int USER_SEX = 0;
    private static DemoApp instance;
    
    public static DemoApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(this);
//        String logDir = getExternalFilesDir("log").getPath();
//        SdkLog.setLogDir(logDir);
//        SdkLog.setSaveLog(true, "opt.txt");
//        SdkLog.setLogEnable(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);

    }

}














