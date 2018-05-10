package com.merrichat.net.fragment.circlefriends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.merrichat.net.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/22.
 */

public class SelectSexDialogAty extends Activity {
    @BindView(R.id.view_top_touch)
    View viewTopTouch;
    @BindView(R.id.lay_sex_all)
    LinearLayout laySexAll;
    @BindView(R.id.lay_sex_male)
    LinearLayout laySexMale;
    @BindView(R.id.lay_sex_female)
    LinearLayout laySexFemale;
    @BindView(R.id.iv_close)
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_select_sex_dialog);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.lay_sex_all, R.id.lay_sex_male, R.id.lay_sex_female, R.id.iv_close,R.id.view_top_touch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lay_sex_all:
                break;
            case R.id.lay_sex_male:
                break;
            case R.id.lay_sex_female:
                break;
            case R.id.iv_close:
                finish();
                break;
            case R.id.view_top_touch:
                finish();
                break;
        }
    }


}
