package com.merrichat.net.permission;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by hezhiqiang on 10/08/2017.
 */

public class CustomPermissonCallBack {
    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions){

    }

    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

    }

    public void showRequestPermissionRationale(int requestCode, final Rationale rationale){

    }
}
