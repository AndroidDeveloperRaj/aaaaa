package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.AboutHomeSettingAdapter;
import com.merrichat.net.model.AboutHomeSettingModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Allen Cheng on 2018/3/28.
 * <p>
 * 虚拟地址--其他账号---账号类型
 */

public class AccountTypeAty extends MerriActionBarActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rc_recycler_view)
    RecyclerView rcRecyclerView;
    private ArrayList<AboutHomeSettingModel> settingItemList;
    private AboutHomeSettingAdapter aboutHomeSettingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("账号类型");
        initView();
    }

    private void initView() {
        settingItemList = new ArrayList<>();
        settingItemList.add(new AboutHomeSettingModel("邮箱", false));
        settingItemList.add(new AboutHomeSettingModel("社交账号", false));
        settingItemList.add(new AboutHomeSettingModel("钱包地址", false));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcRecyclerView.setLayoutManager(layoutManager);
        aboutHomeSettingAdapter = new AboutHomeSettingAdapter(R.layout.item_abouthomesetting, settingItemList);
        rcRecyclerView.setAdapter(aboutHomeSettingAdapter);
        aboutHomeSettingAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int j = 0; j < settingItemList.size(); j++) {
            AboutHomeSettingModel aboutHomeSettingModel = settingItemList.get(j);
            if (j == position) {
                if (!aboutHomeSettingModel.isChecked()) {
                    aboutHomeSettingModel.setChecked(true);
                    Intent intent = new Intent();
                    intent.putExtra("to_account_type", aboutHomeSettingModel);
                    setResult(RESULT_OK, intent);
                    AccountTypeAty.this.finish();
                }
            } else {
                aboutHomeSettingModel.setChecked(false);
            }
        }
        aboutHomeSettingAdapter.notifyDataSetChanged();
    }
}
