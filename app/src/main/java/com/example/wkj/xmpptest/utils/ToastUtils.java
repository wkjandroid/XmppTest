package com.example.wkj.xmpptest.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wkj on 2017/3/20.
 */

public class ToastUtils {
    public static void showToast(final Context context,final String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
