package com.example.l.l_clothers.Info;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.widget.Button;

import com.example.l.l_clothers.Fragment.RegStepOne;
import com.example.l.l_clothers.Fragment.RegStepThree;
import com.example.l.l_clothers.Fragment.RegStepTwo;

/**
 * Created by L on 2017/5/30.
 */

//和注册有关的信息
public class RegActivityData {
    public Fragment[] fragmentarray;
    public RegStepOne regStepOne;
    public RegStepTwo regStepTwo;
    public RegStepThree regStepThree;
    public Button btn_left;
    public Button btn_right;
    public int step;
    public String username;
    public String birday;
    public String password;
    public String sex;
    public Bitmap myBitmap;
    public FragmentManager manager;
    public String checknumber;//验证码
}
