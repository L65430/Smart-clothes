package com.example.l.l_clothers.Info;

/**
 * Created by L on 2017/6/1.
 */

//这是用来在查看医生列表里面显示的
public class Doctorinfo {
    public String doctorId;
    public String doctorname;
    public String doctorinfo;
    @Override
    public String toString(){
        return "Doctorinfo [doctorname=" + doctorname + ", doctorinfo="
                + doctorinfo + ", doctorId=" +doctorId+"]";
    }
}
