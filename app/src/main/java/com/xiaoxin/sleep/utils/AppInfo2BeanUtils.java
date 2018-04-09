package com.xiaoxin.sleep.utils;

import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.sleep.model.db.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangdikai on 2017/10/14.
 */

public class AppInfo2BeanUtils {
    public static AppInfoBean to(AppInfo appInfo) {
        if (null == appInfo) {
            throw new NullPointerException("传入的AppInfo为空");
        }
        return new AppInfoBean(appInfo.appName, appInfo.packageName, appInfo.file_path, appInfo.open_num);

    }
    public static AppInfo from(AppInfoBean appInfoBean) {
        if (null == appInfoBean) {
            throw new NullPointerException("传入的appInfoBean为空");
        }
        return new AppInfo(appInfoBean.appName, appInfoBean.packageName, appInfoBean.file_path, appInfoBean.open_num);

    }

    public static List<AppInfoBean> toList(List<AppInfo> appInfos) {
        if (null==appInfos||0 == appInfos.size()) {
            throw new NullPointerException("传入的List<AppInfo>为空");
        }
        List<AppInfoBean> appInfoBeans = new ArrayList<>();
        for (int i = 0; i <appInfos.size() ; i++) {
            appInfoBeans.add(to(appInfos.get(i)));
        }
        return appInfoBeans;

    }
    public static List<AppInfo> fromList(List<AppInfoBean> appInfoBeans) {
        if (null==appInfoBeans||0 == appInfoBeans.size()) {
            throw new NullPointerException("传入的List<AppInfoBean>为空");
        }
        List<AppInfo> appInfos = new ArrayList<>();
        for (int i = 0; i <appInfoBeans.size() ; i++) {
            appInfos.add(from(appInfoBeans.get(i)));
        }
        return appInfos;

    }
}
