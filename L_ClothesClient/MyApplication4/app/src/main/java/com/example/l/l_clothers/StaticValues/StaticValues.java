package com.example.l.l_clothers.StaticValues;

import com.example.l.l_clothers.Utils.SharedPreferenceUtil;

/**
 * Created by L on 2017/5/9.
 */
public interface StaticValues {

    int SESSION_OPENED=102;//表示和服务器连接成功
    String SERVER_IP="115.28.34.148";//肯定可以直接连接外网的
    //	String SERVER_IP = "10.50.44.130";
    int SERVER_PORT = 7070;

    String VOICEPATH = "/sdcard/L_cloth/voiceRecord/";// 存放语音消息的文件夹
    String IMAGEPATH = "/sdcard/L_cloth/imageRecord/";// 存放图片消息的文件夹，图片消息都放在这里面的//肯定是需要sd卡的
    String USER_HEADPATH = "/sdcard/L_cloth/userHead/";// 用户头像文件夹
    String MSG_TEXT = "/sdcard/L_cloth/msgText/";//导出的text消息文件


    String sharePreName="com.l";
    String userPhone="userPhone";
    String userPasswd="userPasswd";
    String userjob="userjob";

    int FACE_GETTED = 100; // 点击表情
    int DEL_EDIT_TEXT = 3333;//删除

    int VOICE_REC_TIME = 101;// 录音时，实时更新录音时间



}
