package com.example.l.l_clothers.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.Recoderinfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Service.BluetoothChatService;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.Utils.PasswordUtil;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;

import org.apache.http.conn.scheme.HostNameResolver;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.l.l_clothers.Service.BluetoothChatService.STATE_CONNECTED;

public class TreatActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private String mConnectedDeviceName = null;

//    private ArrayAdapter<String> mConversationArrayAdapter;

    private StringBuffer mOutStringBuffer;

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothChatService  mChatService= null;

    static double firstA;
    static double firstB;
    static double secondA;
    static double secondB;
    static double thirdA;
    static double thirdB;

    static double reallyfirstA;
    static double reallyfristB;
    static double reallysecondA;
    static double reallysecondB;
    static double reallythirdA;
    static double reallythirdB;

    Timer mTimer;
    ImageView treatimage;
    TextView treatinfo;
    Button treatstart;
    TextView record;

    Recoderinfo recoderinfo;

    Context context;

    boolean isclick=false;//判断是否点击了

    String gettreatinfo;
    int gettreatimage;
    int gettreatnumber;



    int time=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat);
        context=this;

        final SharedPreferenceUtil util=new SharedPreferenceUtil(this);

        //蓝牙重新连接的模块
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothChatService(this,mHandler);//然后告诉系统这里已经连接上了就好了
        BluetoothDevice bluetoothDevice=mBluetoothAdapter.getRemoteDevice(ClientManger.address);
        mChatService.connect(bluetoothDevice);
        //已经一个连接了另一个不能连接

        MainActivity.myBinder.getrecord();//发送请求获得数据
        init();
        Intent intent=getIntent();
         gettreatinfo=intent.getStringExtra("treatinfo");
         gettreatimage=intent.getIntExtra("treatimage",0);//如果没有取到，默认是1
         gettreatnumber=intent.getIntExtra("treatid",-1);//后面那个是默认值

        util.SaveValues(gettreatinfo,"false");
        if(util.getvalues(gettreatinfo).equals("true")){
            treatstart.setText("开始训练");
        }else{
            treatstart.setText("上传数据");
            Toast.makeText(context,"请等待一段时间",Toast.LENGTH_LONG).show();
        }

        mTimer=new Timer();
        treatimage.setImageResource(gettreatimage);//设置好图片
        treatinfo.setText(gettreatinfo);

        treatstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//1000之后开始发送,每隔1000的时候发送一次(点击的时候就开始不断发送，之前不发送）
                if(treatstart.getText().equals("开始训练")){
                    isclick=true;
//                    mTimer.schedule(task,0,1000);//直接开始发送,每隔1000的时候发送一次
                    //点击的时候才开始发送
//                    record.setText("标准");
////                    checkformal();
//                    while(record.getText().equals("不标准")){
//                        checkformal();
//                    }
//                    if(record.getText().equals("标准")){
//                        Toast.makeText(context,"请保持姿势5秒钟",Toast.LENGTH_SHORT).show();
//                        //5秒钟的时间
//                        Timer newtimer=new Timer();
//                        TimerTask task1=new TimerTask() {
//                            @Override
//                            public void run() {
//                                checkformal();
//                                if(record.getText().equals("标准")){
//                                    Toast.makeText(context,"您完成了一次训练动作",Toast.LENGTH_SHORT).show();
//                                    record.setText("");
//                                    time++;
//                                    MainActivity.myBinder.sendformaltime(gettreatnumber,time);//还要存入的是此时的动作的类型，动作编号
//                                }
//                            }
//                        };
//                        newtimer.schedule(task1,5000);//五秒钟之后执行一次
//                    }

                }
                if(treatstart.getText().equals("上传数据")){
                    recoderinfo=new Recoderinfo();
                    recoderinfo.firstA=reallyfirstA;
                    recoderinfo.firstB=reallyfristB;
                    recoderinfo.secondA=reallysecondA;
                    recoderinfo.secondB=reallysecondB;
                    recoderinfo.thirdA=reallythirdA;
                    recoderinfo.thirdB=reallythirdB;
                    recoderinfo.positionnumber=gettreatnumber;
                    MainActivity.myBinder.handoutrecord(recoderinfo);//上传数据
                    Toast.makeText(context,"上传了数据",Toast.LENGTH_LONG).show();
                    treatstart.setText("开始训练");
                    util.SaveValues(gettreatinfo,"true");
//                    ClientManger.positionumber[gettreatnumber]=1;
                }
            }
        });
//        checkhasrecord();//检查这个时候输入是否为


        //每隔一段时间执行一次


      //把历史记录搞过来

    }

    //通过这种方法持续获得返回值
        TimerTask task=new TimerTask() {
        @Override
        public void run() {
           //要执行的代码放在其中
            sendMessage("a");//发送a过去
        }
    };





    public void checkformal(){
        if(Math.abs(firstA-reallyfirstA)<10&&Math.abs(firstB-reallyfristB)<10&&Math.abs(secondA-reallysecondA)<10
                &&Math.abs(secondB-reallysecondB)<10){
            record.setText("标准");
        }else{
            record.setText("不标准");
}
    }

