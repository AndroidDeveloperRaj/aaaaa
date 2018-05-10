package com.merrichat.net.activity.my.mywallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现详情
 * Created by amssy on 18/1/24.
 */

public class TiXianDetailActivity extends MerriActionBarActivity {
    /**
     * 提现方式
     */
    @BindView(R.id.tv_tixian_type)
    TextView tvTixianType;
    /**
     * 提现金额
     */
    @BindView(R.id.tv_tixian_price)
    TextView tvTixianPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tixian_detail);
        ButterKnife.bind(this);
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        setLeftBack();
        setTitle("提现详情");
        Intent intent = getIntent();
        if (intent != null) {
            int withdrawalMethodFlag = intent.getIntExtra("withdrawalMethodFlag",0);
            double price = intent.getDoubleExtra("price",0);

            if (withdrawalMethodFlag == 1){
                tvTixianType.setText("微信");
            }else {
                tvTixianType.setText("支付宝");
            }
            tvTixianPrice.setText(price + "元");
        }
    }
}
