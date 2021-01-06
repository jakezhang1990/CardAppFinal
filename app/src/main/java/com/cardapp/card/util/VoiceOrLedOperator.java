package com.cardapp.card.util;

import android.util.Log;

import com.cardapp.card.contants.Contants;

import java.io.File;
import java.io.FileOutputStream;

public class VoiceOrLedOperator {
    public static String TAG="VoiceOrLedOperator";

    public static void openVoice(){
        writeProc(Contants.procpath,Contants.open_bee_voice.getBytes());
    }

    public static  void closeVoice(){
        writeProc(Contants.procpath,Contants.close_bee_voice.getBytes());
    }

    private static boolean  writeProc(String path, byte[] buffer) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
           return  true;
        } catch (Exception e) {
            Log.e(TAG,e.getMessage(),e);
            return  false;
        }
    }
}
