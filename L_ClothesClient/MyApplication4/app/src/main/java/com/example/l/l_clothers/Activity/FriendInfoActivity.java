package com.example.l.l_clothers.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.CollapsibleActionView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;

import java.io.File;

public class FriendInfoActivity extends Activity {
    private FriendInfo bean;
    SharedPreferenceUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);
        Intent intent = getIntent();
        bean = (FriendInfo) intent
                .getSerializableExtra("friendInfo");//从另一个界面转过来的，带上一个friendinfo数据
        util=new SharedPreferenceUtil(this);

        //在这里面就需要去把里面所有的信息都更新了，在这里面就需要及时的把所有信息都更新
        MainDoctorActivity.myBinder.getpositioninfo(bean.getFriendId());


        ImageView iv_headlogo = (ImageView) findViewById(R.id.friendinfo_iv_headlogo);
        TextView tv_nickname = (TextView) findViewById(R.id.friendinfo_tv_nickname);
        TextView tv_sex = (TextView) findViewById(R.id.friendinfo_tv_sexvalue);
        TextView tv_singnatrue = (TextView) findViewById(R.id.friendinfo_tv_singnatrue);
        Button treatrecord=(Button)findViewById(R.id.treatrecord);
        String job=util.getvalues(StaticValues.userjob);
        if(job.equals("医生")){
            treatrecord.setVisibility(View.VISIBLE);
        }else
        {
            treatrecord.setVisibility(View.GONE);
        }

        tv_nickname.setText(bean.getFriendName());
        tv_sex.setText(bean.getFriendSex());
        tv_singnatrue.setText(bean.getFriendSignature());
        iv_headlogo.setImageURI(Uri.fromFile(new File(bean.getFriendHeadPath())));

        final Button chat=(Button)findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId=bean.getFriendId();
                String friendName=bean.getFriendName();
                if(util.getvalues(StaticValues.userjob).equals("医生")){
                    if(MainDoctorActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainDoctorActivity.mp0talk.ResetNotReadMsg(friendId);//清空所有的未读消息
                    }else
                    {
                        System.out.print("开始聊天啦！！！和之前没有聊过的！！！");
                        Chatinfoentry chatinfoentry=new Chatinfoentry();//新增一条聊天记录
                        chatinfoentry.setMsg_num(0);
                        chatinfoentry.setChatCreatTime("");
                        chatinfoentry.setChatContent("");
                        chatinfoentry.setFriendId(friendId);
                        chatinfoentry.setMsgtype(0);
                        chatinfoentry.setFriendName(friendName);
                        MainDoctorActivity.mp0talk.addrecentchatitem(chatinfoentry);//这样是ok的，就是点进去然后跳出来的情况
                    }
                }else{
                    if(MainActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainActivity.mp0talk.ResetNotReadMsg(friendId);//清空所有的未读消息
                    }else
                    {
                        System.out.print("开始聊天啦！！！和之前没有聊过的！！！");
                        Chatinfoentry chatinfoentry=new Chatinfoentry();//新增一条聊天记录
                        chatinfoentry.setMsg_num(0);
                        chatinfoentry.setChatCreatTime("");
                        chatinfoentry.setChatContent("");
                        chatinfoentry.setFriendId(friendId);
                        chatinfoentry.setMsgtype(0);
                        chatinfoentry.setFriendName(friendName);
                        MainActivity.mp0talk.addrecentchatitem(chatinfoentry);//这样是ok的，就是点进去然后跳出来的情况
                    }
                }
                Intent intent=new Intent(FriendInfoActivity.this,ChatActivity.class);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
                finish();

            }
        });
        Button delete=(Button)findViewById(R.id.delete);
        //删除好友
        //发送通知给服务器，如果在最近联系人中，删除，包括本地的数据库，数据库中清楚表
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId=bean.getFriendId();
                if(util.getvalues(StaticValues.userjob).equals("医生")){
                    MainDoctorActivity.myBinder.deleteFrienddoctor(friendId);
                    MainDoctorActivity.mp0friend.DeleteAFriend(friendId);//更新好友列表
                }else{
                    MainActivity.myBinder.deleteFriend(friendId);
                    MainActivity.mp0friend.DeleteAFriend(friendId);//更新好友列表
                }

                if(util.getvalues(StaticValues.userjob).equals("医生")){
                    if(MainDoctorActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainDoctorActivity.mp0talk.deleterecentchat(friendId);
                    }
                }else{
                    if(MainActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainActivity.mp0talk.deleterecentchat(friendId);
                    }
                }


                ChatDBUtils chatDBUtils=new ChatDBUtils(FriendInfoActivity.this);
                String name="msg"+ ClientManger.clientId+"_"+friendId+".png";
                if(chatDBUtils.isTableExit(name))
                {
                    chatDBUtils.deletechatitem(friendId);
                }
                if(util.getvalues(StaticValues.userjob).equals("医生")){
                    if(MainDoctorActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainDoctorActivity.mp0talk.deleterecentchat(friendId);
                    }
                }else{
                    if(MainActivity.mp0talk.isExistsInchat(friendId))
                    {
                        MainActivity.mp0talk.deleterecentchat(friendId);
                    }
                }
                finish();
            }
        });

        if(util.getvalues(StaticValues.userjob).equals("医生")){
            treatrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1=new Intent(FriendInfoActivity.this,LookRecordActivity.class);
                    intent1.putExtra("friendid",bean.getFriendId());
                    startActivity(intent1);
                }
            });
        }
    }
    public void onBack(View v)
    {
        finish();
    }

}

