package com.example.l.l_clothers.Activity;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.l.l_clothers.Adapter.TreatListAdapter;
import com.example.l.l_clothers.Info.TreatInfo;
import com.example.l.l_clothers.R;

import java.util.ArrayList;
import java.util.List;

public class TreatlistActivity extends Activity {
    View view;
    List<TreatInfo> list=new ArrayList<TreatInfo>();
    ListView treatlistview;
    TreatListAdapter treatListAdapter;
    Button back;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatlist);
        initlist();
        treatListAdapter=new TreatListAdapter(this,list);
        treatlistview=(ListView)findViewById(R.id.treatlistinfo);
//        back=(Button)findViewById(R.id.btn_back) ;
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        treatlistview.setAdapter(treatListAdapter);
        treatlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TreatInfo treatInfo=list.get(position);
                String treatinfo=treatInfo.getTreatinfo();
                int bitmap=treatInfo.getTreatimage();
                Intent intent=new Intent(TreatlistActivity.this,TreatActivity.class);
                intent.putExtra("treatimage",bitmap);
                intent.putExtra("treatinfo",treatinfo);
                intent.putExtra("treatid",position);
                startActivity(intent);
            }
        });

    }

    public void initlist(){
        TreatInfo treatInfo1=new TreatInfo("测试动作1",R.drawable.position1);
        TreatInfo treatInfo2=new TreatInfo("测试动作2",R.drawable.position2);
        list.add(treatInfo1);
        list.add(treatInfo2);
    }
}
