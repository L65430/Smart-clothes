package Servertools;

import java.util.HashMap;

/**
 * Created by L on 2017/6/3.
 */
public class ManagerIdSessions {
    public static java.util.Map<Long, String> Map = new HashMap<Long, String>();

    public static void addUserId(Long sessionId, String userId) {
        Map.put(sessionId, userId);
        System.out.println("添加 ：会话Id = " + sessionId + "对应用户Id = " + userId);
    }


    //得到和会话对应的用户Id
    public static String getUserId(Long sessionId) {
        if(isContainsId(sessionId))
        {
            return (String)Map.get(sessionId);
        }
        return null;
    }

    public static void deleteUserId(Long sessionId)
    {
        if(isContainsId(sessionId))
        {
            Map.remove(sessionId);
        }
    }

    public static boolean isContainsId(Long sessionId)
    {
        return Map.containsKey(sessionId);
    }
}
