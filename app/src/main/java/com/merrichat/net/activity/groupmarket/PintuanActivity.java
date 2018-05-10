package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.PintuanAdapter;
import com.merrichat.net.adapter.PintuanModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MarketShopModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 拼团
 * Created by amssy on 18/1/22.
 */

public class PintuanActivity extends BaseActivity implements PintuanAdapter.onPintuanItemClickLinster {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private PintuanAdapter adapter;
    private String productId = "";//商品ID
    private String type = "0";//0拼团中，1拼团成功，2失败，(所有传空)

    private List<PintuanModel.DataBean.ListBean> listData;
    private MarketShopModel marketShopModel;
    private PintuanModel pintuanModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintuan);
        ButterKnife.bind(this);
        initView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        listData = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PintuanActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PintuanAdapter(PintuanActivity.this,listData);
        recyclerView.setAdapter(adapter);
        adapter.setPintuanItemClickLinster(this);
        //获取拼团列表数据
        getRegimentList();
    }

    private void initView() {
        tvTitleText.setText("正在拼团");
        Intent intent = getIntent();
        if (intent!=null) {
            productId = intent.getStringExtra("productId");
            marketShopModel = (MarketShopModel) intent.getSerializableExtra("marketShopModel");
        }
    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 去拼团的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(PintuanActivity.this, PintuanDetailActivity.class);
        intent.putExtra("marketShopModel",marketShopModel);
        intent.putExtra("PintuanModel",pintuanModel);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cancelAllTimers();
    }

    /**
     * 查询拼团列表
     */
    private void getRegimentList() {
        OkGo.<String>get(Urls.getRegimentList)
                .tag(this)
                .params("productId", productId)
                .params("type", type)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    pintuanModel = JSON.parseObject(response.body(),PintuanModel.class);
                                    listData.addAll(pintuanModel.getData().getList());
                                    if (pintuanModel.getData().getList() != null) {
                                        //adapter.setDataTime(pintuanModel.getData().getTime());
                                        adapter.notifyDataSetChanged();
                                    }
                                    if (listData.size() == 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("暂无数据哦～");
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText("暂无数据哦～");
                                    }
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
                        if (tvEmpty != null) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText("网络请求失败，请重试");
                        }
                    }
                });
    }
}
