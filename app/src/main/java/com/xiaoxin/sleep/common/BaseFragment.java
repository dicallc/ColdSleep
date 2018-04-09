package com.xiaoxin.sleep.common;

import android.support.v4.app.Fragment;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Administrator on 2017/7/24 0024.
 */

public class BaseFragment extends Fragment {
  private MaterialDialog mDialog;

  protected void showloadDialog(String title) {
    mDialog = new MaterialDialog.Builder(getActivity()).title(title)
        .content("请深呼吸休息一下")
        .progress(true, 0)
        .progressIndeterminateStyle(true)
        .show();
  }

  protected void goneloadDialog() {
    if (null != mDialog) if (mDialog.isShowing()) mDialog.dismiss();
  }
}
