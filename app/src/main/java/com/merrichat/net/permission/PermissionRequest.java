package com.merrichat.net.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by hezhiqiang on 10/08/2017.
 */

public class PermissionRequest {

    private Context context;
    private CustomPermissonCallBack customPermissonCallBack;

    public PermissionRequest(Context context, CustomPermissonCallBack customPermissonCallBack) {
        this.context = context;
        this.customPermissonCallBack = customPermissonCallBack;
    }

    public void request(int requestCode,String[]... permissons){
        // 申请权限。
        AndPermission.with(context)
                .requestCode(requestCode)
                .permission(permissons)
                .callback(permissionListener)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(rationaleListener)
                .start();
    }
    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            if (customPermissonCallBack != null) {
                customPermissonCallBack.onSucceed(requestCode, grantPermissions);
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

            if (customPermissonCallBack != null) {
                customPermissonCallBack.onFailed(requestCode, deniedPermissions);
            }
        }
    };


    /**
     * Rationale支持，这里自定义对话框。
     */
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            // AndPermission.rationaleDialog(Context, Rationale).show();

            if (customPermissonCallBack != null) {
                customPermissonCallBack.showRequestPermissionRationale(requestCode, rationale);
            }
        }

    };
}