@Override
    protected void onDestroy() {
        super.onDestroy();
        mChatService.stop();
        MainActivity.myBinder.sendformaltime(gettreatnumber,time);//退出的时候发送回去
    }





    //最终处理模块
    public void handlecheck() {
        if(isclick==false){
            record.setVisibility(View.GONE);//如果没点击就不显示
        }else{
            checkformal();
            record.setVisibility(View.VISIBLE);
            if (record.getText().equals("标准")) {
                time++;
            }
        }
        ;//此时可以认为开始标准动作了
    }
    public void sendMessage(String message){
        if(mChatService.getState()!= STATE_CONNECTED){
            Toast.makeText(context,"未连接",Toast.LENGTH_LONG).show();
            return;
        }

        if(message.length()>0){
            byte[] send=message.getBytes();
            mChatService.write(send);
        }
    }


    //检查是不是有数据
    public  void checkhasrecord(){
        if(firstA==0&&firstB==0&&secondA==0&&secondB==0){//只用4个
             Toast.makeText(context,"请开始采集数据",Toast.LENGTH_LONG).show();
             treatstart.setText("上传数据");
        }
    }




    //接收到消息
    public static Handler handle=new Handler(){
        public void handleMessage(Message message){
            switch (message.what){//在主界面的地方把资料存进去，发送到这里来
                case CSKeys.GET_RECORD:
                    CSmessage record=(CSmessage)message.obj;
                    JSONObject jsonObject= JSON.parseObject(record.msgJson);
                    firstA=jsonObject.getDouble(JSONKeys.firstA);
                    firstB=jsonObject.getDouble(JSONKeys.firstB);
                    secondA=jsonObject.getDouble(JSONKeys.secondA);
                    secondB=jsonObject.getDouble(JSONKeys.secondB);
                    thirdA=jsonObject.getDouble(JSONKeys.thirdA);
                    thirdB=jsonObject.getDouble(JSONKeys.thirdB);//这里需要改动，只有两个了
                    break;
            }
        }

    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)//Dj就是true
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
//                            mTitle.setText(R.string.title_connected_to);
//                            mTitle.append(mConnectedDeviceName);
//                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
////                    mConversationArrayAdapter.add("Send:  " + writeMessage);//这一块是发送的
                    break;
                case MESSAGE_READ://这是获得的部分

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    byte[] readBuf1=(byte[])msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);//得到的都是string类型
//                    mConversationArrayAdapter.add("Receive" + ":  "
//                            + readMessage);//这里是收到的东西

                    String read1 = null;
                    String read2 =null;
                    String read3= null;
                    String read4 =null;
                    int temp1=0;
                    int temp2=0;
                    int temp3=0;
                    int temp4=0;
                    if(msg.arg1>1){
                        for(int i=0;i<msg.arg1;i++){
                            if(readBuf1[i]=='\n'){
                                for(int j=i+1;j<msg.arg1;j++){
                                    if(readBuf1[j]=='\r'){
                                        read1=readMessage.substring(i+1,j);
                                        temp1=j;
                                        break;
                                    }
                                }
                                for(int k=temp1+2;k<msg.arg1;k++) {
                                    if (readBuf1[k] == '\n') {
                                        read2 = readMessage.substring(temp1+2, k-1);
                                        temp2=k;
                                        break;
                                    }
                                }
                                for(int l=temp2+1;l<msg.arg1;l++) {
                                    if (readBuf1[l] == '\r') {
                                        read3 = readMessage.substring(temp2+1, l);
                                        temp3=l;
                                        break;
                                    }
                                }
                                for(int m=temp3+2;m<msg.arg1;m++){
                                    if(readBuf1[m]=='\n'){
                                        read4=readMessage.substring(temp3+2,m-1);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
//                        mConversationArrayAdapter.add("最终结果为!!" + ":  "
//                                + read1);//这里是收到的东西
//                        mConversationArrayAdapter.add("最终结果为!!" + ":  "
//                                + read2);//这里是收到的东西
//                        mConversationArrayAdapter.add("最终结果为!!" + ":  "
//                                + read3);//这里是收到的东西
//                        mConversationArrayAdapter.add("最终结果为!!" + ":  "
//                                + read4);//这里是收到的东西
                        reallyfirstA=Double.parseDouble(read1);
                        reallyfristB=Double.parseDouble(read2);
                        reallysecondA=Double.parseDouble(read3);
                        reallysecondB=Double.parseDouble(read4);
//                        Toast.makeText(context,"此时的为",Toast.LENGTH_SHORT).show();
                        for(int i=0;i<100;i++){
                            System.out.print(read1);
                        }
                        handlecheck();

                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),"后台连接成功", Toast.LENGTH_SHORT).show();
                    mTimer.schedule(task,0,1000);//在这里就开始发送
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };



    public void init(){
        treatimage=(ImageView)findViewById(R.id.treatimage);
        treatinfo=(TextView)findViewById(R.id.treatinfo);
        treatstart=(Button)findViewById(R.id.treat_start);
        record=(TextView)findViewById(R.id.record);
        record.setText("");

    }
}
