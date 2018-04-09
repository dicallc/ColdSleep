package com.xiaoxin.sleep.model;

import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.sleep.model.db.AppInfoBean;
import com.xiaoxin.sleep.utils.AppInfo2BeanUtils;
import com.xiaoxin.sleep.utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangdikai on 2017/10/14.
 */

public class Dao {
    protected List<AppInfo> list = new ArrayList<>();
    protected List<AppInfo> EnList = new ArrayList<>();
    protected List<AppInfo> Dislist = new ArrayList<>();

    protected List<AppInfo> mAllUserAppInfos = new ArrayList<>();


    protected void clearCache() {
        EnList.clear();
        Dislist.clear();
        mAllUserAppInfos.clear();
    }
//    protected void SetMemoryCache(AppCache mAppCache) {
//        EnList.addAll(mAppCache.en);
//        Dislist.addAll(mAppCache.dis);
//        mAllUserAppInfos.addAll(mAppCache.all);
//    }
    protected void SetMemoryCache(List<AppInfo> list) {
        mAllUserAppInfos.addAll(list);
    }

    /**
     * 获取未被禁用的app列表
     */
    protected void loadEnAppList() {
        EnList.clear();
        ShellUtils.CommandResult allEnAppsRes =
                ShellUtils.execCommand(LibraryCons.allEnablePackageV3, true, true);
        String mAllEnMsg = allEnAppsRes.successMsg;
        String[] mSplit = mAllEnMsg.split("package:");
        for (int i = 1; i < mSplit.length; i++) {
            for (AppInfo mAppInfo : mAllUserAppInfos) {
                if (mSplit[i].equals(mAppInfo.packageName)) {
                    mAppInfo.isEnable = true;
                    EnList.add(mAppInfo);
                    break;
                }
            }
        }
    }

    /**
     * 获取禁用列表
     */
    protected void loadDisAppList() {
        ShellUtils.CommandResult allDisAppsRes =
                ShellUtils.execCommand(LibraryCons.allDisabledPackage, true, true);
        String[] DisApps = allDisAppsRes.successMsg.split("package:");
        for (int i = 1; i < DisApps.length; i++) {
            for (AppInfo mAppInfo : mAllUserAppInfos) {
                if (DisApps[i].equals(mAppInfo.packageName)) {
                    mAppInfo.isEnable = false;
                    Dislist.add(mAppInfo);
                }
            }
        }
    }
}
