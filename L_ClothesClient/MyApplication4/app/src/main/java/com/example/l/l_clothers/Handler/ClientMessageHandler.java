package com.example.l.l_clothers.Handler;

import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.serialization.ObjectSerializationEncoder;
import org.apache.mina.filter.executor.IoEventSizeEstimator;

import java.util.zip.InflaterOutputStream;

/**
 * Created by L on 2017/5/10.
 */
//客户端处理业务逻辑
public class  ClientMessageHandler extends IoHandlerAdapter{
    private Handler handler;
    public ClientMessageHandler(Handler handler){
        this.handler=handler;
    }

    public void sessionCreated(IoSession ioSession)throws Exception{
        super.sessionCreated(ioSession);
    }

    public void sessionOpened(IoSession ioSession)throws Exception{
        super.sessionOpened(ioSession);
        Message message=new Message();
        message.what= StaticValues.SESSION_OPENED;
        handler.sendMessage(message);//一打开就用这个去发送这个消息,这可不是发送到服务器端
    }

    public void sessionClosed(IoSession ioSession)throws Exception{
        super.sessionClosed(ioSession);
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {

    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        super.exceptionCaught(session, cause);
        cause.printStackTrace();
    }


    public void messageReceived(IoSession ioSession,Object message) throws Exception{
        super.messageReceived(ioSession,message);
        CSmessage cSmessage=(CSmessage)message;
        JSONObject msgJson= JSON.parseObject(cSmessage.msgJson);//转码
        Message message1=new Message();
        Log.i("--", "客户端收到： "+cSmessage.toString());
        if(cSmessage.symbol=='+') {//文本信息
            switch (msgJson.getInteger(JSONKeys.msgType))//还是要添加用户id，在后面的聊天中会用到
            {
                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    message1.what=CSKeys.GETA_FRIEND_INFO_HEAD;
                    message1.obj=cSmessage;
                    break;

                case CSKeys.CHECK_TURE:
                    message1.what=CSKeys.CHECK_TURE;
                    break;
                case CSKeys.CHECK_WRONG:
                    message1.what=CSKeys.CHECK_WRONG;
                    break;
                case CSKeys.REGISTER_SUCCESS:
                    message1.what = CSKeys.REGISTER_SUCCESS;
                    break;//发送给handler
                case CSKeys.REGISTER_FAILED:
                    message1.what = CSKeys.REGISTER_FAILED;
                    break;
                case CSKeys.LOGIN_SUPER_HEAD:
                    message1.what = CSKeys.LOGIN_SUPER_HEAD;
                    message1.obj = cSmessage;
                    break;
                case CSKeys.LOGIN_SUPER_FAILED:
                    message1.what = CSKeys.LOGIN_SUPER_FAILED;
                    break;
                case CSKeys.LOGIN_SUCCESS:
                    message1.what = CSKeys.LOGIN_SUCCESS;
                    break;
                case CSKeys.LOGIN_SUCCESS_DOCTOR:
                    message1.what=CSKeys.LOGIN_SUCCESS_DOCTOR;
                    break;
                case CSKeys.LOGIN_FAILED:
                    message1.what = CSKeys.LOGIN_FAILED;
                    break;
                case CSKeys.CONNECT_DOWN:
                    message1.what = CSKeys.CONNECT_DOWN;
                    break;
                case CSKeys.FIND_PASSWD_SUCCESS:
                    message1.what = CSKeys.FIND_PASSWD_SUCCESS;
                    break;
                case CSKeys.FIND_PASSWD_FAILED:
                    message1.what = CSKeys.FIND_PASSWD_SUCCESS;
                    break;
                case CSKeys.CHATING_TEXT_MSG:
                    // 文本信息:
                    message1.what = CSKeys.CHATING_TEXT_MSG;
                    message1.obj = cSmessage;//只有这些要重新传给mainactivity的东西才有重新传回来的需要
                    break;

                case CSKeys.ADD_FRIEND_SUCCESS:
                    message1.what=CSKeys.ADD_FRIEND_SUCCESS;
                    break;
                case CSKeys.ADD_FRIEND_FAILED:
                    message1.what=CSKeys.ADD_FRIEND;
                    break;
                case CSKeys.FRIEND_ID_LIST:
                    message1.what=CSKeys.FRIEND_ID_LIST;
                    message1.obj=cSmessage.msgJson;
                    break;//获得好友列表
                case CSKeys.GET_DOCTOR_LIST://注意是“+"的，还是认为是文本
                    message1.what=CSKeys.GET_DOCTOR_LIST;
                    message1.obj=cSmessage;
                    break;

                case CSKeys.GET_SERVER_COMPLETE:
                    message1.what=CSKeys.GET_SERVER_COMPLETE;
                    message1.obj=cSmessage;
                    break;

                case CSKeys.GET_RECORD:
                    message1.what=CSKeys.GET_RECORD;
                    message1.obj=cSmessage;
                    break;

                case CSKeys.GET_RECORD_LIST:
                    message1.what=CSKeys.GET_RECORD_LIST;
                    message1.obj=cSmessage;
                    break;

            }
        }else if (cSmessage.symbol=='-'){//非纯文本
            switch (msgJson.getInteger(JSONKeys.msgType)){
                case CSKeys.LOGIN_SUPER_NOHEAD:
                    message1.what = CSKeys.LOGIN_SUPER_NOHEAD;
                    message1.obj =cSmessage;//包含用户头像（本地没有头像，
                    break;//本地有头像

                case CSKeys.GET_DOCTOR_INFO:
                    message1.what=CSKeys.GET_DOCTOR_INFO;
                    message1.obj=cSmessage;
                    break;//因为要显示头像什么的

                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    message1.what = CSKeys.GETA_FRIEND_INFO_NOHEAD;
                    message1.obj =cSmessage;//好友的个人信息,包含好友图像,本地无头像，服务器要发头像，所以整个都需要
                    break;

                case CSKeys.CHATING_IMAGE_MSG:
                    // 图片消息:
                    message1.what = CSKeys.CHATING_IMAGE_MSG;
                    message1.obj = cSmessage;
                    break;

                case CSKeys.CHATING_VOICE_MSG:
                    // 语音消息:
                    message1.what = CSKeys.CHATING_VOICE_MSG;
                    message1.obj =cSmessage;
                    break;

                case CSKeys.RESET_HEAD:
                    message1.what = CSKeys.RESET_HEAD;
                    message1.obj = cSmessage;
                    break;

                case CSKeys.ADD_FRIEND:
                    // 添加好友信息（别人添加我）:
                    message1.what = CSKeys.ADD_FRIEND;
                    message1.obj = cSmessage;
                    break;
            }
        }
        handler.sendMessage(message1);
    }

    @Override
    public void messageSent(IoSession ioSession, Object message) throws Exception {
        super.messageSent(ioSession,message);
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        super.inputClosed(ioSession);
    }
}
