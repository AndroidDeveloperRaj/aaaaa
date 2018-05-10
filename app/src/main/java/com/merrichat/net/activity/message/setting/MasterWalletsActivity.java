package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/18.
 * 群主钱包
 */

public class MasterWalletsActivity extends MerriActionBarActivity {

    public static final int activity = MiscUtil.getActivityId();

    /**
     * 现金
     */
    @BindView(R.id.tv_cash)
    TextView tvCash;

    /**
     * 讯美币
     */
    @BindView(R.id.tv_gold)
    TextView tvGold;

    /**
     * 现金/讯美币   文字flag
     */
    @BindView(R.id.tv_balance)
    TextView tvBalance;


    /**
     * 现金/讯美币余额
     */
    @BindView(R.id.tv_money)
    TextView tvMoney;

    /**
     * 提现按钮
     */
    @BindView(R.id.ll_withdraw)
    LinearLayout llWithdraw;

    @BindView(R.id.tv_withdraw)
    TextView tvWithdraw;

    /**
     * 充值
     */
    @BindView(R.id.ll_recharge)
    LinearLayout llRecharge;

    /**
     * 查看交易记录
     */
    @BindView(R.id.tv_history_recording)
    TextView tvHistoryRecording;


    /**
     * 选中标签flag
     * 0现金
     * 1讯美币
     * 默认为0
     */
    private int labelFlag = 0;


    /**
     * 群id
     */
    private String groupId = "";
    /**
     * 群钱包id
     */
    private String communityAccountId;


    /**
     * 账户余额
     */
    private String cashBalance;

    /**
     * 讯美币余额
     */
    private String giftBalance;


    /**
     * 是否是管理员 0：成员 1：管理员 2：群主
     */
    private int isMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_wallets);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setLeftBack();
        initTitle();
        initView();
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_WALLET) {//刷新数据
            queryWalletInfo();
        }
    }


    private void initTitle() {
        setTitle("群主钱包");
        setLeftBack();
    }

    private void initView() {
        isMaster = getIntent().getIntExtra("isMaster", 0);
        if (isMaster == 2) {
            setRightText("设置", R.color.base_FF3D6F, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(cnt, SetMasterWalletsActivity.class).putExtra("groupId", groupId));
                }
            });
        }
        communityAccountId = getIntent().getStringExtra("communityAccountId");
        groupId = getIntent().getStringExtra("groupId");
        if (isMaster == 2) {
            llWithdraw.setVisibility(View.VISIBLE);
        }
        queryWalletInfo();
    }


    /**
     * 查询余额、红包和金币
     */
    private void queryWalletInfo() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", communityAccountId)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                cashBalance = data.optString("cashBalance");//账户余额
                                giftBalance = data.optString("giftBalance");//讯美币余额
                                if (labelFlag == 0) {
                                    tvMoney.setText("￥" + cashBalance);
                                } else {
                                    tvMoney.setText(giftBalance + "讯美币");
                                }

                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @OnClick({R.id.tv_cash, R.id.tv_gold, R.id.ll_withdraw, R.id.ll_recharge, R.id.tv_history_recording})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cash://现金
                if (labelFlag == 0) {
                    return;
                }
                labelFlag = 0;
                tvCash.setBackgroundResource(R.drawable.shape_wallet_left_checked);
                tvCash.setTextColor(cnt.getResources().getColor(R.color.white));
                tvGold.setBackgroundResource(R.drawable.shape_wallet_right);
                tvGold.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));
                tvBalance.setText("群主钱包余额");
                tvMoney.setText("￥" + cashBalance);

                if (isMaster == 2) {
                    tvWithdraw.setText("提现到群主个人现金账户");
                    llRecharge.setVisibility(View.GONE);
                }

                queryWalletInfo();
                break;
            case R.id.tv_gold://讯美币
                if (labelFlag == 1) {
                    return;
                }
                labelFlag = 1;
                tvCash.setBackgroundResource(R.drawable.shape_wallet_left);
                tvCash.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));
                tvGold.setBackgroundResource(R.drawable.shape_wallet_right_checked);
                tvGold.setTextColor(cnt.getResources().getColor(R.color.white));

                tvBalance.setText("群主讯美币钱包余额");
                tvMoney.setText(giftBalance + "讯美币");

                if (isMaster == 2) {
                    tvWithdraw.setText("提现到群主个人讯美币账户");
                    llRecharge.setVisibility(View.VISIBLE);
                }

                queryWalletInfo();
                break;
            case R.id.ll_withdraw://提现
                startActivity(new Intent(cnt, GroupWalletRechargeActivity.class).putExtra("labelFlag", labelFlag).putExtra("communityAccountId", communityAccountId));
                break;
            case R.id.tv_history_recording://查看群交易记录
                startActivity(new Intent(cnt, GroupTransactionRecordsActivity.class).putExtra("cashBalance", cashBalance).putExtra("giftBalance", giftBalance).putExtra("communityAccountId", communityAccountId));
                break;
            case R.id.ll_recharge://讯美币充值
                startActivity(new Intent(cnt, GroupWalletRechargeActivity.class).putExtra("labelFlag", 2).putExtra("communityAccountId", communityAccountId));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
