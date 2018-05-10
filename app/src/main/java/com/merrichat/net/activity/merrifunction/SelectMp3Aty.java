package com.merrichat.net.activity.merrifunction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/15.
 */

public class SelectMp3Aty extends BaseActivity {
    final static String TAG = "SelectMp3Aty";
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;


    String path = "/storage/emulated/0/MerriChat/photo/MerriChat_Photo_20171115164958.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mp3);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_left, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:

                break;
            case R.id.tv_right:

                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
