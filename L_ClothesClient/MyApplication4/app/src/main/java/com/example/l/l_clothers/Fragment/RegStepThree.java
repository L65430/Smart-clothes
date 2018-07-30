package com.example.l.l_clothers.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.l.l_clothers.R;

public class RegStepThree extends android.app.Fragment {

    View view;
    EditText password;
    EditText passwordagain;
    TextView tv_hint;
    RadioGroup job;
    RadioButton left;
    RadioButton right;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reg_step_three, container, false);
        password = (EditText) view.findViewById(R.id.regthree_et_password);
        passwordagain = (EditText) view.findViewById(R.id.regthree_et_passwordagain);
        tv_hint = (TextView) view.findViewById(R.id.regthree_tv_hint);
        job=(RadioGroup)view.findViewById(R.id.regthree_job);
        left=(RadioButton)view.findViewById(R.id.regthree_rb_doctor);
        right=(RadioButton)view.findViewById(R.id.regthree_rb_patient);

        return view;
    }

    public String getPassword (){
        String p = password.getText().toString();
        return p;
    }
    public String getPasswordAgain() {
        String pa = passwordagain.getText().toString();
        return pa;
    }

    public void resetPassword() {
        password.setText("");
        passwordagain.setText("");
    }

    public String getjob(){
        String job="";
        if(left.isChecked()) {
            job = left.getText().toString();
        } else if(right.isChecked()) {
            job = right.getText().toString();
        } else {
            job = null;
        }
        return job;
    }

}