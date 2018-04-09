package com.xiaoxin.sleep.model;

import com.xiaoxin.library.model.AppInfo;

import java.util.List;

/**
 * Created by jiangdikai on 2017/8/6.
 */

public class RxModelWithSy {
    public  int mWarnApp;
    public  int mWarnApp1;
    public List<AppInfo> mData;
    public  List<AppInfo> mOtherAdapterData;

    public RxModelWithSy(int mWarnApp, int mWarnApp1, List<AppInfo> mData, List<AppInfo> mOtherAdapterData) {
        this.mWarnApp = mWarnApp;
        this.mWarnApp1 = mWarnApp1;
        this.mData = mData;
        this.mOtherAdapterData = mOtherAdapterData;
    }

    public RxModelWithSy(List<AppInfo> mData, List<AppInfo> mOtherAdapterData) {
        this.mData = mData;
        this.mOtherAdapterData = mOtherAdapterData;
    }

    public RxModelWithSy() {
    }
}
