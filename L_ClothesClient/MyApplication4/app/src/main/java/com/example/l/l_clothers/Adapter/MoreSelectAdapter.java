package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.l_clothers.R;

/**
 * Created by L on 2017/6/6.
 */

public class MoreSelectAdapter extends BaseAdapter {
    private static final int PHOTO_REQUEST_CAMERA=1;//拍照
    private static final int PHOTO_REQUEST_GALLERY=2;//从相册中选择
    private static final int PHOTO_REQUEST_CUT=3;//裁剪结果
    private String PHOTO_FILE_NAME="";
    private Context context;
    private String[] selections={"图片", "拍照", "位置", "提醒", "语音识别", "视频"};
    private int[] selection_icons = { R.drawable.more_selction_album,
            R.drawable.more_selction_camara, R.drawable.more_selction_location,
            R.drawable.more_selction_notify, R.drawable.chatview_recding,
            R.drawable.more_selction_vidio };
    public MoreSelectAdapter(Context context)
    {
        this.context=context;
    }

    @Override
    public int getCount() {
        return selections.length;
    }

    @Override
    public Object getItem(int position) {
        return selections[position];
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder=null;
        final int selec_position=position;
        if(convertView==null)
        {
            holder=new viewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);//因为传入的是当前这个context。context是系统级的
            //其实本质都是调用了Context.getSystemService()，然后这个，如果这个context里面不包含这个layout的话就会报错
            convertView=inflater.inflate(R.layout.chatview_more_select_item,parent,false);
            holder.sele_icon=(ImageView)convertView.findViewById(R.id.selec_icon);
            holder.selec_name=(TextView)convertView.findViewById(R.id.selec_name);
            convertView.setTag(holder);
        }else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.sele_icon.setImageResource(selection_icons[position]);
        holder.selec_name.setText(selections[position]);
        return convertView;
    }

    private class viewHolder
    {
        public ImageView sele_icon;
        public TextView selec_name;
    }
}
