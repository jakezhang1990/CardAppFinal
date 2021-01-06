package com.cardapp.card;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cardapp.card.reciver.CardDataReciver;
import com.cardapp.card.service.CardService;
import com.cardapp.card.util.CardDataRst;
import com.cardapp.card.util.CardOperator;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView readText;
    private static TextView writeText;
    private static TextView initPwdText;
    private static TextView initCardText;
    private static TextView oldCardSecText;
    private static TextView voidCardText;
    private static TextView txtDisplay;
    private static CardOperator cardOperator = null;

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
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        btnRestOneBlk.setOnClickListener(this);
        //初始化卡设备
        cardInitDev();

        cardDataReciver = new CardDataReciver(new CallBackDataDisPlay());
        IntentFilter intentFilter = new IntentFilter("cn.soft.recvier.MY_RECIVER");
        registerReceiver(cardDataReciver, intentFilter);

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

    public static class CallBackDataDisPlay {
        public void onCallBack(CardDataRst cardDataRst) {
            if (cardDataRst != null) {
                readText.setText("余额:" + String.valueOf(cardDataRst.getM()) + ",次数：" + cardDataRst.getTime());
            }
        }
    }
}