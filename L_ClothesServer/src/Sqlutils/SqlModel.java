package Sqlutils;

import AllMessage.CSmessage;
import AllMessage.MsgDb;
import Info.Doctorinfo;
import Info.Positioninfo;
import Info.Recoderinfo;
import Keys.JSONKeys;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.PostConstruct;
import javax.print.Doc;
import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2017/5/16.
 */
//存放各种sql语句
public class SqlModel {
      public boolean updateDb(String sql,String[]paras){
          SqlHelper sqlHelper=new SqlHelper();
          return sqlHelper.updExecute(sql,paras);
      }

      //登录验证
    public boolean checkUser(String userPhone,String userPasswd,String userwork){
        SqlHelper sqlHelper=null;
        boolean isLegal=false;
        try{
            String sql="select password from ClientMessage where phonenumber = ?";
            String paras[]={userPhone};
            sqlHelper=new SqlHelper();
            ResultSet rs=sqlHelper.query(sql,paras);
            if(rs.next()){
                if(rs.getString(1).equals(userPasswd)){
                    isLegal=true;
                }
            }
        }catch (Exception e){
            isLegal=false;
            e.printStackTrace();
        }finally {
            sqlHelper.close();
        }
        return isLegal;
    }

    //判断一个表是否存在
    public boolean isTableExists(String tableName){
        boolean isExists=true;
        String sql="Show tables like ?";
        String paras[]={tableName};
        SqlHelper sp=null;
        String tempTable=null;
        try{
            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(!rs.next()){
                isExists=false;
            }
        }catch (Exception e){
            isExists=false;
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return  isExists;
    }

    //分配id
    public String allocateId() {
        SqlHelper sp = null;
        int newId = 0;
        try {
            String sql = "select allocate_id from allocation_id where flag = ?";
            String paras[] = { "0" };
            sp = new SqlHelper();
            ResultSet rs = sp.query(sql, paras);
            if (rs.next()) {
                newId = rs.getInt(1);
                sp.updExecute(
                        "update allocation_id set flag = ? where flag = ?",
                        new String[] { "1", "0" });
                sp.updExecute("insert into allocation_id values(?,?)",
                        new String[] { (newId + 1) + "", "0" });
            } else {
                return "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        } finally {
            sp.close();
        }
        return newId + "";
    }

    //根据id或者用户电话返回用户名字
    public String getUserName(String userphone,boolean isphone){//判断输入的是电话还是userid两种情况
        SqlHelper sp = null;
        String userName="null";
        try{
            String sql=null;
            if (isphone) {
                sql="select name from ClientMessage where phonenumber = ?";
            }else{
                sql="select name from ClientMessage where id = ?";
            }
            String paras[]={userphone};
            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(rs.next()){
                userName=rs.getString(1);
            }
        }catch (Exception e){
            userName="null";
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return userName;
    }

    //得到用户信息
    public JSONObject getUserInfo(String userphone,boolean isphone){
        JSONObject userInfo = new JSONObject();
        SqlHelper sp = null;
        String sql = null;
        try{
            if(isphone){
                sql="select name, sex, signature, birth, imghead, id from ClientMessage where phonenumber = ?";
            }else{
                sql="select name, sex, signature, birth, imghead, id from ClientMessage where id = ?";
            }
            String paras[]={userphone};

            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(rs.next()){
                userInfo.put(JSONKeys.userName,rs.getString(1));
                userInfo.put(JSONKeys.userSex,rs.getString(2));
                userInfo.put(JSONKeys.personSignature,rs.getString(3));
                userInfo.put(JSONKeys.userBirthday,rs.getString(4));
                userInfo.put(JSONKeys.userHeadPath,rs.getString(5));
                userInfo.put(JSONKeys.userId,rs.getString(6));
            }
        }catch (Exception e){
            userInfo=null;
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return userInfo;
    }

    //获取完成情况
    public String getcompleteinfo(String userphone){
        SqlHelper sp=null;
        String sql=null;
        String complete=null;
        try{
            sql="select complete from Completetable where phonenumber = ?";
            String paras[]={userphone};
            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(rs.next()){
                complete=rs.getString(1);
            }
        }catch (Exception e){
            complete=null;
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return complete;
    }

    //检查验证码
    public boolean Checknumber(String userphone,String checknumber){
        System.out.print("userphone是："+userphone);
        System.out.print("checknumber是:"+checknumber);
        SqlHelper sp=null;
        boolean isLegal=false;
        String sql=null;
        try{
            sql="select Checknumber from Checktable where Phonenumber = ?";
            String paras[]={ userphone };
            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(rs==null){
                System.out.print("rs是空的！！！");
            }
            if(rs.next()){
                System.out.print("得到的结果是！！:"+rs.getString(1));
                if(rs.getString(1).equals(checknumber)){
                    isLegal=true;
                }
            }
        }catch (Exception e){
            isLegal=false;
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return isLegal;
    }


    //插入离线消息
    public boolean insertCacheMsg(MsgDb msgDb, String userId) {
        String sql = "insert into " + "mc_" + userId + " values (?,?)";
        String[] paras = { msgDb.msgType + "", msgDb.msgJson };
        return updateDb(sql, paras);
    }

    //根据表名清空缓存
    public boolean clearMsgCache(String userId) {
        String sql = "delete from " + "mc_" + userId + " where 1 = ?";
        String[] paras = { "1" };
        SqlHelper sqlHelper = new SqlHelper();
        return sqlHelper.updExecute(sql, paras);
    }

    //创建缓冲表
    public boolean createCacheTable(String userId) {
        String tableName = "mc_" + userId;// 表明尽量短
        String sql = "create table " + tableName
                + " (msgType int(4), msgJson text)";
        SqlHelper sqlHelper = new SqlHelper();
        return sqlHelper.create(sql);
    }

    //得到离线消息数目
    public int getMsgCount(String userId) {
        int count = 0;// "SELECT count(*) FROM sqlite_master WHERE type='table' AND name= ? ";
        String tableName = "mc_" + userId;
        String sql = "select count(*) from " + tableName + " where 1 = ?";
        String paras[] = { "1" };
        SqlHelper sp = null;
        try {
            sp = new SqlHelper();
            ResultSet rs = sp.query(sql, paras);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            count = 0;
            e.printStackTrace();
        } finally {
            sp.close();
        }
        return count;
    }

    //从数据得到离线消息
    public List<MsgDb> getCacheMsgs(String userId) {
        String tableName = "mc_" + userId;
        List<MsgDb> list = new ArrayList<MsgDb>();
        String sql = "select * from " + tableName + " where 1 = ?";
        String paras[] = { "1" };
        SqlHelper sp = null;
        try {
            sp = new SqlHelper();
            ResultSet rs = sp.query(sql, paras);
            while (rs.next()) {
                MsgDb msgDb = new MsgDb();
                msgDb.msgType = rs.getInt("msgType");
                msgDb.msgJson = rs.getString("msgJson");
                list.add(msgDb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sp.close();
        }

        return list;
    }


    //添加医患关系
    public boolean addFriend(String userId, String friendId) {
        SqlHelper sp = null;
        String sql2 = "select userId from friend_list  where userId = ?";
        String[] pp = { userId };
        try {
            sp = new SqlHelper();
            ResultSet rs = sp.query(sql2, pp);
            if (!rs.next()) {
                System.out.println("rs.next() " + rs.next());
                String sql3 = "insert into friend_list(userId) values(?)";
                String paras[] = { userId };
                updateDb(sql3, paras);
            }
            String sql1 = "select friendList from friend_list  where userId = ?";
            String paras1[] = { userId };
            String freindListStr = "";
            sp = new SqlHelper();
            ResultSet rs2 = sp.query(sql1, paras1);
            if (rs2.next()) {
                freindListStr = rs2.getString(1);
            }
            if (freindListStr == null) {
                freindListStr = friendId;
            } else {
                String[] friends = getFriendIds(userId);
                boolean isExists = false;
                for (String string : friends) {
                    if (string.equals(friendId)) {
                        isExists = true;
                        break;
                    }
                }
                if (!isExists)
                    freindListStr += "," + friendId;
            }

            String sql = "update friend_list set friendList = ? where userId = ?";
            String paras[] = { freindListStr, userId };
            return updateDb(sql, paras);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sp.close();
        }
        return false;

    }

    //删除好友
    public boolean deleteFriend(String userId, String friendId) {
        String[] oldFriend = this.getFriendIds(userId);
        String newFriendStr = "";
        for (String str : oldFriend) {
            if (!str.equals(friendId)) {
                newFriendStr += str + ",";
            }
        }
        String sql = "update friend_list set friendList = ? where userId = ?";
        String paras[] = { newFriendStr.substring(0, newFriendStr.length() - 1), userId };
        return updateDb(sql, paras);
    }


    //获取好友列表
    public String[] getFriendIds(String userId) {
        String[] friendList = null;
        String sql = "select friendList from friend_list  where userId = ?";
        String paras[] = { userId };
        SqlHelper sp = null;
        try {
            sp = new SqlHelper();
            ResultSet rs = sp.query(sql, paras);
            if (rs.next()) {
                friendList = rs.getString(1).split(",");
            }
        } catch (Exception e) {
//			e.printStackTrace();
        } finally {
            sp.close();
        }
        return friendList;
    }

    //获取医生列表
    public List<Doctorinfo> getdoctorlist(){
        SqlHelper sp=null;
        String newId="";
        List<Doctorinfo>list=new ArrayList<Doctorinfo>();
        Doctorinfo doctorinfo=new Doctorinfo();
        try{
            String sql="select id from doctorinfo";
            sp=new SqlHelper();
            ResultSet rs=sp.queryno(sql);
            if(rs.next()){
                newId=rs.getString(1);
                sql="select phone, name, personstate, sex, id from doctorinfo where id = ?";
                String paras[]={newId};
                ResultSet rs2=sp.query(sql,paras);
                if(rs2.next()){
                    doctorinfo.doctorid=rs2.getString(5);
                    doctorinfo.doctorinfo=rs2.getString(3);
                    doctorinfo.doctorname=rs2.getString(2);
                }
                list.add(doctorinfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    //获取治疗结果
    public List<Positioninfo> getpositionlist(String friendid){
        SqlHelper sp=null;
        String newId="";
        List<Positioninfo> list=new ArrayList<Positioninfo>();
        Positioninfo positioninfo=new Positioninfo();
        try{
            String sql="select id from Treatment";
            sp=new SqlHelper();
            ResultSet rs=sp.queryno(sql);
            if(rs.next()){
                newId=rs.getString(1);
                sql="select positionnumber, okey from Treatment where id = ?";
                String paras[]={newId};
                ResultSet rs2=sp.query(sql,paras);
                if(rs2.next()){
                    positioninfo.positionid=rs2.getString(1);
                    positioninfo.time=rs2.getString(2);
                }
            }
            list.add(positioninfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    //获取历史记录
    public JSONObject getrecord(String userid){
        JSONObject jsonObject=new JSONObject();
        SqlHelper sp=null;
        String sql=null;
        try{
            sql="select phone, name, firstA, firstB, secondA, secondB, thirdA, thirdB from patientinfo where id = ?";
            String paras[]={userid};
            sp=new SqlHelper();
            ResultSet rs=sp.query(sql,paras);
            if(rs.next()){
                jsonObject.put(JSONKeys.userphone,rs.getString(1));
                jsonObject.put(JSONKeys.userName,rs.getString(2));
                jsonObject.put(JSONKeys.firstA,rs.getDouble(3));
                jsonObject.put(JSONKeys.firstB,rs.getDouble(4));
                jsonObject.put(JSONKeys.secondA,rs.getDouble(5));
                jsonObject.put(JSONKeys.secondB,rs.getDouble(6));
                jsonObject.put(JSONKeys.thirdA,rs.getDouble(7));
                jsonObject.put(JSONKeys.thirdB,rs.getDouble(8));
                jsonObject.put(JSONKeys.userId,userid);
            }
        }catch (Exception e){
            jsonObject=null;
            e.printStackTrace();
        }finally {
            sp.close();
        }
        return jsonObject;
    }







}
