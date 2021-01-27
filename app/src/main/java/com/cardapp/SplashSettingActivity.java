package com.cardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import com.cardapp.card.R;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 设置页面
 * Date: 2021/1/26 17:44
 *
 */
public class SplashSettingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText editv_machineNum,editv_pwdkey,editv_serverAddress,editv_companyName;

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
    }
}