package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.l.l_clothers.StaticValues.StaticValues;

import java.util.Date;

/**
 * Created by L on 2017/5/9.
 */
public class SharedPreferenceUtil {
    SharedPreferences sp;
    SharedPreferences.Editor editor;//用来编辑;

    public SharedPreferenceUtil(Context context){
        sp=context.getSharedPreferences(StaticValues.sharePreName,Context.MODE_PRIVATE);
        editor=sp.edit();
    }

    //保存键
    public void SaveValues(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }

    //获取值
    public String getvalues(String key){
        return sp.getString(key,"");
    }

    // 是否签到
    public void setIsReport(boolean isReport) {
        editor.putBoolean("isReport", isReport);
        editor.commit();
    }

    public boolean getIsReport() {
        return sp.getBoolean("isReport", false);
    }

    //签到日期，因为一天只签到一次
    public void setReportDate(){
        editor.putString("ReportDate", new Date() + "");
        editor.commit();
    }


    public String getReportDate(){
        return sp.getString("ReportDate", "");
    }
    //性别
    public void setsex(boolean sex){
        editor.putBoolean("sex",sex);
        editor.commit();
    }

    public boolean getsex(){
        return sp.getBoolean("sex",true);
    }

    //姓名
    public void setname(String name){
        editor.putString ("name",name);
        editor.commit();
    }

    public String getname(){
        return sp.getString("name","");
    }

    //是否有提示音
    public void setIsRing(boolean isRing){
        editor.putBoolean("isRing",isRing);
        editor.commit();
    }

    public boolean getIsRing(){
        return sp.getBoolean("isRing",true);
    }


    //是否有震动
    public void setIsVibration(boolean isVibration){
        editor.putBoolean("isVibration",isVibration);
        editor.commit();
    }

    public boolean getIsVibration(){
        return sp.getBoolean("isVibration",true);
    }

    //是否有通知栏通知
    public void setIsNotice(boolean isNotice){
        editor.putBoolean("isNotice",isNotice);
        editor.commit();
    }

    public boolean getIsNotice(){
        return sp.getBoolean("isNotice",true);
    }




}
