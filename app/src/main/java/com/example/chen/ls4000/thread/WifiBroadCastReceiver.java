package com.example.chen.ls4000.thread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.chen.ls4000.utils.SharedHelper;

/**
 * Created by Double on 2018/8/1.
 */

public class WifiBroadCastReceiver extends BroadcastReceiver {
    SharedHelper sh;
    @Override
    public void onReceive(Context context, Intent intent) {
        sh = new SharedHelper(context);
    }
}
