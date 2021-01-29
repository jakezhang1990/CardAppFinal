package com.cardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cardapp.Commons;
import com.cardapp.NetURL;
import com.cardapp.TimeRunnable;
import com.cardapp.card.MainActivity;
import com.cardapp.card.R;
import com.cardapp.card.reciver.CardDataReciver;
import com.cardapp.card.service.CardService;
import com.cardapp.card.util.CardOperator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    CardDataReciver cardDataReciver;
    TextView tv_date, tv_schoolName, tv_machineNum;


    private static CardOperator cardOperator = null;

    //以下逻辑实现对IC卡的相关操作

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                cardOperator = (CardOperator) msg.obj;
                Toast.makeText(getApplicationContext(), "初始化成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == -1) {
                Toast.makeText(getApplicationContext(), "初始化失败", Toast.LENGTH_LONG).show();
            }
            return true;
        }
    });


    private String TAG=HomeActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        tv_date=findViewById(R.id.tv_date);
        tv_schoolName=findViewById(R.id.tv_schoolName);
        tv_machineNum=findViewById(R.id.tv_machineNum);

        sharedPreferences=getSharedPreferences(Commons.SHARED_PREF_SPLASH,MODE_PRIVATE);
        tv_schoolName.setText(sharedPreferences.getString(Commons.SETTING_COMPANY_NAME,""));
        tv_machineNum.setText("机号："+sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,"01"));

        //1分钟给服务器发一次机器状态
        new Thread(new MachineStateRunnable()).start();


        TimeRunnable  timeRunnable=new TimeRunnable(tv_date);
        new Thread(timeRunnable).start();

        //初始化卡设备
        cardInitDev();

//        cardDataReciver = new CardDataReciver(new MainActivity.CallBackDataDisPlay());
        IntentFilter intentFilter = new IntentFilter("cn.soft.recvier.MY_RECIVER");
        registerReceiver(cardDataReciver, intentFilter);
    }

    void cardInitDev() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化设备
                Message message = null;
                try {
                    CardOperator card = CardOperator.createObject();
                    message = mHandler.obtainMessage();
                    message.obj = card;
                    message.what = 0;
                    mHandler.sendMessage(message);
                    // Toast.makeText(getApplicationContext(),"初始化成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = -1;
                    mHandler.sendMessage(message);
                }
            }
        });
        thread.start();
    }

    private void getMessageHttp(){
        //post请求提交键值对
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add(Commons.SETTING_MACHINE_NUMBER, sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""))
                .add(Commons.SETTING_COMMUNICATE_PWD, sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""))
                .build();
        Request request=new Request.Builder()
                .url(NetURL.URL_MACHINE_STATE).post(formBody).build();
        Call call=okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "NetURL.URL_MACHINE_STATE onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "NetURL.URL_MACHINE_STATE onResponse: "+response.body().string());
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, CardService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cardOp", cardOperator);
        intent.putExtras(bundle);
        startService(intent);
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cardDataReciver);
    }*/


    class MachineStateRunnable implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(6*1000);
                getMessageHttp();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}