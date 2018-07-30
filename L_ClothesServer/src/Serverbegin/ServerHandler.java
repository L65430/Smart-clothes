package Serverbegin;

import AllMessage.CSmessage;
import Keys.CSKeys;
import Keys.JSONKeys;
import Servertools.ManagerClientSession;
import Servertools.ManagerIdSessions;
import Servertools.ServerUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.activation.ServerTool;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

/**
 * Created by L on 2017/5/30.
 */
public class ServerHandler extends IoHandlerAdapter{
    private IoSession session;

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }


    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }

    public void messageReceived(IoSession session,Object message)throws Exception{
       super.messageReceived(session,message);
        this.session=session;
        CSmessage cSmessage=(CSmessage)message;
        JSONObject msgJson=JSON.parseObject(cSmessage.msgJson);
        if(cSmessage.symbol=='+'){
            switch (msgJson.getInteger(JSONKeys.msgType)){
                case CSKeys.RESET_SIGNATUE:
                    // 修改个人签名:
                    ServerUtils.getInstance().handleResetUserInfo(cSmessage);
                    break;

                //登录
                case CSKeys.LOGIN:
                    // 用户登录:
                    ServerUtils.getInstance().handleLogin(session, cSmessage);
                    break;
                case CSKeys.LOGIN_SUPER_HEAD:
                    // 用户超级登录有头像:
                    ServerUtils.getInstance().handleLoginSuper(session, cSmessage);
                    break;
                case CSKeys.LOGIN_SUPER_NOHEAD:
                    // 用户超级登录无头像:
                    ServerUtils.getInstance().handleLoginSuper(session, cSmessage);
                    break;

                //通知服务器端发送验证码
                case CSKeys.GET_CHECK_NUMBER:
                    ServerUtils.getInstance().handleSendCheckNumber(session,cSmessage);
                    break;

                //判断验证码是否正确
                case CSKeys.CHECK_NUMBER:
                    ServerUtils.getInstance().handleCheckNumber(session,cSmessage);
                    break;

                case CSKeys.ADD_FRIEND:
                    ServerUtils.getInstance().handleAddFriend(session, cSmessage);
                    break;

                //通知已经完成了
                case CSKeys.TELL_SEVER_COMPLETE:
                    ServerUtils.getInstance().handleComplete(cSmessage);
                    break;

                //通知获得完成消息
                case CSKeys.GET_SERVER_COMPLETE:
                    ServerUtils.getInstance().handleGetcomplete(session,cSmessage);
                    break;

                case CSKeys.GET_DOCTOR_LIST:
                    ServerUtils.getInstance().handleDoctorList(session,cSmessage);
                    break;

                //根据id获取信息
                case CSKeys.GET_DOCTOR_INFO:
                    ServerUtils.getInstance().handleGetDoctorInfo(session,cSmessage);
                    break;


                case CSKeys.GET_RECORD:
                    ServerUtils.getInstance().handleGetRecord(session,cSmessage);
                    break;

                case CSKeys.PUT_RECORD:
                    ServerUtils.getInstance().handlePutRecord(cSmessage);
                    break;

                case CSKeys.SEND_FORMATION_TIME:
                    ServerUtils.getInstance().handleSendFormationTime(cSmessage);
                    break;

                case CSKeys.CHATING_TEXT_MSG:
                    // 文本信息:
                    ServerUtils.getInstance().handleChatMsg(cSmessage);
                    break;

                case CSKeys.DELETE_FRIEND:
                    // 删除好友
                    ServerUtils.getInstance().handleDeleteFriend(session, cSmessage);
                    break;

                case CSKeys.GET_FRIEND_ID_LIST:
                    // 获得好友Id列表
                    ServerUtils.getInstance().handleGetFriendIdList(session, cSmessage);
                    break;

                case CSKeys.GET_RECORD_LIST:
                    ServerUtils.getInstance().handleSendRecord(session,cSmessage);
                    break;

                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    ServerUtils.getInstance().handleGetFriendInfo(session, cSmessage);
                    break;

                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    // 请求得到一个陌生人的具体信息，要发头像
                    ServerUtils.getInstance().handleGetFriendInfo(session, cSmessage);
                    break;



            }
        }else if(cSmessage.symbol=='-'){
            switch (msgJson.getInteger(JSONKeys.msgType)){
                case CSKeys.REGISTER:
                    //注册
                    ServerUtils.getInstance().handleRegister(session, cSmessage);
                    break;
                case CSKeys.RESET_HEAD:
                    // 修改头像:
                    ServerUtils.getInstance().handleResetHead(session, cSmessage);
                    break;

                case CSKeys.CHATING_IMAGE_MSG:
                    // 图片消息:
                    ServerUtils.getInstance().handleChatMsg(cSmessage);
                    break;
                case CSKeys.CHATING_VOICE_MSG:
                    // 语音消息:
                    ServerUtils.getInstance().handleChatMsg(cSmessage);
                    break;


            }
        }
    }
    public void messageSent(IoSession session,Object message)throws Exception
    {
        super.messageSent(session,message);
    }

    @Override
    public void sessionClosed(IoSession session)throws Exception
    {
        super.sessionClosed(session);
        long sessionId=session.getId();
        if(ManagerIdSessions.isContainsId(sessionId))
        {
            String userId=ManagerIdSessions.getUserId(sessionId);
            ManagerIdSessions.deleteUserId(sessionId);//删除这个会话
            ManagerClientSession.deleteSession(userId);
//            ManagerLocMap.deleteOneUser(userId);
        }
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
//		System.out.println("连接+" + session);
    }
}
