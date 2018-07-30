package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.Info.Positioninfo;
import com.example.l.l_clothers.R;

import junit.runner.BaseTestRunner;

import java.util.List;

/**
 * Created by L on 2017/6/11.
 */

//获取每一个动作
public class PositionListAdapter extends BaseAdapter{
    private Context context;
    private List<Positioninfo> list;
    public PositionListAdapter(Context context,List<Positioninfo>list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.position_item,parent,false);
            holder=new viewHolder();
            holder.positionid=(TextView)convertView.findViewById(R.id.postion_name);
            holder.time=(TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder=(viewHolder) convertView.getTag();
        }
        holder.positionid.setText(list.get(position).positionid);
        holder.time.setText(list.get(position).time);
        return convertView;
    }

    private class viewHolder{
        TextView positionid;
        TextView time;
    }
}
