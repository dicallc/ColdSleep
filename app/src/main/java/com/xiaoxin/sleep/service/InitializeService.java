package com.xiaoxin.sleep.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.tencent.bugly.crashreport.CrashReport;

public class InitializeService extends IntentService {

    private static final String ACTION_INIT_WHEN_APP_CREATE = "com.xiaoxin.sleep.service.action.INIT";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }

    private void performInit() {
        CrashReport.initCrashReport(getApplicationContext(), "2d2fba589f", false);
    }
}
