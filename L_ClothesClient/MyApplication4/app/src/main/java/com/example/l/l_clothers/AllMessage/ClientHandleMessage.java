package com.example.l.l_clothers.AllMessage;

/**
 * Created by L on 2017/6/6.
 */

public class ClientHandleMessage {
    private String msgJson;// 包含非文本信息详情的解释
    //	private byte[] msgBytes;// 非文本信息
    private boolean isGetted;// 是否是别人发送的消息,根据这个判断信息显示在屏幕左侧或是右侧
    public String getMsgJson() {
        return msgJson;
    }

    public void setMsgJson(String msgJson) {
        this.msgJson = msgJson;
    }

    public boolean isGetted() {
        return isGetted;
    }

    public void setGetted(boolean isGetted) {
        this.isGetted = isGetted;
    }


}

