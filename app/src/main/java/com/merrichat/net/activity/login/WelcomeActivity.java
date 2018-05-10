package com.merrichat.net.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AMSSY1 on 2018/1/13.
 */

public class WelcomeActivity extends AppCompatActivity {
    // 延迟3秒
    private final long SPLASH_DELAY_MILLIS = 3000;
    private final int GO_LOGIN = 0x001;
    private final int GO_GUIDE = 0x002;
    private final int GO_CANCLE = 0x003;
    @BindView(R.id.iv_welcome)
    ImageView ivWelcome;
    private AlphaAnimation aa;
    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_LOGIN:
                    RxActivityTool.skipActivity(WelcomeActivity.this, MainActivity.class);
                    finish();
                    break;
                case GO_GUIDE:
                    RxActivityTool.skipActivity(WelcomeActivity.this, MainActivity.class);
                    finish();
                    break;
                case GO_CANCLE:
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        aa = new AlphaAnimation(0.5f, 1);
        aa.setDuration(3000);
        ivWelcome.startAnimation(aa);
        boolean networkAvailable = NetUtils.isNetworkAvailable(this);
        if (!networkAvailable) {
            RxToast.showToast("无网络连接，请检查网络是否可用！");
        }
        boolean isLogin = UserModel.getUserModel().getIsLogin();
        if (isLogin) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, SPLASH_DELAY_MILLIS);

        }

//        LocationUtil.initial(cnt);
    }


    @Override
    public void onBackPressed() {
        WelcomeActivity.this.finish();
        mHandler.sendEmptyMessage(GO_CANCLE);

    }
}
