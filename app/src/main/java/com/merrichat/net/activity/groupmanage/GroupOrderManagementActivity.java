package com.merrichat.net.activity.groupmanage;

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
 * 购买订单
 */
public class GroupOrderManagementActivity extends MerriActionBarActivity {

    @BindView(R.id.page_tabs)
    PagerSlidingTabStrip pageTabs;  //show tab page
    @BindView(R.id.view_pager)
    ViewPager viewPager;  //ViewPage page view
    private ArrayList<Fragment> fragmentList;  //List pager
    private MyShowPagerAdapter myShowPagerAdapter;  // BuyOrderFragment adapter pager

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_order_manager);
        ButterKnife.bind(this);
        setLeftBack();  //左上角返回
        setTitle("购买订单");   //title name contour
        rl_actionbar.setBackgroundColor(Color.rgb(245,248,250));
        initViews();

    }

    private void initViews() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new BuyOrderFragment(0));
        fragmentList.add(new BuyOrderFragment(1));
        fragmentList.add(new BuyOrderFragment(2));
        fragmentList.add(new BuyOrderFragment(3));
        fragmentList.add(new BuyOrderFragment(4));

        myShowPagerAdapter = new MyShowPagerAdapter(getSupportFragmentManager(), new String[]{"待成团", "待发货", "待收货", "已结束", "退款"}, fragmentList);
        viewPager.setAdapter(myShowPagerAdapter);
        pageTabs.setViewPager(viewPager);
        pageTabs.setTabPaddingLeftRight(10);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((BuyOrderFragment) fragmentList.get(position)).notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

}
