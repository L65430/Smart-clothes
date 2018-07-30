package Servertools;

import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 2017/6/3.
 */
public class ManagerClientSession {
    public static Map<String,IoSession> clientMap=new HashMap<String,IoSession>();
    public static IoSession getSession(String userId)
    {
        if(isContainsId(userId))
        {
            return (IoSession)clientMap.get(userId);
        }
        return null;
    }

    public static void addSession(String userId,IoSession ioSession)
    {
        clientMap.put(userId,ioSession);
    }

    public static void deleteSession(String userId)
    {
        System.out.println("删除session--" + userId);
        if(isContainsId(userId))
        {
            clientMap.remove(userId);
        }
    }


    public static boolean isContainsId(String userId)
    {
        return clientMap.containsKey(userId);
    }
}
