package com.example.l.l_clothers.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Activity.MainDoctorActivity;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Codecfactory.Lcoderfactory;
import com.example.l.l_clothers.Handler.ClientMessageHandler;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.Recoderinfo;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.FileUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.bouncycastle.crypto.engines.CamelliaLightEngine;

import java.net.InetSocketAddress;

public class ClientService extends Service {
    private MyBinder mBinder;

    public ClientService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyBinder();
//		Log.i("--", "创建service");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//里面放的是其他线程过来的消息内容
//		Log.i("--", "收到任务");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CloseSession();
//		Log.i("--", "退出service");
    }

    //连接服务器
    private class ConnectServerThread extends Thread {//放在另一个线程里面

        public void run() {
            NioSocketConnector connector = new NioSocketConnector();//这就是mina框架的使用方法
            connector.setHandler(new ClientMessageHandler(MainActivity.handler));//传到mainactivity的handler里面,这是传入传出的口
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new Lcoderfactory()));//指定编码，过滤器
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        StaticValues.SERVER_IP, StaticValues.SERVER_PORT));//连接建立完成了//这里完成了连接，服务器都是同一个
                future.awaitUninterruptibly();//等待连接建立
                MainActivity.session = future.getSession();//这是创建一个连接实例的意思
//				System.out
//						.println("session is ok" + MainActivity.ClientSession);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//这只是用来创建连接用的


    //新建一个线程给医生端来用
    private class ConnectServerThread1 extends Thread {//放在另一个线程里面

        public void run() {
            NioSocketConnector connector = new NioSocketConnector();//这就是mina框架的使用方法
            connector.setHandler(new ClientMessageHandler(MainDoctorActivity.handler));//传到mainactivity的handler里面,这是传入传出的口
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new Lcoderfactory()));//指定编码，过滤器
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        StaticValues.SERVER_IP, StaticValues.SERVER_PORT));//连接建立完成了//这里完成了连接，服务器都是同一个
                future.awaitUninterruptibly();//等待连接建立
                MainDoctorActivity.session = future.getSession();//这是创建一个连接实例的意思
