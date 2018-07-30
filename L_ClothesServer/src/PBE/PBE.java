package PBE;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

/**
 * Created by L on 2017/5/16.
 */
public class PBE {
    // 盐
    private final static byte[] salt = { 70, -106, 92, -82, 108, 58, 6, -75 };
    // 口令与密钥
    private final static String password = "iMoMo_LineChen";
    public static void main(String[] args) throws Exception {
        String ss = "解密";
        byte[] encresults = enCrypt(ss.getBytes());
        for(int i = 0; i < ss.getBytes().length; i++){
            System.out.print(ss.getBytes()[i] +" ");
        }
        byte[] newss = deCiphering(encresults);
        System.out.println();
        for(int i = 0; i < newss.length; i++){
            System.out.print(newss[i] +" ");
        }
    }
    public static byte[] enCrypt(byte[] encptBytes) {
        byte[] results = null;// 加密结果
        long time1 = System.currentTimeMillis();
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);

            // 加密
            PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(salt, 100);// 迭代次数
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpac);
            results = cipher.doFinal(encptBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time2 = System.currentTimeMillis();
        System.out.println("本次加密用时 ： " + (time2 - time1));
        return results;
    }

    public static byte[] deCiphering(byte[] deciBytes) {
        byte[] results = null;// 解密结果
        long time1 = System.currentTimeMillis();
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);
            // 解密
            PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(salt, 100);// 迭代次数
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpac);
            results = cipher.doFinal(deciBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time2 = System.currentTimeMillis();
        System.out.println("本次解密用时 ： " + (time2 - time1));
        return results;
    }

}