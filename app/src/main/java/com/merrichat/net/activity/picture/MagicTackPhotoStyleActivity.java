package com.merrichat.net.activity.picture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.merrichat.net.R;
import com.merrichat.net.activity.picture.fragments.MagicTackPhotoStyleFrgment;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MyEventBusModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by wangweiwei on 2018/3/31.
 */

public class MagicTackPhotoStyleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_tack_photo_style);

        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);

        MagicTackPhotoStyleFrgment magicTackPhotoStyleFrgment = new MagicTackPhotoStyleFrgment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, magicTackPhotoStyleFrgment)
                .commitAllowingStateLoss();
    }

    //EventBus  回调返回
    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.MAGIC_TACK_STYLE) {
            String callInfo = myEventBusModel.MAGIC_TACK_STYLE_INFO;
            Log.e("--->> onEvent", callInfo);
        }
    }
}