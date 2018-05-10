package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.GoodsTradingAdapter;
import com.merrichat.net.model.GoodsTradingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 2018/2/23.
 * 选择商品
 */

public class ProductListAty extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private GoodsTradingAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<GoodsTradingModel> goodsTradingModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("选择商品");
        goodsTradingModelList = new ArrayList<>();
        goodsTradingModelList.addAll((Collection<? extends GoodsTradingModel>) getIntent().getSerializableExtra("produstList"));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsTradingAdapter(cnt, goodsTradingModelList);
        recyclerView.setAdapter(adapter);

        adapter.setOnTextClick(new GoodsTradingAdapter.onTextClick() {
            @Override
            public void onTvClick(GoodsTradingModel goodsTradingModel) {
                setResult(RESULT_OK, new Intent().putExtra("goodsTradingModel", goodsTradingModel));
                finish();
            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
