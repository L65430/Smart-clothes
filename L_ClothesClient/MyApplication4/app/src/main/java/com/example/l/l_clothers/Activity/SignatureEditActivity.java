package com.example.l.l_clothers.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.StaticValues.CSKeys;

public class SignatureEditActivity extends Activity {
    ImageButton ib_back;
    Button btn_submit;
    EditText et_signature;//nick_et_signature
    String signature = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signature_edit);
        initview();
        ib_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature=et_signature.getText().toString();
                if(!signature.equals("")) {
                    MainActivity.myBinder.ResetUserInfo(CSKeys.RESET_SIGNATUE, signature);
                    ClientManger.personSignature = signature;
                    PersonalInfoActivity.refreshClientInfo(CSKeys.RESET_SIGNATUE, signature);
                    MainActivity.refreshPinfo(CSKeys.RESET_SIGNATUE,signature);
                    finish();
                }else
                {
                    Toast.makeText(SignatureEditActivity.this, "请输入个性签名", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void initview()
    {
        ib_back = (ImageButton) findViewById(R.id.signature_ib_back);
        et_signature = (EditText) findViewById(R.id.nick_et_signature);
        btn_submit = (Button) findViewById(R.id.signature_submit);
    }
}
