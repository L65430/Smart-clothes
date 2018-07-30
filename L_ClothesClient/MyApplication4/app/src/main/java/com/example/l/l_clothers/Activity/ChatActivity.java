package com.example.l.l_clothers.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.l.l_clothers.Adapter.FacePageFragmentAdapter;
import com.example.l.l_clothers.Adapter.MoreSelectAdapter;
import com.example.l.l_clothers.Adapter.MsgContentAdapter;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.AllMessage.ChatMessage;
import com.example.l.l_clothers.AllMessage.ClientHandleMessage;
import com.example.l.l_clothers.Fragment.FacePageFragment;
import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.Utils.ClientUtils;
import com.example.l.l_clothers.Utils.FaceConversionUtil;
import com.example.l.l_clothers.Utils.FileUtils;
import com.example.l.l_clothers.Utils.ImageUtil;
import com.example.l.l_clothers.Utils.MsgConvertionUtil;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;
import com.example.l.l_clothers.Utils.SoundUtil;
import com.example.l.l_clothers.View.MyListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends FragmentActivity {
    SharedPreferenceUtil util;
    String friendId;//聊天好友的ID
    String friendName;//聊天好友名
    public static boolean isgroupMsg=false;//是否是群组聊天
    TextView tv_friendName;//聊天好友名
    private EditText Et_msgEdit;
    private ImageButton IbSend_faceicon;// 发送表情
    private ImageButton IbSend_moreselect;// 发送更多选项中的..
    private ImageButton IbSend_voice;// 发送语音
    private Context context;

    //更多选项窗口
    private LinearLayout ll_popwindow;// 隐藏窗口
    private boolean isMoreSelectWindowInited = false;
    private boolean isMoreSelectWindowOpened = false;
    private GridView moreselect_gridView;// 更多选项(图片 拍照...)
    private BaiduASRDigitalDialog mBaiduASRDigitalDialog;// 百度语音识别

    //分享图片相关
    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private String PHOTO_FILE_NAME = "";
    private File tempFile;// 临时文件,这样做可以得到原图，而不至于得到非常小的缩略图
    /** 图片存储路径 */
    private static final String IMAGEPATH = "/sdcard/L/imageRecord/";

    //语音窗口
    private boolean isVoiceWindowInitede=false;
    private boolean isVoiceWindowOpened = false;
    private TextView Tv_voiceRecding;
    private ImageView Ib_voiceRecding;
    private LinearLayout Ll_voiceRecding;
    private boolean isTosendTextMag = false;// 判断是否是要发送文本信息,是则点击不弹出录音窗口，否则弹出
    private boolean closeRec = false;// 停止录音
    private int voiceLong = 0;// 录音时间
    private String tempVoicePath = null;// 语音文件路径

    //用于语音播放
    private MediaPlayer mediaPlayer=null;
    //用于完成录音
    private MediaRecorder mediaRecorder=null;
    //录音存储路径
    private static final String VOICEPATH="/sdcard/L/voiceRecord/";

    //表情栏窗口
    private GridView face_pagegridView;// 表情栏gridview
    ViewPager FaceviewPager;// 表情栏Viewpager
    private ArrayList<Fragment> facefragmentsList;
    private boolean isFaceWindowInited = false;
    private boolean isFaceWindowOpened = false;
    private LinearLayout ll_face_dot;// 分页节点
    private RelativeLayout rl_face;// 表情栏
    int page_indext = 0;// 当前表情页
    ImageView face_dot0;// 三页表情的下标点
    ImageView face_dot1;
    ImageView face_dot2;


    //消息数据库
    ChatDBUtils MyDatabase;
    MsgConvertionUtil convertionUtil;//消息转化工具

    public Handler handler;
    private MyListView Lv_msgCotent;//消息列表
    private static List<ClientHandleMessage> msglist;//消息集合，本地端存储的所有消息
    private static MsgContentAdapter msgContentAdapter;//消息适配器

    //有人发来信息，显示在界面上
    public static void AddMsgItem(ClientHandleMessage msgClient)//本地端储存的数据
    {
        msglist.add(msgClient);
        msgContentAdapter.notifyDataSetChanged();//及时刷新
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        context=this;
        util=new SharedPreferenceUtil(context);
        MyDatabase=new ChatDBUtils(context);
        convertionUtil=MsgConvertionUtil.getInstance();
        initViews();
        initData();//初始化内容
        handler = new Handler() {//也是一样的，这里新建一个handler，handler接收发送的消息
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticValues.FACE_GETTED:
                        int index = Et_msgEdit.getSelectionStart();//获得光标的起始位置
                        Et_msgEdit.getText().insert(index,
                                (SpannableString) msg.obj);//SpannableString表示对文字进行处理
                        Et_msgEdit.setSelection(index
                                + ((SpannableString) msg.obj).length());//设置此时的光标
                        break;
                    case StaticValues.DEL_EDIT_TEXT:
                        int selection = Et_msgEdit.getSelectionStart();
                        String text = Et_msgEdit.getText().toString();
                        if (selection > 0) {
                            String text2 = text.substring(selection - 1,selection);//提取两个字符的方法
                            if ("]".equals(text2)) {
                                int l = selection - 2;
                                while(l>=0){
                                    String text3 = text.substring(l, l+1);
                                    if ("[".equals(text3)){
                                        Et_msgEdit.getText().delete(l, selection);
                                        break;
                                    }
                                    l--;
                                }
                            }else
                                Et_msgEdit.getText().delete(selection - 1, selection);
                        }
                        break;
                    case StaticValues.VOICE_REC_TIME:
                        voiceLong = msg.arg1;
                        Tv_voiceRecding.setText(msg.arg1 + "″");
                        break;
                }
            };
        };
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        closeHintinpit();
    }

    //关闭输入隐藏框
    public void closeHintinpit()
    {
        if(isFaceWindowOpened)
            closeFaceWindow();
        if(isVoiceWindowOpened)
            closeVoiceWindow();
        if(isMoreSelectWindowInited)
            closeMoreSelectWindow();
    }


    //初始化消息列表
    private void initData() {
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendId");//这个intent是从Mainactivity传过来的,还可能是从part0传过来的
        friendName = intent.getStringExtra("friendName");
        isgroupMsg = intent.getBooleanExtra("isgroupMsg", false);
        tv_friendName.setText(friendName);
        System.out.print("接收到的friendname是:"+friendName);

        if(util.getvalues(StaticValues.userjob).equals("医生")){
            MainDoctorActivity.mp0talk.setChatingId(friendId);
        }else{
            MainActivity.mp0talk.setChatingId(friendId);
        }
        FaceConversionUtil.getInstance().getFileText(context);//把字符变成表情
        Lv_msgCotent = (MyListView) findViewById(R.id.Lv_msgCotent);
        msglist = new ArrayList<ClientHandleMessage>();
        msgContentAdapter = new MsgContentAdapter(context, msglist);
        Lv_msgCotent.setAdapter(msgContentAdapter);//就是对这listview进行adapter

        getMsgFromDb();//获得和该好友的聊天记录

        Lv_msgCotent.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            //长按后出现的情况
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("编辑");
                menu.add(0, 0, 0, "删除");
                menu.add(0, 1, 0, "取消");
                menu.setQwertyMode(true);
            }
        });
        //关闭消息列表，关闭输入法
        Lv_msgCotent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeInputMethod();
                return false;
            }
        });

        //下拉显示更多历史消息
        Lv_msgCotent.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1500);
                            getHistoryMsg();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Lv_msgCotent.onRefreshComplete();//在listview里面的
                    }
                }.execute(null, null, null);//在主线程中执行这个操作
            }
        });
    }

    //上下文菜单监听
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int position=menuInfo.position-1;//menuinfo.positon从1开始计数的，要转换一下
        ClientHandleMessage clientHandleMessage=msglist.get(position);
        JSONObject jsonObject= JSON.parseObject(clientHandleMessage.getMsgJson());
        String time=jsonObject.getString(JSONKeys.sendTime);
        int type=jsonObject.getInteger(JSONKeys.msgType);
        switch (item.getItemId())
        {
            case 0://删除消息
                msglist.remove(position);
                msgContentAdapter=new MsgContentAdapter(context,msglist);//因为msglist是消息源
                msgContentAdapter.notifyDataSetChanged();
                Lv_msgCotent.setAdapter(msgContentAdapter);
                if(position>0)
                {
                    Lv_msgCotent.setSelection(position-1);//减少一位
                }else
                {
                    Lv_msgCotent.setSelection(0);//如果没有的话就定位到原来这个位置
                }
                //如果是语音或者图片，要删除文件，还要删除数据库存储数据
                if(type==CSKeys.CHATING_VOICE_MSG)
                {
                    FileUtils.deletefile(jsonObject.getString(JSONKeys.voicePath));
                }else if(type==CSKeys.CHATING_IMAGE_MSG)
                {
                    FileUtils.deletefile(jsonObject.getString(JSONKeys.imagePath));//都是在删除本地文件
                }
                MyDatabase.deleteonemessage(friendId,jsonObject.getString(JSONKeys.sendTime));
                break;
            case 1:
                String msg=msglist.get(position).getMsgJson();
                break;
        }
        return super.onContextItemSelected(item);
    }


    //初始化消息输入框
    private void initViews()
    {
        ll_popwindow = (LinearLayout) findViewById(R.id.ll_popwindow);// 隐藏框
        IbSend_moreselect = (ImageButton) findViewById(R.id.IbSend_moreselect);
        IbSend_faceicon = (ImageButton) findViewById(R.id.IbSend_faceicon);
        IbSend_voice = (ImageButton) findViewById(R.id.IbSend_voice);
        Et_msgEdit = (EditText) findViewById(R.id.Et_msgEdit);
        Et_msgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMoreSelectWindowOpened)
                {
                    closeMoreSelectWindow();//关闭更多选项窗口
                }else if(isVoiceWindowOpened)
                {
                    closeVoiceWindow();//关闭录音窗口
                }else if(isFaceWindowOpened)
                {
                    closeFaceWindow();//关闭表情栏
                }
            }
        });
        Et_msgEdit.addTextChangedListener(new TextWatcher() {//设置编辑框监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>0)
                {
                    isTosendTextMag=true;//表示可以发送
                    IbSend_voice.setBackgroundResource(R.drawable.chatview_send_msg_ok);
                }else if(s.toString().length()==0)
                {
                    isTosendTextMag=false;
                    IbSend_voice.setBackgroundResource(R.drawable.chatview_voice_rec_selector);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_friendName=(TextView)findViewById(R.id.chating_friendName);

    }

    //从数据库得到和该好友的聊天记录，判断有没有记录，如果有就显示出来
    public void getMsgFromDb()
    {
        ChatDBUtils chatDBUtils=new ChatDBUtils(context);
        if (chatDBUtils.isTableExit("msg" + ClientManger.clientId + "_" + friendId)) {
            List<ChatMessage> list =chatDBUtils.getrecentmessage(friendId, 0, 15);//这里用的方法是按照倒序查找15条
            if (list.size() > 0) {
                // 把数据库存储的消息转化为界面显示的消息
                MsgConvertionUtil convertionUtil = MsgConvertionUtil
                        .getInstance();
                List<ClientHandleMessage> tempList = new ArrayList<ClientHandleMessage>();//判断是不是别人发送的消息，判断是在屏幕左边还是右边
                for (int i = list.size()-1; i>=0; i--) {
                    ChatMessage msgDb = list.get(i);
                    tempList.add(convertionUtil.convert_Db2Client(msgDb));
                }
                msglist.addAll(tempList);
                msgContentAdapter.notifyDataSetChanged();
                Lv_msgCotent.setSelection(msgContentAdapter.getCount() - 1);
            }
        } else {
            chatDBUtils.createtable(friendId);// 建表
        }
    }

    //下拉显示历史更多10条消息
    public void getHistoryMsg()
    {
        ChatDBUtils chatDBUtils=new ChatDBUtils(context);
        if(chatDBUtils.isTableExit("msg"+ClientManger.clientId+"_"+friendId))
        {
            List<ChatMessage>list=chatDBUtils.getrecentmessage(friendId,0,msglist.size()+10);
            int selection=list.size()-msglist.size();//因为是还要继续往下拉10条消息，所以是从这里开始的
            if(list.size()>0)
            {
                //数据库存储转为界面
                MsgConvertionUtil convertionUtil=MsgConvertionUtil.getInstance();
                List<ClientHandleMessage>templist=new ArrayList<ClientHandleMessage>();
                for(int i=list.size()-1;i>=0;i--)
                {
                    ChatMessage chatMessage=list.get(i);
                    templist.add(convertionUtil.convert_Db2Client(chatMessage));//转化列表里面增加一条
                }
                msglist.clear();
                msglist.addAll(templist);//先把list里面的所有都加进去,msglist里面存的是网页转化后的结果
                msgContentAdapter=new MsgContentAdapter(context,msglist);
                msgContentAdapter.notifyDataSetChanged();
                Lv_msgCotent.setAdapter(msgContentAdapter);
                Lv_msgCotent.setSelection(selection);//定位到最上面的几条
            }
        }else
        {
            MyDatabase.createtable(friendId);
        }
    }


    //关闭更多选项窗口
    private void closeMoreSelectWindow()
    {
        ll_popwindow.setVisibility(View.GONE);
        moreselect_gridView.setVisibility(View.GONE);
        isMoreSelectWindowOpened=false;
        IbSend_moreselect.setBackgroundResource(R.drawable.more_select_button_selector);
    }

    //打开更多选项窗口
    private void openMoreSelectWindow()
    {
        closeInputMethod();
        if(isFaceWindowOpened)
        {
            closeFaceWindow();
        }
        if(isVoiceWindowOpened)
        {
            closeVoiceWindow();
        }
        if(!isMoreSelectWindowInited)
            initMoreSelectWindow();
        ll_popwindow.setVisibility(View.VISIBLE);
        moreselect_gridView.setVisibility(View.VISIBLE);
        IbSend_moreselect.setBackgroundResource(R.drawable.chatview_text_button_selector);
        isMoreSelectWindowOpened=true;
    }


    //切换moreselectwindow
    private void toggleMoreSelectWindow()
    {
        closeInputMethod();
        if(isMoreSelectWindowOpened)
            closeMoreSelectWindow();
        else
            openMoreSelectWindow();
    }

    //关闭输入法
    private void closeInputMethod()
    {
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//这是表示弹出软键盘,获取系统的service，就是执行软键盘这个操作
        imm.hideSoftInputFromInputMethod(Et_msgEdit.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //初始化表情栏
    private void initFaceWindow()
    {
        facefragmentsList=new ArrayList<Fragment>();
        facefragmentsList.add(new FacePageFragment(1, handler));//在这里传入了handler,前面的1表示第一页
        facefragmentsList.add(new FacePageFragment(2,handler));
        facefragmentsList.add(new FacePageFragment(3,handler));//3页表情所以传入3个
        rl_face=(RelativeLayout)findViewById(R.id.rl_face);
        face_dot0 = (ImageView) findViewById(R.id.face_dot0);//加载三个屏的表情
        face_dot1 = (ImageView) findViewById(R.id.face_dot1);
        face_dot2 = (ImageView) findViewById(R.id.face_dot2);
        face_dot0.setBackgroundResource(R.drawable.face_page_dot_selected);
        FaceviewPager=(ViewPager)findViewById(R.id.face_vPager);//用来表示左右滑动的
        FaceviewPager.setCurrentItem(0);
        FaceviewPager.setAdapter(new FacePageFragmentAdapter(getSupportFragmentManager(),facefragmentsList));
        FaceviewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        ll_face_dot=(LinearLayout)findViewById(R.id.ll_face_dot);
        isFaceWindowInited=true;
    }

    //打开表情窗口
    private void openFaceWindow()
    {
        closeInputMethod();
        if(isMoreSelectWindowOpened)
            closeMoreSelectWindow();
        if(isVoiceWindowOpened)
            closeVoiceWindow();
        if(!isFaceWindowInited)
            initFaceWindow();
        Animation enter= AnimationUtils.loadAnimation(context,R.anim.dialog_enter);
        FaceviewPager.startAnimation(enter);
        ll_popwindow.setVisibility(View.VISIBLE);
        rl_face.setVisibility(View.VISIBLE);
        IbSend_faceicon.setBackgroundResource(R.drawable.chatview_face_button_selector);
        isFaceWindowOpened=true;

    }

    //关闭表情窗口
    private void closeFaceWindow()
    {
        ll_popwindow.setVisibility(View.GONE);
        rl_face.setVisibility(View.GONE);
        IbSend_faceicon.setBackgroundResource(R.drawable.chatview_face_button_selector);
        isFaceWindowOpened=false;
    }

    //切换表情窗口
    private void toggleFaceWindow()
    {
        if(isFaceWindowOpened)
            closeFaceWindow();
        else
            openFaceWindow();
    }

    //关闭录音窗口
    private void closeVoiceWindow()
    {
        Ll_voiceRecding.setVisibility(View.GONE);
        ll_popwindow.setVisibility(View.GONE);
        isVoiceWindowOpened=false;
    }

    //切换打开关闭录音窗口
    private void toggleVoiceWindow()
    {
        if(isVoiceWindowOpened)
        {
            closeVoiceWindow();
        }else
            openVoiceWindow();//就是反着来

    }

    //打开录音窗口
    private void openVoiceWindow()
    {
        closeInputMethod();
        if(isMoreSelectWindowOpened)
            closeMoreSelectWindow();
        if(isFaceWindowOpened)
            closeFaceWindow();
        if(!isVoiceWindowInitede)//就是没有初始化的意思
            initVoiceWindow();
        ll_popwindow.setVisibility(View.VISIBLE);
        Ll_voiceRecding.setVisibility(View.VISIBLE);
        isVoiceWindowOpened=true;
    }


    //页面滑动监听
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int select_index) {
            switch (select_index)
            {
                case 0:
                    if(page_indext==1)
                    {
                        face_dot1.setBackgroundResource(R.drawable.face_page_dot);
                    }else if(page_indext==2)
                    {
                        face_dot2.setBackgroundResource(R.drawable.face_page_dot);
                    }
                    face_dot0.setBackgroundResource(R.drawable.face_page_dot_selected);
                    break;
                case 1:
                    if (page_indext == 0) {
                        face_dot0.setBackgroundResource(R.drawable.face_page_dot);
                    } else if (page_indext == 2) {
                        face_dot2.setBackgroundResource(R.drawable.face_page_dot);
                    }
                    face_dot1
                            .setBackgroundResource(R.drawable.face_page_dot_selected);
                    break;
                case 2:
                    if (page_indext == 0) {
                        face_dot0.setBackgroundResource(R.drawable.face_page_dot);
                    } else if (page_indext == 1) {
                        face_dot1.setBackgroundResource(R.drawable.face_page_dot);
                    }
                    face_dot2
                            .setBackgroundResource(R.drawable.face_page_dot_selected);
                    break;
            }
            page_indext=select_index;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    //初始化MoreSelectWindow
    private void initMoreSelectWindow()
    {
        moreselect_gridView=(GridView)findViewById(R.id.moreselect_gridView);
        MoreSelectAdapter adapter=new MoreSelectAdapter(context);//传入这个activity
        moreselect_gridView.setAdapter(adapter);//就是设置好控件然后加上adapter就好了
        isMoreSelectWindowInited=true;//当前正在初始化
        moreselect_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView selecView=(ImageView)view.findViewById(R.id.selec_icon);
                Animation animation= AnimationUtils.loadAnimation(context,R.anim.moselect_item_amin);//加载了动画类
                selecView.startAnimation(animation);//对这个imageview使用动画的意思
                switch (position)
                {
                    case 0:
                        //图片
                        getFromGallery();
                        break;
                    case 1:
                        //拍照
                        takePhoto();;
                        break;
                    case 2:
                        Toast.makeText(context,"尚在开发中，请期待",Toast.LENGTH_LONG).show();
                    case 3:

                        Toast.makeText(context,"若想了解更多信息，请登录官网：115.28.34.148/",Toast.LENGTH_LONG).show();
                        break;

                    case 4:
                        Bundle params=new Bundle();
                        //设置成自己的key
                        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
                                "LONYUUZ9scpusCGsGRy7rKmh");
                        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,"O1Xs5r9n9Lbo7Kt3Nzq3sAzfqXt90IGy");
                        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,BaiduASRDigitalDialog.THEME_GREEN_DEEPBG);//设置对话框风格
                        //可以增加一个亮点，自己更改对话框样式
                        mBaiduASRDigitalDialog=new BaiduASRDigitalDialog(context,params);
                        mBaiduASRDigitalDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
                            //回调函数
                            @Override
                            public void onResults(Bundle results) {
                                ArrayList<String>list=results.getStringArrayList(RESULTS_RECOGNITION);
                                String str= Arrays.toString(null==list?null:list.toArray());
                                //发送语音识别
                                String content=str.substring(1,str.length()-2);//识别后的文字消息
                                CSmessage textMsg=new CSmessage();
                                textMsg.symbol='+';//得到的是文字消息
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put(JSONKeys.msgType,CSKeys.CHATING_TEXT_MSG);
                                jsonObject.put(JSONKeys.userId,ClientManger.clientId);
                                jsonObject.put(JSONKeys.friendId,friendId);
                                jsonObject.put(JSONKeys.sendTime,ClientUtils.getNowTime());
                                jsonObject.put(JSONKeys.msgCotent,content);
                                textMsg.msgJson=jsonObject.toJSONString();
                                ClientHandleMessage msgClient=convertionUtil.Convert_Net2Client(context,textMsg,false);
                                AddMsgClientitem(msgClient);

                               if(util.getvalues(StaticValues.userjob).equals("医生")){
                                    MainDoctorActivity.myBinder.sendCharMsgdoctor(textMsg);
                                }else{
                                   MainActivity.myBinder.sendCharMsg(textMsg);
                               }
                                //保存到数据库
                                ChatMessage message=convertionUtil.Convert_Net2Db(0,1,context,textMsg);
                                MyDatabase.savechat(message,friendId);
                            }
                        });
                        mBaiduASRDigitalDialog.show();
                        break;
                    case 5:
                        Toast.makeText(context,"尚在开发中，请期待",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //从相册获得
    private void getFromGallery()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//这个就是用来调用系统程序用的
        startActivityForResult(intent,PHOTO_REQUEST_GALLERY);
    }

    //拍照
    public void takePhoto()
    {
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        if(FileUtils.hasSdcard())
        {
            PHOTO_FILE_NAME=System.currentTimeMillis()+".jpg";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME)));
        }else
        {
            Toast.makeText(context,"未发现内存卡",Toast.LENGTH_LONG).show();
        }
        startActivityForResult(intent,PHOTO_REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            if(requestCode==PHOTO_REQUEST_CAMERA)
            {
                tempFile=new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);//这是用来存放临时的文件的，可以得到原图而不是缩略图
                openPreviewDialog(Uri.fromFile(tempFile));
            }else if(requestCode==PHOTO_REQUEST_GALLERY){
                if(data!=null)
                {
                    Uri uri=data.getData();
                    openPreviewDialog(uri);
                }
            }
        }
    }

    //图片预览对话框
    private void openPreviewDialog(Uri uri)
    {
        final Dialog previewDialog=new Dialog(context,R.style.Dialog_Fullscreen);//表示是满屏幕
        previewDialog.setCanceledOnTouchOutside(true);//点击其他部位消失
        Window window=previewDialog.getWindow();//显示设置动画
        window.setWindowAnimations(R.style.dialog_animstyle);
        WindowManager.LayoutParams wl=window.getAttributes();//获得资源文件
        previewDialog.onWindowAttributesChanged(wl);//设置对话框动画属性
        LinearLayout dialoglayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.takephoto_preivew_dialog,null);
        previewDialog.setContentView(dialoglayout);
        previewDialog.setCancelable(true);
        final ImageView Iv_takenPic = (ImageView) dialoglayout
                .findViewById(R.id.Iv_takenPic);
        Button Bt_send_takenPic = (Button) dialoglayout
                .findViewById(R.id.Bt_send_takenPic);
        ImageButton btn_preview_back = (ImageButton) dialoglayout
                .findViewById(R.id.btn_preview_back);
        Iv_takenPic.setImageURI(uri);
        if (tempFile != null)
            tempFile.delete();
        previewDialog.show();
        Bt_send_takenPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送图片信息
                Bitmap bitmap=((BitmapDrawable)Iv_takenPic.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, 480, 800, true);
                int size = bitmap.getWidth() * bitmap.getHeight();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
                // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                // 发送时要发送这个,客户端处理只用保存图片的path
                // msg.setMsgBytes(baos.toByteArray());
                // 保存到本地文件
                String imagePath = IMAGEPATH + ClientManger.clientId + "_"
                        + friendId + "_" + System.currentTimeMillis() + ".png";
                ImageUtil.getInstance().saveImage(context, baos.toByteArray(),
                        imagePath);

                //发送图片信息
                CSmessage imageMsg=new CSmessage();
                imageMsg.symbol='-';
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(JSONKeys.msgType,CSKeys.CHATING_IMAGE_MSG);
                jsonObject.put(JSONKeys.userId,ClientManger.clientId);
                jsonObject.put(JSONKeys.friendId,friendId);
                jsonObject.put(JSONKeys.sendTime,ClientUtils.getNowTime());
                imageMsg.msgJson=jsonObject.toJSONString();
                imageMsg.msgBytes=baos.toByteArray();

                ClientHandleMessage msgClient=new ClientHandleMessage();//客户端处理的类,界面显示是左边还是右边
                JSONObject msgJson=new JSONObject();
                msgJson.put(JSONKeys.msgType,CSKeys.CHATING_IMAGE_MSG);
                msgJson.put(JSONKeys.sendTime,ClientUtils.getNowTime());
                msgJson.put(JSONKeys.imagePath,imagePath);
                msgClient.setMsgJson(msgJson.toJSONString());
                msgClient.setGetted(false);//是发送的



                AddMsgClientitem(MsgConvertionUtil.getInstance().Convert_Net2Client(context,imageMsg,false));//显示这一项
                if(util.getvalues(StaticValues.userjob).equals("医生")){
                    MainDoctorActivity.myBinder.sendCharMsgdoctor(imageMsg);
                }else{
                    MainActivity.myBinder.sendCharMsg(imageMsg);
                }
                //保存到数据库
                MyDatabase.savechat(convertionUtil.ConvertClient2Db(msgClient),friendId);//客户端转化为数据库
                previewDialog.dismiss();
            }
        });

        btn_preview_back.setOnClickListener(new View.OnClickListener() {//返回
            @Override
            public void onClick(View v) {
                previewDialog.dismiss();
            }
        });
    }

    //初始化VoiceWindow
    private void initVoiceWindow()
    {
        Ll_voiceRecding=(LinearLayout)findViewById(R.id.Ll_voiceRecding);
        Tv_voiceRecding=(TextView)findViewById(R.id.Tv_voiceRecding);
        Ib_voiceRecding=(ImageView)findViewById(R.id.Ib_voiceRecding);
        Ib_voiceRecding.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {//motionevent表示动作事件
                voiceRecTimeTh timeTh=new voiceRecTimeTh();
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        MainDoctorActivity.playSound(3);//录音提示
                        Ib_voiceRecding.setScaleX(0.7f);//表示对图像进行裁剪
                        Ib_voiceRecding.setScaleY(0.7f);
                        Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth()>>1);//就表示对图片的尺寸进行裁剪
                        Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight()>>1);
                        //开始录音
                        String voiceName=System.currentTimeMillis()+"";//录音文件名
                        tempVoicePath=voiceName;//这就是路径
                        SoundUtil.getInstance().startRecord(context,voiceName);
                        timeTh.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        Ib_voiceRecding.setScaleX(1.0f);
                        Ib_voiceRecding.setScaleY(1.0f);
                        Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth() >> 1);
                        Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight() >> 1);
                        //停止录音
                        timeTh.stopRec();
                        SoundUtil.getInstance().stopRecord();
                        if(voiceLong<1)
                        {
                            SoundUtil.getInstance().deleteVoiceFile();//时间太短不要
                        }else
                        {
                            //发送语音消息
                            byte[]voiceBytes=SoundUtil.getInstance().getVoicebytes(context,tempVoicePath);//把文件变成语音消息
                            CSmessage voiceMsg=new CSmessage();//这是和服务器之间的通讯包
                            voiceMsg.symbol='-';//表示不是语音包
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put(JSONKeys.msgType, CSKeys.CHATING_VOICE_MSG);
                            jsonObject.put(JSONKeys.userId,ClientManger.clientId);
                            jsonObject.put(JSONKeys.sendTime, ClientUtils.getNowTime());
                            jsonObject.put(JSONKeys.friendId,friendId);
                            jsonObject.put(JSONKeys.voiceTime,voiceLong);
                            voiceMsg.msgJson=jsonObject.toJSONString();
                            voiceMsg.msgBytes=voiceBytes;

                            ClientHandleMessage msgClient=new ClientHandleMessage();
                            JSONObject msgJson=new JSONObject();
                            msgJson.put(JSONKeys.msgType,
                                    CSKeys.CHATING_VOICE_MSG);
                            msgJson.put(JSONKeys.sendTime, ClientUtils.getNowTime());
                            msgJson.put(JSONKeys.voicePath, SoundUtil.getInstance()
                                    .getVoicePath());
                            msgJson.put(JSONKeys.voiceTime, voiceLong);
                            msgClient.setMsgJson(msgJson.toJSONString());
                            msgClient.setGetted(false);
                            AddMsgClientitem(msgClient);//显示
                            if(util.getvalues(StaticValues.userjob).equals("医生")){
                                MainDoctorActivity.myBinder.sendCharMsgdoctor(voiceMsg);
                            }else{
                                MainActivity.myBinder.sendCharMsg(voiceMsg);
                            }
                            //保存到数据库
                            MyDatabase.savechat(convertionUtil.ConvertClient2Db(msgClient),friendId);

                        }
                        voiceLong=0;
                        Tv_voiceRecding.setText("点击开始录音");
                        break;

                }
                return false;
            }
        });
        isVoiceWindowInitede=true;//表示初始化结束了
    }

    //录音计时器
    private class voiceRecTimeTh extends Thread
    {
        int tempLong=0;
        public voiceRecTimeTh(){
            closeRec=false;
        }
        public void stopRec()
        {
            closeRec=true;
        }
        public void run()
        {
            while(!closeRec)
            {
                try
                {
                    Message msg=handler.obtainMessage();
                    msg.what= StaticValues.VOICE_REC_TIME;//表示记录录音时间
                    msg.arg1=tempLong;
                    handler.sendMessage(msg);
                    Thread.sleep(1000);//在睡眠的时候进程中值不发生改变，进程重新开始的时候恢复到之前的状况
                    tempLong++;//在没有关闭之前不断发送时间，每次睡眠一擦
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //点击按钮
    public void onButtonClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_back:
                finish();
                break;
            case R.id.IbSend_faceicon:
                System.out.print("点击了打开表情");
                toggleFaceWindow();
                break;
            case R.id.IbSend_moreselect:
                System.out.print("点击了打开更多");
                toggleMoreSelectWindow();
                break;
            case R.id.IbSend_voice:
                System.out.print("点击了打开录音");
                if(isTosendTextMag)
                {
                    String content=Et_msgEdit.getText().toString();
                    if(!content.equals(""))
                    {
                        CSmessage textMsg=new CSmessage();
                        textMsg.symbol='+';
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put(JSONKeys.msgType,CSKeys.CHATING_TEXT_MSG);
                        jsonObject.put(JSONKeys.userId,ClientManger.clientId);
                        jsonObject.put(JSONKeys.friendId,friendId);
                        jsonObject.put(JSONKeys.msgCotent,content);
                        jsonObject.put(JSONKeys.sendTime,ClientUtils.getNowTime());
                        System.out.print("json包中要发送的friendid是："+friendId);
                        textMsg.msgJson=jsonObject.toJSONString();

                        //显示
                        ClientHandleMessage msgClient=convertionUtil.Convert_Net2Client(context,textMsg,false);
                        AddMsgClientitem(msgClient);//这是添加到界面用的

                        if(util.getvalues(StaticValues.userjob).equals("医生")){
                            MainDoctorActivity.myBinder.sendCharMsgdoctor(textMsg);
                        }else{
                            MainActivity.myBinder.sendCharMsg(textMsg);
                        }//这是发送消息用的(从part0传过来的friendid存在msgtext中，发送到服务器）
                        //点击的时候还需要及时更新对方的UI，如果对方UI没变是不行的

                        Et_msgEdit.setText("");
                        //保存到数据库
                        ChatMessage msgdb=convertionUtil.Convert_Net2Db(0,1,context,textMsg);
                        MyDatabase.savechat(msgdb,friendId);
                    }
                } else
                    toggleVoiceWindow();
                break;
        }
    }


    //发送消息，更新显示界面
    public void AddMsgClientitem(ClientHandleMessage msgClient)
    {
        msglist.add(msgClient);
        msgContentAdapter.notifyDataSetChanged();
        Lv_msgCotent.setSelection(msgContentAdapter.getCount()-1);
    }

    //清空该好友聊天记录
    public static void clearMsg()
    {
        msglist.clear();
        msgContentAdapter.notifyDataSetChanged();//也是清空再更新
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mBaiduASRDigitalDialog!=null)
        {
            mBaiduASRDigitalDialog.dismiss();//释放资源
        }
        if(util.getvalues(StaticValues.userjob).equals("医生")){
            MainDoctorActivity.mp0talk.setChatingId("");
        }else{
            MainActivity.mp0talk.setChatingId("");
        }
       //没有正在聊天的好友
        updateRecentChat();//就是关闭activity的时候会进行更新
    }


    //更新最近联系人数据库
    public void updateRecentChat() {
        ChatDBUtils chatDBUtils = new ChatDBUtils(context);
        Chatinfoentry chatinfoentry = new Chatinfoentry();
        if (msglist.size() > 0)//消息集合
        {
            ClientHandleMessage msgClient = msglist.get(msglist.size()-1);
            JSONObject jsonObject = JSON.parseObject(msgClient.getMsgJson());
            int type = jsonObject.getIntValue(JSONKeys.msgType);//这是空的
            String content = "";
            if (type == CSKeys.CHATING_IMAGE_MSG) {
                content = "[图片]";
            } else if (type == CSKeys.CHATING_VOICE_MSG) {
                content = "[语音]";
            } else if (type == CSKeys.CHATING_TEXT_MSG) {
                content = jsonObject.getString(JSONKeys.msgCotent);
            }
            chatinfoentry.setFriendId(friendId);
            chatinfoentry.setFriendName(friendName);
            chatinfoentry.setChatContent(content);
            chatinfoentry.setChatCreatTime(jsonObject.getString(JSONKeys.sendTime));
            chatinfoentry.setMsg_num(0);
            chatinfoentry.setMsgtype(0);//表示是发出去的
            if(util.getvalues(StaticValues.userjob).equals("医生")){
                MainDoctorActivity.mp0talk.updaterecentchatitem(friendId, jsonObject.getString(JSONKeys.sendTime), content, true);
            }else{
                MainActivity.mp0talk.updaterecentchatitem(friendId, jsonObject.getString(JSONKeys.sendTime), content, true);//数据库更改，最近联系人界面也要更新
            }
        } else
        {
            chatinfoentry.setChatContent("");
            chatinfoentry.setFriendId(friendId);
            chatinfoentry.setFriendName(friendName);
            chatinfoentry.setChatCreatTime("");
            chatinfoentry.setMsg_num(0);
            chatinfoentry.setMsgtype(0);//因为是发送出去的
            if(util.getvalues(StaticValues.userjob).equals("医生")){
                MainDoctorActivity.mp0talk.updaterecentchatitem(friendId,"","",true);
            }else{
                MainActivity.mp0talk.updaterecentchatitem(friendId,"","",true);//就是一条消息都没有
            }
        }
        if(chatDBUtils.isTableExit(friendId))
        {
            chatDBUtils.updaterecentchat(chatinfoentry);
        }else
        {
            chatDBUtils.insertrecentchat(chatinfoentry);
        }
    }



}

