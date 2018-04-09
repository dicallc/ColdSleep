package com.xiaoxin.sleep.model;

import com.xiaoxin.library.model.AppInfo;
import java.util.List;

/**
 * Created by Administrator on 2017/7/14 0014.
 */

public class AppCache {
  public List<AppInfo> en;
  public List<AppInfo> dis;
  public List<AppInfo> all;

  public AppCache(List<AppInfo> mEn, List<AppInfo> mDis, List<AppInfo> mAll) {
    en = mEn;
    dis = mDis;
    all = mAll;
  }
}
