package com.example.l.l_clothers.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.Activity.ChatActivity;
import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Activity.PersonalInfoActivity;
import com.example.l.l_clothers.AllMessage.ClientHandleMessage;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.Info.FriendInfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.AnimatedGifDrawable;
import com.example.l.l_clothers.Utils.AnimatedImageSpan;
import com.example.l.l_clothers.Utils.FaceManager;
import com.example.l.l_clothers.Utils.FileUtils;
import com.example.l.l_clothers.Utils.SoundUtil;
import com.example.l.l_clothers.View.GifTextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by L on 2017/6/6.
 */

public class MsgContentAdapter extends BaseAdapter {
    public static final int MESSAGE_TYPE_INVALID = -1;
    public static final int MESSAGE_TYPE_MINE_TETX = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_AUDIO = 0x02;
    public static final int MESSAGE_TYPE_OTHER_TEXT = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x05;
    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    private static final int VIEW_TYPE_COUNT = 9;

    //正则表达式
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    private Context context;
    private List<ClientHandleMessage> msglist;//本地处理的消息类
    LayoutInflater inflater;
    private FaceManager fm;

    //聊天界面传过来的handler
    public MsgContentAdapter(Context context, List<ClientHandleMessage> msglist) {
        this.context = context;
        this.msglist = msglist;
        inflater = LayoutInflater.from(context);
        fm=FaceManager.getInstance();
    }

    @Override
    public int getCount() {
        return msglist.size();
    }

