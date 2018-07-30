package com.example.l.l_clothers.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.StaticValues;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;

public class WelcomeActivity extends AppCompatActivity {
    private int splashDelay=3000;//持续时间
    private ImageView iv_welcome;
    MyAnimationListener myAnimationListener;//动画监听器
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        iv_welcome=(ImageView)findViewById(R.id.welcome_iv_welcome);
        myAnimationListener=new MyAnimationListener();
        startanimation();
        checklogin();//进入的时候就去检查

        ChatDBUtils db=new ChatDBUtils(this);
        if(!db.isTableExit("recent_chat_table"))
        {
            db.createLaticeChatTable();//创建聊天表
            System.out.print("已经执行了创建聊天表！！！");
        }


    }

    private void checklogin(){
        //把所有的数据都存在同一个sharedpreference中
        SharedPreferenceUtil util=new SharedPreferenceUtil(this);
//        util.SaveValues(StaticValues.userPasswd,"");
//        util.SaveValues(StaticValues.userPhone,"");
        String phonenumber=util.getvalues(StaticValues.userPhone);
        String pwd=util.getvalues(StaticValues.userPasswd);
        if("".equals(phonenumber)||"".equals(pwd)){
            intent=new Intent(this,SignInActivity.class);
        }else
        {
            if(util.getvalues(StaticValues.userjob).equals("医生")){
                intent=new Intent(this,MainDoctorActivity.class);
            }else{
                intent=new Intent(this,MainActivity.class);
            }

        }
    }

    private void startanimation()
    {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(splashDelay);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(myAnimationListener);
        animationSet.addAnimation(scaleAnimation);
        iv_welcome.startAnimation(animationSet);
    }

    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
             startActivity(intent);
             overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
             finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
