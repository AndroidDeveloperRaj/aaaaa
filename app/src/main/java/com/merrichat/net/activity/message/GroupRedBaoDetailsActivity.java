package com.merrichat.net.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.GoodsTradingAdapter;
import com.merrichat.net.adapter.RedBaoAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.GroupRedDetialModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_CHAI_RED_PACKAGE_CODE;

/**
 * Created by amssy on 2018/2/26.
 */

public class GroupRedBaoDetailsActivity extends BaseActivity {

    /**
     * 返回按钮
     */
    @BindView(R.id.iv_finish)
    ImageView ivFinish;

    /**
     * 好友头像
     */
    @BindView(R.id.cv_friends_img)
    CircleImageView cvFriendsImg;

    /**
     * 好友名字
     */
    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;

    /**
     * 红包内容
     */
    @BindView(R.id.tv_fiend_content)
    TextView tvFiendContent;

    /**
     * 红包金额
     */
    @BindView(R.id.tv_friend_money)
    TextView tvFriendMoney;

    /**
     * 红包状态
     */
    @BindView(R.id.tv_money_staus)
    TextView tvMoneyStaus;
    /**
     * 红包个数
     */
    @BindView(R.id.tv_red_num)
    TextView tvRedNum;
    /**
     * 领取红包列表
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    String status;//红包状态   110:待领取 220:已领取 230:红包失效,500 待转账 510 转账待接收 520 转账成功 530  转账失败 540 转账未接收退款成功
    String redContent;//红包寄语
    String tid;//红包ID
    String senderName;//发红包人
    String sendRedBaoHeaderImg;//发红包人头像
    String hairMemberId;//发红包人id
    String collectMemberId;//收红包人id
    String groupId;//群id
    String group;//群名称
    boolean isChai;//拆
    int index = -1;//红包在消息列表的position索引
    private RedBaoAdapter redBaoAdapter;
    private List<GroupRedDetialModel.DataBean.RedBean> redBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.red_bao_color), 0);
        setContentView(R.layout.activity_group_red_bao_details);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        hairMemberId = getIntent().getStringExtra("hairMemberId");
        collectMemberId = getIntent().getStringExtra("collectMemberId");
        groupId = getIntent().getStringExtra("groupId");
        group = getIntent().getStringExtra("group");
        redContent = getIntent().getStringExtra("redContent");
        senderName = getIntent().getStringExtra("senderName");
        sendRedBaoHeaderImg = getIntent().getStringExtra("sendRedBaoHeaderImg");
        tid = getIntent().getStringExtra("tid");
        isChai = getIntent().getBooleanExtra("isChai", false);
        index = getIntent().getIntExtra("index", -1);
        Glide.with(this).load(sendRedBaoHeaderImg).dontAnimate().error(R.mipmap.ic_preloading).placeholder(R.mipmap.ic_preloading).into(cvFriendsImg);
        collarGroupRedPge();
        redBeanList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        redBaoAdapter = new RedBaoAdapter(R.layout.item_group_red_bao, redBeanList);
        recyclerView.setAdapter(redBaoAdapter);
    }


    /**
     * 拆红包
     */
    private void collarGroupRedPge() {
        OkGo.<String>post(Urls.collarGroupRedPge)
                .params("hairMemberId", hairMemberId)
                .params("collectMemberId", collectMemberId)
                .params("groupId", groupId)
                .params("group", group)
                .params("tid", tid)
                .execute(new StringDialogCallback((Activity) cnt) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        //{"success":true,"error_code":"","error_msg":"","page":"0","hasnext":false,"rows":"0","total":"0","data":{"amount":"4.580000","status":"710"}}
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String amount = data.optString("amount");
                                status = data.optString("status");
                                tvFriendName.setText(senderName + "的红包");
                                tvFiendContent.setText(redContent);
                                if (status.equals("610")) {//610 扣款成功
                                    tvFriendMoney.setVisibility(View.GONE);
                                    tvMoneyStaus.setVisibility(View.VISIBLE);
                                    tvMoneyStaus.setText("红包已被抢光");
                                } else if (status.equals("640") || status.equals("650")) {//640 扣款全额退回成功 650 扣款部分退回成功
                                    tvFriendMoney.setVisibility(View.GONE);
                                    tvMoneyStaus.setVisibility(View.VISIBLE);
                                    tvMoneyStaus.setText("红包已超时");
                                } else if (status.equals("630") || status.equals("701")) {//630 部分扣款成功 701：已领取红包
                                    tvFriendMoney.setVisibility(View.VISIBLE);
                                    tvMoneyStaus.setVisibility(View.GONE);
                                    tvFriendMoney.setText(new DecimalFormat("#0.00").format(Double.parseDouble(amount)) + "讯美币");
                                    if (isChai && status.equals("630")) {
                                        status = "701";
                                    }
                                }
                                queryRedPgeRecord();
                            } else {
                                GetToast.showToast(cnt, jsonObject.optString("error_msg"));
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

    /**
     * 拆红包
     */
    private void queryRedPgeRecord() {
        OkGo.<String>post(Urls.queryRedPgeRecord)
                .params("tid", tid)
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        GroupRedDetialModel groupRedDetialModel = new Gson().fromJson(response.body(), GroupRedDetialModel.class);
                        if (groupRedDetialModel.isSuccess()) {

                            int total = groupRedDetialModel.getData().getTotal();
                            int surTotal = groupRedDetialModel.getData().getSurTotal();//剩余几个
                            int areTotal = total - surTotal;
                            tvRedNum.setVisibility(View.VISIBLE);
                            tvRedNum.setText("已领" + areTotal + "/" + total + "个红包");
                            redBeanList.clear();
                            redBeanList.addAll(groupRedDetialModel.getData().getRed());
                            redBaoAdapter.notifyDataSetChanged();
                        } else {
                            tvRedNum.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(cnt, "请求失败，请稍后重试");
                    }
                });
    }

    @OnClick({R.id.iv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_finish:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("status", status);
        intent.putExtra("tid", tid);
        intent.putExtra("index", index);
        setResult(REQUEST_CHAI_RED_PACKAGE_CODE, intent);

        super.onBackPressed();
    }
}
