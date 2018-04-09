package com.xiaoxin.sleep.model;

import android.support.annotation.IntDef;

import com.xiaoxin.library.model.AppInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Administrator on 2017/7/21 0021.
 */

public class Event {
  public List<AppInfo> list;
  public static final int SUNDAY = 0;
  public static final int MONDAY = 1;
  /**
   * 传递数据，并且刷新adapter
   */
  public static final int NOTIFYADAPTER = 2;
  public static final int getDisList = 3;
  public static final int ShowList = 4;
  public static final int FRIDAY = 5;
  public static final int SATURDAY = 6;

  @IntDef({ SUNDAY, MONDAY, NOTIFYADAPTER, getDisList, ShowList, FRIDAY, SATURDAY })
  @Retention(RetentionPolicy.SOURCE) public @interface WeekDays {
  }

  public Event(@WeekDays int mCurrentDay) {
    currentDay = mCurrentDay;
  }

  public Event(@WeekDays int currentDay,List<AppInfo> list) {
    this.list = list;
    this.currentDay = currentDay;
  }

  @WeekDays int currentDay = SUNDAY;

  @WeekDays public int getCurrentDay() {
    return currentDay;
  }
}
