package com.example.l.l_clothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.l.l_clothers.Adapter.DoctorListAdapter;
import com.example.l.l_clothers.Adapter.PositionListAdapter;
import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.Info.Positioninfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;

import java.util.ArrayList;
import java.util.List;

//在这里面显示最终的结果，显示观察记录
public class LookRecordActivity extends AppCompatActivity {
    static ListView positioninfolist;
    private static List<Positioninfo> data=new ArrayList<Positioninfo>();
    static PositionListAdapter adapter;
    //    static View footer;
    private static boolean loadfinish=true;
    Context context;
    ImageButton btn_back;
    static int disRange=2;
    public static Handler handler=new Handler(){
        public void handleMessage(Message message){//这里需要发送list给服务器请求doctor_list
            if(message.what== CSKeys.GET_RECORD_LIST){
                data=(List<Positioninfo>)message.obj;
                if(adapter==null){
                    return;
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positionrecord_list);
        context=this;
        btn_back=(ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        positioninfolist=(ListView)findViewById(R.id.lv_positioninfolist);
        positioninfolist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String doctorId=data.get(position).doctorId;
//                Intent intent=new Intent(context,FriendAddActivity.class);//不是对话框了
//                intent.putExtra("doctorid",doctorId);
//                startActivity(intent);(不需要点击）
                //然后选择这个医生之后就发送给医生，通知医生

            }
        });

        //这里需要发送

        positioninfolist.setOnScrollListener(new ScrollListener());
//        footer=getLayoutInflater().inflate(R.layout.doctorlist_footer,null);
        adapter=new PositionListAdapter(context,data);
//        doctorlist.addFooterView(footer);
        positioninfolist.setAdapter(adapter);
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
            int lastItemid=positioninfolist.getLastVisiblePosition();//获取当前屏幕最后item的id
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
