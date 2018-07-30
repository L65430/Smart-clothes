package com.example.l.l_clothers.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.opengl.EGLDisplay;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Codecfactory.Lcoderfactory;
import com.example.l.l_clothers.Handler.ClientMessageHandler;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.FileUtils;
import com.example.l.l_clothers.Utils.ForCheck;
import com.example.l.l_clothers.Utils.PasswordUtil;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;
import com.example.l.l_clothers.Utils.myDialog;
import com.example.l.l_clothers.View.CircleImageView;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.File;
import java.net.InetSocketAddress;
import java.security.spec.RSAOtherPrimeInfo;

public class SignInActivity extends Activity {
    private Context context;
    private String userPhone;
    private String pwd;
    private IoSession session;
    private long mExitTime;
    Button btn_forget;
    Button btn_submit;
    CircleImageView userHead;//头像

    MyHandler myHandler=new MyHandler();

    EditText et_userName;
    EditText et_pwd;
    TextView tv_forgetpassword;
    TextView tv_registration;
    MyOnClickListener myOnClickListener;

    SharedPreferenceUtil util;


    private myDialog loginlog;//对话框
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case CSKeys.LOGIN_SUCCESS:
                    //登录成功，跳到主界面
                    //保存相关信息
                    util=new SharedPreferenceUtil(context);
                    util.SaveValues(StaticValues.userPhone,userPhone);
                    util.SaveValues(StaticValues.userPasswd,pwd);
                    Intent intent=new Intent(context,MainActivity.class);//还要写个医生端的东西，所以这里可能会变化

                    startActivity(intent);


                    loginlog.dismiss();
                    CloseSession();
                    finish();
                    break;

                //成功登录了医生端
                case CSKeys.LOGIN_SUCCESS_DOCTOR:
                    SharedPreferenceUtil util1=new SharedPreferenceUtil(context);
                    util1.SaveValues(StaticValues.userPhone,userPhone);
                    util1.SaveValues(StaticValues.userPasswd,pwd);

                    Intent intent1=new Intent(context,MainDoctorActivity.class);
                    startActivity(intent1);
                    loginlog.dismiss();
                    CloseSession();
                    finish();
                    break;

                case CSKeys.LOGIN_FAILED:
                    Toast.makeText(context,"登录失败",Toast.LENGTH_LONG).show();
                    loginlog.dismiss();
                    break;

                case StaticValues.SESSION_OPENED://和服务器连接成功
                    try{
                        util1=new SharedPreferenceUtil(context);
                        String job=util.getvalues(StaticValues.userjob);
                        CSmessage cSmessage=new CSmessage();
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put(JSONKeys.msgType,CSKeys.LOGIN);
                        jsonObject.put(JSONKeys.userphone,userPhone);
                        jsonObject.put(JSONKeys.userPasswd, PasswordUtil.toMD5(pwd));
                        jsonObject.put(JSONKeys.userwork,job);//发送过去连接
                        cSmessage.msgJson=jsonObject.toJSONString();
                        cSmessage.symbol='+';//表示纯文本
                        session.write(cSmessage);
                        //从mainactivity得到消息传递到这来
                    }catch (Exception c){
                        c.printStackTrace();
                    }
                    break;
            }
        }
    };

    private class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            et_userName.setText((String)msg.obj);//接收到消息之后的处理，但是发送消息一般是在另一个线程
        }
    }//这个handler不知道用来干嘛的


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.signin_btn_submit) {
                userPhone = et_userName.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                if (userPhone.equals("")) {
                    Toast.makeText(context, "账号不能为空", Toast.LENGTH_LONG);
                    return;
                }
                if (!ForCheck.PhonenumberFormat(userPhone)) {
                    Toast.makeText(context, "请输入正确的注册电话", Toast.LENGTH_LONG);
                    et_userName.setText("");
                    return;
                }
                if (pwd.equals("")) {
                    Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG);
                    return;
                }
                //发送登录消息
                new ConnectServerThread().start();
                loginlog = new myDialog(context, "登录中...");
                loginlog.show();
            }else if(v.getId()==R.id.signin_tv_registration)
            {
                Intent intent = new Intent(SignInActivity.this,
                        RegistrationActivity.class);
                startActivity(intent);
            }else
            {
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);
        context=this;
        initview();
    }

    public void initview(){
        userHead=(CircleImageView)findViewById(R.id.signin_iv_headicon);
        et_userName=(EditText)findViewById(R.id.signin_et_username);
        et_pwd=(EditText)findViewById(R.id.signin_et_pwd);
        btn_submit=(Button)findViewById(R.id.signin_btn_submit);
        btn_submit.setTag("btn_submit");
//        tv_forgetpassword.setTag("tv_forgetpassword");
        myOnClickListener = new MyOnClickListener();
        tv_registration = (TextView) findViewById(R.id.signin_tv_registration);
        tv_registration.setTag("tv_registration");
        tv_registration.setOnClickListener(myOnClickListener);


        btn_submit.setOnClickListener(myOnClickListener);
//        tv_forgetpassword.setOnClickListener(myOnClickListener);
        tv_registration.setOnClickListener(myOnClickListener);

        et_userName.addTextChangedListener(new TextWatcher() {//输入内容改变的监听器
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String headPath=StaticValues.USER_HEADPATH+s+".png";
                if(FileUtils.isFileexist(headPath))
                {
                    userHead.setImageURI(Uri.fromFile(new File(headPath)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }




    //连接服务器
    private class ConnectServerThread extends Thread{
        public void run(){
            NioSocketConnector connector=new NioSocketConnector();
            connector.setHandler(new ClientMessageHandler(handler));//这个handler是用来接收服务的
            connector.getFilterChain().addLast("code",new ProtocolCodecFilter(new Lcoderfactory()));//解码器
            try{
                ConnectFuture future=connector.connect(new InetSocketAddress(StaticValues.SERVER_IP,StaticValues.SERVER_PORT));
                future.awaitUninterruptibly();//等待直到阻塞结束
                session=future.getSession();
                System.out.print("session is ok"+session);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void CloseSession(){
        if(session!=null&&session.isClosing()){
            session.close();
        }
    }

}
