package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by L on 2017/6/2.
 */

public class ClientUtils {
    public static ClientUtils clientUtils;
    public static ClientUtils getInstance(){
        if(clientUtils==null){
            synchronized (ClientUtils.class){
                if(clientUtils==null){
                    clientUtils=new ClientUtils();
                }
            }
        }
        return clientUtils;
    }

    //通过phone登录
    public void getClientuserphone(Context context){
        SharedPreferenceUtil util=new SharedPreferenceUtil(context);
        ClientManger.ClientPhone=util.getvalues(StaticValues.userPhone);
        //通信还是用id来比较好
        ClientManger.clientId=util.getvalues(ClientManger.ClientPhone);
        //我觉得id可以暂定
    }

    //保存当前用户信息
    public void saveClientInfo(Context context, Message message){
        ClientManger.isOnline=true;
        CSmessage cSmessage=(CSmessage)message.obj;
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        //从服务器获取信息
        if(message.what== CSKeys.LOGIN_SUPER_NOHEAD){
            ImageUtil.getInstance().saveImage(context,cSmessage.msgBytes,StaticValues.USER_HEADPATH+ClientManger.ClientPhone+".png");
        }
        ClientManger.clientId=jsonObject.getString(JSONKeys.userId);
        ClientManger.clientName=jsonObject.getString(JSONKeys.userName);
        ClientManger.clientSex=jsonObject.getString(JSONKeys.userSex);
        ClientManger.clientBirthday =jsonObject.getString(JSONKeys.userBirthday);
        ClientManger.personSignature = jsonObject.getString(JSONKeys.personSignature);
        ClientManger.clientjob=jsonObject.getString(JSONKeys.userwork);

        new SharedPreferenceUtil(context).SaveValues(ClientManger.ClientPhone,ClientManger.clientId);
    }

    //获得当前时间
    public static String getNowTime()
    {
        SimpleDateFormat format=new SimpleDateFormat("yy-MM-dd HH:mm:ss");//把时间显示成这样的形式
        String date=format.format(new Date());
        return date;
    }


}
