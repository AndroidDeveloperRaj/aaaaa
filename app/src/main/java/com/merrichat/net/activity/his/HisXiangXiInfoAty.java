package com.merrichat.net.activity.his;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.MyJingLiAdapter;
import com.merrichat.net.model.HisHomeModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/4.
 */

public class HisXiangXiInfoAty extends BaseActivity {
    public final static int HIS_JIAOYU_TYPE = 0x004;
    public final static int HIS_GONGZUO_TYPE = 0x005;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tvs)
    TextView tvs;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.rl_nick_name)
    RelativeLayout rlNickName;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tv_shengao_text)
    TextView tvShengaoText;
    @BindView(R.id.tv_shengao)
    TextView tvShengao;
    @BindView(R.id.rl_shengao)
    RelativeLayout rlShengao;
    @BindView(R.id.tv_tizhong)
    TextView tvTizhong;
    @BindView(R.id.rl_tizhong)
    RelativeLayout rlTizhong;
    @BindView(R.id.tv_xuexing)
    TextView tvXuexing;
    @BindView(R.id.rl_xuexing)
    RelativeLayout rlXuexing;
    @BindView(R.id.tv_minzu)
    TextView tvMinzu;
    @BindView(R.id.rl_minzu)
    RelativeLayout rlMinzu;
    @BindView(R.id.tv_zongjiao)
    TextView tvZongjiao;
    @BindView(R.id.rl_zongjiao)
    RelativeLayout rlZongjiao;
    @BindView(R.id.tv_xueli)
    TextView tvXueli;
    @BindView(R.id.rl_xueli)
    RelativeLayout rlXueli;
    @BindView(R.id.tvsd)
    TextView tvsd;
    @BindView(R.id.tv_sheng_ri)
    TextView tvShengRi;
    @BindView(R.id.rl_sheng_ri)
    RelativeLayout rlShengRi;
    @BindView(R.id.tvgrjj)
    TextView tvgrjj;
    @BindView(R.id.tv_grjj)
    TextView tvGrjj;
    @BindView(R.id.rl_gerenjianjie)
    RelativeLayout rlGerenjianjie;
    @BindView(R.id.tvgx)
    TextView tvgx;
    @BindView(R.id.tv_guxiang)
    TextView tvGuxiang;
    @BindView(R.id.rl_guxiang)
    RelativeLayout rlGuxiang;
    @BindView(R.id.tvjzd)
    TextView tvjzd;
    @BindView(R.id.tv_juzhudi)
    TextView tvJuzhudi;
    @BindView(R.id.rl_juzhudi)
    RelativeLayout rlJuzhudi;
    @BindView(R.id.tv_zhiye)
    TextView tvZhiye;
    @BindView(R.id.rl_zhiye)
    RelativeLayout rlZhiye;
    @BindView(R.id.tv_danwei)
    TextView tvDanwei;
    @BindView(R.id.tv_ganqing)
    TextView tvGanqing;
    @BindView(R.id.rl_ganqing)
    RelativeLayout rlGanqing;
    @BindView(R.id.rv_receclerView_jiaoyu)
    RecyclerView rvRececlerViewJiaoyu;
    @BindView(R.id.rv_receclerView_gongzuo)
    RecyclerView rvRececlerViewGongzuo;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_Account)
    TextView tvAccount;
    private HisHomeModel.DataBean hisHomeModelData;
    private MyJingLiAdapter eduJingLiAdapter;
    private MyJingLiAdapter workJingLiAdapter;
    private ArrayList<HisHomeModel.DataBean.HisMemberInfoBean.EduExperienceBean> eduExperienceList;
    private ArrayList<HisHomeModel.DataBean.HisMemberInfoBean.WorkExperienceBean> workExperienceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_xiangxiinfo);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("详细资料");
        hisHomeModelData = (HisHomeModel.DataBean) getIntent().getSerializableExtra("hisHomeModelData");
        setData();
    }

    private void setData() {
        eduExperienceList = new ArrayList<>();
        workExperienceList = new ArrayList<>();
        if (hisHomeModelData != null) {
            HisHomeModel.DataBean.HisMemberInfoBean hisMemberInfo = hisHomeModelData.getHisMemberInfo();
            tvNickName.setText(hisMemberInfo.getNickName());
            if ("1".equals(hisMemberInfo.getGender())) {
                tvSex.setText("男");
            } else {
                tvSex.setText("女");
            }
            tvAccount.setText(hisMemberInfo.getMobile());
            tvShengao.setText(hisMemberInfo.getHeight());
            tvTizhong.setText(hisMemberInfo.getWeight());
            tvXuexing.setText(hisMemberInfo.getBloodType());
            tvMinzu.setText(hisMemberInfo.getNation());
            tvZongjiao.setText(hisMemberInfo.getReligion());
            tvXueli.setText(hisMemberInfo.getEduBackGround());
            tvZhiye.setText(hisMemberInfo.getOccupation());
            tvShengRi.setText(hisMemberInfo.getBirthday());
            tvGrjj.setText(hisMemberInfo.getSignature());
            tvGuxiang.setText(hisMemberInfo.getHometown());
            tvJuzhudi.setText(hisMemberInfo.getCurrentProvince() + "" + hisMemberInfo.getCurrentCity() + "" + hisMemberInfo.getCurrentCounty());
            tvDanwei.setText(hisMemberInfo.getCompany());
            eduExperienceList.addAll(hisMemberInfo.getEduExperience());
            workExperienceList.addAll(hisMemberInfo.getWorkExperience());
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager jiaoYuLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerViewJiaoyu.setLayoutManager(jiaoYuLayoutManager);
        eduJingLiAdapter = new MyJingLiAdapter(HIS_JIAOYU_TYPE, R.layout.item_my_jingli, eduExperienceList);
        rvRececlerViewJiaoyu.setAdapter(eduJingLiAdapter);

        LinearLayoutManager gongZuoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerViewGongzuo.setLayoutManager(gongZuoLayoutManager);
        workJingLiAdapter = new MyJingLiAdapter(HIS_GONGZUO_TYPE, R.layout.item_my_jingli, workExperienceList);
        rvRececlerViewGongzuo.setAdapter(workJingLiAdapter);
    }

    @OnClick({R.id.iv_back, R.id.tv_title_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_title_text:
                break;
        }
    }
}
