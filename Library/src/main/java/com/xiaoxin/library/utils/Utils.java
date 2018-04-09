package com.xiaoxin.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.xiaoxin.library.model.AppInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class Utils {
  /**
   * 获取所有用户app信息
   */
  public static List<AppInfo> getAllUserAppInfos(Activity mContext) {
    List<AppInfo> mAppInfos = new ArrayList<>();
    PackageManager pm = mContext.getApplication().getPackageManager();
    List<PackageInfo> packgeInfos =
        pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        /* 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String str_name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(str_name);
         */
    for (PackageInfo packgeInfo : packgeInfos) {
      ApplicationInfo mApplicationInfo = packgeInfo.applicationInfo;
      //如果是系统程序就跳出本次循环
      if ((mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) continue;
      String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();
      String packageName = packgeInfo.packageName;
      Drawable drawable = packgeInfo.applicationInfo.loadIcon(pm);
      BitmapDrawable bd = (BitmapDrawable) drawable;
      Bitmap bm = bd.getBitmap();
      String path = mContext.getFilesDir().getPath();
      String icon_path = Utils.savePic(bm, path, packageName);
      AppInfo appInfo = new AppInfo(appName, packageName, icon_path);
      //如果是在用户已经选择冷冻列表中则要设置选中状态
      mAppInfos.add(appInfo);
    }
    return mAppInfos;
  }
  public static void openApp(Context context,String packageName) {
    Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
    context.startActivity(intent);
  }


  /**
   * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
   *
   * @return 应用程序是/否获取Root权限
   */
  public static boolean upgradeRootPermission(String pkgCodePath) {
    Process process = null;
    DataOutputStream os = null;
    try {
      String cmd = "chmod 777 " + pkgCodePath;
      process = Runtime.getRuntime().exec("su"); //切换到root帐号
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.writeBytes("exit\n");
      os.flush();
      process.waitFor();
      if (process.waitFor() == 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
  }

  public static String savePic(Bitmap b, String filePath, String fileName) {
    File f = new File(filePath);
    if (!f.exists()) {
      f.mkdir();
    }
    String icon_path = filePath + "/" + fileName;
    //存在直接返回
    if (new File(icon_path).exists())
      return icon_path;

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filePath + File.separator + fileName);
      if (null != fos) {
        b.compress(Bitmap.CompressFormat.PNG, 90, fos);
        fos.flush();
        fos.close();
      }
      return icon_path;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (b != null && !b.isRecycled()) {
        b.recycle();
      }

    }
    return null;

  }
}
