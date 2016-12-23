package com.example.prjagannath.castus.API;

/**
 * Created by prjagannath on 9/2/2016.
 */

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;

import java.util.GregorianCalendar;


public class AppInfo {
    private long Interval;
    private static String appVersionName;
    private static String BaseURL = "http://castus.herokuapp.com/";
    private boolean isAccessAllow = true;
    private static volatile AppInfo appInfo = null;

    private AppInfo() {
    }

    public static AppInfo getInstance() {
        if(appInfo == null) {
            Class var0 = AppInfo.class;
            synchronized(AppInfo.class) {
                if(appInfo == null) {
                    appInfo = new AppInfo();
                }
            }
        }

        return appInfo;
    }

    public void setInterval(long ServerTime) {
        this.Interval = ServerTime * 1000L - (new GregorianCalendar()).getTimeInMillis();
    }

    public void setAppVersionName(String version) {
        appVersionName = version;
    }

    public static String getAppVersionName() {
        return appVersionName;
    }

    public String getApkVersion() {
        return "Version " + appVersionName;
    }

    public long getIntervalInMillis() {
        return this.Interval;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getDeviceInfo() {
        return this.getDeviceManufacturer() + ' ' + getDeviceModel();
    }

    public String getAndroidVersion() {
        return VERSION.RELEASE;
    }

    public static String getBaseURL() {
        return BaseURL == null?"":BaseURL;
    }

    public static void setBaseURL(String baseURL) {
        BaseURL = baseURL;
    }

    public void setAccessAllow(boolean status) {
        this.isAccessAllow = status;
    }

    public boolean isAccessAllow() {
        return this.isAccessAllow;
    }

    public static String getPhoneNumber(Context context) {
        String line1Number = getTelephonyManager(context).getLine1Number();
        return line1Number.isEmpty()?"":line1Number;
    }



    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}

