package com.merrichat.net.activity.groupmarket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.adapter.DetailImageAdapter;
import com.merrichat.net.adapter.PintuanAdapter;
import com.merrichat.net.adapter.PintuanModel;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MarketShopModel;
import com.merrichat.net.model.QueryGoodFriendRequestModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 商品详情
 * Created by amssy on 18/1/19.
 */

public class MarketShopDetailAty extends BaseActivity implements PintuanAdapter.onPintuanItemClickLinster {
    public final static int activityId = MiscUtil.getActivityId();

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * 拼团价购买
     */
    @BindView(R.id.tv_market_price_tuan)
    TextView tvMarketPriceTuan;
    /**
     * 原价购买
     */
    @BindView(R.id.tv_market_price_buy)
    TextView tvMarketPriceBuy;
    /**
     * 拼团购买
     */
    @BindView(R.id.lin_market_tuan)
    LinearLayout linMarketTuan;
    /**
     * 立即购买
     */
    @BindView(R.id.lin_market_buy)
    LinearLayout linMarketBuy;
    /**
     * 拼团查看更多
     */
    @BindView(R.id.tv_pin_tuan_more)
    TextView tvPinTuanMore;
    /**
     * 拼团列表
     */
    @BindView(R.id.recycler_view_pin_tuan)
    RecyclerView recyclerViewPinTuan;
    /**
     * 客服
     */
    @BindView(R.id.lin_market_ke_fu)
    LinearLayout linMarketKeFu;
    /**
     * 图片列表
     */
    @BindView(R.id.recycler_view_image)
    RecyclerView recyclerViewImage;
    /**
     * 正在拼团人数
     */
    @BindView(R.id.tv_pintuan_num)
    TextView tvPintuanNum;
    /**
     * 商品封面图
     */
    @BindView(R.id.simple_cover)
    SimpleDraweeView simpleCover;
    /**
     * 商品名称
     */
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    /**
     * 商品原价
     */
    @BindView(R.id.tv_market_price_old)
    TextView tvMarketPriceOld;
    /**
     * 团购价
     */
    @BindView(R.id.tv_market_price_new)
    TextView tvMarketPriceNew;
    /**
     * 邮费
     */
    @BindView(R.id.tv_shop_free)
    TextView tvShopFree;
    /**
     * 库存
     */
    @BindView(R.id.tv_shop_ku_cun)
    TextView tvShopKuCun;
    /**
     * 商品规格
     */
    @BindView(R.id.tv_shop_sku)
    TextView tvShopSku;
    /**
     * 商品描述
     */
    @BindView(R.id.tv_shop_content)
    TextView tvShopContent;
    /**
     * 下架按钮
     */
    @BindView(R.id.btn_sold_out)
    Button btnSoldOut;
    /**
     * 已下架按钮
     */
    @BindView(R.id.btn_sold_out_true)
    Button btnSoldOutTrue;
    /**
     * 已卖完按钮
     */
    @BindView(R.id.btn_none)
    Button btnNone;
    /**
     * 底部
     */
    @BindView(R.id.lin_market_bottom)
    LinearLayout linMarketBottom;
    /**
     * 拼团列表父布局
     */
    @BindView(R.id.lin_pin_tuan)
    LinearLayout linPinTuan;
    /**
     * 拼团价
     */
    @BindView(R.id.ll_group_price)
    LinearLayout llGroupPrice;
    //拼团适配器
    private PintuanAdapter adapter;
    //商品图片适配器
    private DetailImageAdapter imageAdapter;
    private String productId;//商品ID
    private String type = "0";//0拼团中，1拼团成功，2失败，(所有传空)
    private String id = "";//交易Id
    private ArrayList<String> listImg;
    private MarketShopModel marketShopModel;
    private boolean isFriend = false;
    private String toMemberId = "";//卖家ID
    private String toHeaderUrl = "";//卖家头像
    private String toNickName = "";//卖家名称
    //拼团数据
    private List<PintuanModel.DataBean.ListBean> listData;
    private PintuanModel pintuanModel;
    private int groupType = -1;
    private int isMaster = 0;//0成员  1管理员  2 群主

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_market);
        ButterKnife.bind(this);
        initView();
        initRecyclerView();
        initRecyclerViewImage();
        //查询商品详情
        getTransInfoById();
    }

    private void initView() {
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            groupType = intent.getIntExtra("groupType", -1);
        }
        tvTitleText.setText("商品详情");
    }

    @OnClick({R.id.iv_back, R.id.lin_market_buy, R.id.lin_market_tuan, R.id.tv_pin_tuan_more, R.id.lin_market_ke_fu, R.id.btn_sold_out, R.id.btn_sold_out_true})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.lin_market_buy://立即购买
                if (RxDataTool.isEmpty(marketShopModel) || marketShopModel == null) {
                    RxToast.showToast("商品信息获取失败");
                    return;
                }
                Intent intent = new Intent(MarketShopDetailAty.this, MarketDialogActivity.class);
                intent.putExtra("type", "market_buy");
                intent.putExtra("marketShopModel", marketShopModel);
                startActivity(intent);
                this.overridePendingTransition(R.anim.push_dialog_bottom_in,0);
                break;
            case R.id.lin_market_tuan://拼团购买
                if (RxDataTool.isEmpty(marketShopModel) || marketShopModel == null) {
                    RxToast.showToast("商品信息获取失败");
                    return;
                }
                Intent intent1 = new Intent(MarketShopDetailAty.this, MarketDialogActivity.class);
                intent1.putExtra("type", "market_tuan");
                intent1.putExtra("marketShopModel", marketShopModel);
                startActivity(intent1);
                this.overridePendingTransition(R.anim.push_dialog_bottom_in,0);
                break;
            case R.id.tv_pin_tuan_more://拼团查看更多
                if (RxDataTool.isEmpty(marketShopModel) || marketShopModel == null) {
                    RxToast.showToast("商品信息获取失败");
                    return;
                }
                Intent intent_pintuan = new Intent(MarketShopDetailAty.this, PintuanActivity.class);
                intent_pintuan.putExtra("productId", String.valueOf(marketShopModel.getData().getId()));
                intent_pintuan.putExtra("marketShopModel", marketShopModel);
                startActivity(intent_pintuan);
                break;
            case R.id.lin_market_ke_fu://联系买家（跳转到单聊界面）
                if (RxDataTool.isEmpty(marketShopModel) || marketShopModel == null) {
                    RxToast.showToast("商家信息获取失败");
                    return;
                }
                toMemberId = String.valueOf(marketShopModel.getData().getSeller());
                toHeaderUrl = marketShopModel.getData().getMemberUrl();
                toNickName = marketShopModel.getData().getMemberNick();

                Intent intent2 = new Intent(cnt, SingleChatActivity.class);
                intent2.putExtra("receiverMemberId", toMemberId);
                intent2.putExtra("receiverHeadUrl", toHeaderUrl);
                intent2.putExtra("isFriend", isFriend);
                intent2.putExtra("activityId", activityId);
                intent2.putExtra("receiverName", toNickName);
                startActivity(intent2);
                break;
            case R.id.btn_sold_out://下架商品
                showExitScreenDialog(this);
                break;
            case R.id.btn_sold_out_true://已下架商品
                RxToast.showToast("商品已下架");
                break;
        }
    }

    private void showExitScreenDialog(Context context) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("是否下架商品?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //下架商品
                                deleteTransInfoById();
                            }
                        });
                    }
                });
    }

    /**
     * 查询是否是好友
     */
    private void queryGoodFriendRequest() {
        ApiManager.getApiManager().getService(WebApiService.class).queryGoodFriendRequest(UserModel.getUserModel().getMemberId(), toMemberId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryGoodFriendRequestModel>() {
                    @Override
                    public void onNext(QueryGoodFriendRequestModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            if (null == queryWalletInfoModel.data) {
                                return;
                            }
                            if (queryWalletInfoModel.data.state == 0) {
                                isFriend = false;
                            } else if (queryWalletInfoModel.data.state == 1) {
                                isFriend = false;
                            } else if (queryWalletInfoModel.data.state == 2) {
                                isFriend = false;
                            } else {
                                isFriend = true;
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });


//        OkGo.<String>post(Urls.queryGoodFriendRequest)
//                .params("memberId", UserModel.getUserModel().getMemberId())
//                .params("toMemberId", toMemberId)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.body());
//                            JSONObject data = jsonObject.optJSONObject("data");
//                            if (jsonObject.optBoolean("success")) {
//                                int state = data.optInt("state");
//                                if (state == 0) {
//                                    isFriend = false;
//                                } else if (state == 1) {
//                                    isFriend = false;
//                                } else if (state == 2) {
//                                    isFriend = false;
//                                } else {
//                                    isFriend = true;
//                                }
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//
//                    }
//                });
    }

    /**
     * 拼团列表
     */
    private void initRecyclerView() {
        listData = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MarketShopDetailAty.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPinTuan.setLayoutManager(linearLayoutManager);
        adapter = new PintuanAdapter(MarketShopDetailAty.this, listData);
        recyclerViewPinTuan.setAdapter(adapter);
        adapter.setPintuanItemClickLinster(this);
    }

    /**
     * 图片列表
     */
    private void initRecyclerViewImage() {
        listImg = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MarketShopDetailAty.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewImage.setLayoutManager(linearLayoutManager);
        imageAdapter = new DetailImageAdapter(R.layout.item_detail_simple, listImg);
        recyclerViewImage.setAdapter(imageAdapter);
        recyclerViewImage.setNestedScrollingEnabled(false);
    }

    /**
     * 去拼团的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MarketShopDetailAty.this, PintuanDetailActivity.class);
        intent.putExtra("marketShopModel", marketShopModel);
        intent.putExtra("PintuanModel", pintuanModel);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        adapter.cancelAllTimers();
    }

    /**
     * 查询商品详情
     */
    private void getTransInfoById() {
        OkGo.<String>get(Urls.getTransInfoById)
                .tag(this)
                .params("id", id)//交易ID
                .execute(new StringDialogCallback(this) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    marketShopModel = JSON.parseObject(response.body(), MarketShopModel.class);
                                    //商品封面
                                    simpleCover.setImageURI(marketShopModel.getData().getImg());
                                    //商品名称
                                    tvShopName.setText(marketShopModel.getData().getProductName());
                                    //商品价格
                                    tvMarketPriceBuy.setText(StringUtil.getPrice(String.valueOf(marketShopModel.getData().getProductPrice())));
                                    tvMarketPriceTuan.setText(StringUtil.getPrice(String.valueOf(marketShopModel.getData().getGroupPrice())));
                                    tvMarketPriceOld.setText(StringUtil.getPrice(String.valueOf(marketShopModel.getData().getProductPrice())));
                                    tvMarketPriceNew.setText(StringUtil.getPrice(String.valueOf(marketShopModel.getData().getGroupPrice())));
                                    //商品运费
                                    tvShopFree.setText("运费:" + StringUtil.rounded(marketShopModel.getData().getFreight()));
                                    //商品库存
                                    tvShopKuCun.setText("库存:" + marketShopModel.getData().getAvailableSupplyOrPurchaseQuantity());
                                    //商品规格
                                    tvShopSku.setText(marketShopModel.getData().getProductModelDesc());
                                    //商品描述
                                    tvShopContent.setText(marketShopModel.getData().getProductDescribe());
                                    //商品图片
                                    listImg.addAll(marketShopModel.getData().getImgs());
                                    imageAdapter.notifyDataSetChanged();

                                    productId = String.valueOf(marketShopModel.getData().getId());
                                    //拼团数据（只显示2条）
                                    getRegimentList();

                                    //如果不是个人单聊  则 查询群类型和浏览者是不是群主
                                    //BTC群  群主可以下架管理员上传的商品  其他情况 只能自己下架自己的商品
                                    linMarketBottom.setVisibility(View.VISIBLE);
                                    if (groupType != 1){
                                        isBannedOrTrade();
                                    }else {
                                        setBottom();
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

    /**
     * 查询拼团列表
     */
    private void getRegimentList() {
        OkGo.<String>get(Urls.getRegimentList)
                .tag(this)
                .params("productId", productId)
                .params("type", type)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    pintuanModel = JSON.parseObject(response.body(), PintuanModel.class);
                                    listData.clear();
                                    //商品详情页显示的拼团数据最多显示2条
                                    if (pintuanModel.getData().getList().size() > 2) {
                                        for (int i = 0; i < 2; i++) {
                                            PintuanModel.DataBean.ListBean listBean = pintuanModel.getData().getList().get(i);
                                            listData.add(listBean);
                                        }
                                    } else {
                                        listData.addAll(pintuanModel.getData().getList());
                                    }

                                    tvPintuanNum.setText(pintuanModel.getData().getSum() + "人正在拼团");

                                    adapter.notifyDataSetChanged();
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
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 商品下架
     */
    private void deleteTransInfoById() {
        OkGo.<String>post(Urls.deleteTransInfoById)
                .params("id", marketShopModel.getData().getId())//交易ID
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                RxToast.showToast("商品下架成功");
                                MyEventBusModel eventBusModel = new MyEventBusModel();
                                eventBusModel.SHOP_SOLD_OUT = true;
                                EventBus.getDefault().post(eventBusModel);
                                finish();
                            } else {
                                RxToast.showToast(jsonObject.optString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    /**
     * 接受广播刷新拼团界面
     *
     * @param myEventBusModel
     */
    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_NO_ORDER) {
            getTransInfoById();
        }else if (myEventBusModel.CLOSE_ORDER) {
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (marketShopModel != null) {
            productId = String.valueOf(marketShopModel.getData().getId());
            //拼团数据（只显示2条）
            getRegimentList();
        }
    }

    /**
     * 查询群类型和浏览者是不是群主
     */
    private void isBannedOrTrade() {
        OkGo.<String>post(Urls.isBannedOrTrade)
                .params("cid", marketShopModel.getData().getGroupId())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("success")) {
                                    int type = data.optInt("type");//0：交流群，1：微商群（BTC） 2：集市群(CTC),groupType = 0;//0普通交流群 1 个人私聊 2 C2C 3 B2C
                                    isMaster = data.optInt("isMaster");//0成员  1管理员  2 群主
                                    if (type == 1 && isMaster == 2){
                                        isGroupMaster();
                                    }else {
                                        setBottom();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 查询商品发布人是不是管理员
     */
    private void isGroupMaster() {
        OkGo.<String>post(Urls.isBannedOrTrade)
                .params("cid", marketShopModel.getData().getGroupId())
                .params("memberId", marketShopModel.getData().getSeller())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("success")) {
                                    isMaster = data.optInt("isMaster");//0成员  1管理员  2 群主
                                    if (isMaster == 1){
                                        btnSoldOut.setVisibility(View.VISIBLE);
                                        linMarketKeFu.setVisibility(View.GONE);
                                        linMarketTuan.setVisibility(View.GONE);
                                        linMarketBuy.setVisibility(View.GONE);
                                        btnNone.setVisibility(View.GONE);
                                        linPinTuan.setVisibility(View.GONE);
                                    }else {
                                        setBottom();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 设置底部button状态
     */
    private void setBottom(){
        //查询是否是好友
        queryGoodFriendRequest();
        if(marketShopModel.getData().getDeleteFlag() == 1){
            btnSoldOutTrue.setVisibility(View.VISIBLE);
            btnSoldOut.setVisibility(View.GONE);
            linMarketKeFu.setVisibility(View.GONE);
            linMarketTuan.setVisibility(View.GONE);
            linMarketBuy.setVisibility(View.GONE);
            btnNone.setVisibility(View.GONE);
            linPinTuan.setVisibility(View.GONE);
        }
        //自己发布的商品只能看见下架按钮
        else if (TextUtils.equals(String.valueOf(marketShopModel.getData().getSeller()), UserModel.getUserModel().getMemberId())) {
            btnSoldOut.setVisibility(View.VISIBLE);
            linMarketKeFu.setVisibility(View.GONE);
            linMarketTuan.setVisibility(View.GONE);
            linMarketBuy.setVisibility(View.GONE);
            btnNone.setVisibility(View.GONE);
            linPinTuan.setVisibility(View.GONE);
        }
        //库存不足
        else if (marketShopModel.getData().getAvailableSupplyOrPurchaseQuantity() <= 0) {
            btnSoldOut.setVisibility(View.GONE);
            linMarketKeFu.setVisibility(View.VISIBLE);
            linMarketTuan.setVisibility(View.GONE);
            linMarketBuy.setVisibility(View.GONE);
            btnNone.setVisibility(View.VISIBLE);
            linPinTuan.setVisibility(View.GONE);
        } else if (groupType == 1) {//单聊
            btnSoldOut.setVisibility(View.GONE);
            linMarketKeFu.setVisibility(View.VISIBLE);
            //linMarketTuan.setVisibility(View.VISIBLE);
            linMarketBuy.setVisibility(View.VISIBLE);
            btnNone.setVisibility(View.GONE);
            llGroupPrice.setVisibility(View.GONE);
            linPinTuan.setVisibility(View.GONE);
            linMarketTuan.setVisibility(View.GONE);
        } else {
            //不拼团的情况
            if (TextUtils.isEmpty(String.valueOf(marketShopModel.getData().getGroupPrice())) || marketShopModel.getData().getGroupPrice() == 0.0 || marketShopModel.getData().getGroupNum() <= 1) {
                btnSoldOut.setVisibility(View.GONE);
                linMarketKeFu.setVisibility(View.VISIBLE);
                linMarketTuan.setVisibility(View.GONE);
                linMarketBuy.setVisibility(View.VISIBLE);
                btnNone.setVisibility(View.GONE);
                linPinTuan.setVisibility(View.GONE);
                llGroupPrice.setVisibility(View.GONE);
            }
            //都有的情况（分实物商品和虚拟商品）
            else {
                btnSoldOut.setVisibility(View.GONE);
                linMarketKeFu.setVisibility(View.VISIBLE);
                //linMarketTuan.setVisibility(View.VISIBLE);
                linMarketBuy.setVisibility(View.VISIBLE);
                btnNone.setVisibility(View.GONE);
                //0实物 1虚物
                int productType = marketShopModel.getData().getProductType();
                switch (productType) {
                    case 0://实物商品可拼团
                        linPinTuan.setVisibility(View.VISIBLE);
                        linMarketTuan.setVisibility(View.VISIBLE);
                        llGroupPrice.setVisibility(View.VISIBLE);
                        break;
                    case 1://虚拟商品不能拼团
                        linPinTuan.setVisibility(View.GONE);
                        linMarketTuan.setVisibility(View.GONE);
                        llGroupPrice.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}
