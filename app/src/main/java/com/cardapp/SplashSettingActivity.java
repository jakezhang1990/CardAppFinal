package com.cardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cardapp.card.MainActivity;
import com.cardapp.card.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

//    DBManger dbManger;
    private String TAG=SplashSettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_setting);

//        dbManger=DBManger.getInstance(this);
//        dbManger.add();

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

    @Override
    protected void onResume() {
        super.onResume();
        getInfoHttp();
    }

    private void getInfoHttp(){
//
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add(Commons.SETTING_MACHINE_NUMBER, sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""))
                .add(Commons.SETTING_COMMUNICATE_PWD, sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""))
                .build();

        Request request=new Request.Builder()
                .url(NetURL.URL_INFOS).post(formBody).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: URL_INFOS ");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respon=response.body().string();
                Log.i(TAG, "onResponse: URL_INFOS response="+respon);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageInfoBean bean=new Gson().fromJson(respon,new TypeToken<MessageInfoBean>(){}.getType());
                        if (bean!=null&& bean.getData()!=null){
                            String serverAdress=bean.getData().getServer_domain();
                            String schoolName=bean.getData().getOrg_name();
                            editv_serverAddress.setText(serverAdress);
                            editv_companyName.setText(schoolName);

                            editor.putString(Commons.SETTING_SERVER_ADDRESS,serverAdress);
                            editor.putString(Commons.SETTING_COMPANY_NAME,schoolName);
                            editor.commit();
                        }
                    }
                });
            }
        });
    }

}