package com.merrichat.net.activity.groupmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.adapter.PintuanHeaderAdapter;
import com.merrichat.net.adapter.PintuanModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MarketShopModel;
import com.merrichat.net.model.PintuanDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.TimeTools;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 拼团详情
 * Created by amssy on 18/1/22.
 */
public class PintuanDetailActivity extends BaseActivity implements PintuanHeaderAdapter.onPintuanItemClickLinster {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    /**
     * 拼团价
     */
    @BindView(R.id.tv_market_price_tuan)
    TextView tvMarketPriceTuan;
    /**
     * 拼团人列表
     */
    @BindView(R.id.recycler_view_header)
    RecyclerView recyclerViewHeader;
    /**
     * 商品图片
     */
    @BindView(R.id.simpleDraweeView)
    SimpleDraweeView simpleDraweeView;
    /**
     * 商品名字
     */
    @BindView(R.id.tv_name)
    TextView tvName;
    /**
     * 商品原价
     */
    @BindView(R.id.tv_market_price)
    TextView tvMarketPrice;
    /**
     * 拼团剩余时间
     */
    @BindView(R.id.tv_pin_tuan_time)
    TextView tvPinTuanTime;
    /**
     * 参与拼团/还差1人拼团 按钮
     */
    @BindView(R.id.btn_pin_tuan)
    Button btnPinTuan;
    /**
     * 拼团时间右侧的"后结束"
     */
    @BindView(R.id.tv_pin_tuan_time_right)
    TextView tvPinTuanTimeRight;

    private PintuanHeaderAdapter adapter;
    private MarketShopModel marketShopModel;
    private PintuanModel pintuanModel;
    private long dataTime;
    private List<PintuanDetailModel.DataBean.SerialMemberBean> listUrl;
    private CountDownTimer countDownTimer;
    private int position;
    private long serialNumber;//团号
    private int type = 0;//0拼团中，1拼团成功，2失败
    private boolean isPintuan = true;//是否可拼团
    private PintuanDetailModel pintuanDetailModel;
    private long currentTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintuan_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("拼团");
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.fenxiang_2x);

        Intent intent = getIntent();
        if (intent != null) {
            pintuanModel = (PintuanModel) intent.getSerializableExtra("PintuanModel");
            position = intent.getIntExtra("position", 0);
            marketShopModel = (MarketShopModel) intent.getSerializableExtra("marketShopModel");
            //绑定商品信息
            simpleDraweeView.setImageURI(marketShopModel.getData().getImg());
            tvName.setText(marketShopModel.getData().getProductName());
            tvMarketPriceTuan.setText(StringUtil.getPrice(String.valueOf(marketShopModel.getData().getGroupPrice())));
            tvMarketPrice.setText("" + marketShopModel.getData().getProductPrice());

            serialNumber = pintuanModel.getData().getList().get(position).getSerialNumber();
        }
    }

    private void initRecyclerView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PintuanDetailActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewHeader.setLayoutManager(linearLayoutManager);
        adapter = new PintuanHeaderAdapter(PintuanDetailActivity.this, listUrl);
        recyclerViewHeader.setAdapter(adapter);
        adapter.setPintuanItemClickLinster(this);
        adapter.setListSize(pintuanDetailModel.getData().getSum());
    }

    @OnClick({R.id.iv_back, R.id.tv_right_img, R.id.btn_pin_tuan})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_img://分享按钮
                Intent intent = new Intent(PintuanDetailActivity.this,PinTuanShareActivity.class);
                intent.putExtra("currentTimeMillis",currentTimeMillis);
                intent.putExtra("orderId",pintuanModel.getData().getList().get(position).getOrderId());
                intent.putExtra("sum",(pintuanDetailModel.getData().getSum() - pintuanDetailModel.getData().getSize()));
                intent.putExtra("shareImage",marketShopModel.getData().getImg());
                startActivity(intent);
                break;
            case R.id.btn_pin_tuan://去拼团按钮
                if (isPintuan) {
                    Intent intent1 = new Intent(PintuanDetailActivity.this, MarketDialogActivity.class);
                    intent1.putExtra("type", "market_can_tuan");
                    intent1.putExtra("marketShopModel", marketShopModel);
                    intent1.putExtra("serialNumber", pintuanModel.getData().getList().get(position).getSerialNumber());
                    intent1.putExtra("seriaCreateMemberId", pintuanModel.getData().getList().get(position).getSeriaCreateMemberId());
                    startActivity(intent1);
                } else {
                    RxToast.showToast(btnPinTuan.getText().toString());
                }
                break;
        }
    }

    /**
     * 头像的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        offeredList();
    }

    private void offeredList() {
        OkGo.<String>get(Urls.offeredList)
                .tag(this)
                .params("serialNumber", serialNumber)
                .params("type", type)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (data.optJSONObject("data").optBoolean("suc")) {
                                        pintuanDetailModel = JSON.parseObject(response.body(), PintuanDetailModel.class);
                                        //绑定拼团人信息
                                        listUrl = new ArrayList<>();
                                        listUrl.addAll(pintuanDetailModel.getData().getSerialMember());
                                        initRecyclerView();

                                        //拼团人数已满
                                        if (pintuanDetailModel.getData().getSum() == listUrl.size()) {
                                            btnPinTuan.setText("拼团人数已满");
                                            tvPinTuanTime.setText("参与拼团人数已满");
                                            tvPinTuanTimeRight.setVisibility(View.GONE);
                                            isPintuan = false;
                                        } else {
                                            if (pintuanDetailModel.getData().getFlag() == 0) {//未参团
                                                btnPinTuan.setText("参与拼团省" + StringUtil.moneySubtract(String.valueOf(marketShopModel.getData().getProductPrice()), String.valueOf(marketShopModel.getData().getGroupPrice())) + "元");
                                            } else {//已参团
                                                btnPinTuan.setText("还差" + (pintuanDetailModel.getData().getSum() - pintuanDetailModel.getData().getSize()) + "人拼团成功");
                                                isPintuan = false;
                                            }

                                            //创建时间
                                            long paymentTime = pintuanDetailModel.getData().getSerialTime();
                                            //当前时间
                                            dataTime = pintuanDetailModel.getData().getTime();

                                            final long time = 24 * 3600 * 1000 - (dataTime - paymentTime);//倒计时时间

                                            //时间倒计时
                                            if (time > 0) {
                                                countDownTimer = new CountDownTimer(time, 1000) {

                                                    public void onTick(long millisUntilFinished) {

                                                        currentTimeMillis = millisUntilFinished;
                                                        String countTimeByLong = TimeTools.getCountTimeByLong(millisUntilFinished);
                                                        tvPinTuanTime.setText(countTimeByLong);
                                                    }

                                                    public void onFinish() {
                                                        tvPinTuanTime.setText("拼团已结束");
                                                        btnPinTuan.setText("拼团已结束");
                                                        btnPinTuan.setVisibility(View.GONE);
                                                        tvPinTuanTimeRight.setVisibility(View.GONE);
                                                        isPintuan = false;
                                                    }
                                                }.start();
                                            } else {
                                                tvPinTuanTime.setText("拼团已结束");
                                                btnPinTuan.setText("拼团已结束");
                                                tvPinTuanTimeRight.setVisibility(View.GONE);
                                                btnPinTuan.setVisibility(View.GONE);
                                                isPintuan = false;
                                            }
                                        }
                                    }else {
                                        RxToast.showToast("拼团已结束");
                                        finish();
                                    }
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }
}
