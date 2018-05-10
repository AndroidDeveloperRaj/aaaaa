package com.merrichat.net.activity.message.setting;

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
 * Created by amssy on 18/2/5.
 * 群订单管理
 */

public class OrderManagemenGrouptActivity extends MerriActionBarActivity {

    @BindView(R.id.page_tabs)
    PagerSlidingTabStrip pageTabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private MyShowPagerAdapter myShowPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_order_management);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("群订单管理");
        setLeftBack();
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ToBeGroupedFragment());
        fragmentList.add(new ToBeDeliveredFragment());
        fragmentList.add(new ShippedFragment());
        fragmentList.add(new OverOrderFragment());
        fragmentList.add(new ArbitrationRefundsFragment());


        myShowPagerAdapter = new MyShowPagerAdapter(getSupportFragmentManager(), new String[]{"待成团", "待发货", "已发货", "已结束", "仲裁退款"}, fragmentList);
        viewPager.setAdapter(myShowPagerAdapter);
        pageTabs.setViewPager(viewPager);
        pageTabs.setTabPadding(5);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);

    }
}
