package com.merrichat.net.activity.picture.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.adapter.MagicStyleAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.model.QueryAllMagicTypeModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.weidget.HomeViewPager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangweiwei on 2018/4/2.
 */

public class MagicTackPhotoStyleFrgment extends BaseFragment {
    private TabLayout tabLayout;   //tab 名称
    private HomeViewPager viewPager;   //viewpage
    private MagicTackAdapter receiveEvalueAdapter; //  ViewPager适配器

    private QueryAllMagicTypeModel magicTypeModel;   //数据model
    Gson gson = new Gson();

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_magic_tack_photo_style, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (HomeViewPager) view.findViewById(R.id.viewpager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tab被选的时候回调
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //tab未被选择的时候回调
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //tab重新选择的时候回调
            }
        });
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //了解源码得知 线的宽度是根据 tabView的宽度来设置的
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField =
                                tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(0, 0, 0, 0);
                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        //设置tab左右间距为10dp  注意这里不能使用Padding
                        // 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params =
                                (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width ;
                        params.leftMargin = 50;
                        params.rightMargin = 50;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        File file = new File(FileUtils.photoMagicPath);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
            file.mkdirs();
        }
        initPager();
        return view;
    }


    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initPager() { //加载头部列表数据
        ApiManager.apiService(WebApiService.class).queryAllMagicType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryAllMagicTypeModel>() {
                    @Override
                    public void onNext(QueryAllMagicTypeModel allMagicTypeModel) {
                        magicTypeModel = allMagicTypeModel;
                        initData(allMagicTypeModel.data.list.size());  //根据数据来实现预加载数据
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void initData(final int mragmentSize) {
        viewPager.setOffscreenPageLimit(mragmentSize);
        receiveEvalueAdapter = new MagicTackAdapter(getActivity().getSupportFragmentManager()); //adapter 初始化数据保罗list
        tabLayout.setTabsFromPagerAdapter(receiveEvalueAdapter);
        viewPager.setAdapter(receiveEvalueAdapter);
    }


    class MagicTackAdapter extends FragmentPagerAdapter {  //viewpager适配器 动态显示
        @Override
        public Fragment getItem(int position) {
            MagicStyleFragment magicStyleFragment = new MagicStyleFragment(position,magicTypeModel.data.list.get(position));
            return magicStyleFragment;
        }

        public MagicTackAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return magicTypeModel.data.list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return magicTypeModel.data.list.get(position).typeName;
        }
    }

}
