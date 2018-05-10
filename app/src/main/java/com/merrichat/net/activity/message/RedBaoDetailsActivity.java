package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_CHAI_RED_PACKAGE_CODE;

/**
 * Created by amssy on 17/11/15.
 * 红包详情
 */

public class RedBaoDetailsActivity extends BaseActivity {

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

    String amount;//红包金额
    String status;//红包状态   110:待领取 220:已领取 230:红包失效,500 待转账 510 转账待接收 520 转账成功 530  转账失败 540 转账未接收退款成功
    String redMsgstatus;//红包状态  0待领取,1已领取,2已超时
    String redContent;//红包寄语
    int index = -1;//红包在消息列表的position索引
    String tid;//红包ID
    String senderName;//发红包人
    boolean isChai;//是否是拆红包的人
    boolean isControl;//是否做界面更新操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.red_bao_color), 0);
        setContentView(R.layout.activity_red_bao_details);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        amount = getIntent().getStringExtra("amount");
        status = getIntent().getStringExtra("status");
        redContent = getIntent().getStringExtra("redContent");
        tid = getIntent().getStringExtra("tid");
        senderName = getIntent().getStringExtra("senderName");
        isChai = getIntent().getBooleanExtra("isChai", false);
        isControl = getIntent().getBooleanExtra("isControl", false);
        index = getIntent().getIntExtra("index", -1);

        Glide.with(this).load(UserModel.getUserModel().getImgUrl()).dontAnimate().placeholder(R.mipmap.ic_preloading).into(cvFriendsImg);
        tvFriendName.setText(senderName + "的红包");
        tvFriendMoney.setText(amount + "元");
        tvFiendContent.setText(redContent);

        //转账状态和红包状态一致
        //500:待转账 510:转账待接收 520:转账成功 530:转账失败 540:转账未接收退款成功
        if (status.equals("510")) {
            tvFriendMoney.setVisibility(View.VISIBLE);
            tvMoneyStaus.setText("等待对方领取");
            redMsgstatus = "0";
        } else if (status.equals("520")) {
            tvFriendMoney.setVisibility(View.VISIBLE);
            redMsgstatus = "1";
            if (isChai) {
                tvMoneyStaus.setText("已存入我的钱包");
            } else {
                tvMoneyStaus.setText("已存入对方钱包");
            }
        } else if (status.equals("540")) {
            tvMoneyStaus.setText("红包超时已退回");
            tvFriendMoney.setText("");
            redMsgstatus = "2";
            tvFriendMoney.setVisibility(View.GONE);
        }
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
        intent.putExtra("status", redMsgstatus);
        intent.putExtra("tid", tid);
        intent.putExtra("index", index);
        intent.putExtra("isChai", isChai);
        intent.putExtra("isControl", isControl);
        setResult(REQUEST_CHAI_RED_PACKAGE_CODE, intent);
        super.onBackPressed();
    }
}
