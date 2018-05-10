package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.GoodsTradingAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GoodsTradingModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/27.
 * 交易商品设置
 */

public class SetGoodsTradingActivity extends BaseActivity {

    public static final int activityId = MiscUtil.getActivityId();

    /**
     * 实物商品
     */
    @BindView(R.id.iv_physical_goods)
    ImageView ivPhysicalGoods;
    @BindView(R.id.tv_physical_goods)
    TextView tvPhysicalGoods;
    @BindView(R.id.ll_physical_goods)
    LinearLayout llPhysicalGoods;


    /**
     * 虚拟商品
     */
    @BindView(R.id.iv_virtual_merchandise)
    ImageView ivVirtualMerchandise;
    @BindView(R.id.tv_virtual_merchandise)
    TextView tvVirtualMerchandise;
    @BindView(R.id.ll_virtual_merchandise)
    LinearLayout llVirtualMerchandise;

    /**
     * 只允许指定交易商品
     */
    @BindView(R.id.sb_button)
    SwitchButton sbButton;

    /**
     * 添加商品
     */
    @BindView(R.id.tv_add_commodity)
    TextView tvAddCommodity;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @BindView(R.id.iv_back)
    ImageView ivBack;


    @BindView(R.id.tv_title_text)
    TextView tvTitleText;

    private GoodsTradingAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<GoodsTradingModel> goodsTradingModelList;


    /************以下为接口返回字段*****************/


    /**
     * 是否指定商品类型 0：不指定 1：指定
     */
    private int isSpecifiedProduct;

    /**
     * 商品类型默认
     * 0：实物商品
     * 1：虚拟商品
     */
    private int productType;

    /**
     * 群id
     */
    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goods_trading);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        tvTitleText.setText("群商品设置");
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        goodsTradingModelList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsTradingAdapter(cnt, goodsTradingModelList);
        recyclerView.setAdapter(adapter);
        sbButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    isSpecifiedProduct = 1;
                } else {
                    isSpecifiedProduct = 0;
                }
            }
        });
        tradeProductSetPage();
        adapter.setOnDelListener(new GoodsTradingAdapter.onSwipeListener() {
            @Override
            public void onDel(int pos) {
                adapter.notifyItemRemoved(pos);
                goodsTradingModelList.remove(pos);
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 交易商品设置
     */
    private void tradeProductSetPage() {
        OkGo.<String>post(Urls.tradeProductSetPage)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    isSpecifiedProduct = data.optInt("isSpecifiedProduct");
                                    productType = data.optInt("productType");
                                    JSONArray list = data.optJSONArray("list");
                                    Gson gson = new Gson();
                                    for (int i = 0; i < list.length(); i++) {
                                        GoodsTradingModel model = gson.fromJson(list.get(i).toString(), GoodsTradingModel.class);
                                        goodsTradingModelList.add(model);
                                    }
                                    setJieMian();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 设置界面展示
     */
    private void setJieMian() {
        adapter.notifyDataSetChanged();
        if (isSpecifiedProduct == 0) {
            sbButton.setCheckedImmediatelyNoEvent(false);
        } else if (isSpecifiedProduct == 1) {
            sbButton.setCheckedImmediatelyNoEvent(true);
        }
        setButton(productType);
    }

    @OnClick({R.id.ll_physical_goods, R.id.ll_virtual_merchandise, R.id.tv_add_commodity, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setLeftBack();
                break;
            case R.id.ll_physical_goods:
                if (productType == 0) {
                    return;
                }
                productType = 0;
                setButton(productType);
                break;
            case R.id.ll_virtual_merchandise:
                if (productType == 1) {
                    return;
                }
                productType = 1;
                setButton(productType);
                break;
            case R.id.tv_add_commodity://添加商品
                startActivityForResult(new Intent(cnt, ModifyGroupNameActivity.class).addFlags(activityId), ModifyGroupNameActivity.activityId);
                break;
        }
    }


    /**
     * 返回
     */
    private void setLeftBack() {
        if (isSpecifiedProduct == 1 && goodsTradingModelList.size() == 0) {
            GetToast.useString(cnt, "请添加指定的交易商品");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("isSpecifiedProduct", isSpecifiedProduct + "");
        intent.putExtra("productType", productType + "");
        if (goodsTradingModelList.size() == 0) {
            intent.putExtra("productJson", "");
        } else {
//            JSONArray jsonArray = new JSONArray();
//            for (int i = 0; i < goodsTradingModelList.size(); i++) {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("productName", goodsTradingModelList.get(i).getProductName());
//                    jsonObject.put("productImgUrlList", goodsTradingModelList.get(i).getProductImgUrlList());
//                    jsonArray.put(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            String productJson = new Gson().toJson(goodsTradingModelList);
            intent.putExtra("productJson", productJson);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setLeftBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ModifyGroupNameActivity.activityId) {
                String productName = data.getStringExtra("productName");
                ArrayList<String> productImgUrlList = (ArrayList<String>) data.getSerializableExtra("productImgUrlList");
                GoodsTradingModel model = new GoodsTradingModel();
                model.setProductName(productName);
                model.setProductImgUrlList(productImgUrlList);
                goodsTradingModelList.add(model);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 实物商品/虚拟商品展示
     */
    private void setButton(int commodityFlag) {
        switch (commodityFlag) {
            case 0:
                tvPhysicalGoods.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                ivPhysicalGoods.setImageResource(R.mipmap.accept_2x_true);
                tvVirtualMerchandise.setTextColor(getResources().getColor(R.color.black_new_two));
                ivVirtualMerchandise.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                break;

            case 1:
                tvVirtualMerchandise.setTextColor(getResources().getColor(R.color.base_FF3D6F));
                ivVirtualMerchandise.setImageResource(R.mipmap.accept_2x_true);
                tvPhysicalGoods.setTextColor(getResources().getColor(R.color.black_new_two));
                ivPhysicalGoods.setImageResource(R.mipmap.button_weixuanzhong_shixin);
                break;
        }
    }
}
