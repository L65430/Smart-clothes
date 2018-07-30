package com.example.l.l_clothers.StaticValues;

/**
 * Created by L on 2017/5/10.
 */
public interface JSONKeys {
    String msgType = "msgType";// 消息类型
    String userId = "userId";// 发送者Id
    String friendId = "friendId";// 接收者Id
    String friendName = "friendName";// 好友名

    String userphone="userphone";
    String sendTime = "sendTime";// 发送时间
    String msgCotent = "msgCotent";// 聊天信息-文本信息
    String voiceTime = "voiceTime";// 聊天信息-语音信息长度
    String voicePath = "voicePath";// 聊天信息-语音文件路径
    String imagePath = "imagePath";// 聊天信息-图片路径

    String userName = "userName";// 用户名
    String userSex = "userSex";// 用户性别
    String userBirthday = "userBirthday";// 用户生日
    String userPasswd = "userPasswd";// 登录密码
    String personSignature = "personSignature";// 个性签名

    String userHeadPath = "userHeadPath";// 用户头像路径

    String checknumber="checknumber";
    String userwork="job";//表示是病人还是医生

    String friendIdList = "friendIdList";// 好友Id列表
    String doctorlist="doctorlist";
    String personaldoctor="personaldoctor";//表示这个人的医生是谁


    String complete="complete";
    String doctorname="doctorname";
    String doctorinfo="doctorinfo";

    String firstA="firstA";
    String firstB="firstB";
    String secondA="secondA";
    String secondB="secondB";
    String thirdA="thirdA";
    String thirdB="thirdB";

    String positionnumber="positionnumber";//每个动作的编号
    String time="time";//每个动作的次数

    String positionlist="positionlist";

}
