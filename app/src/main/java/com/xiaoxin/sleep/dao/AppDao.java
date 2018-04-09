package com.xiaoxin.sleep.dao;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.App;
import com.xiaoxin.sleep.common.Constant;
import com.xiaoxin.sleep.model.AppCache;
import com.xiaoxin.sleep.model.Event;
import com.xiaoxin.sleep.model.RxModelWithSy;
import com.xiaoxin.sleep.utils.ShellUtils;
import com.xiaoxin.sleep.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static com.xiaoxin.library.common.LibraryCons.load_rencent_run_app;

/**
 * dao层，与数据打交道，所有数据应从此取，存到此
 */
public class AppDao {

    private String[] mSuggestions;

    private AppDao() {
    }

    public String[] getSuggestions() {
        return mSuggestions;
    }

    public static synchronized AppDao getInstance() {
        return SingleHolder.dao;
    }

    private static class SingleHolder {
        private static final AppDao dao = new AppDao();
    }

    List<AppInfo> list = new ArrayList<>();
    List<AppInfo> EnList = new ArrayList<>();
    List<AppInfo> Dislist = new ArrayList<>();
    private List<AppInfo> mAllUserAppInfos = new ArrayList<>();

    public List<AppInfo> getList() {
        return list;
    }


    private void SetMemoryCache(AppCache mAppCache) {
        EnList.addAll(mAppCache.en);
        Dislist.addAll(mAppCache.dis);
        mAllUserAppInfos.addAll(mAppCache.all);
    }


    private void clearCache() {
        //    list.clear();
        EnList.clear();
        Dislist.clear();
        mAllUserAppInfos.clear();
    }

    public void initListData(Activity mActivity) {
        String json = (String) SpUtils.getParam(mActivity, LibraryCons.LOCAL_DB_NAME, "");
        if (!TextUtils.isEmpty(json)) {
            AppCache mAppCache = new Gson().fromJson(json, AppCache.class);
            clearCache();
            SetMemoryCache(mAppCache);
            list.addAll(mAppCache.all);
            EventBus.getDefault().post(new Event(Event.ShowList));
            new SyncThread(mActivity, true).start();
        } else {
            new SyncThread(mActivity, true).start();
        }
    }

    public void SyncData(Activity mActivity) {
        new SyncThread(mActivity, false).start();
    }

    public void SyncDisData(Activity mActivity) {
        new SyncDisThread(mActivity).start();
    }

    class SyncDisThread extends Thread {
        public Activity mContext;

        public SyncDisThread(Activity mContext) {
            this.mContext = mContext;
        }

        @Override
        public void run() {
            List<AppInfo> userSaveDisAppFromDB = getUserSaveDisAppFromDB();
//            if (mAllUserAppInfos.size() == 0)
                mAllUserAppInfos = Utils.getAllUserAppInfos(mContext);
            //从所有列表中找用户所存app是否存在
            mSuggestions = new String[userSaveDisAppFromDB.size()];
            for (int i = 0; i < userSaveDisAppFromDB.size(); i++) {
                mSuggestions[i] = userSaveDisAppFromDB.get(i).appName;
                //使用indexof寻找
                int indexOf = mAllUserAppInfos.indexOf(userSaveDisAppFromDB.get(i));
                if (!(indexOf >= 0)) {
                    userSaveDisAppFromDB.remove(i);
                } else {
                    //如果全部app列表有，就赋值最新状态
                    boolean isEnable = mAllUserAppInfos.get(indexOf).isEnable;
                    userSaveDisAppFromDB.get(i).setEnable(isEnable);
                }
            }
            EventBus.getDefault().post(new Event(Event.getDisList, userSaveDisAppFromDB));
            saveUserSaveDisAppToDB(userSaveDisAppFromDB);
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
            //获取未被禁用的app列表
//      loadEnAppList();
            //获取禁用列表
//      loadDisAppList();
            saveLocalCache();
            list.clear();
            list.addAll(mAllUserAppInfos);
            if (isSendEvent) EventBus.getDefault().post(new Event(Event.ShowList));

            EventBus.getDefault().post(new Event(Event.MONDAY));
        }
    }


    private List<String> loadEnAppListApplyPackage() {

        ShellUtils.CommandResult allEnAppsRes =
                ShellUtils.execCommand(LibraryCons.allEnablePackageV3, true, true);
        String mAllEnMsg = allEnAppsRes.successMsg;
        String[] mSplit = mAllEnMsg.split("package:");
        List<String> arr = new ArrayList<>();
        for (int i = 1; i < mSplit.length; i++) {
            arr.add(mSplit[i]);
        }
        return arr;
    }



    private void saveLocalCache() {
        AppCache mAppCache = new AppCache(EnList, Dislist, mAllUserAppInfos);
        String mJson = new Gson().toJson(mAppCache);
        SpUtils.setParam(App.getAppContext(), LibraryCons.LOCAL_DB_NAME, mJson);
    }

