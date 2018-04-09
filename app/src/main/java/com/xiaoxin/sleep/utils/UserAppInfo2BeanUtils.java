package com.xiaoxin.sleep.utils;

import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.sleep.model.db.AppInfoBean;
import com.xiaoxin.sleep.model.db.UserAppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangdikai on 2017/10/14.
 */

public class UserAppInfo2BeanUtils {
    public static UserAppInfoBean to(AppInfo appInfo) {
        if (null == appInfo) {
            throw new NullPointerException("传入的AppInfo为空");
        }
        return new UserAppInfoBean(appInfo.appName, appInfo.packageName, appInfo.file_path, appInfo.open_num);

    }
    public static AppInfo from(UserAppInfoBean appInfoBean) {
        if (null == appInfoBean) {
            throw new NullPointerException("传入的appInfoBean为空");
        }
        return new AppInfo(appInfoBean.appName, appInfoBean.packageName, appInfoBean.file_path, appInfoBean.open_num);

    }

    public static List<UserAppInfoBean> toList(List<AppInfo> appInfos) {
        if (null==appInfos||0 == appInfos.size()) {
            throw new NullPointerException("传入的List<AppInfo>为空");
        }
        List<UserAppInfoBean> appInfoBeans = new ArrayList<>();
        for (int i = 0; i <appInfos.size() ; i++) {
            appInfoBeans.add(to(appInfos.get(i)));
        }
        return appInfoBeans;

    }
    public static List<AppInfo> fromList(List<UserAppInfoBean> appInfoBeans) {
        if (null==appInfoBeans||0 == appInfoBeans.size()) {
            throw new NullPointerException("传入的List<UserAppInfoBean>为空");
        }
        List<AppInfo> appInfos = new ArrayList<>();
        for (int i = 0; i <appInfoBeans.size() ; i++) {
            appInfos.add(from(appInfoBeans.get(i)));
        }
        return appInfos;

    }
}
