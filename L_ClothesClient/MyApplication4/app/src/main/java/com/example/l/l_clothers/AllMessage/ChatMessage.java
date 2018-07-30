package com.example.l.l_clothers.AllMessage;

/**
 * Created by L on 2017/6/4.
 */

public class ChatMessage {
    public int msgType;// 消息类型，文本消息，图片消息，语音消息(可能后面还要加上视频消息，待定）
    public String msgJson;// 包含非文本信息的解释
    public String sendTime;// 消息接收或发送的时间
    public int isGetted;// 是否是别人发送的消息,根据这个判断信息显示在屏幕左侧或是右侧(1是别人发的）这个包是通过转换CSmessage而来的，本来是没有的
    public int isLooked;// 消息是否被查看 (0 = false , 1 = true)


    @Override
    public String toString() {
        return "ChatMessage [msgType=" + msgType + ", msgJson=" + msgJson
                + ", sendTime=" + sendTime + ", isGetted=" + isGetted
                + ", isLooked=" + isLooked + "]";
    }

}
