package com.merrichat.net.activity.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.view.CustomViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加朋友
 * Created by amssy on 18/1/22.
 */

public class AddFriendsActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * tab
     */
    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;
    /**
     * 自定义ViewPager
     */
    @BindView(R.id.viewPager_add_friend)
    CustomViewPager viewPagerAddFriend;

    private String[] mTitles = {"找人", "找群"};
    private ArrayList<Fragment> list;
    private AddFriendsFragment addFriendsFragment;
    private AddGroupFragment addGroupFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adds_friends);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("添加朋友");

        addFriendsFragment = new AddFriendsFragment();
        addGroupFragment = new AddGroupFragment();
        list = new ArrayList<>();
        list.add(addFriendsFragment);
        list.add(addGroupFragment);
        sgTablayout.setTabData(mTitles);
        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        viewPagerAddFriend.setCurrentItem(0);
                        break;
                    case 1:
                        viewPagerAddFriend.setCurrentItem(1);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };
        viewPagerAddFriend.setAdapter(adapter);
        viewPagerAddFriend.setCurrentItem(0);
        viewPagerAddFriend.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerAddFriend.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
