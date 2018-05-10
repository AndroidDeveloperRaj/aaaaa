package com.merrichat.net.activity.message.util;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.RxTools.RxToast;

/**
 * Created by amssy on 2018/1/22.
 */

public class MapPositioning {
    private static MapPositioning mMapPositioning = null;
    private LocationService locationService;
    public static MapPositioning getInstance() {
        if (mMapPositioning == null) {
            mMapPositioning = new MapPositioning();
        }
        return mMapPositioning;
    }

    private MapPositioning() {
        locationService =  new LocationService(MerriApp.mContext);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
        //注册监听函数
        locationService.registerListener(new BDLocationListener() {

            /**
             * 接受位置内部类
             */
            @Override
            public void onReceiveLocation(BDLocation location) {
                locationService.stop();

                //定位成功
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation) {
                    if (mXLocation != null) {
                        mXLocation.locSuccess(location);
                    }
                }
                //定位失败
                if (location.getLocType() == BDLocation.TypeServerError || location.getLocType() == BDLocation.TypeNetWorkException || location.getLocType() == BDLocation.TypeCriteriaException) {
                    if (mXLocation != null) {
                        mXLocation.locFailure(location.getLocType(), "定位失败,请检查网络");
                    }
                }

                //Receive Location
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());


                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    sb.append("\nspeed : ");
                    // 单位：公里每小时
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    // 单位：米
                    sb.append(location.getAltitude());
                    sb.append("\ndirection : ");
                    // 单位度
                    sb.append(location.getDirection());
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    //运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    RxToast.showToast("定位失败，请重试！");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    RxToast.showToast("定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    RxToast.showToast("定位失败，请检查是否是飞行模式");
                }
                sb.append("\nlocationdescribe : ");
                // 位置语义化信息
                sb.append(location.getLocationDescribe());
//                List<Poi> list = location.getPoiList();// POI数据
                Log.i("BaiduLocationApiDem", sb.toString());
            }
        });
    }

    /**
     * 开始定位
     */
    public MapPositioning start() {
        if (locationService != null) {
            locationService.start();
        }
        return this;
    }

    public void onExit() {
        if (mXLocation != null) {
            mXLocation = null;
        }
    }


    private XbdLocation mXLocation;

    public void setmLocation(XbdLocation location) {
        this.mXLocation = location;
    }

    public interface XbdLocation {
        /**
         * 定位成功
         *
         * @param location 位置信息
         */
        void locSuccess(BDLocation location);

        /**
         * 定位错误
         *
         * @param errorType   错误类型
         * @param errorString 错误提示
         */
        void locFailure(int errorType, String errorString);
    }

}
