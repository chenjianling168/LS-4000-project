package com.example.chen.ls4000.utils;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2018-11-26.
 */

public class ToastUtil {
    public  static void showToast(final Activity activity, final String word, final long time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(activity,word,Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                },time);
            }
        });
    }


}
