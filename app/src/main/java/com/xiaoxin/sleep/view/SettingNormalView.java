package com.xiaoxin.sleep.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxin.sleep.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jiangdikai on 2017/7/26.
 */

public class SettingNormalView extends LinearLayout {
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.sub_title_name)
    TextView subTitleName;
    private String title_text;
    private String sub_title_text;
    public void setTitleText(String str){
        titleName.setText(str);
    }
    public void setSubTitleText(String str){
        subTitleName.setText(str);
    }
    public SettingNormalView(Context context) {
        this(context, null);
    }

    public SettingNormalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingNormalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.setting_normal_view, this);
        ButterKnife.bind(this);
        init(attrs, defStyleAttr);
        titleName.setText(title_text);
        subTitleName.setText(sub_title_text);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.setting_normal, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.setting_normal_title:
                    title_text = a.getString(
                            R.styleable.setting_normal_title);
                    break;
                case R.styleable.setting_normal_sub_title:
                    sub_title_text = a.getString(
                            R.styleable.setting_normal_sub_title);
                    break;
            }
        }

        a.recycle();
    }
}
