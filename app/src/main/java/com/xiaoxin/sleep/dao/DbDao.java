package com.xiaoxin.sleep.dao;

import android.app.Activity;
import android.text.TextUtils;

import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.App;
import com.xiaoxin.sleep.GreenDaoManager;
import com.xiaoxin.sleep.greendao.AppInfoBeanDao;
import com.xiaoxin.sleep.greendao.DaoMaster;
import com.xiaoxin.sleep.greendao.DaoSession;
import com.xiaoxin.sleep.greendao.UserAppInfoBeanDao;
import com.xiaoxin.sleep.model.AppCache;
import com.xiaoxin.sleep.model.Dao;
import com.xiaoxin.sleep.model.Event;
import com.xiaoxin.sleep.model.db.AppInfoBean;
import com.xiaoxin.sleep.model.db.UserAppInfoBean;
import com.xiaoxin.sleep.utils.AppInfo2BeanUtils;
import com.xiaoxin.sleep.utils.UserAppInfo2BeanUtils;
import com.xiaoxin.sleep.utils.Utils;

import java.lang.reflect.Type;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * dao层，与数据打交道，所有数据应从此取，存到此
 */
public class DbDao extends Dao {


    private DbDao() {

    }

    public void initListData(Activity mActivity) {
        AppInfoBeanDao appInfoBeanDao = GreenDaoManager.getInstance().getmDaoSession().getAppInfoBeanDao();
        List<AppInfoBean> appInfoBeans = appInfoBeanDao.queryBuilder()
                .build().list();
        //没有缓存
        if (0 == appInfoBeans.size()) {
            //清理内存
            clearCache();
            //设置内存
            List<AppInfo> appInfos = AppInfo2BeanUtils.fromList(appInfoBeans);
            SetMemoryCache(appInfos);
            list.addAll(appInfos);
            EventBus.getDefault().post(new Event(Event.ShowList));
            new SyncThread(mActivity, true).start();
        } else {
            new SyncThread(mActivity, true).start();
        }
    }


    /**
     * root 获取APP列表
     */
    class SyncThread extends Thread {
        private boolean isSendEvent;
        public Activity mContext;

        public SyncThread(Activity mContext, boolean mB) {
            this.mContext = mContext;
            this.isSendEvent = mB;
        }

        @Override
        public void run() {
            clearCache();
            mAllUserAppInfos = Utils.getAllUserAppInfos(mContext);
//            loadEnAppList();
//            loadDisAppList();
            saveLocalCache();
            list.clear();
            list.addAll(mAllUserAppInfos);
            if (isSendEvent) EventBus.getDefault().post(new Event(Event.MONDAY));
        }
    }

    public List<AppInfo> getUserSaveDisAppFromDB() {
        UserAppInfoBeanDao userAppInfoBeanDao = GreenDaoManager.getInstance().getmDaoSession().getUserAppInfoBeanDao();
        List<UserAppInfoBean> userAppInfoBeans = userAppInfoBeanDao.queryBuilder()
                .orderAsc(AppInfoBeanDao.Properties.Open_num).build().list();
        List<AppInfo> list = UserAppInfo2BeanUtils.fromList(userAppInfoBeans);
        return list;
    }


    private void saveLocalCache() {
        AppInfoBeanDao appInfoBeanDao = GreenDaoManager.getInstance().getmDaoSession().getAppInfoBeanDao();
        List<AppInfoBean> appInfoBeans = AppInfo2BeanUtils.toList(mAllUserAppInfos);
        appInfoBeanDao.insertInTx(appInfoBeans);
    }


}
