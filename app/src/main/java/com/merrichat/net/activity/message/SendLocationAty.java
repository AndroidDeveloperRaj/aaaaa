package com.merrichat.net.activity.message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.util.MapPositioning;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.activity.video.complete.VideoReleaseActivity;
import com.merrichat.net.adapter.MapAdapter;
import com.merrichat.net.model.GroupLocationModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 2018/1/20.
 * 发送位置
 */

public class SendLocationAty extends MerriActionBarActivity {
    private static final int REQUEST_CODE = 3000;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.mMapView)
    MapView mMapView;
    @BindView(R.id.rv_poi)
    RecyclerView rvPoi;
    @BindView(R.id.ib_location)
    ImageButton ibLocation;

    /**
     * 地图放大级别
     */
    private float mapZoom = 19;
    private BaiduMap mBaiduMap;
    private MapAdapter mMapAdapter;
    //    private PoiSearch mPoiSearch;
    private MapPositioning mMapPositioning;
    private GeoCoder mGeoCoder;
    /**
     * 是否是点击列表导致的移动
     */
    private boolean isRvClick = false;
    private ProgressDialog mProgressDialog;
    private List<PoiInfo> poiInfoList;//一般数据
    private String currentCity;
    private double currentLatitude;
    private double currentLongitude;
    private int type = 0;  //获取点击过来的activity
    private GroupLocationModel locationModel = new GroupLocationModel();
    private int activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_location_aty);
        setLeftBack();
        setTitle("位置");
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        activityId = getIntent().getIntExtra("activityId", 0);
        if (activityId == ReleaseGraphicAlbumAty.activityId || activityId == VideoReleaseActivity.activityId) {
        } else {
            setRightText_("发送", getResources().getColor(R.color.normal_red));
        }

        initView();
        initSetting();
        initListener();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        rvPoi.setLayoutManager(new LinearLayoutManager(cnt, LinearLayoutManager.VERTICAL, false));
        poiInfoList = new ArrayList<>();
        mMapAdapter = new MapAdapter(R.layout.item_map, poiInfoList);
        //条目点击移动界面

        mMapAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                isRvClick = true;
                PoiInfo poiInfo = mMapAdapter.getItem(position);
                setNewLatLngZoom(poiInfo.location);
                mMapAdapter.setmIndexTag(position);
                locationModel.address = poiInfo.address;
                locationModel.currentLatitude = poiInfo.location.latitude;
                locationModel.currentLongitude = poiInfo.location.longitude;
                if (activityId == ReleaseGraphicAlbumAty.activityId || activityId == VideoReleaseActivity.activityId) {
                    Intent intent = new Intent();
                    intent.putExtra("locationModel", locationModel);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        rvPoi.setAdapter(mMapAdapter);
    }

    /**
     * 定位用户位置用户位置
     */
    public void initUserLocation() {

        mProgressDialog = ProgressDialog.show(cnt, null, "正在定位,请稍后");
        //开启定位
        mMapPositioning = MapPositioning.getInstance();
        mMapPositioning.setmLocation(new MapPositioning.XbdLocation() {

            @Override
            public void locSuccess(BDLocation location) {
                mProgressDialog.dismiss();
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        //设置精确度
                        .accuracy(0)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0)
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_mark);
                //保存配置，定位图层显示方式，是否允许显示方向信息，用户自定义定位图标
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfiguration(config);
//                //移动到屏幕中心
                LatLng latLng = setLatLng(location.getLatitude(), location.getLongitude());
                setNewLatLngZoom(latLng);

                //设置用户地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = latLng;
                currentCity = location.getCity();
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                currentCity = location.getCity();
                userPoi.address = location.getAddrStr() + location.getLocationDescribe();
                userPoi.name = "我的位置";
                locationModel.address = location.getAddrStr() + location.getLocationDescribe();
                locationModel.currentLatitude = location.getLatitude();
                locationModel.currentLongitude = location.getLongitude();
                mMapAdapter.setmUserPoiInfo(userPoi);

                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            @Override
            public void locFailure(int errorType, String errorString) {
                mProgressDialog.dismiss();
                RxToast.showToast(errorString);
            }
        });
        mMapPositioning.start();
    }

    /**
     * 初始化地图的设置
     */
    private void initSetting() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        //是否允许旋转手势
        uiSettings.setRotateGesturesEnabled(false);
        //是否允许指南针
        uiSettings.setCompassEnabled(false);
        //是否允许俯视手势
        uiSettings.setOverlookingGesturesEnabled(false);
        //是否显示缩放控件
        mMapView.showZoomControls(false);
        //是否显示比例尺
        mMapView.showScaleControl(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //楼快效果
        mBaiduMap.setBuildingsEnabled(true);
        //设置放大缩小级别
        mBaiduMap.setMaxAndMinZoomLevel(21, 10);
        //热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //交通图
//        mBaiduMap.setTrafficEnabled(true);//
        //室内地图
//        mBaiduMap.setIndoorEnable(true);
        //设置是否显示室内图标注, 默认显示
//        mBaiduMap.showMapIndoorPoi(true);
    }

    /**
     * 地图监听
     */
    private void initListener() {
        //地图加载完成回调
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                createSearch();
                initUserLocation();
                ibLocation.setVisibility(View.VISIBLE);
            }

        });
        //单击事件监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //监听地图状态
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (!isRvClick && !isSelectBack) {
                    mapStatus.toString();
                    //得到中心点坐标，开始反地理编码
                    LatLng centerLatLng = mapStatus.target;
                    mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(centerLatLng));
                }
            }
        });
        //监听地图的按下事件
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //如果用户触碰了地图，那么把 isRvClick 还原
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isRvClick = false;
                    isSelectBack = false;
                }
            }
        });

        //右上角按钮点击事件
        setBtnRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    Gson gson = new Gson();
                    Log.e("------>>>>", gson.toJson(locationModel));
                    if (locationModel.currentLongitude != 0 && locationModel.currentLatitude != 0) {
                        PrefAppStore.setGroupLocation(cnt, gson.toJson(locationModel));
                        finish();
                    }
                }

            }
        });

    }

    /**
     * 检索 创建
     */
    private void createSearch() {
//        //兴趣点检索   没有用到
//        mPoiSearch = PoiSearch.newInstance();
//
//        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult result) {
//                //获取POI检索结果
//                mMapAdapter.setDatas(result.getAllPoi(), true);
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult result) {
//                //获取Place详情页检索结果
//            }
//
//            @Override
//            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//                //poi 室内检索结果回调
//            }
//        };
//        //mPoiSearch.searchInCity((new PoiCitySearchOption()).city(“北京”).keyword(“美食”).pageNum(10)).pageNum(10));
//        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        //地里编码
        mGeoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener getGeoListener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    RxToast.showToast("没有检索到结果");
                    return;
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    RxToast.showToast("没有检索到结果");
                    return;
                }
                //设置搜索地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = result.getLocation();
                userPoi.address = result.getSematicDescription();
                userPoi.name = "我的位置";
                locationModel.currentLatitude = result.getLocation().latitude;
                locationModel.currentLongitude = result.getLocation().longitude;
                locationModel.address = result.getSematicDescription();
                mMapAdapter.setmUserPoiInfo(userPoi);
                if (isSelectBack) {
                    setNewLatLngZoom(userPoi.location);
                }

                //获取反向地理编码结果
                List<PoiInfo> poiList = result.getPoiList();
                for (int i = 0; i < poiList.size(); i++) {
                    if (TextUtils.isEmpty(poiList.get(i).address.trim())) {
                        poiList.get(i).address = poiList.get(i).name;
                    }
                }

                mMapAdapter.setDatas(poiList, true);
                rvPoi.scrollToPosition(0);
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(getGeoListener);
    }

    /**
     * 设置xy
     */
    private LatLng setLatLng(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }

    /**
     * 设置标记点的放大级别
     */
    private void setNewLatLngZoom(LatLng latLng) {
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, mapZoom));
    }

    @OnClick({R.id.ll_search, R.id.ib_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                startActivityForResult(new Intent(cnt, SearchPoiAty.class).putExtra("currentCity", currentCity).putExtra("currentLatitude", currentLatitude).putExtra("currentLongitude", currentLongitude), REQUEST_CODE);
                break;
            case R.id.ib_location:
                initUserLocation();
                break;
        }
    }

    boolean isSelectBack;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                isSelectBack = true;
                String address = data.getStringExtra("currentAddress");
                double lon = data.getDoubleExtra("currentLongitude", 0.0);
                double lat = data.getDoubleExtra("currentLatitude", 0.0);
                searchStr(address, lon, lat);
            }
        }
    }


    /**
     * 搜索返回后，需要先搜索
     */
    public void searchStr(String address, double lon, double lat) {
        if (lon > 0 && lat > 0) {
            Logger.e("onActivityResult");
            LatLng latLng = setLatLng(lat, lon);
            currentLatitude = lat;
            currentLongitude = lon;
            //设置搜索地址
            PoiInfo userPoi = new PoiInfo();
            userPoi.location = latLng;
            userPoi.address = address;
            userPoi.name = "我的位置";
            locationModel.currentLatitude = lat;
            locationModel.currentLongitude = lon;
            locationModel.address = address;
            mMapAdapter.setmUserPoiInfo(userPoi);

            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e("onResume");
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapPositioning != null) {
            mMapPositioning.onExit();
        }
        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }
        mMapView.onDestroy();
    }
}
