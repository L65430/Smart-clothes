package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.example.l.l_clothers.R;
import com.example.l.l_clothers.View.GifDrawable;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by L on 2017/6/6.
 */

public class GifExpressionUtil {
    public static String matching="f0[0-9]{2}|f10[0-7]";//用正则表达式判断消息里面是不是有表情
    public static SpannableString getExpressionString(Context context, SpannableString str, Hashtable<Integer,GifDrawable> cache, Vector<GifDrawable> drawables)
    {
        SpannableString spannableString=new SpannableString(str);
        Pattern pattern=Pattern.compile(matching,Pattern.CASE_INSENSITIVE);//忽略大小写，传入一个匹配
        try {

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return spannableString;
    }

    //拼串
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start,
                                      Hashtable<Integer, GifDrawable> cache, Vector<GifDrawable> drawables)throws Exception
    {
        Matcher matcher=patten.matcher(spannableString);//匹配之后的结果
        while(matcher.find())
        {
            String key=matcher.group();
            if(matcher.start()<start)
            {
                continue;
            }
            Field field= R.drawable.class.getDeclaredField(key);//其实就是获得这个指定的键的值
            int id=Integer.parseInt(field.get(null).toString());//然后获得id,如果是静态id，传入null或者staticvalue都是一样的，动态id不一样
            if(id!=0)
            {
                GifDrawable mSmile=null;
                if(cache.containsKey(id))
                {
                    mSmile=cache.get(id);//，每一个id对应一个GifDrawable
                }else
                {
                    mSmile=new GifDrawable(context,id);
                    cache.put(id,mSmile);
                }
                ImageSpan span=new ImageSpan(mSmile,ImageSpan.ALIGN_BASELINE);//用于在文本编辑框里面加图片的
                //只有用spannableString才能够得到复合文本框，才能够在文本框里面插入图片这样
                int mstart=matcher.start();
                int end=mstart+key.length();
                if (key.contains("f065") || key.contains("f066"))
                {
                    ImageSpan ivspan=new ImageSpan(context, BitmapFactory.decodeResource(context.getResources(),id));//前一个是要加载资源的context，后一个是位图id
                    spannableString.setSpan(ivspan,mstart,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else
                {
                    spannableString.setSpan(span, mstart, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (!drawables.contains(mSmile))
                        drawables.add(mSmile);
                }
            }
        }
    }
}
