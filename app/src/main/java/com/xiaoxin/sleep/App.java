package com.xiaoxin.sleep;

import android.app.Application;
import android.content.Context;
import android.os.Debug;

import com.tencent.bugly.crashreport.CrashReport;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.xiaoxin.sleep.service.InitializeService;
import com.xiaoxin.sleep.service.TraceServiceImpl;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class App extends Application {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
    //GreenDao的初始化
//        GreenDaoManager.getInstance()
        context = this;
//    InitializeService.start(this);

    }


    public static Context getAppContext() {
        return context;
    }

}
