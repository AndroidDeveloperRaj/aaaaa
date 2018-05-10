package com.merrichat.net.activity.message;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.model.GroupLocationModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 2018/2/5.
 * 展示地图
 */

public class ShowLocationAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.mMapView)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    /**
     * 地图放大级别
     */
    private float mapZoom = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_location_aty);
        ButterKnife.bind(this);
        initView();
        initMap();
    }

    private void initView() {
        tvTitleText.setText("位置信息");
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    private void initMap() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        //关闭一切手势操作
        uiSettings.setAllGesturesEnabled(false);
        //获取是否允许缩放手势返回:是否允许缩放手势
        uiSettings.setZoomGesturesEnabled(true);
        //获取是否允许缩放手势返回:是否允许平移手势
        uiSettings.setScrollGesturesEnabled(true);
        //是否允许旋转手势
        uiSettings.setRotateGesturesEnabled(false);
        //是否允许指南针
        uiSettings.setCompassEnabled(false);
        //是否允许俯视手势
        uiSettings.setOverlookingGesturesEnabled(false);
        //是否显示缩放控件
        mMapView.showZoomControls(true);
        //是否显示比例尺
        mMapView.showScaleControl(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //楼快效果
        mBaiduMap.setBuildingsEnabled(true);
        GroupLocationModel locationModel = new Gson().fromJson(getIntent().getStringExtra("location"), GroupLocationModel.class);
        LatLng southwest = new LatLng(locationModel.currentLatitude, locationModel.currentLongitude);
        MapStatus mMapStatus = new MapStatus.Builder().target(southwest).zoom(mapZoom).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(southwest).icon(bitmap).position(southwest);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
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
        mMapView.onDestroy();
    }
}
