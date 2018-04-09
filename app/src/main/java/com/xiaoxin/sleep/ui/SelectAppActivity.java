package com.xiaoxin.sleep.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.socks.library.KLog;
import com.xiaoxin.library.common.LibraryCons;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.library.utils.SpUtils;
import com.xiaoxin.sleep.R;
import com.xiaoxin.sleep.adapter.ViewPageAdapter;
import com.xiaoxin.sleep.common.BaseActivity;
import com.xiaoxin.sleep.dao.AppDao;
import com.xiaoxin.sleep.dao.SelectAppDao;
import com.xiaoxin.sleep.model.Event;
import com.xiaoxin.sleep.utils.ToastUtils;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 选择App界面
 */
public class SelectAppActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.tab_layout) TabLayout mTabLayout;
  @BindView(R.id.viewpage) ViewPager mViewpage;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.appbar) AppBarLayout mAppbar;
  @BindView(R.id.fab) FloatingActionButton mFab;
  private List<Fragment> list = new ArrayList<>();
  private ViewPageAdapter adapter;

  private String arr[] = { "App", "系统" };
  private AppListFragment mAppListFragment;
  private boolean mIsFirst;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (initLocalData()) return;
    setContentView(R.layout.activity_select_app);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
    initView();
    initData();
    initTransition();
  }

  private void initView() {
    initTab();
    mToolbar.setTitle("");
    setSupportActionBar(mToolbar);
    mFab.setOnClickListener(this);
  }

  private void initTransition() {
    Transition transitionSlideRight = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      transitionSlideRight =
          TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
      getWindow().setEnterTransition(transitionSlideRight);
    }
  }

  private boolean initLocalData() {
    String action = getIntent().getStringExtra(LibraryCons.ACTION);

    //第一次为false，点击冷冻后既不是第一次
    mIsFirst = (boolean) SpUtils.getParam(mActivity, LibraryCons.NotFIRST, false);
    if (mIsFirst && !LibraryCons.ACTION_OPEN.equals(action)) {
      Intent mIntent = new Intent(mActivity, MainActivity.class);
      startActivity(mIntent);
      finish();
      return true;
    }
    return false;
  }

  private void initData() {
    showloadDialog();
  }

  private void initTab() {
    mTabLayout.addTab(mTabLayout.newTab().setText("第三方应用"));
    mTabLayout.addTab(mTabLayout.newTab().setText("系统应用"));
    mAppListFragment = new AppListFragment();
    list.add(mAppListFragment);
    list.add(new SystemAppFragment());
    //ViewPager的适配器
    adapter = new ViewPageAdapter(getSupportFragmentManager(), arr, list);
    mViewpage.setAdapter(adapter);
    //绑定
    mTabLayout.setupWithViewPager(mViewpage);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onAppDaoMessage(Event msg) {
    switch (msg.getCurrentDay()) {
      case Event.MONDAY:
        KLog.e("MONDAY");
        goneloadDialog();
        break;
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.fab:
        showloadDialog("正在冷冻中");
        SelectAppDao.doSleep(mAppListFragment.getData(), doSleepAfter());
        break;
    }
  }

  /**
   * 睡眠之后回调
   */
  private Consumer<List<AppInfo>> doSleepAfter() {
    return new Consumer<List<AppInfo>>() {
      @Override public void accept(List<AppInfo> part) throws Exception {
        //缓存数据
        AppDao.getInstance().saveUserSaveDisAppToDB(part);
        EventBus.getDefault().post(new Event(Event.NOTIFYADAPTER, part));
        goneloadDialog();
        ToastUtils.showShortToast("冷冻成功");
        if (mIsFirst) {
          finish();
        } else {
          Intent mIntent = new Intent(SelectAppActivity.this, MainActivity.class);
          startActivity(mIntent);
        }
      }
    };
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_select, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    KLog.e("onOptionsItemSelected");
    if (item.getItemId() == R.id.action_colded) {
      mAppListFragment.selectSleepedApp();
    } else if (item.getItemId() == R.id.action_cold) {
      mAppListFragment.filterEnableToHeader();
    }
    return super.onOptionsItemSelected(item);
  }
}
