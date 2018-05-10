package com.merrichat.net.activity.meiyu;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.EncounterPhotoAdapter;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.view.PagingScrollHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/15.
 * 美遇----遇到的人
 */

public class EncounterActivity extends BaseActivity implements PagingScrollHelper.onPageChangeListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_title_text)
    TextView tvTitleText;


    @BindView(R.id.recycler_view_photo)
    RecyclerView recyclerViewPhoto;


//    @BindView(R.id.recycler_view_avatar)
//    RecyclerView recyclerViewAvatar;


    private EncounterPhotoAdapter photoAdapter;

//    private EncounterAvatarAdapter avatarAdapter;

    private LinearLayoutManager hLinearLayoutManager = null;


    PagingScrollHelper scrollHelper = new PagingScrollHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        hLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photoAdapter = new EncounterPhotoAdapter(cnt);
        recyclerViewPhoto.setAdapter(photoAdapter);
        RecyclerView.LayoutManager layoutManager = null;
        layoutManager = hLinearLayoutManager;
        scrollHelper.setUpRecycleView(recyclerViewPhoto);
        scrollHelper.setOnPageChangeListener(this);
        if (layoutManager != null) {
            recyclerViewPhoto.setLayoutManager(layoutManager);
            scrollHelper.updateLayoutManger();
        }


//        recyclerViewPhoto.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(cnt);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerViewPhoto.setLayoutManager(layoutManager);
//        photoAdapter = new EncounterPhotoAdapter(cnt);
//        recyclerViewPhoto.setAdapter(photoAdapter);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onPageChange(int index) {
        GetToast.useString(cnt, "第===" + index + "---页");
    }
}
