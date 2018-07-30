package com.example.l.l_clothers.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by L on 2017/5/15.
 */
public class ForCheck {
    //检查手机号是不是正确的
    public static boolean PhonenumberFormat(String phonenumber)
    {
        Pattern pattern1=Pattern.compile("^1[3578][01379]\\d{8}$");
        Pattern pattern2=Pattern.compile("^1[34578][01256]\\d{8}$");
        Pattern pattern3=Pattern.compile("^(134[012345678]\\d{7}|1[34578][012356789]\\d{8})$");
        if(pattern1.matcher(phonenumber).matches()||
                pattern2.matcher(phonenumber).matches()||
                pattern3.matcher(phonenumber).matches()) {
            return true;
        }else
        {
            return false;
        }
    }

    public static boolean PasswdFormat(String password){
        Pattern pattern=Pattern.compile("^(?=.{6,16}$)(?![0-9]+$)(?!.*(.).*1)[0-9a-zA-Z#@$]+$");
        Matcher matcher=pattern.matcher(password);
        return matcher.matches();
    }
}
