package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by amssy on 2018/2/5.
 * 转账详情
 */

public class ZhuanZhangDetialAty extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_btn)
    TextView tvBtn;
    @BindView(R.id.tv_lq_status)
    TextView tvLQStatus;
    @BindView(R.id.tv_zz_time)
    TextView tvZzTime;
    @BindView(R.id.tv_th_time)
    TextView tvThTime;

    private String tid;
    private String collectMemberId;//入账人id
    private String hairMemberId;//转账人id
    private String collectNickName;//入账人name
    private String hairNickName;//转账人name
    private String senderId;//发消息的人
    private int index = -1;//转账在消息列表的position索引
    private String redStatus = "0";//0转账待接收，1转账成功，2转账超时
    private long msgId;
    private boolean isLeft;
    private boolean isAddMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuan_zhang_detial);
        ButterKnife.bind(this);
        initView();
        redEnState();
    }

    private void initView() {
        tid = getIntent().getStringExtra("tid");
        msgId = getIntent().getLongExtra("id", 0);
        index = getIntent().getIntExtra("index", -1);
        senderId = getIntent().getStringExtra("senderId");
        isLeft = getIntent().getBooleanExtra("isLeft", false);
        tvTitleText.setText("转账详情");

    }

    @OnClick({R.id.iv_back, R.id.tv_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_btn:
                enterAccount();
                break;
        }
    }


    //转账详情
    private void redEnState() {
        OkGo.<String>post(Urls.redEnState)
                .params("tid", tid)
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String amount = data.optString("amount");
                                String status = data.optString("status");
                                String hairTime = data.optString("createTime");
                                String collecTime = data.optString("modifyTime");

                                collectMemberId = jsonObject.optString("collectMemberId");
                                collectNickName = jsonObject.optString("collectNickName");
                                hairMemberId = jsonObject.optString("hairMemberId");
                                hairNickName = jsonObject.optString("hairNickName");
                                tvMoney.setText("￥" + amount);
                                if (status.equals("510")) {//500:待转账 510:转账待接收 520:转账成功 530:转账失败 540:转账未接收退款成功
                                    tvTip.setText("待确认收讯美币");
                                    if (hairMemberId.equals(UserModel.getUserModel().getMemberId())) {
                                        tvBtn.setVisibility(View.GONE);
                                        tvLQStatus.setText("24小时对方不领取将自动退回");
                                    } else {
                                        tvLQStatus.setText("24小时不领取将自动退回");
                                        tvBtn.setVisibility(View.VISIBLE);
                                    }
                                    tvZzTime.setVisibility(View.VISIBLE);
                                    tvThTime.setVisibility(View.GONE);
                                    tvZzTime.setText("转账时间：" + hairTime);
                                    redStatus = "0";
                                } else if (status.equals("520")) {
                                    tvTip.setText(collectNickName + "已收讯美币");
                                    tvLQStatus.setText("已存入" + collectNickName + "钱包中");
                                    tvBtn.setVisibility(View.GONE);
                                    tvZzTime.setVisibility(View.VISIBLE);
                                    tvThTime.setVisibility(View.VISIBLE);
                                    tvZzTime.setText("转账时间：" + hairTime);
                                    tvThTime.setText("收钱时间：" + collecTime);
                                    redStatus = "1";

                                } else if (status.equals("540")) {
                                    tvTip.setText("系统已退还");
                                    tvLQStatus.setText("已超过24小时，自动退回");
                                    tvZzTime.setVisibility(View.VISIBLE);
                                    tvThTime.setVisibility(View.VISIBLE);
                                    tvZzTime.setText("转账时间：" + hairTime);
                                    tvThTime.setText("退还时间：" + collecTime);
                                    redStatus = "2";
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(cnt, "请求失败，请稍后重试");
                    }
                });
    }

    //单聊入账
    private void enterAccount() {
        OkGo.<String>post(Urls.enterAccount)
                .params("tid", tid)
                .params("hairMemberId", hairMemberId)
                .params("collectMemberId", collectMemberId)
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                String collecTime = jsonObject.optString("collecTime");
                                tvTip.setText(collectNickName + "已收讯美币");
                                tvLQStatus.setText("已存入" + collectNickName + "钱包中");
                                tvBtn.setVisibility(View.GONE);
                                tvZzTime.setVisibility(View.VISIBLE);
                                tvThTime.setVisibility(View.VISIBLE);
                                tvThTime.setText("收钱时间：" + collecTime);
                                redStatus = "1";
                                isAddMsg = true;
                            } else {
                                RxToast.showToast(jsonObject.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(cnt, "请求失败，请稍后重试");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        String money = getIntent().getStringExtra("money");
        Intent intent = new Intent();
        if (redStatus.equals("1")) {
            if (senderId.equals(collectMemberId)) {
            } else {
                redStatus = "10";
            }
        }
        intent.putExtra("status", redStatus);
        intent.putExtra("tid", tid);
        intent.putExtra("id", msgId);
        intent.putExtra("index", index);
        intent.putExtra("money", money);
        intent.putExtra("isLeft", isLeft);
        intent.putExtra("isAddMsg", isAddMsg);
        intent.putExtra("hairMemberId", hairMemberId);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
