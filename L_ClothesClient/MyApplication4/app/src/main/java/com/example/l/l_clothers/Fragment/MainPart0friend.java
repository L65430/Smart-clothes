package com.example.l.l_clothers.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.l.l_clothers.Activity.FriendInfoActivity;
import com.example.l.l_clothers.Adapter.FriendListAdapter;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Utils.CharacterParser;
import com.example.l.l_clothers.View.BaseListView;
import com.example.l.l_clothers.View.ClearEditText;
import com.example.l.l_clothers.View.SideBar;
import com.example.l.l_clothers.comparator.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by L on 2017/6/6.
 */
public class MainPart0friend extends Fragment implements SectionIndexer {
    View view;
    private Context context;
    // private ListView sortListView;
    private BaseListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private FriendListAdapter adapter;
    private ClearEditText mClearEditText;
    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    //上次第一个可见元素
    private int lastFirstVisibleItem=-1;
    //汉字转化为拼音的类
    private CharacterParser characterParser;
    private List<FriendInfo> FriendDataList;

    //根据拼音来排列listview里面的数据类
    private PinyinComparator pinyinComparator;
    private TextView friendsNum;//好友人数



    //可能会多次调用
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_part1,container,false);
        context=getActivity();
        FriendDataList=new ArrayList<FriendInfo>();
        initviews();
        return view;
    }

    //返回一个好友的昵称
    public String getFriendName(String friendId)
    {
        String friendName="";
        for(FriendInfo friendinfo:FriendDataList)
        {
            if(friendId.equals(friendinfo.getFriendId()))
            {
                friendName=friendinfo.getFriendName();
                break;
            }
        }
        return friendName;
    }

    //返回所有好友ID
    public List<String> getFriendsIds()
    {
        List<String>list=new ArrayList<String>();
        if(FriendDataList.size()>0)
        {
            for(FriendInfo friendinfo:FriendDataList)
            {
                list.add(friendinfo.getFriendId());
            }
        }
        return list;
    }


    //返回一个好友的info
    public FriendInfo getfriendinfo(String friendid)
    {
        for(FriendInfo friendinfo:FriendDataList)
        {
            if(friendid.equals(friendinfo.getFriendId()))
            {
                return friendinfo;
            }
        }
        return null;
    }

    //删除一个好友
    public void DeleteAFriend(String friendId)
    {
        for(FriendInfo friendinfo:FriendDataList)
        {
            if(friendinfo.getFriendId().equals(friendId))
            {
                FriendDataList.remove(friendinfo);
                FriendDataList=filledData(FriendDataList);//根据拼音重新设置好友ITEM
                Collections.sort(FriendDataList,pinyinComparator);
                adapter=new FriendListAdapter(context,FriendDataList);
                sortListView.setAdapter(adapter);
                friendsNum.setText("共有"+FriendDataList.size()+"位好友");
                break;
            }
        }
    }


    //判断好友是不是已经在表里
    public boolean isFriendExists(String id){
        boolean isExists = false;
        if(FriendDataList.size() > 0){
            for(FriendInfo afriend : FriendDataList){
                if(id.equals(afriend.getFriendId())){
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }

    //新增一个好友
    public void AddAFriend(FriendInfo info)
    {
        if(!isFriendExists(info.getFriendId()))
        {
            FriendDataList.add(info);
            //加入之后搜索的时候又需要重新开始,根据现在的序列重新进行搜索
            FriendDataList=filledData(FriendDataList);
            Collections.sort(FriendDataList,pinyinComparator);
            adapter=new FriendListAdapter(context,FriendDataList);//每次都是新建一个对象
            sortListView.setAdapter(adapter);
            friendsNum.setText("共有" + FriendDataList.size() + "位好友");
        }
    }

    private void initviews()
    {
        tvNofriends=(TextView)this.view.findViewById(R.id.title_layout_no_friends);
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        //设置右侧戳监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position=adapter.getPositionForSection(s.charAt(0));//第一个字母第一次出现的位置
                if(position!=-1)
                {
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView=(BaseListView)view.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendinfo=(FriendInfo)adapter.getItem(position);
                Intent intent=new Intent(getActivity(),FriendInfoActivity.class);
                intent.putExtra("friendInfo", friendinfo);
                startActivity(intent);
            }
        });
//        FriendDataList=new ArrayList<FriendInfo>();
        //不能每次进来都新建

        //frienddatalist还没有操作
        adapter=new FriendListAdapter(context,FriendDataList);
        View footer=LayoutInflater.from(context).inflate(R.layout.friendlist_footer,null);
        footer.setEnabled(false);
        friendsNum=(TextView)footer.findViewById(R.id.FriendsNum);
        friendsNum.setText("共有"+FriendDataList.size()+"位好友");
        sortListView.addFooterView(footer);
        sortListView.setAdapter(adapter);
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 这个时候不需要挤压效果 就把他隐藏掉
//				titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }




    //根据listview item中的好友名重新来设置item拼音的标志
    private List<FriendInfo> filledData(List<FriendInfo>list)
    {
        List<FriendInfo>mSortList=new ArrayList<FriendInfo>();
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            FriendInfo sortModel=(FriendInfo)iterator.next();
            String pinyin=characterParser.getSelling(sortModel.getFriendName());
            String sortString=pinyin.substring(0,1).toUpperCase();//取首尾字母进行排序
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());//设置首字母，不过肯定都是这里面的啊
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);//因为这个序列是新建的
        }
        return mSortList;//得到新的序列
    }

    //根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr)
    {
        List<FriendInfo>filterDataList=new ArrayList<FriendInfo>();
        if(TextUtils.isEmpty(filterStr))
        {
            filterDataList=FriendDataList;
            tvNofriends.setVisibility(View.GONE);//不可见并且不占据空间
        }else
        {
            filterDataList.clear();
            for(FriendInfo friendinfo:FriendDataList)
            {
                String name=friendinfo.getFriendName();
                if(name.indexOf(filterStr.toString())!=-1|| characterParser.getSelling(name).startsWith(
                        filterStr.toString()))//就是如果包含输入的东西
                {
                    filterDataList.add(friendinfo);//这样的话就加上这个东西，原来的列表都已经清空了
                }
            }
        }
        //根据a-z进行排序
        Collections.sort(filterDataList,pinyinComparator);//后面输入的是比较器,比较器返回的是一个Int值
        adapter.updateListView(filterDataList);//应用这个新的列表
        if(filterDataList.size()==0)
        {
            tvNofriends.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object[] getSections() {
        return  null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < FriendDataList.size(); i++) {
            String sortStr = FriendDataList.get(i).getSortLetters();//首字母
            char firstChar = sortStr.toUpperCase().charAt(0);//得到的第一个拼音在A-Z中排的位置
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        if(FriendDataList!=null&&FriendDataList.size()>0)
        {
            return FriendDataList.get(position).getSortLetters().charAt(0);
        }
        return 0;
    }
}

