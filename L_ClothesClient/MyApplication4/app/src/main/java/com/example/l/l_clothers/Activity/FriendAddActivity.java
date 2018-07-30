package com.example.l.l_clothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.ImageUtil;

import java.net.ConnectException;

public class FriendAddActivity extends AppCompatActivity {
    ImageButton ib_back;
    Button btn_add;

    static ImageView iv_headlogo;
    static TextView tv_nickname;
    static TextView tv_sex;
    static TextView tv_singnatrue;

    private String doctorId;
    static String name;
    static String sex;
    static String birthday;
    static String personSignature;

    static Context context;
    static byte[] headbytes;

    public static Handler handler=new Handler(){
        public void handleMessage(Message message){
            if(message.what== CSKeys.GET_DOCTOR_INFO){//获得医生信息
                CSmessage doctorinfo=(CSmessage)message.obj;
                JSONObject jsonObject= JSON.parseObject(doctorinfo.msgJson);
                name=jsonObject.getString(JSONKeys.userName);
                sex=jsonObject.getString(JSONKeys.userSex);
                personSignature=jsonObject.getString(JSONKeys.personSignature);
                tv_nickname.setText(name);
                tv_sex.setText(sex);
                tv_singnatrue.setText(personSignature);
                headbytes=doctorinfo.msgBytes;
                iv_headlogo.setImageBitmap(ImageUtil.getInstance().getBitMapFromByte(headbytes));
            }else if(message.what==CSKeys.ADD_FRIEND_SUCCESS){
                Toast.makeText(context, "添加好友成功", Toast.LENGTH_LONG).show();

                //添加好友成功之后，要发送消息告诉服务器端我这里已经完成了添加
                MainActivity.myBinder.tellsevercomplete();//就是用来告诉已经完成了
            } else if (message.what == CSKeys.ADD_FRIEND_FAILED) {
                Toast.makeText(context, "添加好友失败", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void initview(){
        iv_headlogo=(ImageView)findViewById(R.id.friendadd_iv_headlogo);
        tv_nickname=(TextView) findViewById(R.id.friendadd_tv_nickname);
        tv_sex=(TextView)findViewById(R.id.friendadd_tv_sexvalue);
        tv_singnatrue=(TextView)findViewById(R.id.friendadd_tv_singnatrue);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_add);
        context=this;
        Intent intent=getIntent();
        doctorId=intent.getStringExtra("doctorid");
        initview();
        MainActivity.myBinder.getDoctorInfo(doctorId);//根据id获得信息

        ib_back=(ImageButton)findViewById(R.id.friendadd_ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        btn_add = (Button) findViewById(R.id.friendadd_btn_friendadd);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.myBinder.addFriend(doctorId);//调用了添加函数
                String headPath= StaticValues.USER_HEADPATH+doctorId+".png";
                ImageUtil.getInstance().saveImage(FriendAddActivity.this,headbytes,headPath);
                FriendInfo friendInfo=new FriendInfo();
                friendInfo.setFriendId(doctorId);
                friendInfo.setFriendName(name);
                friendInfo.setFriendSex(sex);
                friendInfo.setFriendBirthday(birthday);
                friendInfo.setFriendSignature(personSignature);
                friendInfo.setFriendHeadPath(headPath);

                MainActivity.mp0friend.AddAFriend(friendInfo);

            }
        });
    }
}
