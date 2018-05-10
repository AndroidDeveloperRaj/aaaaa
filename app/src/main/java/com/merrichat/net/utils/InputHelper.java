package com.merrichat.net.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by amssy on 17/11/29.
 */

public class InputHelper {
    private static InputHelper instance;
    private Context mContext;

    public InputHelper(Context mContext) {
        this.mContext = mContext;
    }

    public static InputHelper getInstance(Context mContext) {
        if (instance == null) {
            synchronized (InputHelper.class) {
                if (instance == null) {
                    instance = new InputHelper(mContext);
                }
            }
        }
        return instance;
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public void showKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()) {
            manager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }
}
