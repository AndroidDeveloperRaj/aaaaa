package com.merrichat.net.activity.grouporder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.MyShowPagerAdapter;
import com.merrichat.net.view.PagerSlidingTabStrip;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AMSSY1 on 2018/1/19.
 * <p>
 * 销售订单
 */

public class SellOrderManageAty extends MerriActionBarActivity {
    @BindView(R.id.page_tabs)
    PagerSlidingTabStrip pageTabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private MyShowPagerAdapter myShowPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouporder_manage);
        ButterKnife.bind(this);
        setLeftBack();
        rl_actionbar.setBackgroundColor(Color.rgb(245,248,250));
        setTitle("销售订单");
        initView();
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new PendingRegimentFragment());
        fragmentList.add(new PendingDeliveryFragment());
        fragmentList.add(new DeliveredFragment());
        fragmentList.add(new AlreadyEndFragment());
        fragmentList.add(new RefundmentFragment());


        myShowPagerAdapter = new MyShowPagerAdapter(getSupportFragmentManager(), new String[]{"待成团", "待发货", "已发货", "已结束", "退款"}, fragmentList);
        viewPager.setAdapter(myShowPagerAdapter);
        pageTabs.setViewPager(viewPager);
        pageTabs.setTabPaddingLeftRight(10);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);

    }
}