    @Override
    public Object getItem(int position) {
        return msglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if (position >= msglist.size()) {
            return MESSAGE_TYPE_INVALID;//没有这种类型
        }
        ClientHandleMessage item = msglist.get(position);
        if (item != null) {
            boolean isGetted = item.isGetted();
            JSONObject jsonObject = JSON.parseObject(item.getMsgJson());
            int msgtype = jsonObject.getInteger(JSONKeys.msgType);
            if (isGetted) {
                switch (msgtype) {
                    case CSKeys.CHATING_IMAGE_MSG:
                        return MESSAGE_TYPE_OTHER_IMAGE;
                    case CSKeys.CHATING_TEXT_MSG:
                        return MESSAGE_TYPE_OTHER_TEXT;
                    case CSKeys.CHATING_VOICE_MSG:
                        return MESSAGE_TYPE_OTHER_AUDIO;
                    default:
                        break;
                }
            } else {
                switch (msgtype) {
                    case CSKeys.CHATING_IMAGE_MSG:
                        return MESSAGE_TYPE_MINE_IMAGE;
                    case CSKeys.CHATING_TEXT_MSG:
                        return MESSAGE_TYPE_MINE_TETX;
                    case CSKeys.CHATING_VOICE_MSG:
                        return MESSAGE_TYPE_MINE_AUDIO;
                    default:
                        break;
                }
            }
        }
        return MESSAGE_TYPE_INVALID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.print("位置：" + position + "   ");
        ClientHandleMessage clientHandleMessage = msglist.get(position);
        if (clientHandleMessage == null) {
            return null;
        } else {
            System.out.println("布局不为空");
        }
        int type = getItemViewType(position);
        ViewHolderBase holder;
        if (null == convertView) {
            holder = new ViewHolderBase();
            System.out.println("布局为空");
            switch (type) {
                case MESSAGE_TYPE_MINE_TETX: {
                    System.out.println("布局为文字，是自己发送的文字");
                    convertView = inflater.inflate(R.layout.chatting_item_text_right, parent, false);
                    holder = new ViewHolderText();
                    convertView.setTag(holder);
                    fillTextMessageHolder((ViewHolderText) holder, convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_IMAGE: {
                    System.out.println("布局为图片");
                    convertView = inflater.inflate(R.layout.chatting_item_msg_image_right, parent, false);
                    holder = new ViewHolderImg();
                    convertView.setTag(holder);
                    fillImageMessageHolder((ViewHolderImg) holder, convertView);
                    break;



                }
                case MESSAGE_TYPE_MINE_AUDIO: {
                    System.out.println("布局为录音");
                    convertView = inflater.inflate(R.layout.chatting_item_text_right, parent, false);
                    holder = new ViewHolderText();
                    convertView.setTag(holder);
                    fillTextMessageHolder((ViewHolderText) holder, convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_TEXT: {
                    System.out.println("布局为文字");
                    convertView = inflater.inflate(R.layout.chatting_item_msg_text_left, parent, false);
                    holder = new ViewHolderText();
                    convertView.setTag(holder);
                    fillTextMessageHolder((ViewHolderText) holder, convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_IMAGE: {
                    System.out.println("布局为图片");
                    convertView = inflater.inflate(
                            R.layout.chatting_item_msg_image_left, parent, false);
                    holder = new ViewHolderImg();
                    convertView.setTag(holder);
                    fillImageMessageHolder((ViewHolderImg) holder, convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_AUDIO: {
                    System.out.println("布局为录音");
                    convertView = inflater.inflate(
                            R.layout.chatting_item_msg_text_left, parent, false);
                    holder = new ViewHolderText();
                    convertView.setTag(holder);
                    fillTextMessageHolder((ViewHolderText) holder, convertView);
                    break;
                }
                default:
                    break;
            }
        }else
        {
            System.out.println("布局不为空");
            holder=(ViewHolderBase)convertView.getTag();
        }
        final JSONObject msgJson=JSON.parseObject(clientHandleMessage.getMsgJson());//本地端存储的消息
        final  boolean isGetted=clientHandleMessage.isGetted();
        if(msgJson!=null)
        {
            int msgType=msgJson.getInteger(JSONKeys.msgType);//转过来的信息是msgType
            if(msgType==CSKeys.CHATING_TEXT_MSG)
            {
                System.out.println("消息为文字");
                handleTextMessage((ViewHolderText)holder,msgJson,parent,isGetted);
            }
            else if(msgType==CSKeys.CHATING_IMAGE_MSG)
            {
                System.out.println("消息为图像");
                handleImageMessage((ViewHolderImg)holder,msgJson,parent,isGetted);
            } else if(msgType == CSKeys.CHATING_VOICE_MSG)
            {
                System.out.println("消息为录音");
                handleAudioMessage((ViewHolderText) holder, msgJson, parent,
                        isGetted);
            }
        }
        return convertView;
    }

    private static class ViewHolderBase {
        ImageView head;
        TextView tv_sendtime;
    }

    private void fillBaseMessageholder(ViewHolderBase holder,View convertView)
    {
        holder.head=(ImageView)convertView.findViewById(R.id.iv_userhead);
        holder.tv_sendtime=(TextView)convertView.findViewById(R.id.tv_sendtime);
    }

    private void fillTextMessageHolder(ViewHolderText holder,View convertView)
    {
        fillBaseMessageholder(holder,convertView);
        holder.tv_chatcontent=(GifTextView)convertView.findViewById(R.id.tv_chatcontent);
    }

    private void fillImageMessageHolder(ViewHolderImg holder,View convertview)
    {
        fillBaseMessageholder(holder,convertview);
        holder.Iv_chatimage=(ImageView)convertview.findViewById(R.id.Iv_chatimage);
    }

    private void handleBaseMessage(ViewHolderBase holder,final JSONObject mItem,final boolean isGetted)
    {
        final  String friendId=mItem.getString(JSONKeys.friendId);//这一块待定，头像的问题
        if(isGetted)
        {
            if(ChatActivity.isgroupMsg)//如果是群组消息
            {
                holder.head.setImageResource(R.drawable.imomo2);//群组聊天不能看个人头像
            }else
            {
                String headpath= StaticValues.USER_HEADPATH+friendId+".png";
                holder.head.setImageURI(Uri.fromFile(new File(headpath)));
            }
        }else
        {
            String headpath=StaticValues.USER_HEADPATH+ ClientManger.ClientPhone+".png";//自己发送的
            holder.head.setImageURI(Uri.fromFile(new File(headpath)));
        }
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                if(isGetted)
                {
                    if(ChatActivity.isgroupMsg)
                    {
                        Toast.makeText(context,"陌生人要添加为好友才能看哦",Toast.LENGTH_LONG).show();
                    }else{
                        if(ClientManger.isOnline)
                        {
                            intent.setClass(context, FriendInfo.class);
                            intent.putExtra("frendInfo", MainActivity.mp0friend.getfriendinfo(friendId) );
                            context.startActivity(intent);
                        }else
                        {
                            Toast.makeText(context,"当前不在线，无法查看",Toast.LENGTH_LONG).show();//在群组聊天中陌生人有一个互相聊天的群组功能，这个时候的头像都是陌生人，后面可以加上其他的功能，好友之间建立联系，添加好友之后可以一起玩
                        }
                    }
                }else
                {
                    intent.setClass(context, PersonalInfoActivity.class);//点击自己头像，跳转到自己的显示界面
                    context.startActivity(intent);
                }
            }
        });
        holder.tv_sendtime.setText(mItem.getString(JSONKeys.sendTime));
    }


    //对文本消息的处理
    private void handleTextMessage(final ViewHolderText holder,final JSONObject mitem,final View parent,boolean isGetted)
    {
        handleBaseMessage(holder,mitem,isGetted);
        final String msgcontent=mitem.getString(JSONKeys.msgCotent);
        holder.tv_chatcontent.insertGif(convertNormalStringToSpannableString(msgcontent+" ",holder.tv_chatcontent));
        holder.tv_chatcontent.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("文本内容");//可以考虑如果是音频文件显示翻译
                menu.add(0, Menu.FIRST+1,1,msgcontent);
            }
        });//长按某项出现的消息结果

    }

    //对图片消息的处理
    private void handleImageMessage(final ViewHolderImg holder,final JSONObject mitem,final View parent,boolean isGetted)
    {
        handleBaseMessage(holder,mitem,isGetted);
        final String imagePath=mitem.getString(JSONKeys.imagePath);
        if(FileUtils.isFileexist(imagePath))//判断本地是否存在这张图
        {
            holder.Iv_chatimage.setImageURI(Uri.fromFile(new File(imagePath)));//本地是存在这张图的，但是没有显示出来
        }else
        {
            Toast.makeText(context,"文件不存在或被破坏",Toast.LENGTH_LONG).show();
        }
        holder.Iv_chatimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog LookDialog=new Dialog(context,R.style.Dialog_Fullscreen);
                LookDialog.setCanceledOnTouchOutside(true);//点击其他部位消失
                Window window=LookDialog.getWindow();
                window.setWindowAnimations(R.style.dialog_animstyle);
                WindowManager.LayoutParams w1=window.getAttributes();//用来调整参数的
                LookDialog.setContentView(R.layout.look_image_dialog);
                ImageView Iv_lookPic=(ImageView)LookDialog.findViewById(R.id.Iv_lookPic);
                Iv_lookPic.setImageURI(Uri.fromFile(new File(imagePath)));
                LookDialog.show();
                Iv_lookPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LookDialog.dismiss();//点击图片的时候对话框消失
                    }
                });
            }
        });
        holder.Iv_chatimage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //可以保存到本地相册或者添加到表情，这之后再说
            }
        });
    }

    private void handleAudioMessage(final ViewHolderText holder,final JSONObject mItem,final View parent,boolean isGetted)
    {
        handleBaseMessage(holder,mItem,isGetted);
        int voiceTime=mItem.getInteger(JSONKeys.voiceTime);
        String msgCotent="";
        if(isGetted)
        {
            msgCotent =  voiceTime + "″";
        }else {
            msgCotent = voiceTime + "″";
        }
        holder.tv_chatcontent.insertGif(convertNormalStringToSpannableString(msgCotent+" ",holder.tv_chatcontent));
        //这里说了是insertgif，所以后面得到的是insertgif
        holder.tv_chatcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String voicePath=mItem.getString(JSONKeys.voicePath);
                if(voicePath!=null)
                {
                    SoundUtil.getInstance().playRecorder(context,voicePath);
                }
            }
        });
        holder.tv_chatcontent.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //估计是为了防止长按的时候出现问题
                //可以加上翻译语音的功能
            }
        });
    }


    //文本信息和语音信息
    private class ViewHolderText extends ViewHolderBase
    {
        GifTextView tv_chatcontent;
    }
    //图片消息
    private class ViewHolderImg extends ViewHolderBase
    {
        ImageView Iv_chatimage;
    }

    private SpannableString convertNormalStringToSpannableString(String message, final TextView tv)
    {
        System.out.println("匹配前"+message);
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }//正则表达式的匹配模式
//        SpannableString value1=SpannableString.valueOf(hackTxt);
//        Matcher localMatcher=EMOTION_URL.matcher(value1);//按这个模式来匹配message
//        while (localMatcher.find())//先进行匹配
//        {
//            String str2=localMatcher.group(0);
//            int k=localMatcher.start();
//            int m=localMatcher.end();
//
//            String value= FaceConversionUtil.getInstance().getFaceMap().get(str2);//获得当前匹配好的表情
//            int face=fm.getFaceId(str2);
//            if(FaceConversionUtil.getInstance().getFaceMap().containsKey(str2))
//                    {
////                String faceName=context.getResources().getString(context.getResources().getIdentifier(value,"drawble",context.getPackageName()));
//                //value是表情的名字
//                //getIdentifier是用来返回资源id的
//                        WeakReference<AnimatedImageSpan> localImageSpanRef = new WeakReference<AnimatedImageSpan>(new AnimatedImageSpan(new AnimatedGifDrawable(context.getResources().openRawResource(face), new AnimatedGifDrawable.UpdateListener() {
//                            @Override
//                            public void update() {//update the textview
//                                tv.postInvalidate();//这只是表示弱引用
//                            }
//                        })));
//                        value1.setSpan(localImageSpanRef.get(),k,m, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
////                System.out.println("facetime:"+faceName);
////                CharSequence name=options(faceName);
////                message=message.replace(str2,name);
//            }
//        }
//        System.out.println("匹配后" + message);
//        return message;
        SpannableString value = SpannableString.valueOf(message);
        Matcher localMatcher = EMOTION_URL.matcher(value);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            int k = localMatcher.start();
            int m = localMatcher.end();
            if (m - k < 8) {
                int face = fm.getFaceId(str2);

                if(-1!=face){//wrapping with weakReference
                    WeakReference<AnimatedImageSpan> localImageSpanRef = new WeakReference<AnimatedImageSpan>(new AnimatedImageSpan(new AnimatedGifDrawable(context.getResources().openRawResource(face), new AnimatedGifDrawable.UpdateListener() {
                        @Override
                        public void update() {//update the textview
                            tv.postInvalidate();//这只是表示弱引用
                        }
                    })));
                    value.setSpan(localImageSpanRef.get(), k, m, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
        return value;
    }



    private CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }

    @Override
    public int getViewTypeCount()
    {
        return VIEW_TYPE_COUNT;
    }
}
