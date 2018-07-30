package AllMessage;

/**
 * Created by L on 2017/5/16.
 */
public class CSmessage {
    public char symbol;//判断是不是纯文本
    public String msgJson;//对非文本信息的解释
    public byte[] msgBytes;//比如头像信息之类的

    public String toString() {
        return "CSmessage [symbol=" + symbol + ", msgJson=" + msgJson + "]";
    }
}