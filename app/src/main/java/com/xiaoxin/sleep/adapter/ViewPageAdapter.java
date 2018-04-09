package com.xiaoxin.sleep.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ViewPageAdapter extends FragmentPagerAdapter {
  private String[] titles;
  private List<Fragment> list;

  public ViewPageAdapter(FragmentManager fm, String[] mTitles, List<Fragment> mList) {
    super(fm);
    titles = mTitles;
    list = mList;
  }

  public ViewPageAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public Fragment getItem(int position) {
    return list.get(position);
  }

  @Override public int getCount() {
    return list.size();
  }

  //重写这个方法，将设置每个Tab的标题
  @Override public CharSequence getPageTitle(int position) {
    return titles[position];
  }
}
