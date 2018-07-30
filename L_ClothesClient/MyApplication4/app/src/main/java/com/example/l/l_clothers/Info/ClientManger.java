package com.example.l.l_clothers.Info;

import com.example.l.l_clothers.StaticValues.StaticValues;

/**
 * Created by L on 2017/5/30.
 */

public class ClientManger {
    public static boolean isOnline;// 记录在线状态
    public static String clientId = "";// 本机登录Id，都是static的
    public static String ClientPhone="";//登录账号
    public static String clientBirthday = "";
    public static String personSignature = "";
    public static String clientName = "";// 本机登录用户名
    public static String clientSex = "";
    public static String clientjob="";
    public static String address="";//存放连接地址

    public static boolean isconnect=false;//判断是否连接上了蓝牙

    public static boolean isRing;//是否提示响铃
    public static boolean isVibration;//是否开启震动

    public static String complete="false";//判断是否完成了个人内容的完善，能不能进行其他操作，否则的话点击其他地方会出现弹窗



}
