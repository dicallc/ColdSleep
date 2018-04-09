package com.xiaoxin.sleep.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.library.utils.Utils;
import com.xiaoxin.sleep.R;
import com.xiaoxin.sleep.adapter.OtherAppListAdapter;
import com.xiaoxin.sleep.common.BaseActivity;
import com.xiaoxin.sleep.dao.AppDao;
import com.xiaoxin.sleep.model.Event;
import com.xiaoxin.sleep.model.RxModelWithSy;
import com.xiaoxin.sleep.utils.ShellUtils;
import com.xiaoxin.sleep.utils.ToastUtils;
import com.xiaoxin.sleep.view.DialogWithCircularReveal;
import com.xiaoxin.sleep.view.SleepHeaderView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity
    implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

  List<AppInfo> other_list = new ArrayList<>();
  @BindView(R.id.fab) FloatingActionButton mFab;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.other_app_list) RecyclerView mOtherAppList;
  @BindView(R.id.search_view) MaterialSearchView mSearchView;

  private OtherAppListAdapter mOtherAdapter;
  private SleepHeaderView mSleepHeaderView;
  private List<AppInfo> mLists;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    EventBus.getDefault().register(this);
    ButterKnife.bind(this);
    initView();
    initData();
    isIgnoreBatteryOption(this);
    getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mViewComplete);
  }

  private void initData() {
    Observable<RxModelWithSy> observable = AppDao.getInstance().MainInit();
    observable .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RxModelWithSy>() {
      @Override
      public void accept(RxModelWithSy rxModelWithSy) throws Exception {

        initList(rxModelWithSy.mData, rxModelWithSy.mOtherAdapterData);
      }
    });
    mSleepHeaderView.getAdapter().setOnItemClickListener(this);
    mOtherAdapter.setOnItemClickListener(this);
    mSleepHeaderView.getAdapter().setOnItemLongClickListener(this);
    mOtherAdapter.setOnItemLongClickListener(this);
  }

  @Override protected void onResume() {
    super.onResume();
    AppDao.getInstance().SyncDisData(mActivity);
  }

  private void initList(List<AppInfo> mSList, List<AppInfo> mHeadList) {
    mSleepHeaderView.setList(mHeadList);
    if(mSList.size()>8)
    mOtherAdapter.setList(mSList.subList(8, mSList.size()));
  }

  private void WarnApp(AppInfo mAppInfo) {
    ShellUtils.execCommand(LibraryCons.make_app_to_enble + mAppInfo.packageName, true, true);
    mAppInfo.isWarn = true;
  }

  private void openApp(AppInfo mAppInfo) {
    goneloadDialog();
    Utils.openApp(MainActivity.this, mAppInfo.packageName);
    notifyDataSetChanged();
  }

  @OnClick(R.id.fab) public void onFabClicked() {
    sleepApp();
  }

  private void sleepApp() {
    showloadDialog("正在冷冻中");
    Observable.create(new ObservableOnSubscribe<RxModelWithSy>() {
      @Override
      public void subscribe(ObservableEmitter<RxModelWithSy> subscriber) throws Exception {
        //找出已经解冻的app进行冻结
        List<AppInfo> mData = mSleepHeaderView.getAdapter().getData();
        List<AppInfo> mOtherAdapterData = mOtherAdapter.getData();
        int mWarnApp = AppDao.getInstance().findWarnApp(mData,mOtherAdapterData);
        RxModelWithSy rxModelWithSy = new RxModelWithSy(mWarnApp, 0, mData, mOtherAdapterData);
        subscriber.onNext(rxModelWithSy);
        subscriber.onComplete();
      }
    }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RxModelWithSy>() {
      @Override
      public void accept(RxModelWithSy rxModelWithSy) throws Exception {
        notifyDataSetChanged();
        //缓存
        saveUserDis(rxModelWithSy.mData, rxModelWithSy.mOtherAdapterData);
        goneloadDialog();
        int num = rxModelWithSy.mWarnApp + rxModelWithSy.mWarnApp1;
        ToastUtils.showShortToast("睡眠" + num + "个");

      }
    });


  }

  private void saveUserDis(List<AppInfo> mData, List<AppInfo> mOtherAdapterData) {
    List<AppInfo> sList = new ArrayList<>();
    sList.addAll(mData);
    sList.addAll(mOtherAdapterData);
    AppDao.getInstance().saveUserSaveDisAppToDB(sList);
  }

  private void saveUserDis() {
    List<AppInfo> sList = new ArrayList<>();
    sList.addAll(mSleepHeaderView.getAdapter().getData());
    sList.addAll(mOtherAdapter.getData());
    AppDao.getInstance().saveUserSaveDisAppToDB(sList);
  }

  private void initView() {
    mSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSearchView.closeSearch();
        showloadDialog("加载中");

        final List<AppInfo> mListData = getListData();
        final TextView mTextView = (TextView) view.findViewById(R.id.suggestion_text);
        Observable.create(new ObservableOnSubscribe<AppInfo>() {
          @Override public void subscribe(ObservableEmitter<AppInfo> subscriber) throws Exception {
            AppInfo mAppInfo = null;
            String str = mTextView.getText().toString();
            for (AppInfo sAppInfo : mListData) {
              if (str.equals(sAppInfo.appName)) {
                mAppInfo = sAppInfo;
                break;
              }
            }
            WarnApp(mAppInfo);
            subscriber.onNext(mAppInfo);
            subscriber.onComplete();
          }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
            .observeOn(AndroidSchedulers.mainThread()).subscribe(mAppInfo -> openApp(mAppInfo));
      }
    });
    mSearchView.setEllipsize(true);
    mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        Snackbar.make(findViewById(R.id.main_layout), "Query: " + query, Snackbar.LENGTH_LONG)
            .show();
        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        //Do some magic
        return false;
      }
    });
    initRecylerView();
    setSupportActionBar(mToolbar);
  }

  private List<AppInfo> getListData() {
    return mLists;
  }

  private void initRecylerView() {
    initSleepList();
  }

  private void initSleepList() {
    //建立headView
    mSleepHeaderView = new SleepHeaderView(mActivity);
    mOtherAppList.setLayoutManager(new GridLayoutManager(this, 4));
    mOtherAdapter = new OtherAppListAdapter(other_list);
    mOtherAdapter.addHeaderView(mSleepHeaderView);
    mOtherAppList.setAdapter(mOtherAdapter);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    mSearchView.setMenuItem(item);
    return true;
  }

  @Override public void onItemClick(BaseQuickAdapter mBaseQuickAdapter, View mView, int position) {
    //打开app先解冻
    Observable.create(new ObservableOnSubscribe<AppInfo>() {
      @Override public void subscribe(ObservableEmitter<AppInfo> subscriber) throws Exception {
        List<AppInfo> mData = mBaseQuickAdapter.getData();
        AppInfo mAppInfo = mData.get(position);
        mAppInfo.open_num++;
        WarnApp(mAppInfo);
        subscriber.onNext(mAppInfo);
        subscriber.onComplete();
      }
    }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AppInfo>() {
      @Override public void accept(AppInfo mAppInfo) throws Exception {
        notifyDataSetChanged();
        openApp(mAppInfo);
        //缓存
        saveUserDis();
      }
    });
  }

  @Override public void onBackPressed() {
    if (mSearchView.isSearchOpen()) {
      mSearchView.closeSearch();
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_search) {

    } else {
      Intent mIntent = new Intent(mActivity, SettingActivity.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        startActivityWithOptions(mIntent);
      } else {
        startActivity(mIntent);
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void startActivityWithOptions(Intent intent) {
    ActivityOptions transitionActivity = null;
    transitionActivity = ActivityOptions.makeSceneTransitionAnimation(mActivity);
    startActivity(intent, transitionActivity.toBundle());
  }

  DialogWithCircularReveal dialog = null;

  @Override
  public boolean onItemLongClick(final BaseQuickAdapter mBaseQuickAdapter, View mView, int mI) {
    final List<AppInfo> mData = mBaseQuickAdapter.getData();
    final AppInfo mAppInfo = mData.get(mI);
    View mInflate = View.inflate(MainActivity.this, R.layout.dialog_dis_content_normal, null);
    TextView uninstall_app = (TextView) mInflate.findViewById(R.id.fuc_delete);
    TextView fuc_remoove = (TextView) mInflate.findViewById(R.id.fuc_remoove);
    TextView fuc_wake = (TextView) mInflate.findViewById(R.id.fuc_wake);
    TextView app_title = (TextView) mInflate.findViewById(R.id.app_title);
    ImageView app_icon = (ImageView) mInflate.findViewById(R.id.app_icon);
    app_title.setText(mAppInfo.appName);
    Glide.with(MainActivity.this).load(mAppInfo.file_path).into(app_icon);
    //解冻
    fuc_wake.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
        showloadDialog("操作中");
        Observable.create(new ObservableOnSubscribe<String>() {
          @Override public void subscribe(ObservableEmitter<String> mObservableEmitter)
              throws Exception {
            WarnApp(mAppInfo);
            mObservableEmitter.onNext("");
            mObservableEmitter.onComplete();
          }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
          @Override public void accept(String mS) throws Exception {
            goneloadDialog();
            notifyDataSetChanged();
          }
        });
      }
    });
    //移出列表
    fuc_remoove.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        dialog.dismiss();
        showloadDialog("操作中");
        Observable.create(new ObservableOnSubscribe<AppInfo>() {
          @Override public void subscribe(ObservableEmitter<AppInfo> mObservableEmitter)
              throws Exception {
            WarnApp(mAppInfo);
            mBaseQuickAdapter.getData().remove(mAppInfo);
            mObservableEmitter.onNext(mAppInfo);
            mObservableEmitter.onComplete();
          }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AppInfo>() {
          @Override public void accept(AppInfo mAppInfo) throws Exception {
            notifyDataSetChanged();
            //缓存
            saveUserDis();
            goneloadDialog();
          }
        });
      }
    });
    uninstall_app.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
        showloadDialog("删除中");
        Observable.create(new ObservableOnSubscribe<String>() {
          @Override public void subscribe(ObservableEmitter<String> mObservableEmitter)
              throws Exception {
            ShellUtils.execCommand(LibraryCons.uninstall_app + mAppInfo.packageName, true, true);
            mBaseQuickAdapter.getData().remove(mAppInfo);
            mObservableEmitter.onNext("");
            mObservableEmitter.onComplete();
          }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
          @Override public void accept(String mS) throws Exception {
            notifyDataSetChanged();
            goneloadDialog();
            //缓存
            saveUserDis();
          }
        });
      }
    });
    dialog = new DialogWithCircularReveal(MainActivity.this, mInflate);
    dialog.setRevealview(R.id.myView);
    dialog.showDialog();
    return true;
  }

  private void notifyDataSetChanged() {
    mSleepHeaderView.getAdapter().notifyDataSetChanged();
    mOtherAdapter.notifyDataSetChanged();
  }



  @Subscribe(threadMode = ThreadMode.MAIN) public void onAppDaoMessage(Event msg) {
    switch (msg.getCurrentDay()) {
      case Event.getDisList:
        mSearchView.setSuggestions(AppDao.getInstance().getSuggestions());
        mLists = msg.list;
        //排序取出8个使用频率比较高的
        List<AppInfo> mAppInfos = AppDao.getInstance().sortAppList(mLists);
        //赋值给头部
        initList(mLists, mAppInfos);
        break;
      case Event.NOTIFYADAPTER:
        List<AppInfo> mList = msg.list;
        List<AppInfo> headList = AppDao.getInstance().sortAppList(mList);
        initList(mList, headList);
        notifyDataSetChanged();
        break;
    }
  }

  @Override protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }
}
