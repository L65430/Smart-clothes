package com.example.l.l_clothers.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Activity.RegistrationActivity;
import com.example.l.l_clothers.AllMessage.CSmessage;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;

import java.sql.BatchUpdateException;


public class RegStepOne extends android.app.Fragment {

    View view;
    EditText tv_username;
    EditText tv_phone;
    EditText tv_checknumber;
    Button tv_getchecknumber;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_reg_step_one, container, false);
        tv_username = (EditText) view.findViewById(R.id.regone_et_username);
        tv_phone= (EditText) view.findViewById(R.id.regone_et_phone);
        tv_checknumber=(EditText)view.findViewById(R.id.checknumber);
        tv_getchecknumber=(Button)view.findViewById(R.id.getchecknumber);
        tv_getchecknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message sendmessage=new Message();
                sendmessage.what= CSKeys.ASK_FOR_GET_CHECK_NUMBER;//发送这个告诉主注册界面可以发送验证码了
                RegistrationActivity.handler.sendMessage(sendmessage);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        System.out.println("RegStepOne执行onAttach"+this.getClass().toString());
        super.onAttach(activity);
    }

    public String getUsername(){

        return tv_username.getText().toString();

    }
    public void resetUsername() {
        tv_username.setText("");
    }
    public String getPhonenumber(){

        return tv_phone.getText().toString();

    }
    public void resetPhonenumber() {
        tv_phone.setText("");
    }

    public String getchecknumber(){
       String result=tv_checknumber.getText().toString();
        System.out.print("result是！！！"+result);
        return result;
    }
    public void resetchecknumber(){
        tv_checknumber.setText("");
    }

}
