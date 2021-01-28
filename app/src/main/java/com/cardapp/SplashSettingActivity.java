package com.cardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cardapp.card.MainActivity;
import com.cardapp.card.R;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 设置页面
 * Date: 2021/1/26 17:44
 *
 */
public class SplashSettingActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText editv_machineNum,editv_pwdkey,editv_serverAddress,editv_companyName;
    Button btn_back,btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_setting);

        sharedPreferences=getSharedPreferences(Commons.SHARED_PREF_SPLASH,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        editv_machineNum=findViewById(R.id.editv_machineNum);
        editv_pwdkey=findViewById(R.id.editv_pwdkey);
        editv_serverAddress=findViewById(R.id.editv_serverAddress);
        editv_companyName=findViewById(R.id.editv_companyName);

        editv_machineNum.setText(sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""));
        editv_pwdkey.setText(sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""));
        editv_serverAddress.setText(sharedPreferences.getString(Commons.SETTING_SERVER_ADDRESS,""));
        editv_companyName.setText(sharedPreferences.getString(Commons.SETTING_COMPANY_NAME,""));

        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn_back)){
            startActivity(new Intent(this,/*HomeActivity*/MainActivity.class));
            finish();
        }else if (v.equals(btn_save)){
            String terminal_numStr=editv_machineNum.getText().toString().trim();
            editor.putString(Commons.SETTING_MACHINE_NUMBER,terminal_numStr);
            String key=editv_pwdkey.getText().toString().trim();
            editor.putString(Commons.SETTING_COMMUNICATE_PWD,key);
            String serverAddress=editv_serverAddress.getText().toString().trim();
            editor.putString(Commons.SETTING_SERVER_ADDRESS,serverAddress);
            String companyName=editv_companyName.getText().toString();
            editor.putString(Commons.SETTING_COMPANY_NAME,companyName);
            editor.commit();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
}