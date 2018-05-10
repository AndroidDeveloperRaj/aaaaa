package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.weidget.HomeViewPager;

/**
 * Created by amssy on 18/1/23.
 * 群订单管理
 */

public class GroupOrderBuyActivity extends MerriActionBarActivity {

    private ReceiveEvaluateAdapter receiveEvalueAdapter;

    private RelativeLayout rlWaiteGroupBuy;   //待成团
    private RelativeLayout rlWaiteGroupSend;  //待发货
    private RelativeLayout rlGroupSend;  //已发货
    private RelativeLayout rlGroupBuyOver;   //已结束
    private RelativeLayout rlGroupBuyOverArbitrament;   //仲裁
    private HomeViewPager mHomeViewPager;   //viewpager content 内容
    private String mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_order_buy);
        setLeftBack();  //左上角返回
        setTitle("群订单管理");
        mGroupId = getIntent().getStringExtra("groupId");

        initViews();
        initData();
    }

    private void initViews() {
        rlWaiteGroupBuy = (RelativeLayout) findViewById(R.id.rl_waite_group_buy);
        rlWaiteGroupSend = (RelativeLayout) findViewById(R.id.rl_waite_group_send);
        rlGroupSend = (RelativeLayout) findViewById(R.id.rl_group_send);
        rlGroupBuyOver = (RelativeLayout) findViewById(R.id.rl_group_buy_over);
        rlGroupBuyOverArbitrament = (RelativeLayout) findViewById(R.id.rl_group_buy_over_arbitrament);

        mHomeViewPager = (HomeViewPager) findViewById(R.id.home_view_pager);

        rlWaiteGroupBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(0);
            }
        });
        rlWaiteGroupSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(1);
            }
        });
        rlGroupSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(2);
            }
        });
        rlGroupBuyOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(3);
            }
        });
        rlGroupBuyOverArbitrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(4);
            }
        });
        rlWaiteGroupBuy.setSelected(true);

        mHomeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //设置评价选择tab
    private void setTab(int position) {
        if (position == 0) {
            rlWaiteGroupBuy.setSelected(true);
            rlWaiteGroupSend.setSelected(false);
            rlGroupSend.setSelected(false);
            rlGroupBuyOver.setSelected(false);
            rlGroupBuyOverArbitrament.setSelected(false);
            if (position != mHomeViewPager.getCurrentItem()) {
                mHomeViewPager.setCurrentItem(0, false);
            }
        } else if (position == 1) {
            rlWaiteGroupBuy.setSelected(false);
            rlWaiteGroupSend.setSelected(true);
            rlGroupSend.setSelected(false);
            rlGroupBuyOver.setSelected(false);
            rlGroupBuyOverArbitrament.setSelected(false);
            if (position != mHomeViewPager.getCurrentItem()) {
                mHomeViewPager.setCurrentItem(1, false);
            }
        } else if (position == 2) {
            rlWaiteGroupBuy.setSelected(false);
            rlWaiteGroupSend.setSelected(false);
            rlGroupSend.setSelected(true);
            rlGroupBuyOver.setSelected(false);
            rlGroupBuyOverArbitrament.setSelected(false);
            if (position != mHomeViewPager.getCurrentItem()) {
                mHomeViewPager.setCurrentItem(2, false);
            }

        } else if (position == 3) {
            rlWaiteGroupBuy.setSelected(false);
            rlWaiteGroupSend.setSelected(false);
            rlGroupSend.setSelected(false);
            rlGroupBuyOver.setSelected(true);
            rlGroupBuyOverArbitrament.setSelected(false);
            if (position != mHomeViewPager.getCurrentItem()) {
                mHomeViewPager.setCurrentItem(3, false);
            }
        } else {
            rlWaiteGroupBuy.setSelected(false);
            rlWaiteGroupSend.setSelected(false);
            rlGroupSend.setSelected(false);
            rlGroupBuyOver.setSelected(false);
            rlGroupBuyOverArbitrament.setSelected(true);
            if (position != mHomeViewPager.getCurrentItem()) {
                mHomeViewPager.setCurrentItem(4, false);
            }
        }
    }

    private void initData() {
        receiveEvalueAdapter = new ReceiveEvaluateAdapter(getSupportFragmentManager());
        mHomeViewPager.setAdapter(receiveEvalueAdapter);
    }


    class ReceiveEvaluateAdapter extends FragmentPagerAdapter {
        public ReceiveEvaluateAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GroupBuyManagementFragment groupBuyManagementFragment = new GroupBuyManagementFragment(position,mGroupId);
            return groupBuyManagementFragment;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
