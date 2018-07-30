package Servertools;

import AllMessage.CSmessage;
import AllMessage.MsgDb;
import Keys.CSKeys;
import Keys.JSONKeys;
import Keys.StaticValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by L on 2017/6/3.
 */
//用来转换包的
public class MsgTranceUtil {
    public static MsgTranceUtil getInstance(){
        return new MsgTranceUtil();
    }

    //数据库消息转化为网络传输消息
    public CSmessage Trance_Db2Net(MsgDb msgDb){
        CSmessage cSmessage=new CSmessage();
        switch (msgDb.msgType){
            case CSKeys.CHATING_TEXT_MSG:
                cSmessage.symbol='+';
                cSmessage.msgJson=msgDb.msgJson;
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                JSONObject jsonObject1= JSON.parseObject(msgDb.msgJson);
                cSmessage.symbol='-';
                String imagepath=jsonObject1.getString(JSONKeys.imagePath);
                cSmessage.msgBytes=FileTools.getInstance().getMultyFileBytes(
                        imagepath);
                jsonObject1.remove(JSONKeys.imagePath);
                cSmessage.msgJson=jsonObject1.toJSONString();
                FileTools.getInstance().delete(imagepath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                cSmessage.symbol='-';
                JSONObject jsonObject= JSON.parseObject(msgDb.msgJson);
                String voicepath=jsonObject.getString(JSONKeys.voicePath);
                cSmessage.msgBytes= FileTools.getInstance().getMultyFileBytes(
                        voicepath);
                jsonObject.remove(JSONKeys.voicePath);
                cSmessage.msgJson=jsonObject.toJSONString();
                FileTools.getInstance().delete(voicepath);
                break;
            case CSKeys.ADD_FRIEND:
                cSmessage.symbol='+';
                cSmessage.msgJson=msgDb.msgJson;
                break;
            case CSKeys.RESET_HEAD:
                JSONObject jsonObject2=JSON.parseObject(msgDb.msgJson);
                cSmessage.symbol='-';
                String headpath=jsonObject2.getString(JSONKeys.imagePath);
                cSmessage.msgBytes=FileTools.getInstance().getMultyFileBytes(
                        headpath);
                jsonObject2.remove(JSONKeys.imagePath);
                cSmessage.msgJson=jsonObject2.toJSONString();
                FileTools.getInstance().delete(headpath);//都是要删除本地的缓存图片，否则十分占内存和空间
                break;
        }
        return cSmessage;
    }



    //网络消息转化为数据库存储消息
    public MsgDb Trance_Net2Db(CSmessage cSmessage){
        MsgDb msgDb=new MsgDb();
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int msgtype=jsonObject.getIntValue(JSONKeys.msgType);
        switch (msgtype){
            case CSKeys.CHATING_TEXT_MSG:
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                String imagePath = StaticValue.MSG_CACHE_IMA_P_PATH
                        + jsonObject.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".png";
                FileTools.getInstance().saveMultyFile(imagePath, cSmessage.msgBytes);
                jsonObject.put(JSONKeys.imagePath, imagePath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                String voicePath = StaticValue.MSG_CACHE_VOI_P_PATH
                        + jsonObject.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".amr";
                FileTools.getInstance().saveMultyFile(voicePath, cSmessage.msgBytes);
                jsonObject.put(JSONKeys.voicePath, voicePath);
                break;
            case CSKeys.ADD_FRIEND:
                break;
            case CSKeys.RESET_HEAD:
                String headPath = StaticValue.MSG_CACHE_IMA_P_PATH
                        + jsonObject.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".png";
                FileTools.getInstance().saveMultyFile(headPath, cSmessage.msgBytes);
                jsonObject.put(JSONKeys.imagePath, headPath);
                break;
        }
        msgDb.msgType=msgtype;
        msgDb.msgJson=jsonObject.toJSONString();
        return msgDb;
    }

    
}
