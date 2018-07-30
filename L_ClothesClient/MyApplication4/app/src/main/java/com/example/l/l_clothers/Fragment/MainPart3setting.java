package com.example.l.l_clothers.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Activity.PersonalInfoActivity;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Utils.ChatDBUtils;
import com.example.l.l_clothers.Utils.SharedPreferenceUtil;
import com.example.l.l_clothers.Utils.SwitchButton;
import com.example.l.l_clothers.Utils.myDialog;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by L on 2017/6/6.
 */

public class MainPart3setting extends android.app.Fragment {
    View view;
    private Context context;
    RelativeLayout rl_clearAllmsg;
    SwitchButton sb_voice;
    RelativeLayout rl_clearLatice;
    SwitchButton sb_shake;
    RelativeLayout r1_personalsetting;
    SharedPreferenceUtil mSpUtil;

    boolean isRing;
    boolean isVibartion;
    boolean isShareLoc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_part3, container, false);
        sb_voice = (SwitchButton) view.findViewById(R.id.setting_sb_voice);
        sb_shake = (SwitchButton) view.findViewById(R.id.setting_sb_shake);
        r1_personalsetting=(RelativeLayout)view.findViewById(R.id.setting_rl_personal);
        r1_personalsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
        rl_clearAllmsg=(RelativeLayout)view.findViewById(R.id.rl_clearAllmsg);
        rl_clearAllmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllMsg();
            }
        });
        rl_clearLatice=(RelativeLayout)view.findViewById(R.id.rl_clearLatice);
        rl_clearAllmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLatice();
            }
        });

        isRing = ClientManger.isRing;
        isVibartion = ClientManger.isVibration;
        sb_voice.setChecked(!isRing);
        sb_shake.setChecked(!isVibartion);

        sb_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSpUtil.setIsRing(false);
                    ClientManger.isRing = false;
                } else {
                    mSpUtil.setIsRing(true);
                    ClientManger.isRing = true;
                }
            }
        });


        sb_shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mSpUtil.setIsVibration(false);
                    ClientManger.isVibration = false;
                    System.out.println("close");
                } else {
                    mSpUtil.setIsVibration(true);
                    ClientManger.isVibration = true;
                    System.out.println("open");
                }
            }
        });


        return view;
    }


    //清空聊天记录
    public void clearAllMsg() {
        myDialog dialog = new myDialog(context, "清空中...");
        dialog.show();
        ChatDBUtils db = new ChatDBUtils(context);
        List<String> friendIds = MainActivity.mp0friend.getFriendsIds();//这个还不好说，具体要怎么搞
        if (friendIds.size() > 0) {
            for (String Id : friendIds) {
                String tName = "msg" + ClientManger.clientId + "_" + Id;
                if (db.isTableExit(tName)) {
                    db.clearMsg(Id);
                }
            }
        }
        dialog.dismiss();
        Toast.makeText(context, "清空完成", Toast.LENGTH_LONG).show();
    }

    //清空最近联系人
    public void clearLatice()
    {
        myDialog dialog = new myDialog(context, "清空中...");
        dialog.show();
        MainActivity.mp0talk.clearrecent();
        dialog.dismiss();
        Toast.makeText(context, "清空完成", Toast.LENGTH_LONG).show();
    }


}

