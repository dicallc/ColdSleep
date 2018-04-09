package com.xiaoxin.sleep.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by jiangdikai on 2017/10/14.
 * 数据库已冷冻表
 */
@Entity
public class UserAppInfoBean {
    @Id
    public Long id;
    public String appName;
    public String packageName;
    public String file_path;
    public int open_num;


    public UserAppInfoBean(String appName, String packageName, String file_path, int open_num) {
        this.appName = appName;
        this.packageName = packageName;
        this.file_path = file_path;
        this.open_num = open_num;
    }


    @Generated(hash = 1535016616)
    public UserAppInfoBean(Long id, String appName, String packageName, String file_path,
            int open_num) {
        this.id = id;
        this.appName = appName;
        this.packageName = packageName;
        this.file_path = file_path;
        this.open_num = open_num;
    }


    @Generated(hash = 452371196)
    public UserAppInfoBean() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getAppName() {
        return this.appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getPackageName() {
        return this.packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public String getFile_path() {
        return this.file_path;
    }


    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }


    public int getOpen_num() {
        return this.open_num;
    }


    public void setOpen_num(int open_num) {
        this.open_num = open_num;
    }
}
