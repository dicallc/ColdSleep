package com.xiaoxin.sleep.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xiaoxin.sleep.R;

/**
 * Created by Administrator on 2017/7/21 0021.
 */

public class BaseActivity extends AppCompatActivity {
  private MaterialDialog mDialog;
  private  int REQUEST_IGNORE_BATTERY_CODE;
  protected Activity mActivity;
  protected Animator mAnim;
  private int mCx;
  private int mCy;
  private int mHypotenuse;
  protected ViewCompleteImpl mViewComplete;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = BaseActivity.this;
    mViewComplete = new ViewCompleteImpl();
  }

  protected void showloadDialog(String title) {
    mDialog = new MaterialDialog.Builder(this).title(title)
        .content("请深呼吸休息一下")
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .show();
  }
  protected void showloadDialog() {
    mDialog = new MaterialDialog.Builder(this)
        .content("请深呼吸休息一下")
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .show();
  }

  protected void goneloadDialog() {
    if (null != mDialog&&mDialog.isShowing()) if (mDialog.isShowing()) mDialog.dismiss();
  }

  /**
   * 渲染完回调,开始水波动画
   */
  protected class ViewCompleteImpl implements ViewTreeObserver.OnGlobalLayoutListener {

    @Override public void onGlobalLayout() {
      revealShow(getContentView());
      getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mViewComplete);
    }
  }

  /**
   * 针对N以上的Doze模式
   */
  public  void isIgnoreBatteryOption(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      try {
        Intent intent = new Intent();
        String packageName = activity.getPackageName();
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
          //               intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
          intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
          intent.setData(Uri.parse("package:" + packageName));
          activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      //if (requestCode == BatteryUtils.REQUEST_IGNORE_BATTERY_CODE){
      //    //TODO something
      //}
    } else if (resultCode == RESULT_CANCELED) {
      isIgnoreBatteryOption(mActivity);
    }
  }
  protected void revealShow(View mView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mCx = (mView.getWidth());
      mCy = (mView.getHeight());
      mHypotenuse = (int) Math.hypot(mCx, mCy);
      mAnim = ViewAnimationUtils.createCircularReveal(mView, mCx, mCy, 0, mHypotenuse);
      mAnim.setDuration(800);
      mView.setVisibility(View.VISIBLE);
      mAnim.addListener(new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animation) {

        }

        @Override public void onAnimationEnd(Animator animation) {
          ChangeStatusColor();
        }

        @Override public void onAnimationCancel(Animator animation) {

        }

        @Override public void onAnimationRepeat(Animator animation) {

        }
      });
      mAnim.start();

    }
  }

  private void ChangeStatusColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      //设置状态栏颜色
      Window window = getWindow();
      //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getResources().getColor(R.color.main_color));
    }
  }

  protected void revealGone(final View mView) {
    Animator anim = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      anim = ViewAnimationUtils.createCircularReveal(mView, mCx, mCy, mHypotenuse, 0);
      anim.setDuration(300);
      anim.addListener(new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animation) {

        }

        @Override public void onAnimationEnd(Animator animation) {

          finish();
        }

        @Override public void onAnimationCancel(Animator animation) {

        }

        @Override public void onAnimationRepeat(Animator animation) {

        }
      });
      mView.setVisibility(View.GONE);
      anim.start();
    }else{
      finish();
    }

  }
  protected  View getContentView(){
    ViewGroup view = (ViewGroup)getWindow().getDecorView();
    FrameLayout content = (FrameLayout)view.findViewById(android.R.id.content);
    return content.getChildAt(0);
  }

//  @Override protected void onResume() {
//    super.onResume();
//    overridePendingTransition(0, android.R.anim.fade_out);
//  }
}
