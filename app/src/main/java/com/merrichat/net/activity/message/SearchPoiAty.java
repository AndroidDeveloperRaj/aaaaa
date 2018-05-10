package com.merrichat.net.activity.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.MapAdapter;
import com.merrichat.net.utils.InputHelper;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ClearEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 2018/1/22.
 * 搜索地点
 */

//public class SearchPoiAty extends BaseActivity implements OnLoadmoreListener {
public class SearchPoiAty extends BaseActivity {
    @BindView(R.id.et)
    ClearEditText et;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    //    @BindView(R.id.refreshLayout)
//    SmartRefreshLayout refreshLayout;
    private MapAdapter mMapAdapter;
    private List<PoiInfo> poiInfoList;//一般数据
    public SuggestionSearch mSuggestionSearch;
//    private PoiSearch mPoiSearch;
    private String currentCity;
    private double currentLatitude;
    private double currentLongitude;

    private int currentPage = 1;//当前页
    private int pageSize = 15;//每页个数
    private boolean isLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
        initSearch();
    }

    private void initView() {
        currentCity = getIntent().getStringExtra("currentCity");
        currentLatitude = getIntent().getDoubleExtra("currentLatitude", 0.0);
        currentLongitude = getIntent().getDoubleExtra("currentLongitude", 0.0);
        et.setHint("搜索地点");
        poiInfoList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mMapAdapter = new MapAdapter(R.layout.item_map, poiInfoList);
        recyclerView.setAdapter(mMapAdapter);
        mMapAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mMapAdapter.setmIndexTag(position);

                PoiInfo poiInfo = mMapAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("currentLongitude", poiInfo.location.longitude);
                intent.putExtra("currentLatitude", poiInfo.location.latitude);
                intent.putExtra("currentAddress", poiInfo.address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        search();
        et.addTextChangedListener(new MyTextWatcher());
//        refreshLayout.setEnableRefresh(false);//取消下拉刷新
//        refreshLayout.setOnLoadmoreListener(this);
    }


    /**
     * 搜索
     */
    public void initSearch() {
        //关键词搜索
//        mPoiSearch = PoiSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance();
//        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult poiResult) {
//
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//                //未找到相关结果
//                if (poiDetailResult == null) {
//                    return;
//                }
//                Logger.e(poiDetailResult.toString());
//            }
//
//            @Override
//            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//
//            }
//        });

        OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
            // 获取poiResult
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<PoiInfo> poiList = poiResult.getAllPoi();
                    if (poiList.size() > 0) {
                        if (isLoadMore) {
//                            refreshLayout.finishLoadmore();
                            mMapAdapter.addDatas(poiList);
                        } else {
                            mMapAdapter.setDatas(poiList);
                        }
                    }
                    Logger.e("总共查到" + poiResult.getTotalPoiNum() + "个兴趣点,分为"
                            + poiResult.getTotalPageNum() + "页");
                } else {
                    if (mMapAdapter != null) {
                        mMapAdapter.clear();
                        RxToast.showToast("暂无搜索结果");
                    }
                }
            }

            // 当点击覆盖物的时候,查询详细的数据信息,作为回调返回数据信息
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };
//        mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //未找到相关结果
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    RxToast.showToast("未找到相关结果");
                    return;
                }
                List<SuggestionResult.SuggestionInfo> ssList = suggestionResult.getAllSuggestions();
                List<PoiInfo> poiList = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : ssList) {
                    if (info.key != null&&info.pt!=null&&! TextUtils.isEmpty(info.district)&&info.city.equals(currentCity)) {
                        //设置搜索地址
                        PoiInfo userPoi = new PoiInfo();
                        userPoi.name = info.key;
                        userPoi.location = info.pt;
                        userPoi.city = info.city;
                        userPoi.address = info.city+info.district;
                        poiList.add(userPoi);
                    }
                }
                mMapAdapter.setDatas(poiList);
            }
        });
    }

    private void search() {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    // 对应逻辑操作
                    String inputStr = et.getText().toString().trim();
                    if (!TextUtils.isEmpty(inputStr)) {
                        //搜索关键词
                         mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(inputStr).city(currentCity).citylimit(true));
//                        mPoiSearch.searchInCity(new PoiCitySearchOption().city(currentCity).keyword(inputStr).pageNum(1));
//                        citySearch(currentPage, inputStr);
                    }else {
                        mMapAdapter.clear();
                    }
                    return true;
                }
                return false;
            }
        });
    }

//    @Override
//    public void onLoadmore(RefreshLayout refreshlayout) {
//        currentPage++;
//        isLoadMore = true;
//        citySearch(currentPage, et.getText().toString().trim());
//    }


    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 对应逻辑操作
            String inputStr = et.getText().toString().trim();
            isLoadMore = false;
            currentPage = 1;
            if (!TextUtils.isEmpty(inputStr)) {
                //搜索关键词
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(inputStr).city(currentCity).citylimit(true));
//                mPoiSearch.searchInCity(new PoiCitySearchOption().city(currentCity).keyword(inputStr).pageNum(1));
//                citySearch(currentPage, inputStr);
            } else {
                mMapAdapter.clear();
            }
        }
    }


    /**
     * 城市内搜索,直接根据输入框的内容去实现Poi搜索.
     */
    private void citySearch(int page, String key) {
        // 城市内检索
        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
        // 关键字
        poiCitySearchOption.keyword(key);
        // 城市
        poiCitySearchOption.city(currentCity);
        // 设置每页容量，默认为每页10条
        poiCitySearchOption.pageCapacity(pageSize);
        // 分页编号
        poiCitySearchOption.pageNum(page);
//        mPoiSearch.searchInCity(poiCitySearchOption);
    }

    /**
     * 范围检索,范围搜索需要制定坐标.以矩形的方式进行范围搜索.
     */
    private void boundSearch(int page) {
//        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
//        LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
//        LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
//        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
//                .include(northeast).build();// 得到一个地理范围对象
//        boundSearchOption.bound(bounds);// 设置poi检索范围
//        boundSearchOption.keyword(editSearchKeyEt.getText().toString());// 检索关键字
//        boundSearchOption.pageNum(page);
//        poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }

    /**
     * 附近检索,范围搜索需要指定圆心.以圆形的方式进行搜索.
     */
    private void nearbySearch(int page, String key) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(currentLatitude, currentLongitude));
        nearbySearchOption.keyword(key);
        nearbySearchOption.radius(1000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
//        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    @OnClick({R.id.tv_cancle})
    public void onClick(View view) {
        InputHelper.getInstance(getApplicationContext()).hideKeyboard(et);
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放POI检索实例；
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
//        if (mPoiSearch != null) {
//            mPoiSearch.destroy();
//        }
    }
}
