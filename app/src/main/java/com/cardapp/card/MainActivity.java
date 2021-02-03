package com.cardapp.card;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cardapp.Commons;
import com.cardapp.CreateOrderBean;
import com.cardapp.HomeActivity;
import com.cardapp.NetURL;
import com.cardapp.OrderStatusResultBean;
import com.cardapp.ResultBean;
import com.cardapp.TimeRunnable;
import com.cardapp.card.reciver.CardDataReciver;
import com.cardapp.card.service.CardService;
import com.cardapp.card.util.CardDataRst;
import com.cardapp.card.util.CardOperator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView readText;
    private static TextView writeText;
    private static TextView initPwdText;
    private static TextView initCardText;
    private static TextView oldCardSecText;
    private static TextView voidCardText;
    private static TextView txtDisplay;
    private static CardOperator cardOperator = null;

    private TextView netStatTV, netStatTV2;

    CardDataReciver cardDataReciver;


    //以下逻辑实现对IC卡的相关操作

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                cardOperator = (CardOperator) msg.obj;
                Toast.makeText(getApplicationContext(), "初始化成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == -1) {
                Toast.makeText(getApplicationContext(), "初始化失败", Toast.LENGTH_LONG).show();
            } else if(msg.what==0x011){
                boolean isNetwork=isNetworkConnected();
//                boolean wifi=isWifiConnected();
//                boolean mobile=isMobileConnected();
                if (isNetwork){
                    netStatTV2.setText("已联网");

                    /*if (wifi){
                        netStatTV2.setText("已联网-wifi");
                    }
                    if (mobile){
                        netStatTV2.setText("已联网-4G");
                    }*/
                }else {
                    netStatTV2.setText("设备未联网");
                }
            }
            return true;
        }
    });

    static TextView tv_date, tv_schoolName, tv_machineNum , tv_title;
    private SharedPreferences sharedPreferences;
    private String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_main);
        readText = findViewById(R.id.cardReadData);
        writeText = findViewById(R.id.cardWriteData);
        initPwdText = findViewById(R.id.initPwdData);
        initCardText = findViewById(R.id.cardNumTxt);
        voidCardText = findViewById(R.id.voidCardInitMTxt);
        oldCardSecText = findViewById(R.id.oldCardSec);
        txtDisplay = findViewById(R.id.txtDisplay);
        Button btnRead = findViewById(R.id.btnread);
        Button btnWrite = findViewById(R.id.btnwrite);
        Button btnInitSec = findViewById(R.id.btnchkSec);
        Button btnCardNum = findViewById(R.id.btnIntCard);
        Button btnAutoRun = findViewById(R.id.btnAuto);
        Button btnVoidCardInit = findViewById(R.id.voidCardInit);
        Button btnOldCardRestCtrl = findViewById(R.id.btnRestOldCardSec);
        Button btnStopService = findViewById(R.id.btnStopService);
        Button btnRestOneBlk = findViewById(R.id.btnRestOneBlk);

        //添加监听
        btnRead.setOnClickListener(this);
        btnWrite.setOnClickListener(this);
        btnInitSec.setOnClickListener(this);
        btnCardNum.setOnClickListener(this);
        btnAutoRun.setOnClickListener(this);
        btnVoidCardInit.setOnClickListener(this);
        btnOldCardRestCtrl.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
        btnRestOneBlk.setOnClickListener(this);*/
        //初始化卡设备
        cardInitDev();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        tv_date=findViewById(R.id.tv_date);
        tv_schoolName=findViewById(R.id.tv_schoolName);
        tv_machineNum=findViewById(R.id.tv_machineNum);

        netStatTV=findViewById(R.id.netStatTV);
        netStatTV2=findViewById(R.id.netStatTV2);

        tv_title=findViewById(R.id.tv_title);

        sharedPreferences=getSharedPreferences(Commons.SHARED_PREF_SPLASH,MODE_PRIVATE);
        tv_schoolName.setText(sharedPreferences.getString(Commons.SETTING_COMPANY_NAME,""));
        tv_machineNum.setText("机号："+sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,"01"));

        //1分钟给服务器发一次机器状态
        new Thread(new MachineStateRunnable()).start();

        //判断网络状态，10秒判断一次
        new Thread(new NetStatRunable()).start();


        TimeRunnable timeRunnable=new TimeRunnable(tv_date);
        new Thread(timeRunnable).start();

        cardDataReciver = new CardDataReciver(new CallBackDataDisPlay());
        IntentFilter intentFilter = new IntentFilter("cn.soft.recvier.MY_RECIVER");
        registerReceiver(cardDataReciver, intentFilter);

        Intent intent = new Intent(this, CardService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cardOp", cardOperator);
        intent.putExtras(bundle);
        startService(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnIntCard:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = null;
                        try {
                            str = cardOperator.getCardNumberStr();
                            if (str == null) {
                                Toast.makeText(getApplicationContext(), "未检查IC卡", Toast.LENGTH_LONG).show();
                            } else {
                                initCardText.setText(str);
                            }
                        } catch (InterruptedException e) {
                            Toast.makeText(getApplicationContext(), "读取卡出错", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.btnAuto:
                Intent intent = new Intent(this, CardService.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cardOp", cardOperator);
                intent.putExtras(bundle);
                startService(intent);
                break;
            case R.id.btnchkSec:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = null;
                        try {
                            str = cardOperator.getCardKeyStr();
                            if (str == null) {
                                Toast.makeText(getApplicationContext(), "密钥校验失败！", Toast.LENGTH_LONG).show();
                            } else {
                                initPwdText.setText(str);
                            }
                        } catch (InterruptedException e) {
                            Toast.makeText(getApplicationContext(), "内部错误！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.btnread:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CardDataRst ss = cardOperator.readCardData();
                            if (ss != null) {
                                readText.setText("余额:" + String.valueOf(ss.getM()) + ",次数：" + ss.getTime());
                            } else {
                                readText.setText("未读到数据");
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.btnwrite:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = writeText.getText().toString();
                            boolean ss = cardOperator.writeData(data, 1);
                            writeText.setText(String.valueOf(ss));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.voidCardInit:
                String data = voidCardText.getText().toString();
                try {
                    int f = cardOperator.voidCardInit(data);
                    if (f == 0) {
                        Toast.makeText(getApplicationContext(), "初始化成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "初始化失败", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "初始化异常", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnRestOldCardSec:
                String oldCardKeyHexStr = oldCardSecText.getText().toString();
                String data1 = writeText.getText().toString();
                try {
                    boolean f = cardOperator.resetOldCardCtrl(oldCardKeyHexStr, data1);
                    if (f) {
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnStopService:
                Intent intent1 = new Intent(this, CardService.class);
                stopService(intent1);
                break;
            case R.id.btnRestOneBlk:
                try {
                    boolean f = cardOperator.restFirstBlock();
                    if (f) {
                        txtDisplay.setText("修改成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cardDataReciver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public /*static*/ class CallBackDataDisPlay {
        public void onCallBack(CardDataRst cardDataRst) {
            if (cardDataRst != null) {
//                readText.setText("余额:" + String.valueOf(cardDataRst.getM()) + ",次数：" + cardDataRst.getTime());
//                tv_title.setText("余额:" + String.valueOf(cardDataRst.getM()) + ",次数：" + cardDataRst.getTime());
                tv_title.setText("余额:" + String.valueOf(cardDataRst.getM()) );

                Log.i(TAG, "onCallBack: 广播回调刷新界面");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Log.i(TAG, "run: cardDataRst.getReadKeyHexStr()="+cardDataRst.getReadKeyHexStr());
                        Log.i(TAG, "run: "+cardDataRst.getCardNumHexStr());
                        String card_number1=Integer.valueOf(cardDataRst.getCardNumHexStr().substring(0,4),16).toString();
                        String card_number2=Integer.valueOf(cardDataRst.getCardNumHexStr().substring(4,cardDataRst.getCardNumHexStr().length()),16).toString();
                        String card_number_full=card_number1+card_number2;
                        Log.i(TAG, "run: card_number_full="+card_number_full);
                        getMoneyHttp(cardDataRst.getM() ,card_number_full);//申请领款
                    }
                },2*1000);


            }
        }
    }

    class MachineStateRunnable implements Runnable{
        @Override
        public void run() {
            do {
                try {
                    getMessageHttp();
                    Thread.sleep(60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);

        }
    }

    private void getMessageHttp(){
        //post请求提交键值对，1分钟向服务器发送一次状态
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add(Commons.SETTING_MACHINE_NUMBER, sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""))
                .add(Commons.SETTING_COMMUNICATE_PWD, sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""))
                .build();
        Log.i(TAG, "getMessageHttp: 状态报告接口参数："
                +Commons.SETTING_MACHINE_NUMBER+":"+sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,"")
                +Commons.SETTING_COMMUNICATE_PWD+":"+sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""));
        Request request=new Request.Builder()
                .url(NetURL.URL_MACHINE_STATE).post(formBody).build();
        Call call=okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "NetURL.URL_MACHINE_STATE onFailure: "+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netStatTV.setText("离线");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respon=response.body().string();
                Log.i(TAG, "NetURL.URL_MACHINE_STATE onResponse: "+respon);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResultBean resultBean=new Gson().fromJson(respon, new TypeToken<ResultBean>(){}.getType());
                        if (resultBean!=null && resultBean.getStatus()==1){
                            netStatTV.setText("在线");
                        }else{
                            netStatTV.setText("离线");
                        }

                    }
                });
            }
        });

    }

    private void getMoneyHttp(BigDecimal balance, String ReadKeyHexStr){

        Log.i(TAG, "getMoneyHttp: 卡片ReadKeyHexStr= "+ReadKeyHexStr);

        if (TextUtils.isEmpty(ReadKeyHexStr)){
            Toast.makeText(this,"卡片SN号读取失败，非IC卡",Toast.LENGTH_SHORT).show();
        }else{
            //申请领款接口
            OkHttpClient okHttpClient=new OkHttpClient();
            FormBody formBody = new FormBody.Builder()
                    .add(Commons.SETTING_MACHINE_NUMBER, sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""))
                    .add(Commons.SETTING_COMMUNICATE_PWD, sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""))
                    .add(Commons.SN_CARD_SN_NUMBER, ReadKeyHexStr)
                    .build();
            Log.i(TAG, "getMoneyHttp: 申请领款接口参数"
                    +Commons.SETTING_MACHINE_NUMBER+":"+sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,"")
                    +Commons.SETTING_COMMUNICATE_PWD+":"+sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,"")
                    +Commons.SN_CARD_SN_NUMBER+":"+ReadKeyHexStr);
            Request request=new Request.Builder()
                    .url(NetURL.URL_APPLY_DRAW_MONEY).post(formBody).build();
            Call call=okHttpClient.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.i(TAG, "NetURL.URL_APPLY_DRAW_MONEY onFailure: "+e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String respon=response.body().string();
                    Log.i(TAG, "NetURL.URL_APPLY_DRAW_MONEY onResponse: "+respon);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            CreateOrderBean createOrderBean=new Gson().fromJson(respon, new TypeToken<CreateOrderBean>(){}.getType());
                            if (createOrderBean!=null){
                                int status=createOrderBean.getStatus();
                                String msg=createOrderBean.getMsg();
                                if (status==1){
                                    //成功，开始写卡
//                                    tv_title.setText("余额:");
                                    CreateOrderBean.DataBean dataBean=createOrderBean.getData();
                                    if (dataBean!=null){
                                        String data=dataBean.getMoney();
                                        String totalMoney = String.valueOf(balance.add(new BigDecimal(data)) );
                                        String orderNumber=dataBean.getSerial();
                                        try {
//                                                String data = writeText.getText().toString();
                                            boolean ss = cardOperator.writeData(data, 1);
                                            Log.i(TAG, "run: data="+data);
                                            Log.i(TAG, "run: ss="+ss);
//                                            writeText.setText(String.valueOf(ss));
                                            int write_status=0;
                                            if (ss){
                                                //写卡成功 1
                                                write_status=1;
                                            }else{
                                                //写卡失败 2
                                                write_status=2;
                                            }
                                            sendOrderStatusHttp(write_status, orderNumber, data, totalMoney);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }else{
                                    //失败
                                    tv_title.setText("领款失败");
                                    tv_schoolName.setText(msg);
                                    //5秒以后恢复状态
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_title.setText("领 款 机");
                                            tv_schoolName.setText(sharedPreferences.getString(Commons.SETTING_COMPANY_NAME,""));
                                        }
                                    },5*1000);
                                }
                            }
                        }
                    });

                }
            });
        }


    }


    //给服务器发送写卡成功还是失败的状态
    private void sendOrderStatusHttp(int orderStatus,String orderNum ,String money, String totalMoney){
        //post请求提交键值对，1分钟向服务器发送一次状态
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add(Commons.SETTING_MACHINE_NUMBER, sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,""))
                .add(Commons.SETTING_COMMUNICATE_PWD, sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,""))
                .add(Commons.SN_CARD_STATUS,String.valueOf(orderStatus))
                .add(Commons.SN_CARD_ORDER_NUM,orderNum)
                .build();
        Log.i(TAG, "sendOrderStatusHttp: 读写卡接口请求参数："
                +Commons.SETTING_MACHINE_NUMBER+":"+sharedPreferences.getString(Commons.SETTING_MACHINE_NUMBER,"")
                +Commons.SETTING_COMMUNICATE_PWD+":"+sharedPreferences.getString(Commons.SETTING_COMMUNICATE_PWD,"")
                +Commons.SN_CARD_STATUS+":"+String.valueOf(orderStatus)
                +Commons.SN_CARD_ORDER_NUM+":"+orderNum);

        Request request=new Request.Builder()
                .url(NetURL.URL_CONFIRM_WRITE_STATUS).post(formBody).build();
        Call call=okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "NetURL.URL_CONFIRM_WRITE_STATUS onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respon=response.body().string();
                Log.i(TAG, "NetURL.URL_CONFIRM_WRITE_STATUS onResponse: "+respon);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OrderStatusResultBean resultBean=new Gson().fromJson(respon, new TypeToken<OrderStatusResultBean>(){}.getType());
                        if (resultBean!=null){
                            int status=resultBean.getStatus();
                            String msg=resultBean.getMsg();
                            if (status==1){
                                //成功，删除本地保存的订单号


                            }else {
                                //失败，保存订单号到本地数据库，


                            }
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();

                            tv_title.setText("领款成功");
                            tv_schoolName.setText("领款金额："+money+"\n当前余额："+totalMoney);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tv_title.setText("领 款 机");
                                    tv_schoolName.setText(sharedPreferences.getString(Commons.SETTING_COMPANY_NAME,""));
                                }
                            },5*1000);

                        }

                    }
                });
            }
        });

    }


    //TODO 需要增加一个后台不断提交给服务端写卡成功，未成功报告状态给服务端的http请求方法。


    //TODO 需要增加本地缓存订单策略。



    /**
     * 网络是否可用
     * @return
     */
    public boolean isNetworkConnected(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo()!=null){
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 判断WiFi是否可用
     */
    public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable();
        }else {
            return false;
        }
    }

    /**
     * 判断mobile 4G网络是否可用
     */
    public boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }else{
            return false;
        }
    }

    public class NetStatRunable implements Runnable{
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message=mHandler.obtainMessage();
                message.what=0x011;
                mHandler.sendMessage(message);
            }while (true);


        }
    }


}