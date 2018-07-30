package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.R;

import java.net.ConnectException;
import java.net.ContentHandler;
import java.util.List;

/**
 * Created by L on 2017/6/1.
 */

public class DoctorListAdapter extends BaseAdapter {
    private Context context;
    private List<Doctorinfo> list;
    public DoctorListAdapter(Context context,List<Doctorinfo>list){
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
            convertView=inflater.inflate(R.layout.doctor_item,parent,false);
            holder=new viewHolder();
            holder.doctorname=(TextView)convertView.findViewById(R.id.doctor_name);
            holder.doctorinfo=(TextView)convertView.findViewById(R.id.doctor_info);
            convertView.setTag(holder);
        }else{
            holder=(viewHolder)convertView.getTag();
        }
        holder.doctorname.setText(list.get(position).doctorname);
        holder.doctorinfo.setText(list.get(position).doctorinfo);
        return convertView;
    }

    private class viewHolder{
        TextView doctorname;
        TextView doctorinfo;
    }
}
