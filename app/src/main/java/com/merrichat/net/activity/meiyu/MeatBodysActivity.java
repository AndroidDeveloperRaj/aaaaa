package com.merrichat.net.activity.meiyu;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.EncounterBodyAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.EncounterPersonModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.weidget.AlphaPageTransformer;
import com.merrichat.net.weidget.VpAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MeatBodysActivity extends MerriActionBarActivity {

    private ViewPager mViewPager;
    private ViewPager mPagerUserUrl;

    private LinearLayout relativeLayout;

    private VpAdapter adapter;    //下方显示头像的viewpager
    private EncounterBodyAdapter encounterBodyAdapter;  //上方显示介绍的 viewpager

    private int screenWidth = 0;
    private List<EncounterPersonModel.EncounterBody> listEncounterBody; //遇到的人列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_bodys);
        setLeftBack();
        setTitle("遇到的人");

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerUserUrl = (ViewPager) findViewById(R.id.viewpager_user_ico);
        relativeLayout = (LinearLayout) findViewById(R.id.container);

        // 獲取屏幕寬度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        encounterPerson();

        // 将父节点Layout事件分发给viewpager，否则只能滑动中间的一个view对象
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mPagerUserUrl.dispatchTouchEvent(event);
            }
        });

    }


    /**
     * 遇到的人
     */
    public void encounterPerson() {
        ApiManager.apiService(WebApiService.class).encounterPerson(UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<EncounterPersonModel>() {
                    @Override
                    public void onNext(EncounterPersonModel giftListsMode) {
                        if (giftListsMode.success) {
                            if (giftListsMode.data.list.size() > 0) {
                                listEncounterBody = giftListsMode.data.list;
                                initViewPager();
                            } else {
                                //没有遇到任何人
                                Toast.makeText(MeatBodysActivity.this, "没有遇到任何人～！", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (giftListsMode.message != null)
                                Toast.makeText(MeatBodysActivity.this, giftListsMode.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        Toast.makeText(MeatBodysActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    //页面加载遇到的人详情页
    public void initViewPager() {
        mViewPager.setPageMargin(20);
        mViewPager.setOffscreenPageLimit(3);
        encounterBodyAdapter = new EncounterBodyAdapter(listEncounterBody, MeatBodysActivity.this);
        mViewPager.setAdapter(encounterBodyAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                mPagerUserUrl.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setPageTransformer(true, new AlphaPageTransformer());
        mPagerUserUrl.setOffscreenPageLimit(10); // viewpager缓存页数
        mPagerUserUrl.setPageMargin(30); // 设置各页面的间距
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                screenWidth / 6, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPagerUserUrl.setLayoutParams(params);
        adapter = new VpAdapter(listEncounterBody, MeatBodysActivity.this);
        mPagerUserUrl.setAdapter(adapter);
        mPagerUserUrl.setCurrentItem(0);
        mPagerUserUrl.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                adapter.setSelectPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


}
