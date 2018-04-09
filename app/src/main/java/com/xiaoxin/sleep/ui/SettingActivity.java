package com.xiaoxin.sleep.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.CompoundButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.SuperTextView;
import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.R;
import com.xiaoxin.sleep.common.Constant;
import com.xiaoxin.sleep.service.TraceServiceImpl;
import com.xiaoxin.sleep.view.SettingNormalView;

import static com.xiaoxin.sleep.common.Constant.SLEEP_TIME_VALUE;

public class SettingActivity extends AppCompatActivity {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.main_layout) CoordinatorLayout mainLayout;
  @BindView(R.id.setting_select_app) SettingNormalView settingSelectApp;
  @BindView(R.id.setting_screen_sleep) SuperTextView settingScreenSleep;
  @BindView(R.id.setting_screen_sleep_time) SettingNormalView settingScreenSleepTime;
  int select_index = 0;
  @BindView(R.id.setting_recent_app_kill) SuperTextView mSettingRecentAppKill;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    ButterKnife.bind(this);
    initToobar();
    if (Constant.isOpenScreenSL) {
      settingScreenSleep.setSwitchIsChecked(true);
    } else {
      settingScreenSleep.setSwitchIsChecked(false);
    }
    if (Constant.isOpenRecentKill) {
      mSettingRecentAppKill.setSwitchIsChecked(true);
    } else {
      mSettingRecentAppKill.setSwitchIsChecked(false);
    }
    settingScreenSleep.setSwitchCheckedChangeListener(
        new SuperTextView.OnSwitchCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
            Constant.isOpenScreenSL = mB;
            SpUtils.setParam(SettingActivity.this, Constant.ISOPENSCREENSLKEY,
                Constant.isOpenScreenSL);
            if (mB == true) {
              startService(new Intent(SettingActivity.this, TraceServiceImpl.class));
            } else {
              stopService(new Intent(SettingActivity.this, TraceServiceImpl.class));
            }
          }
        });
    mSettingRecentAppKill.setSwitchCheckedChangeListener(
        new SuperTextView.OnSwitchCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
            Constant.isOpenRecentKill = mB;
            SpUtils.setParam(SettingActivity.this, Constant.ISOPENRECENTKILLKEY,
                Constant.isOpenRecentKill);
          }
        });

    Transition transitionSlideRight = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      transitionSlideRight =
          TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
      getWindow().setEnterTransition(transitionSlideRight);
    }
    initData();
  }

  private void initData() {

    loadSelectIndex();
  }

  private void loadSelectIndex() {
    String time = (String) SpUtils.getParam(SettingActivity.this, Constant.SLEEP_TIME_KEY, "");
    if (null == time || TextUtils.isEmpty(time)) {
      SLEEP_TIME_VALUE = 0;
      settingScreenSleepTime.setSubTitleText("默认是立即睡眠");
    } else {
      SLEEP_TIME_VALUE = Integer.parseInt(time);
      settingScreenSleepTime.setSubTitleText("锁屏" + SLEEP_TIME_VALUE + "分钟后睡眠");
    }
    String[] mArray = getResources().getStringArray(R.array.sleep_time);
    for (int i = 0; i < mArray.length; i++) {
      if (mArray[i].contains(SLEEP_TIME_VALUE + "")) {
        select_index = i;
        break;
      }
    }
  }

  private void initToobar() {
    toolbar.setTitle("设置");
    setSupportActionBar(toolbar);
    //关键下面两句话，设置了回退按钮，及点击事件的效果
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
  }

  @OnClick(R.id.setting_select_app) public void toSelectApp() {
    overridePendingTransition(0, 0);
    Intent mIntent = new Intent(SettingActivity.this, SelectAppActivity.class);
    mIntent.putExtra(LibraryCons.ACTION, LibraryCons.ACTION_OPEN);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      startActivityWithOptions(mIntent);
    } else {
      startActivity(mIntent);
    }
  }

  @OnClick(R.id.setting_screen_sleep_time) public void showSingleChoice() {
    new MaterialDialog.Builder(this).title("睡眠时间")
        .items(R.array.sleep_time)
        .positiveText("确定")
        .itemsCallbackSingleChoice(select_index, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog mMaterialDialog, View mView, int mI,
              CharSequence mCharSequence) {
            select_index = mI;
            String time = mCharSequence.toString().replace("分钟", "");
            int mInt = Integer.parseInt(time);
            SpUtils.setParam(SettingActivity.this, Constant.SLEEP_TIME_KEY, mInt + "");
            return true;
          }
        })
        .show();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void startActivityWithOptions(Intent intent) {
    ActivityOptions transitionActivity = null;
    transitionActivity = ActivityOptions.makeSceneTransitionAnimation(SettingActivity.this);
    startActivity(intent, transitionActivity.toBundle());
  }
}
