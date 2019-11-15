package com.example.chen.ls4000.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.chen.ls4000.activity.FirstActivity;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
            System.out.println("数据捕捉到了开机启动！");
            try {
                Thread.sleep(1);
                Intent intent2 = new Intent(context, FirstActivity.class);
                //          intent2.setAction("android.intent.action.MAIN");
                //          intent2.addCategory("android.intent.category.LAUNCHER");
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
