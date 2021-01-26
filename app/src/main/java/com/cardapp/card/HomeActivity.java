package com.cardapp.card;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.cardapp.TimeRunnable;

import okhttp3.OkHttpClient;

public class HomeActivity extends AppCompatActivity {

    TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        tv_date=findViewById(R.id.tv_date);

        TimeRunnable  timeRunnable=new TimeRunnable(tv_date);
        new Thread(timeRunnable).start();


    }

    private void getMessageHttp(){
        OkHttpClient okHttpClient=new OkHttpClient();
        
    }

}