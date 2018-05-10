package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.groupmanage.GroupOrderManagementActivity;
import com.merrichat.net.app.MyEventBusModel;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 购买完成界面
 * Created by amssy on 18/1/22.
 */

public class OrderBuyFinishActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_name_phone)
    TextView tvNamePhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_state)
    TextView tvState;
    private double tradeTotalAmount;
    private String address;
    private String phone;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_buy_finish);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("购买成功");
        Intent intent = getIntent();
        if (intent != null) {
            tradeTotalAmount = intent.getDoubleExtra("tradeTotalAmount", 0);
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
            address = intent.getStringExtra("address");
            int flag = intent.getIntExtra("flag", 0);
            if (flag == 1) {
                tvState.setText("付款成功等待卖家发货");
            } else {
                tvState.setText("付款成功等待拼团成功");
            }
            tvNamePhone.setText(name + "  " + phone);
            tvAddress.setText(address);
            tvPrice.setText(tradeTotalAmount + "元");
        }
    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //发送广播关闭之前的界面
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.CLOSE_ORDER = true;
                EventBus.getDefault().post(myEventBusModel);

                Intent intent = new Intent(OrderBuyFinishActivity.this, GroupOrderManagementActivity.class);
                startActivity(intent);

                finish();
                break;
        }
    }

    /**
     * 返回键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //发送广播关闭之前的界面
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            myEventBusModel.CLOSE_ORDER = true;
            EventBus.getDefault().post(myEventBusModel);

            Intent intent = new Intent(OrderBuyFinishActivity.this, GroupOrderManagementActivity.class);
            startActivity(intent);

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
