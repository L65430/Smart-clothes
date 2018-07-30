package com.example.l.l_clothers.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Fragment.MainPart0friend;
import com.example.l.l_clothers.Fragment.MainPart0talk;
import com.example.l.l_clothers.Fragment.MainPart1treat;
import com.example.l.l_clothers.Fragment.MainPart3setting;
import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.Info.InvitationInfoEntity;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Service.ClientService;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.Utils.ClientUtils;
import com.example.l.l_clothers.Utils.ImageUtil;
import com.example.l.l_clothers.Utils.MsgConvertionUtil;
import com.example.l.l_clothers.Utils.MyApplication;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;
import com.example.l.l_clothers.Utils.myDialog;

import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocationClient mLocation;//位置监听模块
    SharedPreferenceUtil mSpUtil;

    GestureDetector gestureDetector;//手势识别系统
    MyOnClickListener myOnClickListener;
    private RelativeLayout topView;
    private RelativeLayout bottonView;
    ImageView ib_part0;
    ImageView ib_part1;
    ImageView ib_part2;
    ImageView ib_part3;
    Fragment[] fragmentarray;
    public static MainPart0talk mp0talk;//最近联系人的界面
    public static MainPart0friend mp0friend;//通讯录
    public static MainPart1treat mp1treat;//寻找陌生人界面
    public static MainPart3setting mp3setting;//个人中心界面
    Button btn_send;
    //侧边栏
    RelativeLayout rl_head;
    static ImageView main_iv_head;// 头像
    static TextView main_tv_nick;// 昵称
    static TextView main_tv_sign;// 个性签名
    RelativeLayout rl_loginout;
    RelativeLayout rl_exit;
    Button btn_setting;
    Button btn_reback;
    Button btn_report;
    ////////////////////////////////各种控件///////////////////////////
    public static ClientService.MyBinder myBinder;
    public static IoSession session;
    private static myDialog initDialog;//初始化对话框
    private static NotificationManager notifier;//状态栏通知管理器
    public static MainActivity context;

    ///////////////////////////////标记////////////用来做判断/////
    private boolean isStart = false;
    private boolean canShake = false;
    private long mExitTime;//点击两次退出

    //消息提示
    public static SoundPool soundpool = null;//播放短小的音乐
    private static int msgcoming;//消息来了的提示音
    private static int voicerec;//录音提示
    private static int voicesend;//发送语音提示



    //service连接
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i("--", "MainActivity-->onServiceConnected");
            myBinder = (ClientService.MyBinder) service;
            myBinder.ConnectServer();
            initDialog.show();
        }//用来绑定binder的

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    public static Handler handler=new Handler(){
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message message){
            switch (message.what){
                case StaticValues.SESSION_OPENED:
                    myBinder.SuperLogin();//告诉对方我已经上线了
                    myBinder.Getcompleteinfo();
                    break;

                case CSKeys.LOGIN_SUPER_HEAD:
//                    Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                    ClientUtils.getInstance().saveClientInfo(context,message);
                    initPinfo();
                    //获取好友ID列表
                    myBinder.getFriendIdList();
                    initDialog.dismiss();
                    break;
                case CSKeys.LOGIN_SUPER_NOHEAD:
//                    Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                    ClientUtils.getInstance().saveClientInfo(context,message);
                    initPinfo();
                    //获取好友ID列表
                    myBinder.getFriendIdList();
                    initDialog.dismiss();
                    break;
                case CSKeys.LOGIN_SUPER_FAILED:
                    Toast.makeText(context, "和服务器连接失败", Toast.LENGTH_LONG).show();
                    initDialog.dismiss();
                    break;

                //添加好友成功
                case CSKeys.ADD_FRIEND_SUCCESS:
                    // 添加好友成功:
                    Message notify1 = new Message();
                    notify1.what = CSKeys.ADD_FRIEND_SUCCESS;
                    FriendAddActivity.handler.sendMessage(notify1);
                    break;

                //添加好友失败
                case CSKeys.ADD_FRIEND_FAILED:
                    // 添加失败:
                    Message notify2 = new Message();
                    notify2.what = CSKeys.ADD_FRIEND_FAILED;
                    FriendAddActivity.handler.sendMessage(notify2);//主线程
                    break;

                //获取完成情况
                case CSKeys.GET_SERVER_COMPLETE://handler发送message过来
                    CSmessage cSmessage1=(CSmessage)message.obj;
                    JSONObject jsonObject= JSON.parseObject(cSmessage1.msgJson);
                    String complete=jsonObject.getString(JSONKeys.complete);
                    ClientManger.complete=complete;
                    break;//就用这个来判断是不是完成了绑定




                //用来接收医生列表
                case CSKeys.GET_DOCTOR_LIST:
                    CSmessage cSmessage=(CSmessage)message.obj;
                    List<Doctorinfo> doctorinfoList= MsgConvertionUtil.getInstance().
                            getdoctorlist(cSmessage);
                    Message doctorlist=new Message();
                    doctorlist.what=CSKeys.GET_DOCTOR_LIST;
                    doctorlist.obj=doctorinfoList;
                    ChooseDoctorActivity.handler.sendMessage(doctorlist);
                    break;

                //获取医生信息
                case CSKeys.GET_DOCTOR_INFO:
                    Message info=new Message();
                    info.what=CSKeys.GET_DOCTOR_INFO;
                    info.obj=message.obj;
                    FriendAddActivity.handler.sendMessage(info);
                    break;

                case CSKeys.FRIEND_ID_LIST:
                    System.out.print("开始FRIEND_ID_LIST!!!!!!!");
                    JSONObject json = JSON.parseObject(message.obj.toString());
                    String IdStr = json.getString(JSONKeys.friendIdList);
                    myBinder.getFriendList(IdStr);// 获得好友列表GETA_FRIEND_INFO_HEAD
                    break;

                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    FriendInfo bean = MsgConvertionUtil.getInstance()
                            .getfriendinfo((CSmessage) message.obj,context);//怀疑根本没有存入数据库
                    System.out.println("最后一步的好友id为！！！！："+bean.getFriendId());
                    mp0friend.AddAFriend(bean);
                    Log.i("--", "好友 ：" + bean.toString());
                    break;

                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    FriendInfo bean2 = MsgConvertionUtil.getInstance()
                            .getfriendinfo((CSmessage) message.obj,context);
                    mp0friend.AddAFriend(bean2);
                    System.out.print("最后一步的好友id为！！！！（nohead情况下):"+bean2.getFriendId());
                    Log.i("--", "好友 ：" + bean2.toString());
                    break;


                case CSKeys.ADD_FRIEND://被添加为好友
                    CSmessage addF = (CSmessage) message.obj;
                    JSONObject addFjson = JSON.parseObject(addF.msgJson);//转换为json格式
                    Notification.Builder notification2 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification2.setContentText(addFjson.getString(JSONKeys.friendName) + "添加你为好友");
                    notification2.setContentTitle("添加好友");
                    notification2.setSmallIcon(R.drawable.imomo2);
                    notification2.setTicker("系统消息");
                    notification2.setAutoCancel(true);//点击后跳转到界面
                    notification2.setWhen(System.currentTimeMillis());
//                    Intent intent2 = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);//点击之后消失
//                    notification2.setContentIntent(pendingIntent2);
                    Notification nt2 = notification2.build();
                    notifier.notify(0, nt2);//它的新建在oncreate里,面进行,然后执行这个操作
                    FriendInfo addInfo = MsgConvertionUtil.getInstance().getfriendinfo(addF, context);
                    mp0friend.AddAFriend(addInfo);
                    MsgComeNotify();//更新
                    break;


                case CSKeys.CHATING_IMAGE_MSG://发送的是图片消息
                    HandleChatMsg(message);
                    //要做的事情有判断震动还是提示音
                    MsgComeNotify();//更新消息
                    break;
                case CSKeys.CHATING_TEXT_MSG:
                    HandleChatMsg(message);
                    MsgComeNotify();
                    break;
                case CSKeys.CHATING_VOICE_MSG:
                    HandleChatMsg(message);
                    MsgComeNotify();
                    break;


                case CSKeys.RESET_HEAD:
                    CSmessage resetHead = (CSmessage) message.obj;
                    JSONObject resetHeadJson = JSON.parseObject(resetHead.msgJson);
                    String friendId = resetHeadJson.getString(JSONKeys.friendId);
                    ImageUtil.getInstance().saveImage(context, resetHead.msgBytes, StaticValues.USER_HEADPATH + friendId + ".png");
                    break;


                case CSKeys.CONNECT_DOWN://连接断开
                    ClientManger.isOnline = false;
                    Notification.Builder notification4 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification4.setContentText("服务器断开连接，请检查网络");
                    notification4.setContentTitle("系统消息");
                    notification4.setSmallIcon(R.drawable.imomo2);
                    notification4.setTicker("新消息");
                    notification4.setAutoCancel(true);//点击后跳转到界面
                    notification4.setWhen(System.currentTimeMillis());
//                    Intent intent4 = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent4 = PendingIntent.getActivity(context, 0, intent4, PendingIntent.FLAG_CANCEL_CURRENT);//点击之消失
//                    notification4.setContentIntent(pendingIntent4);
                    Notification nt4 = notification4.build();
                    notifier.notify(0, nt4);//它的新建在oncreate里面进行,然后执行这个操作
                    break;

                //发送结果给Treatactivity
                case CSKeys.GET_RECORD:
                    Message record=new Message();
                    record.what=CSKeys.GET_RECORD;
                    record.obj=message.obj;
                    TreatActivity.handle.sendMessage(record);
                    break;

            }
        }
    };

    //新消息提醒
    public static void MsgComeNotify() {
        if (ClientManger.isRing) {
            playSound(1);//提示音
        }
        if (ClientManger.isVibration) {
            Longvibrate();//震动
        }

    }


    //处理接收的聊天消息
    public static void HandleChatMsg(Message msg) {
        CSmessage csmessage = (CSmessage) msg.obj;
        JSONObject json = JSONObject.parseObject(csmessage.msgJson);
        String friendId = json.getString(JSONKeys.userId);//因为接受的时候对面的friendid是自己的userid，这个就是用来确保在接受到信息的时候mp0能够显示出来
        ChatDBUtils db =new ChatDBUtils(context);
        int type = json.getIntValue(JSONKeys.msgType);
        if (mp0talk.isExistsInchat(friendId)) {
            if (mp0talk.getChatingId().equals(friendId))//如果正在聊天
            {
                ChatActivity.AddMsgItem(MsgConvertionUtil.getInstance().Convert_Net2Client(context, csmessage, true));
                if (!db.isTableExit("msg" + ClientManger.clientId + "_" + friendId)) {
                    db.createtable(friendId);
                }
                db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1, 1, context, csmessage), friendId);
            } else {
                if (!db.isTableExit("msg" + ClientManger.clientId + "_" + friendId)) {
                    db.createtable(friendId);
                }//如果不在最近联系人中
                db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1,0,context,csmessage), friendId);
                String content = "";
                if (type == CSKeys.CHATING_IMAGE_MSG) {
                    content = "[图片]";
                } else if (type == CSKeys.CHATING_TEXT_MSG) {
                    content = json.getString(JSONKeys.msgCotent);
                } else if (type == CSKeys.CHATING_VOICE_MSG) {
                    content = "[语音]";
                }
                mp0talk.updaterecentchatitem(friendId, json.getString(JSONKeys.sendTime), content, false);
            }
        } else//不在最近联系人中，所以要创建最近联系人
        {
            if (!db.isTableExit("msg" + ClientManger.clientId + "_"
                    + friendId))//不在最近联系人中，消息保存到数据库
            {
                db.createtable(friendId);
            }
            db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1, 0, context, csmessage), friendId);
            //添加到最近联系人中
            Chatinfoentry entity = new Chatinfoentry();
            if (type == CSKeys.CHATING_IMAGE_MSG) {
                entity.setChatContent("[图片]");
            } else if (type == CSKeys.CHATING_VOICE_MSG) {
                entity.setChatContent("[语音]");
            } else if (type == CSKeys.CHATING_TEXT_MSG) {
                entity.setChatContent(json.getString(JSONKeys.msgCotent));
            }
            entity.setFriendId(friendId);
            entity.setFriendName(mp0friend.getFriendName(friendId));
            entity.setChatCreatTime(json.getString(JSONKeys.sendTime));
            entity.setMsg_num(1);

            //在mp0中显示出来!!!!!!!终于找到了！！！！！！！！！
            mp0talk.addrecentchatitem(entity);
