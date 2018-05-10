package com.merrichat.net.utils;

import android.content.Context;
import android.widget.Toast;

import com.merrichat.net.R;

/**
 * Created by amssy on 17/6/6.
 */

public class GetToast {
    public static void useString(Context context, String string) {
        if (context == null)
            return;
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static void useid(Context context, int id) {
        if (context == null)
            return;
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();

    }

    /**
     * 當請求接口失敗的時候調用
     **/
    public static void useRequestError(Context context) {
        useString(context, context.getResources().getString(R.string.connect_to_server_fail));
    }

    /***实现单例的Toast方法***/
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }


    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }
}
