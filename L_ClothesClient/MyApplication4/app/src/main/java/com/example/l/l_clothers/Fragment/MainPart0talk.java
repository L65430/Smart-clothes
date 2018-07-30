package com.example.l.l_clothers.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.l.l_clothers.Activity.ChatActivity;
import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Adapter.ChatListViewAdapter;
import com.example.l.l_clothers.Info.Chatinfoentry;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.View.SlideCutListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2017/6/6.
 */

public class MainPart0talk extends Fragment implements ChatListViewAdapter.RemoveListener,SlideCutListView.OnItemLongClickListener,SlideCutListView.OnItemClickListener{
    View view;
    View connect_down_header;//和服务器断开时，显示header
    SlideCutListView slideCutListView;
    ChatListViewAdapter chatListViewAdapter;
    private List<Chatinfoentry> recentchatlist;//最近聊天对象
    static String chatingId="";//当前正在聊天的好友id
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_part0, container, false);
        connect_down_header = inflater.inflate(R.layout.connect_down_header,
                null);
        init();
        initrecentchatlist();// 初始化最近联系人
        // updateHeader();//判断是否连接服务器了，
        return view;
    }
    //设置正在聊天的好友id
    public void setChatingId(String chatId)
    {
        chatingId=chatId;
    }

    //得到聊天id
    public String getChatingId()
    {
        return chatingId;
    }

    //初始化聊天好友
    public void initrecentchatlist()
    {
        ChatDBUtils chatDBUtils=new ChatDBUtils(getActivity());
        List<Chatinfoentry> list=chatDBUtils.getrecentlist();
        recentchatlist.addAll(list);//就是加入所有的list
        chatListViewAdapter.notifyDataSetChanged();//及时刷新
    }

    //判断一个好友是否在聊天列表里
    public boolean isExistsInchat(String friendId)
    {
        boolean isExists=false;
        for(Chatinfoentry entity:recentchatlist)//for,each模式前面那个是单个的类型
        {
            if(friendId.equals(entity.getFriendId()))
            {
                isExists=true;
                break;
            }
        }
        return isExists;
    }

    //删除一个最近好友
    public void deleterecentchat(String friendId)
    {
        System.out.print("删除了一个最近联系人！！！！！");
        for(Chatinfoentry entity:recentchatlist)
        {
            if(friendId.equals(entity.getFriendId()))
            {
                recentchatlist.remove(entity);
                chatListViewAdapter.notifyDataSetChanged();
                ChatDBUtils database=new ChatDBUtils(getActivity());
                if(database.isTableExit(friendId))//每个表都是单独存在的
                {
                    database.deletechatitem(friendId);
                }
                break;
            }
        }
    }

    //清空最近联系人
    public void clearrecent()
    {
        if(recentchatlist.size()>0)
        {
            ChatDBUtils database=new ChatDBUtils(getActivity());
            database.clearrecent();
            recentchatlist.clear();
            chatListViewAdapter.notifyDataSetChanged();
        }
    }

    //添加一个好友到最近联系人，之前没有聊过的
    public void addrecentchatitem(Chatinfoentry entiry)
    {
        ChatDBUtils chatDBUtils=new ChatDBUtils(getActivity());
        chatDBUtils.insertrecentchat(entiry);
        List<Chatinfoentry>list=new ArrayList<Chatinfoentry>();
        list.add(entiry);//最近的插入到最上面
        recentchatlist.addAll(0,list);//先来的消息插入到最上面
        chatListViewAdapter.notifyDataSetChanged();
        System.out.print("插入消息为！！！！："+entiry.toString());
    }

    //把一个好友的未读消息变为0
    public void ResetNotReadMsg(String friendId)
    {
        for(Chatinfoentry entity:recentchatlist)
        {
            if(friendId.equals(entity.getFriendId()))
            {
                if(entity.getMsg_num()>0)
                {
                    ChatDBUtils chatDBUtils=new ChatDBUtils(getActivity());
                    entity.setMsg_num(0);
                    chatListViewAdapter.notifyDataSetChanged();
                    if(chatDBUtils.isTableExit(friendId))
                    {
                        chatDBUtils.updaterecentchat(entity);
                    }
                    if(chatDBUtils.isTableExit("msg"+ ClientManger.clientId+"_"+friendId))
                    {
                        //这个表专门用来存放所有聊天记录的
                        chatDBUtils.updateread(friendId);//把所有消息变成已读
                    }
                    break;
                }
            }
        }
    }

    //更新一个最近聊天的item，已经存在在消息列表中的
    //如果不是和这个好友聊天的时候，未读消息要加1，如果是刚从activity里面跳出来，没有读的消息是0
    public void updaterecentchatitem(String friendId,String time,String content,boolean iszero)
    {
        for(Chatinfoentry chatinfoentry:recentchatlist)
        {
            if(friendId.equals(chatinfoentry.getFriendId()))
            {
                chatinfoentry.setChatCreatTime(time);//当前时间
                if(iszero)
                {
                    chatinfoentry.setMsg_num(0);//刚从chatactivity里面跳出来
                }else
                {
                    chatinfoentry.setMsg_num(chatinfoentry.getMsg_num()+1);
                }
                chatinfoentry.setChatContent(content);//要从之前的消息传进去
                List<Chatinfoentry>list=new ArrayList<Chatinfoentry>();
                list.add(chatinfoentry);
                recentchatlist.remove(chatinfoentry);
                recentchatlist.addAll(0,list);
                chatListViewAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
    //
    private void init() {
        System.out.println("开始初始化slideCutListView");
        slideCutListView = (SlideCutListView) view
                .findViewById(R.id.fra_p0_listview);
        System.out.println(" 初始化slideCutListView");
        recentchatlist = new ArrayList<Chatinfoentry>();
        // initData();
        System.out.println("开始初始化Adapter");
        chatListViewAdapter = new ChatListViewAdapter(getActivity(), recentchatlist);
        chatListViewAdapter.setSlideCutListVie(slideCutListView);
        chatListViewAdapter.setRemoveListener(this);
        System.out.println("为listview设置Adapter");

        slideCutListView.addHeaderView(connect_down_header);// setAdapter之前添加，否则之后添加不上
        slideCutListView.setAdapter(chatListViewAdapter);
        slideCutListView.removeHeaderView(connect_down_header);
        slideCutListView.setonRefreshListener(new SlideCutListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    // 运行在UI线程中，在调用doInBackground()之前执行
                    protected void onPostExecute(Void result) {
                        chatListViewAdapter.notifyDataSetChanged();
                        slideCutListView.onRefreshComplete();
                        // updateHeader();
                    }

                    // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                            // 重新登录
                            if (!ClientManger.isOnline) {
                                MainActivity.myBinder.ConnectServer();
                                // MainActivity.myBinder.SuperLogin();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                }.execute();
            }
        });

        slideCutListView.setOnItemLongClickListener(this);
        slideCutListView.setOnItemClickListener(this);
        System.out.println("完成MainPart0的初始化");
    }

    @Override
    public void removeItem() {
        // System.out.println("开始删除laticeChatList中数据：" + laticeChatList.size());
        int position = slideCutListView.getslidePosition() - 1;
        deleterecentchat(recentchatlist.get(position).getFriendId());// 删除一项
        // laticeChatList.remove(position);
        // System.out.println("删除laticeChatList中数据");
        // adapter.notifyDataSetChanged();
        slideCutListView.oldScrollClose();
    }

    @Override
    public void onItemLongClick() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除最近联系人").setMessage("确认删除用户？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (recentchatlist.size() > 0) {
                    recentchatlist.remove(slideCutListView.getslidePosition() - 1);
                    System.out.println("删除laticeChatList中数据");
                    chatListViewAdapter.notifyDataSetChanged();
                    slideCutListView.oldScrollClose();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemClick() {
        if (recentchatlist.size() > 0) {
            Chatinfoentry entity = recentchatlist.get(slideCutListView
                    .getslidePosition() - 1);

            Intent intent = new Intent();
            if (entity.getMsgtype() == 0) {
                intent.setClass(getActivity(), ChatActivity.class);//
                intent.putExtra("friendName", entity.getFriendName());
                intent.putExtra("friendId", entity.getFriendId());
                System.out.print("friendid是:"+entity.getFriendId());
                System.out.print("friendname是:"+entity.getFriendName());

            } else if (entity.getMsgtype() == 2) {
                intent.setClass(getActivity(), ChatActivity.class);
                intent.putExtra("friendName", entity.getFriendName());
                intent.putExtra("friendId", entity.getFriendId());
                intent.putExtra("isgroupMsg", true);// 群组聊天 标识
            }
            startActivity(intent);

            /** 把所有未读消息置为已读 */
            ResetNotReadMsg(entity.getFriendId());
        }
    }

}
