package com.xiaoxin.library.common;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class LibraryCons {
  public static final String allEnablePackage="pm list packages -e";
  public static final String allEnablePackageV3="pm list packages -e -3";
  public static final String allDisabledPackage="pm list packages -d -3";
  public static final String allDisabledPackageV3="pm list packages -d -3";
  public static final String make_app_to_disenble ="pm disable-user ";
  public static final String make_app_to_enble="pm enable  ";
  public static final String uninstall_app="pm uninstall ";
  public static final String load_rencent_run_app="dumpsys activity | grep \"mFocusedActivity\"";
  public static final String SELECTENABLE="enable";
  public static final String SELECTDisabled="Disabled";
  public static final String SELECTEAll="all";
  public static final String LOCAL_DB_NAME="appcache";
  public static final String LOCAL_USER_DISAPP_DB_NAME="user_disapp_cache";
  public static final String NotFIRST ="not_First";
  public static final String ACTION="action";
  public static final String ACTION_OPEN="open";


}
