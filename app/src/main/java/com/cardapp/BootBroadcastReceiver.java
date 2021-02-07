package com.cardapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/1/26 11:53
 */
public class BootBroadcastReceiver extends BroadcastReceiver  {

    static  final  String ACTION =  "android.intent.action.BOOT_COMPLETED" ;



    @Override
    public void onReceive(Context context, Intent intent) {

        /*if  (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent =  new  Intent(context, HomeActivity. class );   // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }*/

        //启动app代码
        /*if  (intent.getAction().equals(ACTION)) {
            Intent newIntent = context.getPackageManager()
                    .getLaunchIntentForPackage("com.cardapp.card");  //apk包名
            context.startActivity(newIntent);*/

        Intent autoStart = new Intent(context, SplashSettingActivity.class);//启动跳转到登录页面
        autoStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必加  否则系统无法接受发送广播通知App启动
        context.startActivity(autoStart);

    }
}
