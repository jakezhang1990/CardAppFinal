package com.cardapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/1/26 16:40
 */
public class TimeRunnable implements Runnable{

    private static final int key1=0x10;


    private TextView textView;

    public TimeRunnable( TextView textView){
        this.textView=textView;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
                Message message=new Message();
                message.what=key1;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (true);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==key1){
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm ", Locale.CHINA);
                String date=sdf.format(new Date());
                textView.setText(date);
            }
        }
    };
}