//				System.out
//						.println("session is ok" + MainActivity.ClientSession);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//这只是用来创建连接用的


    private void CloseSession() {
        if (MainActivity.session != null
                && !MainActivity.session.isClosing()) {
            MainActivity.session.close();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("--", "绑定service");
        return mBinder;//返回了Binder,此时客户端就可以调用binder里面有的service的方法了,但是其他activity
        //如果要调用这个binde
    }


    public class MyBinder extends Binder {
        //和mainactivity的连接
        public void ConnectServer() {
            new ConnectServerThread().start();
        }

        public void ConnectServer1(){
            new ConnectServerThread1().start();
        }

        //超级登录,得到头像
        public void SuperLogin() {
            try {
                CSmessage moMoMsg = new CSmessage();
                JSONObject Json = new JSONObject();
                Json.put(JSONKeys.userphone, ClientManger.ClientPhone);
                // 本地有用户头像，不需要从服务器获取
                if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                        + ClientManger.ClientPhone + ".png")) {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_HEAD);//这个head就是指没有本地图像的意思，需要服务器去获取
                }
                // 本地无，需获取
                else {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_NOHEAD);
                }
                moMoMsg.msgJson = Json.toJSONString();
                moMoMsg.symbol = '+';
                MainActivity.session.write(moMoMsg);//表示向服务器发送这个请求
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //医生版本的
        public void SuperLogindoctor() {
            try {
                CSmessage moMoMsg = new CSmessage();
                JSONObject Json = new JSONObject();
                Json.put(JSONKeys.userphone, ClientManger.ClientPhone);
                // 本地有用户头像，不需要从服务器获取
                if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                        + ClientManger.ClientPhone + ".png")) {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_HEAD);//这个head就是指没有本地图像的意思，需要服务器去获取
                }
                // 本地无，需获取
                else {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_NOHEAD);
                }
                moMoMsg.msgJson = Json.toJSONString();
                moMoMsg.symbol = '+';
                MainDoctorActivity.session.write(moMoMsg);//表示向服务器发送这个请求
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //修改个人信息
        public void ResetUserInfo(int type, String value) {
            Log.i("--", "type = " + type + "value = " + value);
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '+';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.userId, ClientManger.clientId);
            switch (type) {
                case CSKeys.RESET_USERNAME:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_USERNAME);
                    Json.put(JSONKeys.userName, value);
                    break;
                case CSKeys.RESET_SEX:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_SEX);
                    Json.put(JSONKeys.userSex, value);
                    break;
                case CSKeys.RESET_BIRTHDAY:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_BIRTHDAY);
                    Json.put(JSONKeys.userBirthday, value);
                    break;
                case CSKeys.RESET_SIGNATUE:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_SIGNATUE);
                    Json.put(JSONKeys.personSignature, value);
                    break;
                case CSKeys.RESET_PASSWD:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_PASSWD);
                    Json.put(JSONKeys.userPasswd, value);
                    break;
            }
            moMoMsg.msgJson = Json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMoMsg);
        }


        //医生端修改资料
        public void ResetUserInfodoctor(int type, String value) {
            Log.i("--", "type = " + type + "value = " + value);
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '+';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.userId, ClientManger.clientId);
            switch (type) {
                case CSKeys.RESET_USERNAME:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_USERNAME);
                    Json.put(JSONKeys.userName, value);
                    break;
                case CSKeys.RESET_SEX:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_SEX);
                    Json.put(JSONKeys.userSex, value);
                    break;
                case CSKeys.RESET_BIRTHDAY:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_BIRTHDAY);
                    Json.put(JSONKeys.userBirthday, value);
                    break;
                case CSKeys.RESET_SIGNATUE:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_SIGNATUE);
                    Json.put(JSONKeys.personSignature, value);
                    break;
                case CSKeys.RESET_PASSWD:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_PASSWD);
                    Json.put(JSONKeys.userPasswd, value);
                    break;
            }
            moMoMsg.msgJson = Json.toJSONString();
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(moMoMsg);
        }

        //修改头像
        public void ResetUserHead(byte[] headBytes) {
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '-';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.msgType, CSKeys.RESET_HEAD);
            Json.put(JSONKeys.userphone, ClientManger.ClientPhone);
            moMoMsg.msgJson = Json.toJSONString();
            moMoMsg.msgBytes = headBytes;
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMoMsg);
        }



        //修改头像医生端
        public void ResetUserHeaddoctor(byte[] headBytes) {
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '-';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.msgType, CSKeys.RESET_HEAD);
            Json.put(JSONKeys.userphone, ClientManger.ClientPhone);
            moMoMsg.msgJson = Json.toJSONString();
            moMoMsg.msgBytes = headBytes;
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(moMoMsg);
        }


        //获取好友列表
        public void getFriendIdList() {
            System.out.print("开始GETFRIENIDLIST!!!!!");
            CSmessage msg = new CSmessage();
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_FRIEND_ID_LIST);//
            json.put(JSONKeys.userId, ClientManger.clientId);
            msg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }


        //获取好友列表医生端
        public void getFriendIdListdoctor() {
            System.out.print("开始GETFRIENIDLIST!!!!!");
            CSmessage msg = new CSmessage();
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_FRIEND_ID_LIST);//
            json.put(JSONKeys.userId, ClientManger.clientId);
            msg.msgJson = json.toJSONString();
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(msg);
        }

        //获得好友列表
        public void getFriendList(String IdStr) {
            Log.i("--", "好友Id列表 ：" + IdStr);
            if (!IdStr.equals("none")) {
                String[] Ids = IdStr.split(",");
                for (String friendId : Ids) {
                    CSmessage msg = new CSmessage();
                    msg.symbol = '+';
                    JSONObject json = new JSONObject();
                    json.put(JSONKeys.userId, ClientManger.clientId);
                    json.put(JSONKeys.friendId, friendId);

                    if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                            + friendId + ".png")) {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_HEAD);// 不需要获取头像
                    } else {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_NOHEAD);// 需要获取头像
                    }
                    msg.msgJson = json.toJSONString();//放到这里面，然后发送给服务器，服务器解析
                    if (MainActivity.session != null
                            && !MainActivity.session.isClosing())
                        MainActivity.session.write(msg);
                    Log.i("--", "获取好友信息： " + msg.msgJson);
                }
            }
        }


        //医生端
        public void getFriendListdoctor(String IdStr) {
            Log.i("--", "好友Id列表 ：" + IdStr);
            if (!IdStr.equals("none")) {
                String[] Ids = IdStr.split(",");
                for (String friendId : Ids) {
                    CSmessage msg = new CSmessage();
                    msg.symbol = '+';
                    JSONObject json = new JSONObject();
                    json.put(JSONKeys.userId, ClientManger.clientId);
                    json.put(JSONKeys.friendId, friendId);

                    if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                            + friendId + ".png")) {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_HEAD);// 不需要获取头像
                    } else {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_NOHEAD);// 需要获取头像
                    }
                    msg.msgJson = json.toJSONString();//放到这里面，然后发送给服务器，服务器解析
                    if (MainDoctorActivity.session != null
                            && !MainDoctorActivity.session.isClosing())
                        MainDoctorActivity.session.write(msg);
                    Log.i("--", "获取好友信息： " + msg.msgJson);
                }
            }
        }

        //获得医生信息
        public void getDoctorInfo(String doctorid) {
            Log.i("--", "getFriendInfo doctorid =   ：" + doctorid);
            CSmessage cSmessage = new CSmessage();
            cSmessage.symbol = '+';
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.msgType, CSKeys.GET_DOCTOR_INFO);
            jsonObject.put(JSONKeys.userId, ClientManger.clientId);
            jsonObject.put(JSONKeys.friendId, doctorid);
            cSmessage.msgJson = jsonObject.toJSONString();
            if (MainActivity.session != null && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);//向服务器发起请求
        }

        //添加好友
        public void addFriend(String doctorId) {
            CSmessage cSmessage = new CSmessage();
            cSmessage.symbol = '+';
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.msgType, CSKeys.ADD_FRIEND);//通知要添加好友了
            jsonObject.put(JSONKeys.userId, ClientManger.clientId);
            jsonObject.put(JSONKeys.friendId, doctorId);
            cSmessage.msgJson = jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }

        public void addFrienddoctor(String doctorId) {
            CSmessage cSmessage = new CSmessage();
            cSmessage.symbol = '+';
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.msgType, CSKeys.ADD_FRIEND);//通知要添加好友了
            jsonObject.put(JSONKeys.userId, ClientManger.clientId);
            jsonObject.put(JSONKeys.friendId, doctorId);
            cSmessage.msgJson = jsonObject.toJSONString();
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(cSmessage);
        }


        //删除好友
        public void deleteFriend(String doctorId) {
            CSmessage msg = new CSmessage();//客户端和服务器之间交互用的包
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.DELETE_FRIEND);// 添加好友
            json.put(JSONKeys.userId, ClientManger.clientId);
            json.put(JSONKeys.friendId, doctorId);
            msg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }




        //医生端
        public void deleteFrienddoctor(String doctorId) {
            CSmessage msg = new CSmessage();//客户端和服务器之间交互用的包
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.DELETE_FRIEND);// 添加好友
            json.put(JSONKeys.userId, ClientManger.clientId);
            json.put(JSONKeys.friendId, doctorId);
            msg.msgJson = json.toJSONString();
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(msg);
        }

        //告诉服务器我这里已经完成了
        public void tellsevercomplete() {
            CSmessage cSmessage = new CSmessage();
            cSmessage.symbol = '+';
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.msgType, CSKeys.TELL_SEVER_COMPLETE);
            jsonObject.put(JSONKeys.userId, ClientManger.ClientPhone);
            cSmessage.msgJson = jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //医生不需要这个

        //获取complete info
        public void Getcompleteinfo() {
            CSmessage cSmessage = new CSmessage();
            cSmessage.symbol = '+';
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONKeys.msgType, CSKeys.GET_SERVER_COMPLETE);
            jsonObject.put(JSONKeys.userId, ClientManger.ClientPhone);
            cSmessage.msgJson = jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //获取医生列表
        public void Getdoctorlist() {
            CSmessage cSmessage = new CSmessage();
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_DOCTOR_LIST);//只是发送通知需要这部分的东西,发送回来的时候需要id
            //请求获得doctorname和doctorinfo
            cSmessage.symbol = '+';
            cSmessage.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing()) {
                MainActivity.session.write(cSmessage);//通过这个东西来进行发送
            }
        }

        public void sendCharMsg(CSmessage moMsg) {
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMsg);//反正是发送出去给服务器，服务器怎么搞要继续写，服务器要转给其他人,发送出去的时候都是写成CSmessage的形式的
        }

        public void sendCharMsgdoctor(CSmessage moMsg) {
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(moMsg);//反正是发送出去给服务器，服务器怎么搞要继续写，服务器要转给其他人,发送出去的时候都是写成CSmessage的形式的
        }

        //获得初始记录
        public void getrecord(){
            CSmessage cSmessage=new CSmessage();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put(JSONKeys.msgType,CSKeys.GET_RECORD);
            jsonObject.put(JSONKeys.userId,ClientManger.clientId);
            cSmessage.symbol='+';
            cSmessage.msgJson=jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //发送过去
        public void handoutrecord(Recoderinfo recoderinfo){
            CSmessage cSmessage=new CSmessage();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put(JSONKeys.msgType,CSKeys.PUT_RECORD);
            jsonObject.put(JSONKeys.userId,ClientManger.clientId);
            jsonObject.put(JSONKeys.userphone,ClientManger.ClientPhone);
            jsonObject.put(JSONKeys.userName,ClientManger.clientName);
            jsonObject.put(JSONKeys.firstA,recoderinfo.firstA);
            jsonObject.put(JSONKeys.firstB,recoderinfo.firstB);
            jsonObject.put(JSONKeys.secondA,recoderinfo.secondA);
            jsonObject.put(JSONKeys.secondB,recoderinfo.secondB);
            jsonObject.put(JSONKeys.thirdA,recoderinfo.thirdA);
            jsonObject.put(JSONKeys.thirdB,recoderinfo.thirdB);
            cSmessage.symbol='+';
            cSmessage.msgJson=jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //发送每个动作的合格次数
        public void sendformaltime(int positionnumber,int time){
            CSmessage cSmessage=new CSmessage();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put(JSONKeys.userId,ClientManger.clientId);
            jsonObject.put(JSONKeys.positionnumber,positionnumber);//当前动作的编号
            jsonObject.put(JSONKeys.time,time);
            jsonObject.put(JSONKeys.msgType,CSKeys.SEND_FORMATION_TIME);
            cSmessage.symbol='+';
            cSmessage.msgJson=jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //获得每个人的结果
        public void getpositioninfo(String friendid){
            CSmessage cSmessage=new CSmessage();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put(JSONKeys.friendId,friendid);
            jsonObject.put(JSONKeys.msgType,CSKeys.GET_RECORD_LIST);
            cSmessage.symbol='+';
            cSmessage.msgJson=jsonObject.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(cSmessage);
        }


        //医生端
        public void getpositioninfodoctor(String friendid){
            CSmessage cSmessage=new CSmessage();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put(JSONKeys.friendId,friendid);
            jsonObject.put(JSONKeys.msgType,CSKeys.GET_RECORD_LIST);
            cSmessage.symbol='+';
            cSmessage.msgJson=jsonObject.toJSONString();
            if (MainDoctorActivity.session != null
                    && !MainDoctorActivity.session.isClosing())
                MainDoctorActivity.session.write(cSmessage);
        }

    }
}


