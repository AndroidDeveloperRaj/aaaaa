package com.merrichat.net.activity.my.mywallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by amssy on 17/7/1.
 * 现金余额
 * <p>
 * 注： 可用余额 = 我的余额-应交税金
 * 应交税金接口：order/getTaxAmountByMonth
 */

public class WalletBalanceActivity extends BaseActivity {


    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;

    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;


    /**
     * 我的余额
     */
    @BindView(R.id.tv_my_balance)
    TextView tvMyBalance;

    /**
     * 可用余额
     */
    @BindView(R.id.tv_available_balance)
    TextView tvAvailableBalance;

    /**
     * 提现按钮
     */
    @BindView(R.id.bt_withdraw)
    Button btWithdraw;


    /**
     * 账户余额/我的余额
     */
    private double cashBalance = 0.00;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_balance);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTitleBar();
        initView();

    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.FINISH_WALLET_BALANCE) {//关闭页面
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_MY_WALLET = true;
            EventBus.getDefault().post(eventBusModel);
            finish();
        }
    }

    private void initTitleBar() {
        tvTitleText.setText("现金余额");
    }

    private void initView() {
        getBalance();
    }


    /**
     * 查询余额、红包和金币
     */
    private void getBalance() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("maxMoney", "")
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.optBoolean("success")) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    cashBalance = data.optDouble("cashBalance");//账户余额

                                    tvMyBalance.setText("￥"+cashBalance);
                                    tvAvailableBalance.setText("￥"+cashBalance);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.bt_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_withdraw:
                RxActivityTool.skipActivity(WalletBalanceActivity.this, RechargeActivity.class);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
