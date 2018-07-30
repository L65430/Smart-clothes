package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.l.l_clothers.AllMessage.ChatMessage;
import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.Info.ClientManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2017/6/4.
 */

public class ChatDBUtils extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME="L_cloth_db";//数据库名
    private static final int  VERSION=1;//建立的数据库的个数

    public ChatDBUtils(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //这个方法是新建一个数据库的时候去调用的
    }


    //判断表是否存在
    public boolean isTableExit(String tname)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? ";
        String paras[] = { tname };//tname是输入的值
        try {
            Cursor cursor = db.rawQuery(sql, paras);
            if(cursor.moveToFirst())
            {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (name.equals(tname)) {
                    return true;
                }else
                    return false;
            }
            // Log.i("--", "tname = " + name);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //删除一张表
    //表里面存的都是和每个人的消息记录
    public void droptable(String frientId)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String sql1="msg"+ ClientManger.clientId+"_"+frientId;
        String sql="drop table"+sql1;
        sqLiteDatabase.execSQL(sql);
    }

    //创建表
    public void createtable(String friendid)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String tname="msg"+ClientManger.clientId+"_"+friendid;//都是这种形式
        String sql = "create table "
                + tname
                + "( msgType integer, msgJson text, sendTime text, isGetted integer, isLooked integer)";
