package com.xiaoxin.sleep.view;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.xiaoxin.sleep.R;

public  class BaseDialogWithTuchSide extends Dialog {
  public BaseDialogWithTuchSide(Context context) {
    super(context, R.style.BottomDialog);
  }

  public BaseDialogWithTuchSide(Context context, int themeResId) {
    super(context, R.style.BottomDialog);
  }

  protected BaseDialogWithTuchSide(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, R.style.BottomDialog);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
        /* 触摸外部弹窗 */
    if (isOutOfBounds(getContext(), event)) {
      mOnTouchOutsideListener.onTouchOutside(event);
      return false;
    }
    return super.onTouchEvent(event);
  }

  private boolean isOutOfBounds(Context context, MotionEvent event) {
    final int x = (int) event.getX();
    final int y = (int) event.getY();
    final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
    final View decorView = getWindow().getDecorView();
    return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y
        > (decorView.getHeight() + slop));
  }

  public void setCallBack(OnTouchOutsideListener callBack) {
    this.mOnTouchOutsideListener = callBack;
  }

  private OnTouchOutsideListener mOnTouchOutsideListener;

  public interface OnTouchOutsideListener {
    public void onTouchOutside(MotionEvent mEvent);
  }
}