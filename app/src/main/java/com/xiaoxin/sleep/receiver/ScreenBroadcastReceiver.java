package com.xiaoxin.sleep.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.socks.library.KLog;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.dao.AppDao;
import com.xiaoxin.sleep.common.Constant;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;

import static com.xiaoxin.sleep.common.Constant.SLEEP_TIME_VALUE;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
  private String action = null;
  private SingleObserver<Long> mObserver;
  private Disposable disposable;

  @Override public void onReceive(final Context context, Intent intent) {
    action = intent.getAction();
    if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
      if (Constant.isOpenScreenSL) {
        if (null != disposable) {
          disposable.dispose();
        }
      }
    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
      if (Constant.isOpenScreenSL) {
        String time = (String) SpUtils.getParam(context, Constant.SLEEP_TIME_KEY, "");
        if (null==time|| TextUtils.isEmpty(time)){
          SLEEP_TIME_VALUE=0;
        }else{
          SLEEP_TIME_VALUE=Integer.parseInt(time);
        }
        KLog.e("Single发送");
        Single.just(15).delay(Constant.SLEEP_TIME_VALUE, TimeUnit.MINUTES).subscribe(new SingleObserver<Integer>() {
          @Override public void onSubscribe(Disposable mDisposable) {
            disposable = mDisposable;
          }

          @Override
          public void onSuccess(Integer value) {
            AppDao.getInstance().DoScreenBrSync(context);
            KLog.e("锁屏");
          }
          @Override
          public void onError(Throwable error) {

          }
        });

      }
    }
  }

  public SingleObserver<Long> getObserver(final Context mContext) {
    if (null == mObserver) {
      mObserver = new SingleObserver<Long>() {

        @Override public void onSubscribe(Disposable d) {
          disposable = d;
        }

        @Override public void onSuccess(Long value) {
          AppDao.getInstance().DoScreenBrSync(mContext);
          KLog.e("锁屏");
        }

        @Override public void onError(Throwable e) {
        }
      };
    }
    return mObserver;
  }
}