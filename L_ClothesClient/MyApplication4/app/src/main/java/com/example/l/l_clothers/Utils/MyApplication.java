package com.example.l.l_clothers.Utils;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.l.l_clothers.Info.ClientManger;

/**
 * Created by L on 2017/6/6.
 */

public class MyApplication extends Application {
    private static MyApplication myApplication;
    SharedPreferenceUtil mSpUtil;

    //和定位有关的
    public LocationClient mLocationClient;
    BDLocation mlocation;//BDLocation获取定位地址

    public synchronized static MyApplication getInstance()
    {
        return myApplication;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        myApplication=this;
        SDKInitializer.initialize(this);//要先加这一句话，用来解析sdk
        initData();
    }


    private void initData()
    {
        mSpUtil=new SharedPreferenceUtil(this);
    }

    public synchronized SharedPreferenceUtil getSpUtil()
    {
        if(mSpUtil==null)
        {
            mSpUtil=new SharedPreferenceUtil(this);
        }
        return mSpUtil;
    }

    public synchronized BDLocation getMyLocation()
    {
        return mlocation;
    }

}

