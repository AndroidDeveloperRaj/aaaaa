package com.merrichat.net.activity.grouporder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.VideoChatActivity;
import com.merrichat.net.activity.meiyu.VoiceChatActivity;
import com.merrichat.net.activity.message.AddGroupFragment;
import com.merrichat.net.activity.message.MessageVideoCallAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.fragment.GroupListFragment;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.BottomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_INFO_KEY;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_TYPE_KEY;

/**
 * Created by AMSSY1 on 2018/1/19.
 * <p>
 * 群组列表
 */

public class GroupListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.tabLayout)
    SegmentTabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private String[] mTitles = {"群组列表", "附近的群"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        tvTitleText.setText("群组列表");
        initView();
    }

    @OnClick({R.id.iv_back, R.id.tv_right_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_right_img:
                showCallDialog();
                break;
        }
    }

    private void initView() {

        //存Frangment的list
        GroupListFragment groupListFragment = new GroupListFragment();
        AddGroupFragment addGroupFragment = new AddGroupFragment();
        mFragments.add(groupListFragment);
        mFragments.add(addGroupFragment);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragAdapter(getSupportFragmentManager()));
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    tvRightImg.setVisibility(View.VISIBLE);
                    tvRightImg.setImageResource(R.mipmap.fujin_shaixuan);
                } else {
                    tvRightImg.setVisibility(View.GONE);
                }
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    tvRightImg.setVisibility(View.VISIBLE);
                    tvRightImg.setImageResource(R.mipmap.fujin_shaixuan);
                } else {
                    tvRightImg.setVisibility(View.GONE);
                }
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0, false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    /**
     * 筛选dialog
     */
    private void showCallDialog() {
        final BottomDialog bottomDialog = new BottomDialog(this, "全部", "交流群", "电商群", "集市群");
        bottomDialog.showAnim(null);
        bottomDialog.show();
        bottomDialog.setOnViewClick(new BottomDialog.OnViewClick() {
            @Override
            public void onClick(View v) {
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.FILTER_NEAR_GROUP = true;
                switch (v.getId()) {
                    case R.id.tv_first://全部
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "";
                        break;
                    case R.id.tv_second://交流群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "0";
                        break;
                    case R.id.tv_third://交流群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "1";
                        break;
                    case R.id.tv_four://电商群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "2";
                        break;
                }
                EventBus.getDefault().post(myEventBusModel);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private class FragAdapter extends FragmentPagerAdapter {
        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
