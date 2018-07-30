package Servertools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by L on 2017/5/16.
 */
public class PasswordUtil {
    private final static String[] hexDigits={"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    public static PasswordUtil getInstance()
    {
        return new PasswordUtil();
    }

    //生成密码
    public String createNewPwd() {
        String pwd = "";
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                pwd += (char) ((int) (Math.random() * 10) + 'a') + "";
            } else
                pwd += (int) (Math.random() * 10);
        }
        return pwd;
    }

    private String byteToHexString(byte b)
    {
        int n=b;
        if(n<0)
        {
            n=256+n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private String byteArrayToHexString(byte[]b)
    {
        StringBuffer resultSb=new StringBuffer();
        for(int i=0;i<b.length;i++)
        {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    public String toMD5(String pwd)
    {
        byte[] results_byte = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");//引入加密方法
            results_byte = md5.digest(pwd.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteArrayToHexString(results_byte);
    }
}