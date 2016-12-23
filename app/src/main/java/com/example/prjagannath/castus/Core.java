package com.example.prjagannath.castus;

import android.app.Application;
import android.content.Context;

import com.example.prjagannath.castus.API.AppInfo;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class Core extends Application {
    private static AppInfo appInfo;
    private static Context context;
    private Session exchangeSession;

    public Core() {
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        this.setup();
    }

    public static AppInfo getAppInfo() {
        return appInfo;
    }


    private void setup() {
        appInfo = AppInfo.getInstance();
    }


    public Session getExchangeSession() {
        return this.exchangeSession;
    }





}
