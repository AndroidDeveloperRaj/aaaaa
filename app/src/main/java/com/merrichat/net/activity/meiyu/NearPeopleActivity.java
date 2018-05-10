package com.merrichat.net.activity.meiyu;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.NearPeopleAdapter;
import com.merrichat.net.utils.GetToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/15.
 * 美遇---附近的人
 */

public class NearPeopleActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right_img)
    ImageView ivRightButton;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private NearPeopleAdapter nearPeopleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_people);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("附近的人");
        ivRightButton.setVisibility(View.VISIBLE);
        ivRightButton.setBackgroundResource(R.mipmap.shouye_shaixuan_2x);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        nearPeopleAdapter = new NearPeopleAdapter(cnt);
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 初始化动画效果
        recyclerView.setAdapter(nearPeopleAdapter);

        nearPeopleAdapter.setOnItemClickListener(new NearPeopleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GetToast.useString(cnt, "点击====" + position);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_img:
                GetToast.useString(cnt, "点击按钮....");
                break;
        }
    }
}
