package com.merrichat.net.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.MinZuAdapter;
import com.merrichat.net.utils.MiscUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/10.
 */

public class MinZuActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    public final static int activityId = MiscUtil.getActivityId();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    private MinZuAdapter minZuAdapter;
    private List<String> minZuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_min_zu);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("民族");
        String minZuString = "汉族、蒙古族、回族、藏族、维吾尔族、苗族、彝族、壮族、布依族、朝鲜族、满族、侗族、瑶族、白族、土家族、哈尼族、哈萨克族、" +
                "傣族、黎族、僳僳族、佤族、畲族、高山族、拉祜族、水族、东乡族、纳西族、景颇族、柯尔克孜族、土族、达斡尔族、仫佬族、羌族、布朗族、" +
                "撒拉族、毛南族、仡佬族、锡伯族、阿昌族、普米族、塔吉克族、怒族、乌孜别克族、俄罗斯族、鄂温克族、德昂族、保安族、裕固族、京族、塔塔尔族、" +
                "独龙族、鄂伦春族、赫哲族、门巴族、珞巴族、基诺族";
        String[] minZuSplit = minZuString.split("、");
        minZuList = Arrays.asList(minZuSplit);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        minZuAdapter = new MinZuAdapter(R.layout.item_personinfo_minzu, minZuList);
        rlRecyclerview.setAdapter(minZuAdapter);
        minZuAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();
        intent.putExtra("minZuText", minZuList.get(position));
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