//        System.out.print("表已经存在");//可能就是这个的问题
        sqLiteDatabase.execSQL(sql);
    }

    //获得消息记录，没有未读消息的时候默认读取15条消息
    public List<ChatMessage> getrecentmessage(String friendid, int offset, int maxresult)//读取的个数
    {
        String tname="msg"+ClientManger.clientId+"_"+friendid;
        SQLiteDatabase database=this.getReadableDatabase();
        List<ChatMessage>list=new ArrayList<>();
        String sql="select * from " + tname//要注意from后面是有空格的，空格都要注意的
                + " order by sendTime desc limit ?, ?";
        int count=getmessagecount(friendid);
        if (count < 1)
        {
            return list;
        }else if (count<maxresult) {
            maxresult = count;
        }
        Cursor cursor=database.rawQuery(sql,new String[]{String.valueOf(offset),String.valueOf(maxresult)});
        while(cursor.moveToNext())//获得的chat
        {
            ChatMessage chatmessage=new ChatMessage();
            chatmessage.isGetted=cursor.getInt(cursor.getColumnIndex("isGetted"));
            chatmessage.isLooked=cursor.getInt(cursor.getColumnIndex("isLooked"));
            chatmessage.msgJson=cursor.getString(cursor.getColumnIndex("msgJson"));
            chatmessage.msgType=cursor.getInt(cursor.getColumnIndex("msgType"));
            chatmessage.sendTime=cursor.getString(cursor.getColumnIndex("sendTime"));
            list.add(chatmessage);
        }
        cursor.close();
        return list;

    }
    //获得消息数目
    public int getmessagecount(String frientid)
    {
        SQLiteDatabase database=this.getReadableDatabase();
        String tname="msg"+ClientManger.clientId+"_"+frientid;
        String sql="select count(*)from"+tname;
        Cursor cursor=database.rawQuery(sql,null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        return count;
    }

    //获取未读信息
    public List<ChatMessage> getMsgNotLooked(String friendId) {// select top 65
        // *// by id
        // desc
        String tName = "msg" + ClientManger.clientId + "_" + friendId; // from
        // table
        // order
        SQLiteDatabase db = this.getReadableDatabase();//就是获得一个只读的数据库
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        String where = "isLooked = ?";
        Cursor cursor = db.query(tName, null, where, new String[] { "0" },//没有被看到就是0，有就是1
                null, null, "sendTime desc");//也是根据发送时间来的,这是自己帮你平写好时间
        while (cursor.moveToNext()) {
            ChatMessage msgDb = new ChatMessage();
            msgDb.msgType = cursor.getInt(cursor.getColumnIndex("msgType"));
            msgDb.msgJson = cursor.getString(cursor.getColumnIndex("msgJson"));
            msgDb.sendTime = cursor
                    .getString(cursor.getColumnIndex("sendTime"));
            msgDb.isGetted = cursor.getInt(cursor.getColumnIndex("isGetted"));
            msgDb.isLooked = cursor.getInt(cursor.getColumnIndex("isLooked"));
            list.add(msgDb);
        }
        cursor.close();
        return list;
    }

    //保存信息，插入表
    public boolean savechat(ChatMessage chatmessage,String friendid)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        try {
            String tname = "msg" + ClientManger.clientId + "_" + friendid;
            String sql = "insert into "
                    + tname
                    + "(msgType, msgJson, sendTime, isGetted, isLooked) values(?, ?, ?, ?, ?)";
            String[] args = {chatmessage.msgType + "", chatmessage.msgJson,
                    chatmessage.sendTime, chatmessage.isGetted + "", chatmessage.isLooked + ""};
            database.execSQL(sql, args);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    //把未查看到的变成已经查看过的
    public void updateread(String friendid)
    {
        String tName = "msg" + ClientManger.clientId + "_" + friendid;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "update " + tName + " set isLooked = ? where isLooked = ?";
        String where = " isLooked = ?";
        db.execSQL(sql, new String[] { "1", "0" });
    }

    //清空一个好友的所有对话记录
    public void clearMsg(String friendid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String tname = "msg" + ClientManger.clientId + "_" + friendid;
        db.delete(tname,null,null);
        db.close();
    }

    //删除一条聊天记录
    public void deleteonemessage(String friendid,String sendtime)
    {
        String tname = "msg" + ClientManger.clientId + "_" + friendid;
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="delete from "+tname+" where sendtime = ?";
        db.execSQL(sql,new String[]{sendtime});
        db.close();
    }

    //创建最近联系人
    public void createLaticeChatTable() {
        String tName = "recent_chat_table";//最近聊天人
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "create table "
                + tName
                + "( userphone text, friendId text primary key, friendName text, chatCreatTime text, chatContent text, msg_num integer, Msgtype integer)";
        db.execSQL(sql);
        Log.i("--", "createrecentChatTable");
    }


    //判断之前是不是聊过
    public boolean havechatted(String friendid)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String sql="select * from recent_chat_table where userphone = ? and friendId = ? ";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(ClientManger.ClientPhone),String.valueOf(friendid)});
        cursor.moveToNext();
        String id=cursor.getString(cursor.getColumnIndex("friendId"));
        if(id.equals(friendid))
        {
            return true;
        }
        else
            return false;
    }

    //插入最近聊天好友列表
    public boolean insertrecentchat(Chatinfoentry entity)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String tname="recent_chat_table";
        try {
            String sql = "insert into "
                    + tname
                    + "(userphone , friendId, friendName, chatCreatTime, chatContent, msg_num, Msgtype) values(?, ?, ?, ?, ? ,?, ?)";
            String[] args = { ClientManger.ClientPhone, entity.getFriendId(),
                    entity.getFriendName(), entity.getChatCreatTime(),
                    entity.getChatContent(), entity.getMsg_num() + "",
                    entity.getMsgtype() + "" };
            db.execSQL(sql, args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //更新最近聊天的人
    public void updaterecentchat(Chatinfoentry entity)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String tname="recent_chat_table";
        String sql = "update "
                + tname
                + " set chatCreatTime = ? ,chatContent = ?, msg_num = ? , Msgtype = ? where friendId = ?";
        db.execSQL(
                sql,
                new String[] { entity.getChatCreatTime(),
                        entity.getChatContent(), entity.getMsg_num() + "",
                        entity.getMsgtype() + "", entity.getFriendId() });
    }

    //消除一项最近聊天项
    public void deletechatitem(String friendId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String tname = "recent_chat_table";
        String sql = "delete from " + tname
                + " where userphone = ? and friendId = ?";
        db.execSQL(sql,new String[]{ClientManger.ClientPhone,friendId});
        db.close();
    }

    //清空一个用户的最近联系人
    public void clearrecent()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String tname="recent_chat_table";
        String sql="delete from "+tname+" where userphone = ?";
        String paras[]={ClientManger.ClientPhone};
        db.execSQL(sql,paras);
    }

    //获得最近聊天列表
    public List<Chatinfoentry> getrecentlist() {
        // String tName = "latice_chat_table";
        List<Chatinfoentry> list = new ArrayList<Chatinfoentry>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select friendId, friendName, chatContent, chatCreatTime, msg_num, Msgtype from recent_chat_table where userphone = ?";
        Cursor cursor = db.rawQuery(sql,
                new String[] { ClientManger.ClientPhone });
        while (cursor.moveToNext()) {
            Chatinfoentry entity = new Chatinfoentry();
            entity.setFriendId(cursor.getString(cursor
                    .getColumnIndex("friendId")));
            entity.setFriendName(cursor.getString(cursor
                    .getColumnIndex("friendName")));
            entity.setChatContent(cursor.getString(cursor
                    .getColumnIndex("chatContent")));
            entity.setChatCreatTime(cursor.getString(cursor
                    .getColumnIndex("chatCreatTime")));
            entity.setMsg_num(cursor.getInt(cursor.getColumnIndex("msg_num")));
            entity.setMsgtype(cursor.getInt(cursor.getColumnIndex("Msgtype")));
            list.add(entity);
        }
        cursor.close();
        return list;
    }

    //获得最近聊天的一个好友
    public Chatinfoentry getrecentchatfriend(String friendid)
    {
        SQLiteDatabase database=this.getReadableDatabase();
        Chatinfoentry chatinfo=new Chatinfoentry();
        String sql="select friendId, friendName, chatContent, chatCreatTime, msg_num, Msgtype from recent_chat_table where userphone = ? and friendId = ?";
        Cursor cursor=database.rawQuery(sql,new String[]{ClientManger.ClientPhone,friendid});
        cursor.moveToNext();//转到最开始的位置
        chatinfo.setFriendId(friendid);
        chatinfo.setFriendName(cursor.getString(cursor.getColumnIndex("friendId")));
        chatinfo.setChatCreatTime(cursor.getString(cursor.getColumnIndex("chatCreatTime")));
        chatinfo.setMsg_num(cursor.getInt(cursor.getColumnIndex("msg_num")));
        chatinfo.setMsg_num(cursor.getInt(cursor.getColumnIndex("msg_num")));
        chatinfo.setMsgtype(cursor.getInt(cursor.getColumnIndex("Msgtype")));
        return  chatinfo;
    }

    //群组聊天邀请信息,发起聊天
    public void createInviteTable() {
        String tName = "invitations";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "create table "
                + tName
                + "( groupId text primary key, invitorName text , groupName text, topic text, groupIconPath text)";//group是主键
        db.execSQL(sql);
    }


    //删除邀请
    public void deleteInvite(String groupId) {
        String sql = "delete from invitations where groupId = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(sql, new String[] { groupId });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //结束


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

