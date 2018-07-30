package com.example.l.l_clothers.Codecfactory;

import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.PBE.PBE;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by L on 2017/5/10.
 */
public class LDecoder extends CumulativeProtocolDecoder {
    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    //用于编码
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in,//也是mina框架中的一种字节缓冲区
                               ProtocolDecoderOutput out) throws Exception {
        CSmessage cSmessage = new CSmessage();
        int pos = in.position();
        int remaining = in.remaining();
        try {
            // 判断长度,开头symbol 是char型,用来进行解码
            if (remaining < 2) {
                in.position(pos);
                return false;
            }
            char symbol = in.getChar();// 相当于消费掉字节流
            cSmessage.symbol = symbol;
            if (symbol == '+') {
                int msgJsonLen = 0;// msgJson长度
                // 判断是否够解析出的长度
                msgJsonLen = in.getInt();
                if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
                    in.position(pos);
                    return false;
                }
                byte[] temp = new byte[msgJsonLen];
                in.get(temp);//得到加密后byte数组
                cSmessage.msgJson = new String(PBE.deCiphering(temp), "UTF-8");
                out.write(cSmessage);
//				Log.i("--","客户端解码："+moMsg.toString());
            } else if (symbol == '-') {
                // 接收文本信息
                int msgJsonLen = 0;// msgJson长度
                int msgBytesLen = 0;// msgBytes长度
                msgJsonLen = in.getInt();
                if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
                    in.position(pos);
                    return false;
                }
                byte[] temp1 = new byte[msgJsonLen];
                in.get(temp1);//得到加密后byte数组
                cSmessage.msgJson = new String(PBE.deCiphering(temp1), "UTF-8");
                // 接收图片信息
                msgBytesLen = in.getInt();
                if (remaining - 2 - 4 - 4 - msgJsonLen < msgBytesLen
                        || msgBytesLen < 0) {
                    in.position(pos);
                    return false;
                }
                byte[] temp2 = new byte[msgBytesLen];
                in.get(temp2);//得到加密后byte数组
                cSmessage.msgBytes = PBE.deCiphering(temp2);
                out.write(cSmessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            in.position(pos);
            return false;
        }
        return true;
    }
}

