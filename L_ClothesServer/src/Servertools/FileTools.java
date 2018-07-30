package Servertools;

import Keys.StaticValue;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by L on 2017/5/16.
 */
//用于传输数据
public class FileTools {
    static FileTools fileTools;
    public static void main(String[]args){

    }

    public static FileTools getInstance(){
        if(fileTools==null){
            fileTools=new FileTools();
            return fileTools;
        }
        else
            return fileTools;
    }


    //存储用户的图片或者语音消息
    public void saveMultyFile(String filepath,byte[]msgBytes)
    {
        if(msgBytes.length>0)
        {
            File file=new File(filepath);
            FileOutputStream fileOutputStream=null;
            FileChannel fc=null;
            try
            {
                fileOutputStream=new FileOutputStream(file);
                fc=fileOutputStream.getChannel();//先获得fileoutputstream
                fileOutputStream.write(msgBytes);
            }catch (Exception e)
            {
                e.printStackTrace();
            }finally {
                try
                {
                    fc.close();
                    fileOutputStream.close();
                }catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }else
        {
            System.out.println("警告：文件为空，无法保存!!");
        }
    }

    //获取文件
    public byte[] getMultyFileBytes(String filepath) {
        File file = new File(filepath);
        ByteBuffer byteBuffer = null;
        FileInputStream fileInputStream = null;
        FileChannel fileChannel = null;
        try {
            if (!file.exists()) {
                System.err.println("该文件不存在...");
            } else {
                fileInputStream = new FileInputStream(file);//输入到file中去
                fileChannel = fileInputStream.getChannel();
                byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
                byteBuffer.clear();//清空缓冲区
                fileChannel.read(byteBuffer);
                return byteBuffer.array();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileChannel.close();
                fileInputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    //删除文件
    public void delete(String filepath){
        File file=new File(filepath);
        if(file.exists()){
            file.delete();
        }
    }

    //保存用户反馈信息
    public void saveReback(String userId,String reback)
    {
        File file=new File(StaticValue.REBACK_PATH);
        BufferedWriter out=null;
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            out.write(userId + ":" + reback );//+ "\r\n\r\n"
            out.write("\r\n");
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            try
            {
                out.close();
            }catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
    }



    

}
