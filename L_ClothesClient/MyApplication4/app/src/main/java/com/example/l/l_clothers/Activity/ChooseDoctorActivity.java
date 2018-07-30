package com.example.l.l_clothers.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.l.l_clothers.Adapter.DoctorListAdapter;
import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;

import java.util.ArrayList;
import java.util.List;

public class ChooseDoctorActivity extends Activity {
    static ListView doctorlist;
    private static List<Doctorinfo> data=new ArrayList<Doctorinfo>();
    static DoctorListAdapter adapter;
//    static View footer;
    private static boolean loadfinish=true;
    Context context;
    ImageButton btn_back;
    static int disRange=2;
    public static Handler handler=new Handler(){
        public void handleMessage(Message message){//这里需要发送list给服务器请求doctor_list
            if(message.what== CSKeys.GET_DOCTOR_LIST){
                data=(List<Doctorinfo>)message.obj;
                if(adapter==null){
                    return;
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_doctor);
        context=this;
        btn_back=(ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doctorlist=(ListView)findViewById(R.id.lv_doctorList);
        doctorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String doctorId=data.get(position).doctorId;
                Intent intent=new Intent(context,FriendAddActivity.class);//不是对话框了
                intent.putExtra("doctorid",doctorId);
                startActivity(intent);
                //然后选择这个医生之后就发送给医生，通知医生
                
            }
        });

        //这里需要发送

        doctorlist.setOnScrollListener(new ScrollListener());
//        footer=getLayoutInflater().inflate(R.layout.doctorlist_footer,null);
        adapter=new DoctorListAdapter(context,data);
//        doctorlist.addFooterView(footer);
        doctorlist.setAdapter(adapter);
//        doctorlist.removeFooterView(footer);
    }

    private final class ScrollListener implements AbsListView.OnScrollListener
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i("MainActivity", "onScrollStateChanged(scrollState="
                    + scrollState + ")");
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastItemid=doctorlist.getLastVisiblePosition();//获取当前屏幕最后item的id
            if(lastItemid+1==totalItemCount)//达到数据最后一条记录
            {
                if(totalItemCount>0)
                {
                    if(loadfinish)
                    {
                        loadfinish=false;
//                        doctorlist.addFooterView(footer);
//                        MainActivity.myBinder.getStrangerListMore(disRange);//向Mainactivity获取新的diarange
                    }
                }
            }
        }
    }

}
