package com.xiaoxin.sleep.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.sleep.dao.AppDao;
import com.xiaoxin.sleep.model.RunSericeModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class Utils {
  private static String TAG = "RunServiceInfo";

  public static void getRunningServiceInfo(Activity activity) {
    List<RunSericeModel> serviceInfoList = null;
    ActivityManager mActivityManager =
        (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
    // 设置一个默认Service的数量大小
    int defaultNum = 20;
    // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
    List<ActivityManager.RunningServiceInfo> runServiceList =
        mActivityManager.getRunningServices(defaultNum);

    System.out.println(runServiceList.size());

    // ServiceInfo Model类 用来保存所有进程信息
    serviceInfoList = new ArrayList<RunSericeModel>();

    for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {

      // 获得Service所在的进程的信息
      int pid = runServiceInfo.pid; // service所在的进程ID号
      int uid = runServiceInfo.uid; // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
      // 进程名，默认是包名或者由属性android：process指定
      String processName = runServiceInfo.process;

      // 该Service启动时的时间值
      long activeSince = runServiceInfo.activeSince;
      boolean foreground = runServiceInfo.foreground;

      // 如果该Service是通过Bind方法方式连接，则clientCount代表了service连接客户端的数目
      int clientCount = runServiceInfo.clientCount;

      // 获得该Service的组件信息 可能是pkgname/servicename
      ComponentName serviceCMP = runServiceInfo.service;
      String serviceName = serviceCMP.getShortClassName(); // service 的类名
      String pkgName = serviceCMP.getPackageName(); // 包名

      // 打印Log
      Log.e(TAG, " 所在进程名："
          + processName
          + "\n"
          + "该service的组件信息:"
          + serviceName
          + " 是否在后台运行:  "
          + foreground
          + "\n");

      // 这儿我们通过service的组件信息，利用PackageManager获取该service所在应用程序的包名 ，图标等
      PackageManager mPackageManager = activity.getPackageManager(); // 获取PackagerManager对象;

      try {
        // 获取该pkgName的信息
        ApplicationInfo appInfo = mPackageManager.getApplicationInfo(pkgName, 0);

        RunSericeModel runService = new RunSericeModel();
        runService.setAppIcon(appInfo.loadIcon(mPackageManager));
        runService.setAppLabel(appInfo.loadLabel(mPackageManager) + "");
        runService.setServiceName(serviceName);
        runService.setPkgName(pkgName);
        // 设置该service的组件信息
        Intent intent = new Intent();
        intent.setComponent(serviceCMP);
        runService.setIntent(intent);

        runService.setPid(pid);
        runService.setProcessName(processName);

        // 添加至集合中
        serviceInfoList.add(runService);
      } catch (PackageManager.NameNotFoundException e) {
        // TODO Auto-generated catch block
        System.out.println("--------------------- error -------------");
        e.printStackTrace();
      }
    }
  }

  /**
   * 获取所有用户app信息
   */
  public static List<AppInfo> getAllUserAppInfos(Activity mContext) {
    List<AppInfo> mAppInfos = new ArrayList<>();
    PackageManager pm = mContext.getApplication().getPackageManager();
    List<PackageInfo> packgeInfos =
        pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    //如果是在用户已经选择冷冻列表中则要设置选中状态
    List<AppInfo> mUserSaveDisAppFromDB = AppDao.getInstance().getUserSaveDisAppFromDB();
    for (PackageInfo packgeInfo : packgeInfos) {
      ApplicationInfo mApplicationInfo = packgeInfo.applicationInfo;
      //如果是系统程序就跳出本次循环
      if ((mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) continue;
      //是否禁用
      boolean enabled = mApplicationInfo.enabled;
      String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();
      String packageName = packgeInfo.packageName;
      Drawable drawable = packgeInfo.applicationInfo.loadIcon(pm);
      Bitmap bm=null;
      if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
        bm=AppIconHelperV26.getAppIcon(pm,packageName);
      }else{
        BitmapDrawable bd = (BitmapDrawable) drawable;
         bm = bd.getBitmap();
      }
      String path = mContext.getFilesDir().getPath();
      String icon_path = com.xiaoxin.library.utils.Utils.savePic(bm, path, packageName);
      AppInfo appInfo = new AppInfo(appName, packageName, icon_path,enabled);

      //如果内存为空就直接赋值为false
      if (null == mUserSaveDisAppFromDB || mUserSaveDisAppFromDB.size() == 0) {
        appInfo.isSelect = false;
      } else {
        //对比用户所存列表是否存在设置是否选中
        if (mUserSaveDisAppFromDB.contains(appInfo)) {
          appInfo.isSelect = true;
        } else {
          appInfo.isSelect = false;
        }
      }
      mAppInfos.add(appInfo);
    }
    return mAppInfos;
  }

  public static String loadRecentRunSubStr(String str) {
    int indexOf = str.indexOf("u0");
    int lastIndexOf = str.indexOf("/");
    String result_str = str.substring(indexOf + 1, lastIndexOf);
    return result_str;
  }
}

