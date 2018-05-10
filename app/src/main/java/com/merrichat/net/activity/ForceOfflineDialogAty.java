package com.merrichat.net.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.merrichat.net.MainActivity;
import com.merrichat.net.app.AppManager;

import butterknife.ButterKnife;

/**
 * Created by xly on 2018/4/29.
 */
public class ForceOfflineDialogAty extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_force_offline_dialog);
        ButterKnife.bind(this);
    }


    private void showOffLine() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.title("提示").content("您好，您的账号已在其它设备上登录，请注意账户安全!")//
                .btnNum(1)
                .btnText("知道了")
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        if (AppManager.getAppManager().hasActivity(MainActivity.class)) {
                            AppManager.getAppManager().finishActivity(MainActivity.class);
                        }
                        dialog.dismiss();
                        finish();
                        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                    }
                }
        );
    }
}
