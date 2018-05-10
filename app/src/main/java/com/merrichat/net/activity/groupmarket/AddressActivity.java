package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.Button;
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
 * 收货地址
 * Created by amssy on 18/1/23.
 */

public class AddressActivity extends BaseActivity implements AddressFragment.onAddressOnClicklinster, VirtualAdressFragment.OnVirtualAddressOnClicklinster {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;
    @BindView(R.id.viewPager_address)
    CustomViewPager viewPagerAddress;

    @BindView(R.id.btn_add)
    Button btnAdd;

    private ArrayList<Fragment> list;
    private String[] mTitles = {"实物地址", "虚拟地址"};
    private AddressFragment addressFragment;
    private VirtualAdressFragment virtualAdressFragment;

    private int productType = -1;//0实物 1虚物

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("收货地址");

        Intent intent = getIntent();
        if (intent != null) {
            productType = intent.getIntExtra("productType", -1);

        }

        addressFragment = new AddressFragment();
        virtualAdressFragment = new VirtualAdressFragment();
        list = new ArrayList<>();
        list.add(addressFragment);
        list.add(virtualAdressFragment);
        btnAdd.setText("添加新地址");
        sgTablayout.setTabData(mTitles);

        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        viewPagerAddress.setCurrentItem(0);
                        productType = 0;
                        btnAdd.setText("添加新地址");
                        break;
                    case 1:
                        viewPagerAddress.setCurrentItem(1);
                        productType = 1;
                        btnAdd.setText("添加新地址");
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
        viewPagerAddress.setAdapter(adapter);
        if (productType == -1) {
            viewPagerAddress.setCurrentItem(0);
            sgTablayout.setCurrentTab(0);
        } else {
            if (productType == 1) {
                viewPagerAddress.setCurrentItem(1);
                sgTablayout.setCurrentTab(1);
            } else if (productType == 0) {
                viewPagerAddress.setCurrentItem(0);
                sgTablayout.setCurrentTab(0);
            }
            sgTablayout.setVisibility(View.GONE);
        }
        if (productType != -1) {
            addressFragment.setAddressOnClicklinster(this);
            virtualAdressFragment.setVirtualAddressOnClicklinster(this);
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_add:
                if (productType == 0 || productType == -1) {//实物地址
                    Intent intent = new Intent(AddressActivity.this, EditorAddressActivity.class);
                    intent.putExtra("type", "address");
                    startActivity(intent);
                } else if (productType == 1) {//虚拟地址
                    Intent intent = new Intent(AddressActivity.this, VirtualAddressActivity.class);
                    intent.putExtra("type", "address");
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 实物和虚拟地址点击事件的回调
     *
     * @param addresseeJson
     */
    @Override
    public void onAddress(String addresseeJson) {
        Intent intent = new Intent();
        intent.putExtra("addresseeJson", addresseeJson);
        setResult(RESULT_OK, intent);
        finish();
    }
}
