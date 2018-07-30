package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;

import java.io.File;
import java.util.List;

/**
 * Created by L on 2017/6/6.
 */

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    final static class ViewHolder
    {
        ImageView friend_headpic;
        TextView friend_name;
        TextView tvLetter;
    }

    private List<FriendInfo> list=null;
    private Context mContext;

    public FriendListAdapter(Context mContext,List<FriendInfo>list)
    {
        this.mContext=mContext;
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
        ViewHolder viewHolder=null;
        final FriendInfo aFriend=list.get(position);
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.friend_item,null);
            viewHolder.friend_headpic=(ImageView)convertView.findViewById(R.id.friend_headpic);
            viewHolder.friend_name = (TextView) convertView
                    .findViewById(R.id.friend_name);
            viewHolder.tvLetter = (TextView) convertView
                    .findViewById(R.id.friend_tag);
            convertView.setTag(viewHolder);//用settag的方式来取出viewholder
        }else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        int section=getSectionForPosition(position);
        //这个时候很明显是第一次出现下一个字母开头的
        if(position==getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(aFriend.getSortLetters());
        }else
        {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.friend_name.setText(aFriend.getFriendName());
        viewHolder.friend_headpic.setImageURI(Uri.fromFile(new File(aFriend.getFriendHeadPath())));
        return convertView;
    }

    //当listview发生变化时，用这个方法更新ListView
    public void updateListView(List<FriendInfo>list)
    {
        this.list=list;
        notifyDataSetChanged();
    }

    //根据分类的首字母char ascii值直接获得第一次出现该首字母的位置
    @Override
    public int getPositionForSection(int section)
    {
        for(int i=0;i<getCount();i++)
        {
            String sortstr=list.get(i).getSortLetters();
            char firstChar=sortstr.toUpperCase().charAt(0);//索引第一个字母
            if(firstChar==section)
                return i;
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    //提取英文首字母，如果不是英文用#代替（昵称可能不是英文）
    private String getAlpha(String str)
    {
        String sortstr=str.trim().substring(0,1).toUpperCase();//取第一个
        if(sortstr.matches("[A-Z]"))
        {
            return sortstr;
        }else
            return "#";
    }


    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }
}
