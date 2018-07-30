package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.l_clothers.Info.Doctorinfo;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;

import java.io.File;
import java.util.List;

/**
 * Created by L on 2017/6/10.
 */

public class PatientListAdapter extends BaseAdapter{
    private Context context;
    private List<FriendInfo> list;
    public PatientListAdapter(Context context, List<FriendInfo> list){
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
        PatientListAdapter.viewHolder holder;
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.patient_item,parent,false);
            holder=new viewHolder();
            holder.patientname=(TextView)convertView.findViewById(R.id.friend_name);
            holder.patient_head=(ImageView) convertView.findViewById(R.id.friend_headpic);
            convertView.setTag(holder);
        }else{
            holder=(viewHolder) convertView.getTag();
        }
        holder.patientname.setText(list.get(position).getFriendName());
        holder.patient_head.setImageURI(Uri.fromFile(new File(list.get(position).getFriendHeadPath())));
        return convertView;
    }

    private class viewHolder{
        TextView patientname;
        TextView patienthead;
        ImageView patient_head;
    }
}
