package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/2/24.
 * 群钱包充值或者提现
 */

public class GroupWalletRechargeActivity extends MerriActionBarActivity {

    /**
     * 方式
     * 提现到我的余额/提现到讯美币余额/充值讯美币
     */
    @BindView(R.id.tv_the_way)
    TextView tvTheWay;

    /**
     * 提现金额（元）/提现讯美币数量/充值讯美币数
     */
    @BindView(R.id.tv_money_num)
    TextView tvMoneyNum;

    @BindView(R.id.edit_text_money)
    EditText editTextMoney;

    @BindView(R.id.bt_withdraw)
    Button btWithdraw;
    /**
     * 0 现金提现
     * 1讯美币提现
     * 2讯美币充值
     */
    private int labelFlag;

    /**
     * 群钱包id
     */
    private String communityAccountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_wallet_recharge);
        ButterKnife.bind(this);
        initView();
        setLeftBack();
    }

    private void initView() {
        labelFlag = getIntent().getIntExtra("labelFlag", 0);
        communityAccountId = getIntent().getStringExtra("communityAccountId");
        switch (labelFlag) {
            case 0:
                setTitle("提现");
                tvTheWay.setText("提现到我的余额");
                tvMoneyNum.setText("提现金额（元）");
                btWithdraw.setText("提现");
                break;
            case 1:
                setTitle("提现");
                tvTheWay.setText("提现到讯美币余额");
                tvMoneyNum.setText("提现讯美币数量");
                btWithdraw.setText("提现");
                break;
            case 2:
                setTitle("充值");
                tvTheWay.setText("充值讯美币");
                tvMoneyNum.setText("充值讯美币数");
                btWithdraw.setText("充值");
                break;
        }
    }

    @OnClick({R.id.bt_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_withdraw:
                if (TextUtils.isEmpty(editTextMoney.getText().toString())) {
                    if (labelFlag == 0) {
                        GetToast.useString(cnt, "请输入提现金额");
                    } else {
                        GetToast.useString(cnt, "请输入讯美币数量");
                    }
                    return;
                }
                if (labelFlag == 0) {
                    withdrawGroupWallent(UserModel.getUserModel().getAccountId(), communityAccountId, "0", "群钱包提现");
                } else if (labelFlag == 1) {
                    withdrawGroupWallent(UserModel.getUserModel().getAccountId(), communityAccountId, "1", "群讯美币提现");
                } else {
                    withdrawGroupWallent(communityAccountId, UserModel.getUserModel().getAccountId(), "1", "群讯美币充值");
                }
                break;
        }
    }

    /**
     * 群钱包提现到个人钱包
     * 群讯美币提现
     * 群讯美币充值
     * transferType  金额类型 0：现金 1：讯美币
     */
    private void withdrawGroupWallent(String inAccountId, String outAccountId, String transferType, String title) {
        OkGo.<String>post(Urls.withdrawGroupWallent)
                .params("inAccountId", inAccountId)
                .params("outAccountId", outAccountId)
                .params("amount", editTextMoney.getText().toString())
                .params("transferType", transferType)
                .params("title", title)
                .params("remark", title)
                .params("platformId", "100005")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (labelFlag == 0 || labelFlag == 1) {
                                    GetToast.useString(cnt, "提现成功");
                                } else {
                                    GetToast.useString(cnt, "充值成功");
                                }
                                MyEventBusModel eventBusModel = new MyEventBusModel();
                                eventBusModel.REFRESH_GROUP_WALLET = true;
                                EventBus.getDefault().post(eventBusModel);
                                finish();
                            } else {
                                String error_msg = jsonObject.optString("error_msg");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(cnt, error_msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
