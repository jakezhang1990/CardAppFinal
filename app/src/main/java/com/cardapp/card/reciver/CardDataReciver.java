package com.cardapp.card.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cardapp.card.MainActivity.CallBackDataDisPlay;
import com.cardapp.card.util.CardDataRst;


public class CardDataReciver extends BroadcastReceiver {

    CallBackDataDisPlay callBackDataDisPlay;

    public CardDataReciver(CallBackDataDisPlay callBackDataDisPlay) {
        this.callBackDataDisPlay = callBackDataDisPlay;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        CardDataRst cardDataRst = (CardDataRst) bundle.getSerializable("cardData");
        this.callBackDataDisPlay.onCallBack(cardDataRst);
    }
}
