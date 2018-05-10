package com.merrichat.net.activity.circlefriend;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.MorePersonDianZanAdapter;
import com.merrichat.net.model.CircleDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AMSSY1 on 2018/1/12.
 * <p>
 * 更多点赞的人
 */


public class MorePersonDianZanAty extends MerriActionBarActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    ArrayList<CircleDetailModel.DataBean.MemberIdBean> memberList = new ArrayList<>();
    private MorePersonDianZanAdapter morePersonDianZanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreperson_dizan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setLeftBack();
        setTitle("点赞的人");
        ArrayList<CircleDetailModel.DataBean.MemberIdBean> xiHuanList = (ArrayList<CircleDetailModel.DataBean.MemberIdBean>) getIntent().getSerializableExtra("xiHuanList");
        if (xiHuanList != null && xiHuanList.size() > 0) {
            memberList.addAll(xiHuanList);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        morePersonDianZanAdapter = new MorePersonDianZanAdapter(R.layout.item_moreperson_dizan, memberList);
        rlRecyclerview.setAdapter(morePersonDianZanAdapter);
        morePersonDianZanAdapter.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (!StringUtil.isLogin(this)) {
            RxToast.showToast("请登录后查看！");
            return;
        }
        String likePersonId = memberList.get(position).getLikePersonId();
        if (likePersonId.equals(UserModel.getUserModel().getMemberId())) {
            RxActivityTool.skipActivity(this, MyDynamicsAty.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("hisMemberId", Long.parseLong(likePersonId));
            bundle.putString("hisImgUrl", memberList.get(position).getPersonUrl());
            bundle.putString("hisNickName", memberList.get(position).getNickName());
            RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle);
        }
    }
}
