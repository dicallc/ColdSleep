package com.xiaoxin.sleep.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoxin.library.model.AppInfo;
import com.xiaoxin.sleep.R;
import com.xiaoxin.sleep.adapter.AppListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangdikai on 2017/7/26.
 */

public class SleepHeaderView extends LinearLayout {

  private List<AppInfo> list = new ArrayList<>();
  private Context mContext;
  @BindView(R.id.app_list) RecyclerView mAppList;
  private AppListAdapter mAdapter;

  public AppListAdapter getAdapter() {
    return mAdapter;
  }

  public SleepHeaderView(Context context) {
    this(context, null);
  }

  public SleepHeaderView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SleepHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    LayoutInflater.from(context).inflate(R.layout.sleep_cold_list_header_view, this);
    ButterKnife.bind(this);
    initView();
  }

  private void initView() {
    initAppList();
  }

  private void initAppList() {
    GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 4);
    mAppList.setLayoutManager(mGridLayoutManager);
    mAdapter = new AppListAdapter(list);
    mAppList.setAdapter(mAdapter);
  }

  public void setList(List<AppInfo> mList) {
    mAdapter.getData().clear();
    mAdapter.getData().addAll(mList);
    mAdapter.notifyDataSetChanged();
  }
}
