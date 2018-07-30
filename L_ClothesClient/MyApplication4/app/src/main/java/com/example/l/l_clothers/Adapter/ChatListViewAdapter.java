package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.View.SlideCutListView;

import java.io.File;
import java.util.List;

/**
 * Created by L on 2017/6/6.
 */

public class ChatListViewAdapter extends BaseAdapter {

    private List<Chatinfoentry> coll;
    private LayoutInflater mInflater;
    private Context context;
    private RemoveListener removeListener;
    SlideCutListView slideCutListView;//展现更多消息用的

    public ChatListViewAdapter(Context context,List<Chatinfoentry> coll)
    {
        this.coll=coll;
        this.context=context;
        mInflater=LayoutInflater.from(context);//就是从这个context里面得到布局的意思
    }

    @Override
    public int getCount() {
        return coll.size();
    }

    @Override
    public Object getItem(int position) {
        return coll.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chatinfoentry chatinfoentry=coll.get(position);
        ViewHolderChatListView viewHolder=null;
        if(mInflater==null)
        {
            System.out.println("mInflater为空");
            return null;
        }
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.chat_listview_item,null);//先找到这个
            //layout，然后才能获得其中的id
            viewHolder=new ViewHolderChatListView();
            viewHolder.iv_hp = (ImageView) convertView
                    .findViewById(R.id.chat_listview_item_iv_hp);
            viewHolder.tv_username = (TextView) convertView
                    .findViewById(R.id.chat_listview_item_tv_username);
            viewHolder.tv_chatcretime = (TextView) convertView
                    .findViewById(R.id.chat_listview_item_tv_time);
            viewHolder.tv_chatcontent = (TextView) convertView
                    .findViewById(R.id.chat_listview_item_tv_content);
            viewHolder.btn_del = (ImageButton) convertView
                    .findViewById(R.id.chat_listview_item_btn_del);
            viewHolder.tv_msg_num = (TextView) convertView
                    .findViewById(R.id.chat_lsitview_item_tv_num);
            viewHolder.num_bg = (ImageView) convertView.findViewById(R.id.chat_lsitview_item_iv_numbg);
            viewHolder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeListener.removeItem();//移除一条记录
                }
            });
            convertView.setTag(viewHolder);//settag就是给他放上一个viewholder的标记，之后gettag(viewholder)就好了
        }else{
            viewHolder=(ViewHolderChatListView)convertView.getTag();//否则的话直接获得这个类
            //因为存的时候把这个类作为tag存进去了
        }
        viewHolder.position=position;
        if(chatinfoentry.getMsgtype()==0)
        {
            viewHolder.iv_hp.setImageURI(Uri.fromFile(new File(StaticValues.USER_HEADPATH+ chatinfoentry.getFriendId()+".png")));//设置头像
        }else
        {
            viewHolder.iv_hp.setImageResource(R.drawable.imomo2);
        }
        viewHolder.tv_username.setText(chatinfoentry.getFriendName());
        viewHolder.tv_chatcretime.setText(chatinfoentry.getChatCreatTime());
        viewHolder.tv_chatcontent.setText(chatinfoentry.getChatContent());
        if(chatinfoentry.getMsg_num()==0)//未读消息数
        {
            viewHolder.num_bg.setBackgroundDrawable(null);
            viewHolder.tv_msg_num.setText(null);
        }else
        {
            viewHolder.num_bg.setImageResource(R.drawable.red_point);
            viewHolder.tv_msg_num.setText(Integer.valueOf(chatinfoentry.getMsg_num()).toString());
        }
        return  convertView;
    }

    //设置下滑出现更多数据
    public void setSlideCutListVie(SlideCutListView slideCutListView)
    {
        this.slideCutListView=slideCutListView;
        this.slideCutListView.requestDisallowInterceptTouchEvent(true);//我的事件我自己处理
    }

    public void setRemoveListener(RemoveListener removeListener)
    {
        this.removeListener=removeListener;
    }

    public interface RemoveListener
    {
        public void removeItem();
    }

    public class ViewHolderChatListView
    {
        public ImageView iv_hp;
        public TextView tv_username;
        public TextView tv_chatcretime;
        public TextView tv_chatcontent;
        public TextView tv_msg_num;
        public ImageButton btn_del;
        public ImageView num_bg;
        //		public ImageView iv_isSystem;//如果是系统消息，则显示一个太阳
        public int extra;
        public int position;

        //把所有的控件都放在一个类里面
    }
}
