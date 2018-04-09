package com.xiaoxin.sleep.view;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import com.xiaoxin.sleep.R;

public class DialogWithCircularReveal {

  private Context mContext;
  private View mView;
  private int startX = 0, startY = 0;
  private long duration = 500;
  private boolean rippleStartSet = false;
  private final BaseDialogWithTuchSide mBottomDialog;
  private View mRevealview;
  private Animator mAnim;
  private int mCx;
  private int mCy;
  private int mHypotenuse;

  public DialogWithCircularReveal(Context c, View view) {
    mContext = c;
    this.mView=view;
    //mView = View.inflate(mContext, layoutID, null);
    mBottomDialog = new BaseDialogWithTuchSide(c, R.style.BottomDialog);
    mBottomDialog.setContentView(mView);
    ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
    layoutParams.width = c.getResources().getDisplayMetrics().widthPixels;
    mView.setLayoutParams(layoutParams);
    mBottomDialog.getWindow().setGravity(Gravity.BOTTOM);
  }

  public void showDialog() {
    mBottomDialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override public void onShow(DialogInterface dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          revealShow();
        }
      }
    });
    mBottomDialog.setCallBack(new BaseDialogWithTuchSide.OnTouchOutsideListener() {
      @Override public void onTouchOutside(MotionEvent mEvent) {
        gone();
      }
    });
    mBottomDialog.show();
  }

  private void gone() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      revealGone();
    }else{
      mBottomDialog. dismiss();
    }
  }

  public View getChildView(int id) {
    return mView.findViewById(id);
  }

  public void setRippleStart(Point p) {
    setRippleStart(p.x, p.y);
  }

  public void setRippleStart(int x, int y) {
    rippleStartSet = true;
    startX = x;
    startY = y;
  }

  public void setDuration(long d) {
    duration = d;
  }

  public void setRevealview(int mRevealview) {

    this.mRevealview = mView.findViewById(mRevealview);
  }

  public Dialog getDialog() {
    return mBottomDialog;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private void revealShow() {
    mCx = (mRevealview.getWidth());
    mCy = (mRevealview.getHeight());
    mHypotenuse = (int) Math.hypot(mRevealview.getWidth(), mRevealview.getHeight());
    mAnim = ViewAnimationUtils.createCircularReveal(mRevealview, mCx, mCy, 0, mHypotenuse);
    mAnim.setDuration(duration);
    mRevealview.setVisibility(View.VISIBLE);
    mAnim.start();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private void revealGone() {
    Animator anim = ViewAnimationUtils.createCircularReveal(mRevealview, mCx, mCy, mHypotenuse, 0);
    anim.setDuration(300);
    anim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        //mBottomDialog.set
        mBottomDialog.dismiss();
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });
    anim.start();
  }
  public void dismiss(){
    gone();
  }
}