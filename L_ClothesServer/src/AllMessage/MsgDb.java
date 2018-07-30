package AllMessage;

/**
 * Created by L on 2017/6/3.
 */
//数据库中的信息
public class MsgDb {
    public int msgType;
    public String msgJson;

    @Override
    public String toString() {
        return "MsgDb [msgType=" + msgType + ", msgJson=" + msgJson + "]";
    }

}
