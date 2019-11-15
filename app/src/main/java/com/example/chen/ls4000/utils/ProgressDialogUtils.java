package com.example.chen.ls4000.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2019-1-23.
 */

public class ProgressDialogUtils {
    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context, CharSequence message){
        if(mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(context, "", message);
            mProgressDialog.setCancelable(false);
        }else{
            mProgressDialog.show();
        }
    }


    public static void dismissProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
