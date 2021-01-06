package com.cardapp.card.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.cardapp.card.util.CardDataRst;
import com.cardapp.card.util.CardOperator;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class CardService extends Service {

    //
    //  CardOperator cardOperator;



    @Override
    public void onCreate() {
        super.onCreate();
        //registerReceiver()
//        try {
//            cardOperator=CardOperator.createObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        CardOperator cardOperator = (CardOperator) bundle.getSerializable("cardOp");
        Intent cardIntent = new Intent("cn.soft.recvier.MY_RECIVER");

        Thread thread = new Thread(new Runnable() {
            String tempCardNum="";
            @Override
            public void run() {
                while (true) {
                    try {
                        String cardNum = cardOperator.getCardNumberStr();
                        if (cardNum !=null  && !cardNum.equals(tempCardNum)) {
                            tempCardNum=cardNum;
                            CardDataRst cardDataRst = cardOperator.readCardData();
                            Bundle bundle1 = new Bundle();
                            bundle1.putSerializable("cardData", cardDataRst);
                            cardIntent.putExtras(bundle1);
                            sendBroadcast(cardIntent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
