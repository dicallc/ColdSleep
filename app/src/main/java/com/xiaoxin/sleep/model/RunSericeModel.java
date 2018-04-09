package com.xiaoxin.sleep.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class RunSericeModel {
  
	private String appLabel;    //Ӧ�ó����ǩ
	private Drawable appIcon ;  //Ӧ�ó���ͼ��
	private String serviceName  ;  //��Service������
	private String pkgName ;    //Ӧ�ó�������Ӧ�İ���
	
	private Intent intent ;  //��Service�������Ӧ��Intent
	
	private int pid ;  //��Ӧ�ó������ڵĽ��̺�
	private String processName ;  // ��Ӧ�ó������ڵĽ�����
	
	public RunSericeModel(){}
	
	public String getAppLabel() {
		return appLabel;
	}
	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

    public String getServiceName(){ 
    	return serviceName ;
    }
    public void setServiceName(String serviceName){
    	this.serviceName = serviceName ;
    }
	
	public String getPkgName(){
		return pkgName ;
	}
	public void setPkgName(String pkgName){
		this.pkgName=pkgName ;
	}	
	
	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	
}
