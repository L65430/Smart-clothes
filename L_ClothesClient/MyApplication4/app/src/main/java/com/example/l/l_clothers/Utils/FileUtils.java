package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.l.l_clothers.AllMessage.ChatMessage;
import com.example.l.l_clothers.StaticValues.CSKeys;
import com.example.l.l_clothers.StaticValues.JSONKeys;
import com.example.l.l_clothers.StaticValues.StaticValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2017/5/11.
 */
//文件工具类
public class FileUtils {
    //读取表情文件
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<>();
            InputStream in = context.getResources().getAssets().open("faces.txt"); //先获得输入流
            //读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));//以gbk的形式读取inputstream
            String str = null;
            while ((str = reader.readLine()) != null) {
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //删除文件
    public static void deletefile(String path)
    {
        File file=new File(path);
        if(file.exists())
        {
            file.delete();
        }
    }

    //判断文件是否存在
    public static boolean isFileexist(String path)
    {
        File file=new File(path);
        if(file.exists())
        {
            return true;
        }
        else
            return false;
    }

    //判断是否存在内存卡
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    //存储文件到本地
    public  static void saveMsgToText(List<ChatMessage>list, String friendname) throws Exception//不是网络抱，是已经得到的转化为chatmessage的包
    {
        String path=null;
        BufferedWriter writer=null;//文本写入流
        File msgdir=new File(StaticValues.MSG_TEXT);//建立名字叫这个
        if(!msgdir.exists())
        {
            msgdir.mkdirs();//建立叫这个的文件夹
        }
        //在这个文件夹下面创建文件
        path=StaticValues.MSG_TEXT+friendname+"_"+System.currentTimeMillis()+".txt";
        File file=new File(path);
        file.createNewFile();
        writer=new BufferedWriter(new FileWriter(file));
        for(int i=list.size()-1;i>=0;i--)
        {
            ChatMessage chatMessage=list.get(i);
            if(chatMessage.msgType== CSKeys.CHATING_TEXT_MSG)//如果是文本的话
            {
                JSONObject jsonObject= JSON.parseObject(chatMessage.msgJson);
                String content=jsonObject.getString(JSONKeys.msgCotent);
                if(chatMessage.isGetted==1)
                {
                    writer.write(friendname+"  "+chatMessage.sendTime);
                }
                else
                    writer.write("我      "+chatMessage.sendTime);
                writer.newLine();//另起一行
                writer.write(content);
                writer.newLine();
            }
        }
        writer.close();
    }
}
