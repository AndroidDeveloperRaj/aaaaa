package com.merrichat.net.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.find.NearGroupAty;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.meiyu.NewMeetNiceActivity;
import com.merrichat.net.activity.my.InviteToMakeMoneyAty;
import com.merrichat.net.fragment.circlefriends.BangDanActivity;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by amssy on 18/3/14.
 * 发现fragment
 */

public class FindFragment extends BaseFragment {

    @BindView(R.id.ll_near_group)
    RelativeLayout llNearGroup;
    @BindView(R.id.ll_nice_meet)
    RelativeLayout llNiceMeet;
    @BindView(R.id.ll_mining_list)
    RelativeLayout llMiningList;
    @BindView(R.id.ll_find)
    LinearLayout llFind;
    Unbinder unbinder;
    /**
     * 未登录
     */
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.btn_login_friend)
    Button btnLoginFriend;
    @BindView(R.id.lin_toast)
    LinearLayout linToast;

    /**
     * Fragment 的构造函数。
     */
    public FindFragment() {
    }

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_find, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserModel.getUserModel().getIsLogin()) {
            linToast.setVisibility(View.VISIBLE);
            llFind.setVisibility(View.GONE);
            tvNone.setText("你还未登录请前去登录");
            return;

        } else {
            linToast.setVisibility(View.GONE);
            llFind.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_login_friend, R.id.ll_near_group, R.id.ll_nice_meet, R.id.ll_mining_list, R.id.ll_share_for_money})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login_friend://未登录
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.ll_near_group:
                RxActivityTool.skipActivity(getContext(), NearGroupAty.class);
                break;
            case R.id.ll_nice_meet:
                startActivity(new Intent(cnt, NewMeetNiceActivity.class));
                break;
            case R.id.ll_mining_list:
                //GetToast.useString(cnt, "挖矿榜单");
                startActivity(new Intent(getActivity(), BangDanActivity.class));
                break;
            case R.id.ll_share_for_money:
                //GetToast.useString(cnt, "挖矿榜单");
                startActivity(new Intent(getActivity(), InviteToMakeMoneyAty.class));
                break;
        }
    }


}
