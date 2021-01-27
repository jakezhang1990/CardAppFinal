package com.cardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.cardapp.card.R;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 设置页面
 * Date: 2021/1/26 17:44
 *
 */
public class SplashSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_setting);
    }
}