//            mp0.updaterecentchatitem(friendId, json.getString(JSONKeys.sendTime), entity.getChatContent(), false);

            //就是在接受的时候要确保也能出现
        }

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    //初始化控件
    private void initview() {
        myOnClickListener = new MyOnClickListener();
        // topView = (RelativeLayout) findViewById(R.id.main_layout_main);
        // bottonView = (RelativeLayout) findViewById(R.id.main_layout_edge);
        ib_part0 = (ImageView) findViewById(R.id.main_ib_part0);
        ib_part1 = (ImageView) findViewById(R.id.main_ib_part1);
        ib_part2 = (ImageView) findViewById(R.id.main_ib_part2);//三个下面的图标
        ib_part3=  (ImageView)findViewById(R.id.main_ib_part3);

        ib_part0.setOnClickListener(myOnClickListener);//先建立监听器，写成onclicklistener，然后设置监听器
        ib_part1.setOnClickListener(myOnClickListener);
        ib_part2.setOnClickListener(myOnClickListener);
        ib_part3.setOnClickListener(myOnClickListener);

        main_tv_nick = (TextView) findViewById(R.id.main_tv_nick);//昵称
        main_tv_sign = (TextView) findViewById(R.id.main_tv_sign);//签名

        //跳转到个人信息
        main_iv_head = (ImageView) findViewById(R.id.main_iv_head);
        main_iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class
                );
                startActivity(intent);//只有点击它的时候是跳转进去创建，其他都没有，所以肯定是不行的，所以要规定一开始就创建好
            }
        });

        //注销
        rl_loginout = (RelativeLayout) findViewById(R.id.main_rl_loginout);
        rl_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("注销").setMessage("确定注销？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();//之前只是设定了属性，并没有创建，此时才是创建
                dialog.show();
            }
        });

        //退出
        rl_exit = (RelativeLayout) findViewById(R.id.main_rl_exit);
        rl_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("退出").setMessage("确认退出?");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 断开服务
                                android.os.Process
                                        .killProcess(android.os.Process.myPid());
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        });


        btn_report = (Button) findViewById(R.id.main_btn_report);

        //初始化个人设置
        mSpUtil = new SharedPreferenceUtil(context);
        initSettings();
        boolean isReported = mSpUtil.getIsReport();//判断有没有签到
        String ReportDate = mSpUtil.getReportDate();
        if (ReportDate.equals(new Date()) && isReported) {
            isReported = true;
        }
        if (!isReported) {
            //没签到
            btn_report.setBackgroundResource(R.drawable.bg_btn_mini);
            btn_report.setTextColor(Color.WHITE);
            btn_report.setText("签到");
        } else {
            btn_report.setBackgroundResource(R.drawable.bg_btn_mini_nor);
            btn_report.setTextColor(Color.rgb(128, 128, 128));
            btn_report.setText("已签到");
        }

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientManger.isOnline) {
                    if (mSpUtil.getIsReport())//sharedpreference，别的activity传过来的
                    {
                        Toast.makeText(context, "已签到", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        //如果没有签到就设置签到
                        mSpUtil.setIsReport(true);
                        mSpUtil.setReportDate();
                        btn_report
                                .setBackgroundResource(R.drawable.bg_btn_mini_nor);
                        btn_report.setTextColor(Color.rgb(128, 128, 128));
                        btn_report.setText("已签到");
                        //myBinder.handleVitality(CSKeys.SIGN);这是关于活力值的属性，不需要
                    }
                } else {
                    Toast.makeText(context, "您处于离线状态，无法签到", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化个人设置
    public void initSettings() {
        ClientManger.isRing = mSpUtil.getIsRing();
        ClientManger.isVibration = mSpUtil.getIsVibration();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocation != null) {
            mLocation.stop();
        }
        if (connection != null) {
            unbindService(connection);
        }
    }

    private void initFragment() {
        fragmentarray = new Fragment[4];
        mp0talk = new MainPart0talk();
        mp0friend = new MainPart0friend();//在这个时候就已经新建了
        mp1treat = new MainPart1treat();
        mp3setting = new MainPart3setting();
        fragmentarray[0] = mp0talk;
        fragmentarray[1] = mp0friend;
        fragmentarray[2] = mp1treat;//估计是因为MainPart2还没有写完，还有个跳转没写
        fragmentarray[3] = mp3setting;
        fragmentarray[0].setRetainInstance(true);
        fragmentarray[1].setRetainInstance(true);
        fragmentarray[2].setRetainInstance(true);//这是用来保存之前的，不让他被销毁的！！！
        fragmentarray[3].setRetainInstance(true);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_layout_mid, fragmentarray[0]);
        ft.add(R.id.main_layout_mid, fragmentarray[1]);
        ft.add(R.id.main_layout_mid, fragmentarray[2]);
        ft.add(R.id.main_layout_mid, fragmentarray[3]);

        ft.hide(fragmentarray[1]);//隐藏了这三个
        ft.hide(fragmentarray[2]);
        ft.hide(fragmentarray[3]);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//加载动画
        ft.commit();
    }

//    private void initMap() {
//        mLocation = ((MyApplication) getApplication()).mLocationClient;
//        mLocation.start();
//    }


    //初始化侧边栏
    public static void initPinfo() {
        main_tv_nick.setText(ClientManger.clientName);
        main_tv_sign.setText(ClientManger.personSignature);
        main_iv_head.setImageURI(Uri.fromFile(new File(StaticValues.USER_HEADPATH
                + ClientManger.ClientPhone + ".png")));//注意这个过程
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //这个要在这里初始化才行
        context = this;
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ClientUtils.getInstance().getClientuserphone(context);//初始化用户useremail
        Intent bindIntent = new Intent(this, ClientService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);//绑定这个service之后马上停止
        initview();
        initFragment();
//        initMap();//初始化地图

        //初始化音乐池
        soundpool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        //向音乐池中添加音乐
        msgcoming = soundpool.load(getApplicationContext(), R.raw.msgcoming, 0);//最后一个参数是优先级
        voicerec = soundpool.load(getApplicationContext(), R.raw.voicerec, 0);
        voicesend = soundpool.load(getApplicationContext(), R.raw.voicesend, 0);

        initDialog = new myDialog(context, "加载中...");//但是没有显示要用这个
    }

    //更新个人消息
    public static void refreshPinfo(int type, String value) {
        switch (type) {
            case CSKeys.RESET_USERNAME:
                main_tv_nick.setText(value);
                break;
            case CSKeys.RESET_SIGNATUE:
                main_tv_sign.setText(value);
                break;
            case CSKeys.RESET_HEAD:
                main_iv_head.setImageURI(Uri.fromFile(new File(value)));
                Log.i("--", "侧边栏头像修改成功");
                break;
        }
    }


    //点击事件
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            canShake = false;
            ImageView ib = (ImageView) v;
            ib_part0.setImageResource(R.drawable.icon_chat_home);
            ib_part1.setImageResource(R.drawable.icon_myfriend_nor);
            ib_part2.setImageResource(R.drawable.icon_find_nor1);
            ib_part3.setImageResource(R.drawable.personal_center8);

            if (ib.equals(ib_part0)) {
                System.out.println("点击模块零");
                ib_part0.setImageResource(R.drawable.icon_chat_home_press);//如果按下去会变颜色
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作，通过这种方法
                //获得一个fragmentTrransaction的实例
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[2]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[0]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);//这表示不添加动画
                ft.commit();//保存，因为相当于一个管理器
            }
            if (ib.equals(ib_part1)) {
                System.out.println("点击模块一");
                ib_part1.setImageResource(R.drawable.icon_myfriend_press);
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[2]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[1]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();

            }
            if (ib.equals(ib_part2)) {
                System.out.println("点击模块二");
                ib_part2.setImageResource(R.drawable.icon_find_press1);
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[2]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();
                isStart = true;
                // if (mlocation!=null)
                // mp2.setMyLoc(mlocation);
            }
            if (ib.equals(ib_part3)){
                System.out.println("点击模块三");
                ib_part3.setImageResource(R.drawable.person_center_pressed);//如果按下去会变颜色
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作，通过这种方法
                //获得一个fragmentTrransaction的实例
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[2]);
                ft.show(fragmentarray[3]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);//这表示不添加动画
                ft.commit();//保存，因为相当于一个管理器
            }
        }
    }



    //跳转到最近联系人的界面
    public void LocToMp0() {
        ib_part0.setImageResource(R.drawable.icon_chat_home_press);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(fragmentarray[1]);
        ft.hide(fragmentarray[2]);
        ft.show(fragmentarray[0]);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //播放消息提示音
    public static void playSound(int id) {
        switch (id) {
            case 1:
                soundpool.play(msgcoming, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundpool.play(voicerec, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundpool.play(voicesend, 1, 1, 0, 0, 1);
                break;
        }
    }

    //震动
    public static void Longvibrate()
    {
        Vibrator vibe=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(500);
    }


}


