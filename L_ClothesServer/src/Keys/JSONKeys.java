package Keys;

/**
 * Created by L on 2017/5/16.
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
    String checknumber="checknumber";//验证码

    String userwork="job";//表示是病人还是医生
    String complete="complete";




    String friendIdList = "friendIdList";// 好友Id列表
    String doctorlist="doctorlist";

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