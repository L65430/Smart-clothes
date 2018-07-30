package com.example.l.l_clothers.StaticValues;

/**
 * Created by L on 2017/5/9.
 */
//客户端和服务器端之间的通讯
public interface CSKeys {
    //返回数据也是一样的
    int REGISTER = 0;// 注册
    int REGISTER_SUCCESS = 1;// 注册成功
    int REGISTER_FAILED = 2;// 注册失败

    int LOGIN = 3;// 登录
    int LOGIN_SUCCESS = 4;// 登录成功
    int LOGIN_FAILED = 5;// 登录失败

    int LOGIN_SUPER_HEAD = 6;// 登录，用户手机有头像
    int LOGIN_SUPER_NOHEAD = 7;//登录，用户没有头像，从服务器获取

    int FIND_PASSWD = 8;// 找回密码
    int FIND_PASSWD_SUCCESS = 9;// 找回成功
    int FIND_PASSWD_FAILED = 10;// 找回密码失败

    int RESET_PASSWD = 11;// 重置密码
    int RESET_PASSWD_SUCCESS = 12;// 重置密码成功
    int RESET_PASSWD_FAILED = 13;// 重置密码失败

    int RESET_SEX=14;//重置性别
    int RESET_AGE=15;//重置年龄

    int RESET_HEAD = 17;// 修改头像
    int RESET_HEAD_SUCCESS = 18;// 修改头像成功
    int RESET_HEAD_FAILED = 19;// 修改头像失败

    int LOGIN_SUPER_FAILED = 20;// 失败

    int CONNECT_DOWN = 21;//和服务器的连接断开

    int CHATING_TEXT_MSG = 22;// 聊天信息-文本信息
    int CHATING_VOICE_MSG = 23;// 聊天信息-语音信息
    int CHATING_IMAGE_MSG = 24;// 聊天信息-图片信息

    int GETA_FRIEND_INFO_HEAD = 25;//得到一个陌生人的具体信息(本地有图像,服务器不用发送头像)
    int GETA_FRIEND_INFO_NOHEAD = 26;//得到一个陌生人的具体信息(本地无图像,服务器要发头像),就是说这个时候请求添加一个陌生人为好友了


    int RESET_BIRTHDAY=27;
    int RESET_USERNAME=28;
    int RESET_SIGNATUE=29;
    int CHECK_NUMBER=30;//客户端发送验证码给服务器端

    int CHECK_TURE=31;
    int CHECK_WRONG=32;

    int GET_CHECK_NUMBER=33;//通知获取验证码

    int GET_DOCTOR_LIST=34;//通知获得医生列表
    int GET_FRIEND_ID_LIST=35;//获取好友id列表

    int ADD_FRIEND=36;
    int ADD_FRIEND_SUCCESS=37;
    int ADD_FRIEND_FAILED = 38;
    int DELETE_FRIEND = 39;

    int GET_DOCTOR_INFO=40;
    int TELL_SEVER_COMPLETE=41;
    int GET_SERVER_COMPLETE=42;

    int FRIEND_ID_LIST = 43;

    int GET_RECORD=44;//获得开始时的数据
    int PUT_RECORD=45;//上传数据

    int SEND_FORMATION_TIME=46;

    int ASK_FOR_GET_CHECK_NUMBER=47;

    int ASK_FOR_PUT_CHECK_NUMBER=48;

    int LOGIN_SUCCESS_DOCTOR=49;//医生端登录成功

    int GET_RECORD_LIST=50;

}