    /**
     * 存储用户操作需要冻结app列表数据
     */
    public void saveUserSaveDisAppToDB(List<AppInfo> mAppInfos) {
        new SaveUserDisThread(mAppInfos).start();
    }

    public Observable<RxModelWithSy> MainInit(){
        Observable<RxModelWithSy> observable = Observable.create(new ObservableOnSubscribe<RxModelWithSy>() {
            @Override
            public void subscribe(ObservableEmitter<RxModelWithSy> subscriber) throws Exception {
                String time = (String) SpUtils.getParam(App.getAppContext(), Constant.SLEEP_TIME_KEY, "");
                if (null == time || TextUtils.isEmpty(time)) {
                    Constant.SLEEP_TIME_VALUE = 0;
                } else {
                    Constant.SLEEP_TIME_VALUE = Integer.parseInt(time);
                }
                //找出已经解冻的app进行冻结
                List<AppInfo> list = getUserSaveDisAppFromDB();
                RxModelWithSy rxModelWithSy=null;
                //超过8个才进行筛选
                if (list.size()>8){
                    List<AppInfo> sortAppList = sortAppList(list);
                     rxModelWithSy = new RxModelWithSy(list, sortAppList);
                }else{
                    rxModelWithSy = new RxModelWithSy(list, list);
                }
                subscriber.onNext(rxModelWithSy);
                subscriber.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        return observable;

    }

    public List<AppInfo> getUserSaveDisAppFromDB() {
        String json =
                (String) SpUtils.getParam(App.getAppContext(), LibraryCons.LOCAL_USER_DISAPP_DB_NAME, "");
        Type type = new TypeToken<List<AppInfo>>() {
        }.getType();
        List<AppInfo> list = new Gson().fromJson(json, type);
        return list;
    }

    class SaveUserDisThread extends Thread {
        List<AppInfo> mAppInfos;

        public SaveUserDisThread(List<AppInfo> list) {
            this.mAppInfos = list;
        }

        @Override
        public void run() {
            saveUserDisFunc(mAppInfos);
        }
    }

    private void saveUserDisFunc(List<AppInfo> mAppInfos) {
        String mJson = new Gson().toJson(mAppInfos);
        SpUtils.setParam(App.getAppContext(), LibraryCons.LOCAL_USER_DISAPP_DB_NAME, mJson);
    }


    public List<AppInfo> sortAppList(List<AppInfo> list) {
        if (list.size()<8){
            return list;
        }
        Collections.sort(list);
        List<AppInfo> appInfos = list.subList(0, 8);
        return appInfos;
    }

    public int findWarnApp(List<AppInfo> mData,List<AppInfo> mSecData) {
        int num = 0;
        List<String> cmmoands = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            AppInfo mAppInfo = mData.get(i);
            if (mAppInfo.isWarn) {
                num++;
                cmmoands.add(LibraryCons.make_app_to_disenble + mAppInfo.packageName);
                mAppInfo.isWarn = false;
            }
        }
        for (int i = 0; i < mSecData.size(); i++) {
            AppInfo mAppInfo = mSecData.get(i);
            if (mAppInfo.isWarn) {
                num++;
                cmmoands.add(LibraryCons.make_app_to_disenble + mAppInfo.packageName);
                mAppInfo.isWarn = false;
            }
        }
        ShellUtils.execCommand(cmmoands, true, true);

        return num;
    }

    public void DoScreenBrSync(Context mContext) {
        new ScreenBrSyncThread(mContext).start();
    }

    class ScreenBrSyncThread extends Thread {
        private Context mContext;

        public ScreenBrSyncThread(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void run() {
            super.run();
            ShellUtils.CommandResult commandResult =
                    ShellUtils.execCommand(load_rencent_run_app, true, true);
            String result = Utils.loadRecentRunSubStr(commandResult.successMsg);
            KLog.e("正在运行的app: " + result);
            long startTime = System.nanoTime();  //開始時間
            //未睡眠列表
            List<String> mList = loadEnAppListApplyPackage();
            //从缓存中拿到用户保存的需要睡眠的列表
            List<AppInfo> mUserSaveDisAppFromDB = getUserSaveDisAppFromDB();
            if (null == mUserSaveDisAppFromDB || mUserSaveDisAppFromDB.size() == 0) return;
            //从未睡眠列表中寻找用户需要睡眠是否有已经苏醒
            for (AppInfo userdis : mUserSaveDisAppFromDB) {
                if (mList.contains(userdis.packageName)) {
                    //如果在前台默认不睡眠
                    if (Constant.isOpenRecentKill && userdis.packageName.equals(result)) {
                        continue;
                    }
                    KLog.e("睡眠" + userdis.appName);
                    userdis.isWarn = false;
                    ShellUtils.execCommand(LibraryCons.make_app_to_disenble + userdis.packageName, true,
                            true);
                } else {
                    userdis.isWarn = false;
                }
            }
            long consumingTime = System.nanoTime() - startTime; //消耗時間
            KLog.e(consumingTime / 1000000 + "微秒");
            saveUserDisFunc(mUserSaveDisAppFromDB);
        }
    }
}
