package Servertools;

import AllMessage.CSmessage;
import AllMessage.MsgDb;
import Info.Doctorinfo;
import Info.Positioninfo;
import Info.Recoderinfo;
import Keys.CSKeys;
import Keys.JSONKeys;
import Keys.StaticValue;
import Sqlutils.SqlModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.parser.deserializer.StackTraceElementDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializerMap;
import com.sun.corba.se.impl.activation.ServerTool;
import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import com.sun.xml.internal.ws.dump.LoggingDumpTube;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import javafx.geometry.Pos;
import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by L on 2017/5/16.
 */
public class ServerUtils {
    static ServerUtils serverUtils;
    int number=0;//表示进来的次数
    public static ServerUtils getInstance(){
        if(serverUtils==null){
            serverUtils=new ServerUtils();
            return serverUtils;
        }else
            return serverUtils;
    }

    //处理注册
    public void handleRegister(IoSession session, CSmessage cSmessage){
        JSONObject json= JSON.parseObject(cSmessage.msgJson);
        SqlModel sqlModel=new SqlModel();
        String userPhone=json.getString(JSONKeys.userphone);
        String password=json.getString(JSONKeys.userPasswd);
        String sex=json.getString(JSONKeys.userSex);
        String name=json.getString(JSONKeys.userName);
        String birthday=json.getString(JSONKeys.userBirthday);
        String job=json.getString(JSONKeys.userwork);
        String userId=sqlModel.allocateId();//分配id
        String userHeadPath= StaticValue.HEAD_P_PATH+userPhone+".png";
        FileTools.getInstance().saveMultyFile(userHeadPath,cSmessage.msgBytes);
        String sql1="insert into ClientMessage values (?,?,?,?,?,?,?,?)";
        String[]paras1={userId,userPhone,password,name,sex,"",birthday,userHeadPath};

        String sql2="insert into doctorinfo values (?,?,?,?,?)";
        String []paras2={userPhone,name,"",sex,userId};

        String sql3="insert into patientinfo values (?,?,?,?,?,?,?,?,?)";
        String []paras3={userPhone,name,0+"",0+"",0+"",0+"",0+"",0+"",userId};

        String sql4="insert into Treatment values(?,?,?)";
        String []paras4={0+"",0+"",userId};

        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        if(sqlModel.updateDb(sql1,paras1))
        {
            if(job.equals("医生")){
               sqlModel.updateDb(sql2,paras2);
            }
            if(job.equals("患者")){
                sqlModel.updateDb(sql3,paras3);
                sqlModel.updateDb(sql4,paras4);
            }
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_SUCCESS);
            System.out.println("注册成功");
        }else
        {
            System.out.print("注册失败");
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);//发送message消息，返回注册成功消息
    }




    //通知发送验证码
    public void handleSendCheckNumber(IoSession session, CSmessage cSmessage){
        try{
            JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
            String userphone=jsonObject.getString(JSONKeys.userphone);//获取了电话号码
            String url="xxxx";
            String appkey= "xxxx";
            String appsecret="xxxx";
            TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
            AlibabaAliqinFcSmsNumSendRequest request=new AlibabaAliqinFcSmsNumSendRequest();
            request.setExtend("xxx");
            request.setSmsType("xxx");
            request.setSmsFreeSignName("xxx");
            request.setRecNum(userphone);
            request.setSmsTemplateCode("xxx");
            //生成随机数
            String code="";
            Random r=new Random(new Date().getTime());
            for(int i=0;i<6;i++){
                code=code+r.nextInt(10);
            }
            String json="{\"number\":\""+code+"\"}";
            request.setSmsParamString(json);
            AlibabaAliqinFcSmsNumSendResponse rsp=client.execute(request);
            SqlModel model=new SqlModel();

            if(number==0){
                String sql1="insert into Checktable values (?,?)";
                String []paras1={userphone,code};
                model.updateDb(sql1,paras1);//第一次进来的时候直接插入
                number++;
                System.out.print("此时number:"+number);
            }else {
                String sql = "update Checktable set Checknumber = ? where Phonenumber = ?";
                String[] paras = {code, userphone};
                model.updateDb(sql, paras);//修改验证码
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //通知检查验证码
    public void handleCheckNumber(IoSession session, CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userphone=jsonObject.getString(JSONKeys.userphone);
        String userchecknumber=jsonObject.getString(JSONKeys.checknumber);
        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject notifyjson=new JSONObject();
        SqlModel sqlModel=new SqlModel();
        if(sqlModel.Checknumber(userphone,userchecknumber)){
            notifyjson.put(JSONKeys.msgType, CSKeys.CHECK_TURE);
        }else{
            notifyjson.put(JSONKeys.msgType,CSKeys.CHECK_WRONG);
        }
        notify.msgJson=notifyjson.toJSONString();
        session.write(notify);
    }

    //用户登录
    public void handleLogin(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        if(new SqlModel().checkUser(jsonObject.getString(JSONKeys.userphone),jsonObject.getString(JSONKeys.userPasswd),jsonObject.getString(JSONKeys.userwork)))
        {
            String job=jsonObject.getString(JSONKeys.userwork);
            if(job.equals("医生")){
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUCCESS_DOCTOR);//登录陈宫
            }else{
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUCCESS);//病人
            }
        }else
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);
    }


    //处理修改用户信息
    public void handleResetUserInfo(CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String useId=jsonObject.getString(JSONKeys.userId);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        SqlModel sqlModel=new SqlModel();
        String sql="";
        String paras[]=new String[2];
        switch (type){
            case CSKeys.RESET_SIGNATUE:
                sql="update ClientMessage set signature = ? where id = ?";
                paras[0]=jsonObject.getString(JSONKeys.personSignature);
                break;
        }
        paras[1]=useId;
        if(sqlModel.updateDb(sql,paras)){
            System.out.println("修改用户信息成功");
        }else{
            System.out.println("修改用户信息失败");
        }
    }

    //处理用户超级登录
    public void handleLoginSuper(IoSession session,CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        SqlModel model=new SqlModel();
        String userphone=jsonObject.getString(JSONKeys.userphone);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        JSONObject info= model.getUserInfo(userphone,true);
        String userId=info.getString(JSONKeys.userId);
        CSmessage notify=new CSmessage();
        JSONObject NotifyJson=new JSONObject();
        if(info!=null){
            ManagerIdSessions.addUserId(session.getId(),userId);
            ManagerClientSession.addSession(info.getString(JSONKeys.userId),session);
            NotifyJson.put(JSONKeys.userId,userId);
            NotifyJson.put(JSONKeys.userName,info.getString(JSONKeys.userName));
            NotifyJson.put(JSONKeys.userSex,info.getString(JSONKeys.userSex));
            NotifyJson.put(JSONKeys.userBirthday,info.getString(JSONKeys.userBirthday));
            NotifyJson.put(JSONKeys.personSignature,info.getString(JSONKeys.personSignature));
//            NotifyJson.put(JSONKeys.userwork,);
            if(type==CSKeys.LOGIN_SUPER_HEAD){
                notify.symbol='+';
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_HEAD);
                System.out.println("用户本地有头像");
            }else if(type==CSKeys.LOGIN_SUPER_NOHEAD){
                System.out.println("用户本地无头像");
                notify.symbol='-';
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_NOHEAD);
                notify.msgBytes=FileTools.getInstance().getMultyFileBytes(info.getString(JSONKeys.userHeadPath));
            }else{
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_FAILED);
            }
            notify.msgJson=NotifyJson.toJSONString();
            session.write(notify);
            sendCacheMsg(session,userId,model);
        }
    }

    //转发离线消息
    private void sendCacheMsg(IoSession session,String userId,SqlModel model)
    {
        if(model.isTableExists("mc_"+userId))
        {
            if(model.getMsgCount(userId)>0)//表示有离线消息
            {
                List<MsgDb> list=model.getCacheMsgs(userId);
                for(MsgDb msgDb:list)
                {
                    CSmessage cSmessage=MsgTranceUtil.getInstance().Trance_Db2Net(msgDb);//转化为网络消息
                    session.write(cSmessage);//每一次都转发
                }
                model.clearMsgCache(userId);//清空离线文件
            }
        }
    }

    //处理添加好友
    public void handleAddFriend(IoSession session,CSmessage cSmessage) {
        JSONObject jsonObject = JSON.parseObject(cSmessage.msgJson);
        String userId = jsonObject.getString(JSONKeys.userId);
        String friendId = jsonObject.getString(JSONKeys.friendId);
        CSmessage cSmessage1 = new CSmessage();
        cSmessage1.symbol = '+';
        JSONObject notify = new JSONObject();
        SqlModel model = new SqlModel();
        if (model.addFriend(userId, friendId)) {
            notify.put(JSONKeys.msgType, CSKeys.ADD_FRIEND_SUCCESS);
            model.addFriend(friendId, userId);//对方的好友列表里面也要占个位置
            JSONObject info = model.getUserInfo(userId, false);
            CSmessage addmsg = new CSmessage();
            addmsg.symbol = '-';
            String headPath = info.getString(JSONKeys.userHeadPath);
            addmsg.msgBytes = FileTools.getInstance().getMultyFileBytes(headPath);
            info.put(JSONKeys.msgType, CSKeys.ADD_FRIEND);//通知对面要去添加好友了
            info.remove(JSONKeys.userHeadPath);
            info.put(JSONKeys.friendId, userId);
            String friendName = model.getUserName(userId, false);
            info.put(JSONKeys.friendName, friendName);
            addmsg.msgJson = info.toJSONString();
            if (ManagerClientSession.isContainsId(friendId)) {
                ManagerClientSession.getSession(friendId).write(addmsg);
                System.out.println("转发成功..");
            } else {
                if (!model.isTableExists("mc_" + friendId)) {
                    model.createCacheTable(friendId);//创造缓存数据库
                }
                MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(addmsg);
                if (model.insertCacheMsg(msgDb, friendId)) {
                    System.out.println("缓存成功");
                } else {
                    System.out.println("缓存失败");
                }
            }
        }else{
            notify.put(JSONKeys.msgType,CSKeys.ADD_FRIEND_FAILED);
        }
        cSmessage1.msgJson=notify.toJSONString();
        session.write(cSmessage1);
    }

    //删除好友
    public void handleDeleteFriend(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        String friendId= jsonObject.getString(JSONKeys.friendId);
        SqlModel model=new SqlModel();
        model.deleteFriend(userId,friendId);
        model.deleteFriend(friendId,userId);//相互接触双方关系
    }


    //处理获取好友id列表
    public void handleGetFriendIdList(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        SqlModel model=new SqlModel();
        String []ids=model.getFriendIds(userId);//获取一个朋友的好友Id列表
        String IdList="";
        if(ids.length>0)
        {
            for(String string:ids)
            {
                IdList+=string+"";
            }
        }else
        {
            IdList="none";
        }
        CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        JSONObject jsonSend=new JSONObject();
        jsonSend.put(JSONKeys.msgType,CSKeys.FRIEND_ID_LIST);
        jsonSend.put(JSONKeys.friendIdList,IdList);
        cSmessage1.msgJson=jsonSend.toJSONString();
        session.write(cSmessage1);
    }

    //处理修改头像
    public void handleResetHead(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId= jsonObject.getString(JSONKeys.userId);
        String userHeadPath=StaticValue.HEAD_P_PATH+userId+".png";//这是服务器端的东西
        CSmessage Notify=new CSmessage();
        Notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        try
        {
            FileTools.getInstance().saveMultyFile(userHeadPath,cSmessage.msgBytes);
            System.out.print("修改头像成功");
            NotifyJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD_SUCCESS);

            SqlModel sqlModel=new SqlModel();
            String[] friendList=sqlModel.getFriendIds(userId);
            CSmessage resetHead=new CSmessage();
            resetHead.symbol='-';
            JSONObject resetHeadJson=new JSONObject();
            resetHeadJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD);//此时还不知道是否成功了
            resetHeadJson.put(JSONKeys.friendId,userId);//通知好友是谁改头像
            resetHeadJson.put(JSONKeys.userId,userId);//是用户发的这条消息
            resetHead.msgJson=resetHeadJson.toJSONString();
            resetHead.msgBytes=cSmessage.msgBytes;//就是传进来的消息
            for(String friendId:friendList)
            {
                //判断是否在线
                if(ManagerClientSession.isContainsId(friendId))
                {
                    ManagerClientSession.getSession(friendId).write(resetHead);//发送给好友谁修改了头像
                }else
                {
                    if(!sqlModel.isTableExists("mc_"+friendId))
                        sqlModel.createCacheTable(friendId);//创建缓存数据库，上线之后再发送
                    MsgDb msgDb=MsgTranceUtil.getInstance().Trance_Net2Db(resetHead);//转换成数据库消息
                    if(sqlModel.insertCacheMsg(msgDb,friendId))
                    {
                        System.out.println("缓存成功");
                    }else
                        System.out.println("缓存失败");
                }
            }
        }catch (Exception e)
        {
            //修改失败
            NotifyJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD_FAILED);
        }
        Notify.msgJson=NotifyJson.toJSONString();
        session.write(Notify);//这是发送给用户的，告诉用户他的修改信息失败了
    }

    //处理通知完成了
    public void handleComplete(CSmessage cSmessage){
         JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        SqlModel sqlModel=new SqlModel();
        String sql="";
        String paras[]=new String[2];
        switch (type){
            case CSKeys.TELL_SEVER_COMPLETE:
                sql="update Completetable set complete = ? where phonenumber = ?";
                paras[0]="true";
                break;
        }
        paras[1]=userId;
        if(sqlModel.updateDb(sql,paras)){
            System.out.println("修改用户信息成功");
        }else{
            System.out.println("修改用户信息失败");
        }
    }

    //处理发送complete信息
    public void handleGetcomplete(IoSession session,CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        SqlModel model=new SqlModel();
        String userphone=jsonObject.getString(JSONKeys.userId);//获得了上面的东西
        String info=model.getcompleteinfo(userphone);//获取complete的值
        CSmessage notify=new CSmessage();
        JSONObject NotifyJson=new JSONObject();
        NotifyJson.put(JSONKeys.complete,info);
        NotifyJson.put(JSONKeys.msgType,CSKeys.GET_SERVER_COMPLETE);
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);
    }


    //获得医生列表,一开始的时候就把id都加进去了,直接去获得那一列的列表
    public void handleDoctorList(IoSession session,CSmessage cSmessage){
        SqlModel sqlModel=new SqlModel();
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        JSONObject jsonSend=new JSONObject();
        if(type==CSKeys.GET_DOCTOR_LIST){
            List<Doctorinfo>list=sqlModel.getdoctorlist();
            JSONArray jsonArray=(JSONArray)JSONArray.toJSON(list);
            jsonSend.put(JSONKeys.msgType,CSKeys.GET_DOCTOR_LIST);
            jsonSend.put(JSONKeys.doctorlist,jsonArray);
        }
        cSmessage1.msgJson=jsonSend.toJSONString();
        session.write(cSmessage1);
    }

    public void handleGetDoctorInfo(IoSession session,CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject.getInteger(JSONKeys.msgType);
        String doctorid=jsonObject.getString(JSONKeys.friendId);
        SqlModel sqlModel=new SqlModel();
        JSONObject info=sqlModel.getUserInfo(doctorid,false);
        CSmessage cSmessage1=new CSmessage();
        if(type==CSKeys.GET_DOCTOR_INFO){
            cSmessage1.symbol='-';
            info.put(JSONKeys.msgType,CSKeys.GET_DOCTOR_INFO);
            String headpath=info.getString(JSONKeys.userHeadPath);
            cSmessage1.msgBytes=FileTools.getInstance().getMultyFileBytes(headpath);
        }
        info.remove(JSONKeys.userHeadPath);
        cSmessage1.msgJson=info.toJSONString();
        session.write(cSmessage1);
    }


    //请求获得数据库中的数据
    public void handleGetRecord(IoSession ioSession,CSmessage cSmessage){
         JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
         String userId=jsonObject.getString(JSONKeys.userId);
         SqlModel sqlModel=new SqlModel();
         JSONObject record=sqlModel.getrecord(userId);
         CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        record.put(JSONKeys.msgType,CSKeys.GET_RECORD);
        cSmessage1.msgJson=record.toJSONString();
        ioSession.write(cSmessage1);
    }

    //插入数据库中
    public void handlePutRecord(CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        SqlModel sqlModel=new SqlModel();
        String sql="";
        String paras[]=new String[7];
        sql="update patientinfo set firstA = ?, firstB = ?,secondA = ?,secondB = ?,thirdA = ?,thirdB = ? where id = ?";
        //如何往数据库中插入double类型的数据
        paras[0]=jsonObject.getDouble(JSONKeys.firstA)+"";
        paras[1]=jsonObject.getDouble(JSONKeys.firstB)+"";
        paras[2]=jsonObject.getDouble(JSONKeys.secondA)+"";
        paras[3]=jsonObject.getDouble(JSONKeys.secondB)+"";
        paras[4]=jsonObject.getDouble(JSONKeys.thirdA)+"";
        paras[5]=jsonObject.getDouble(JSONKeys.thirdB)+"";
        paras[6]=userId;
        if(sqlModel.updateDb(sql,paras)){
            System.out.print("修改病人信息成功");
        }else{
            System.out.print("修改病人信息失败");
        }
    }


    public void handleSendFormationTime(CSmessage cSmessage){
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userid=jsonObject.getString(JSONKeys.userId);
        int positionnumber=jsonObject.getInteger(JSONKeys.positionnumber);
        int time=jsonObject.getInteger(JSONKeys.time);
        SqlModel sqlModel=new SqlModel();
        String sql="";
        String paras[]=new String[3];
        sql="update Treatment set positionnumber = ?,okey = ? where id = ?";
        paras[0]=positionnumber+"";
        paras[1]=time+"";//存的就是次数以及动作的编号
        paras[2]=userid;
        if(sqlModel.updateDb(sql,paras)){
            System.out.print("修改病人训练进度成功");
        }else{
            System.out.print("修改病人训练进度失败");
        }

    }

    //转发消息
    private void sendMsgToUser(CSmessage cSmessage,String getterId)
    {
        if(ManagerClientSession.isContainsId(getterId))
        {
            //直接发送的情况，显示转发成功
            ManagerClientSession.getSession(getterId).write(cSmessage);//每一个会话都有自己的id,根据id进行转发就可以了
        }else
        {
            SqlModel model=new SqlModel();
            if(!model.isTableExists("mc_"+getterId))
            {
                model.createCacheTable(getterId);//创建缓存数据库
            }
            MsgDb msgDb=MsgTranceUtil.getInstance().Trance_Net2Db(cSmessage);
            if(model.insertCacheMsg(msgDb,getterId))
            {
                System.out.println("缓存成功");
            }else
            {
                System.out.println("缓存失败");
            }
        }
    }

    //获取当前时间
    //获取当前时间
    public String getNowTime()
    {
        SimpleDateFormat format=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String date=format.format(new Date());
        return date;
    }



    public void handleChatMsg(CSmessage cSmessage)
    {
        JSONObject jsonObject1=JSON.parseObject(cSmessage.msgJson);
        int isGroupMsg=0;
        if(isGroupMsg==0)
        {
            String getterId=jsonObject1.getString(JSONKeys.friendId);

            //说明这不是群组，是个人的
            //判断是否在线。在线就直接转发，不在线就缓存到数据库中
            sendMsgToUser(cSmessage,getterId);
        }
    }


    //发送训练数据回来
    public void handleSendRecord(IoSession session,CSmessage cSmessage){
         JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
         String friendid=jsonObject.getString(JSONKeys.friendId);
         SqlModel sqlModel=new SqlModel();
         List<Positioninfo> list=sqlModel.getpositionlist(friendid);

        JSONObject jsonsend=new JSONObject();
        CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        JSONObject jsonObject1=new JSONObject();
        JSONArray jsonArray=(JSONArray)JSONArray.toJSON(list);
        jsonsend.put(JSONKeys.msgType,CSKeys.GET_RECORD_LIST);
        jsonsend.put(JSONKeys.positionlist,jsonArray);
        cSmessage1.msgJson=jsonsend.toJSONString();
        session.write(cSmessage1);

    }


    public void handleGetFriendInfo(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject.getInteger(JSONKeys.msgType);
        String userId=jsonObject.getString(JSONKeys.userId);
        String friendId=jsonObject.getString(JSONKeys.friendId);
        SqlModel model=new SqlModel();
        JSONObject info=model.getUserInfo(friendId,false);
        CSmessage cSmessage1=new CSmessage();
        if(type==CSKeys.GETA_FRIEND_INFO_HEAD)
        {
            //本地有头像，所以不用传输头像
            cSmessage1.symbol='+';
            info.put(JSONKeys.msgType,CSKeys.GETA_FRIEND_INFO_HEAD);
        }else if(type==CSKeys.GETA_FRIEND_INFO_NOHEAD)
        {
            cSmessage1.symbol='-';
            info.put(JSONKeys.msgType,CSKeys.GETA_FRIEND_INFO_NOHEAD);
            String headPath=info.getString(JSONKeys.userHeadPath);
            cSmessage1.msgBytes=FileTools.getInstance().getMultyFileBytes(headPath);
        }
        info.remove(JSONKeys.userHeadPath);
        cSmessage1.msgJson=info.toJSONString();
        session.write(cSmessage1);
    }

}
