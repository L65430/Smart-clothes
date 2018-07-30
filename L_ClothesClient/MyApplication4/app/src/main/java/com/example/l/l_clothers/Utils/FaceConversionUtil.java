package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.example.l.l_clothers.Info.ChatEmoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by L on 2017/6/6.
 */

public class FaceConversionUtil {
    private int pageSize=21;//每一页表情的个数
    private static FaceConversionUtil mFaceConversionUtil;

    //保存在表情中的hashmap
    private HashMap<String,String> emojiMap=new HashMap<String,String>();

    //表情在内存中的表情集
    public List<ChatEmoji> emojis=new ArrayList<ChatEmoji>();

    //表情分页的结果集
    public List<List<ChatEmoji>> emojiLists=new ArrayList<List<ChatEmoji>>();//把每一页的emoji进行分类

    private FaceConversionUtil()
    {

    }

    public static FaceConversionUtil getInstance()
    {
        if(mFaceConversionUtil==null)
        {
            mFaceConversionUtil=new FaceConversionUtil();
        }
        return mFaceConversionUtil;
    }

    //得到spanableString对象，对传入的字符串进行正则判断,判断有没有表情集
    public SpannableString getExpressionString(Context context, String str)
    {
        SpannableString spannableString=new SpannableString(str);
        String zhengze="\\[[^\\]]+\\]";//表情都是有方框的
        Pattern pattern=Pattern.compile(zhengze,Pattern.CASE_INSENSITIVE);//不管大小写都可以
        try
        {
            dealExpression(context,spannableString,pattern,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    //添加表情
    public SpannableString addFace(Context context,int imgId,String spannableString)
    {
        if(TextUtils.isEmpty(spannableString))
        {
            return null;
        }
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),imgId);
        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    //对spannableString进行正则判断
    public void dealExpression(Context context,SpannableString spannableString,Pattern pattern,int start)throws Exception
    {
        Matcher matcher=pattern.matcher(spannableString);
        while(matcher.find())//因为匹配成功的话是返回true，所以这个循环一直成立
        {
            String key=matcher.group();//匹配得到的结果
            if(matcher.start()<start)//一直到匹配结束
            {
                continue;
            }
            String value=emojiMap.get(key);
            if(TextUtils.isEmpty(value))
            {
                continue;//如果没有这个emoji，就继续
            }
            //生成资源id
            int resId = context.getResources().getIdentifier(value, "drawable",
                    context.getPackageName());
            if(resId!=0)
            {
                Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),resId);
                bitmap = Bitmap.createScaledBitmap(bitmap, 130, 130, true);
                ImageSpan imageSpan=new ImageSpan(bitmap);
                //计算这个图片名字的长度，也就是要替换的字符串长度
                int end=matcher.start()+key.length();
                //将图片替换到指定位置
                spannableString.setSpan(imageSpan,matcher.start(),end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//效果前面包括，后面不包括
                if(end<spannableString.length())
                {
                    //如果整个字符串没有验证完，继续
                    dealExpression(context,spannableString,pattern,end);//从上次结束的地方开始
                }
                break;
            }

        }
    }

    public void getFileText(Context context)
    {
        ParseData(FileUtils.getEmojiFile(context), context);
    }

    //解析字符
    private void ParseData(List<String>data,Context context)
    {
        if(data==null)
        {
            return;
        }
        ChatEmoji emojEentry;
        try {
            for (String str : data) {
                String[] text = str.split(",");
                String fileName = text[1]
                        .substring(0, text[1].lastIndexOf("."));
                emojiMap.put(text[0], fileName);
                int resID = context.getResources().getIdentifier(fileName,
                        "drawable", context.getPackageName());

                if (resID != 0) {
                    emojEentry = new ChatEmoji();
                    emojEentry.setId(resID);
                    emojEentry.setCharacter(text[0]);
                    emojEentry.setFaceName(fileName);
                    emojis.add(emojEentry);
                }
            }
            int pageCount = (int) Math.ceil(emojis.size() / 20 + 0.1);

            for (int i = 1; i <= pageCount; i++) {
                emojiLists.add(getOnePageFaces(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //根据页数获取第n页的表情数据
    public List<ChatEmoji> getOnePageFaces(int page) {
        int startIndex = (page-1) * pageSize;
        int endIndex = startIndex + pageSize;
        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }
        List<ChatEmoji> list = new ArrayList<ChatEmoji>();
        list.addAll(emojis.subList(startIndex, endIndex));//通常情况是找不到文件，
        // if (list.size() < pageSize) {
        for (int i = list.size(); i < pageSize; i++) {
            ChatEmoji object = new ChatEmoji();
            list.add(object);
        }
        return list;
    }

    //根据当前页数和点击的position返回表情
    public ChatEmoji getSelectEmoji(int page_index,int select_position)
    {
        int position=(page_index-1)*pageSize+select_position;
        return emojis.get(position);
    }

    public HashMap<String,String>getFaceMap()
    {
        return emojiMap;
    }
}

