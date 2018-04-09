package com.xiaoxin.sleep.dao;

import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.App;
import com.xiaoxin.sleep.utils.ShellUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dicallc on 2018/4/9 0009.
 */

public class SelectAppDao {

  public  static void doSleep(List<AppInfo> mList,Consumer<List<AppInfo>> mConsumer){
    Observable.create(new ObservableOnSubscribe<List<AppInfo>>() {
      @Override
      public void subscribe(ObservableEmitter<List<AppInfo>> subscriber)
          throws Exception {
        SpUtils.setParam(App.getAppContext(), LibraryCons.NotFIRST, true);
        List<AppInfo> part = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        for (AppInfo mAppInfo : mList) {
          if (mAppInfo.isSelect) {
            //  如果已经禁用了，就不执行命令了
            if (mAppInfo.isEnable) {
              mAppInfo.isEnable = false;
              commands.add(LibraryCons.make_app_to_disenble + mAppInfo.packageName);

            }
            part.add(mAppInfo);
          }
        }
        ShellUtils.execCommand(commands, true,
            true);
        subscriber.onNext(part);
        subscriber.onComplete();
      }
    }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
        .observeOn(AndroidSchedulers.mainThread()).subscribe(mConsumer);
  }
}
