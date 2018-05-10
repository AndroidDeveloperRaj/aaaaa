package com.merrichat.net.activity.meiyu;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.fragments.FragmentGiftDialog;
import com.merrichat.net.activity.meiyu.fragments.Gift;
import com.merrichat.net.activity.meiyu.fragments.view.GiftItemView;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.GiftListsMode;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.CameraUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amssy on 17/12/18.
 * 美遇
 */

public class NiceMeetActivity extends BaseActivity implements SurfaceHolder.Callback {


    @BindView(R.id.my_surfaceView)
    SurfaceView mySurfaceView;

    @BindView(R.id.touch_surface_view)
    RelativeLayout touchSurfaceView;


    /**
     * 关闭按钮
     */
    @BindView(R.id.iv_close)
    ImageView ivClose;

    /**
     * 筛选条件
     */
    @BindView(R.id.tv_filter_conditions)
    TextView tvFilterConditions;

    /**
     * 遇到的人
     */
    @BindView(R.id.tv_encounter)
    TextView tvEncounter;

    /**
     * 搜索
     */
    @BindView(R.id.ll_search_friends)
    LinearLayout llSearchFriends;


    /**
     * 寻找新的朋友
     */
    @BindView(R.id.ll_search_time)
    LinearLayout llSearchTime;


    /**
     * 筛选条件  遇到的人 等布局
     */
    @BindView(R.id.rl_shai_xuan)
    RelativeLayout rlShaiXuan;


    /**
     * 时间
     */
    @BindView(R.id.tv_time)
    TextView tvTime;

    /**
     * 搜索按钮
     */
    @BindView(R.id.tv_search)
    TextView tvSearch;

    /**
     * 相机
     */
    private Camera mCamera;


    /**
     * 1 ：前置摄像头
     * 0： 后置摄像头
     * <p>
     * 默认前置
     */
    private int cameraPosition = 1;


    private SurfaceHolder holder;

    /**
     * 筛选男女、  不知道默认为2
     * 0：女
     * 1：男
     */
    private int filterGender = 2;

    private LocationService locationService;

    /**
     * 精度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;


    /**
     * 搜索按钮
     * true 为搜索
     * <p>
     * false为 停止
     */
    private boolean isSearch = true;

    private Thread threadStarAnim;

    private int recLen = 0;
    // 点击出现星星
    private TopicDetailTouchListener topicDetailTouchListener;

    @BindView(R.id.gift)
    TextView tvGift;

    @BindView(R.id.gift_item_first)
    GiftItemView giftView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏黑色
        StatusBarUtil.setColor(this, getResources().getColor(R.color.black), 255);
        setContentView(R.layout.activity_nice_meet);
        ButterKnife.bind(this);
        CameraUtil.init(this);
        AppManager.getAppManager().addActivity(this);
        initData();

        topicDetailTouchListener = new TopicDetailTouchListener(touchSurfaceView, this);
        touchSurfaceView.setOnTouchListener(topicDetailTouchListener);  //布局可以点击出现小星星

        threadStarAnim = new Thread(new ThreadShow());  //小星星线程
        threadStarAnim.start();

        getGifts();

    }

    private void initData() {
        holder = mySurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this); // 回调接口
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        if (threadStarAnim.isAlive()) {
            threadStarAnim.stop();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(cameraPosition);
            if (holder != null) {
                startPreview(mCamera, holder);
            }
        }
    }


    /**
     * 画布创建
     *
     * @param
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startPreview(mCamera, holder);
    }


    /**
     * 画布改变
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    /**
     * 画布销毁的时候：   释放相机资源
     *
     * @param
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            CameraUtil.getInstance().setCameraDisplayOrientation(this, cameraPosition, camera);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置 相机参数
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported 自动对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        Camera.Size previewSize = CameraUtil.findBestPreviewResolution(camera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 1000);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);

        int picHeight = CameraUtil.screenWidth * previewSize.width / previewSize.height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(CameraUtil.screenWidth, picHeight);
        mySurfaceView.setLayoutParams(params);
    }

    /**
     * 获取Camera实例
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {

        }
        return camera;
    }


    @OnClick({R.id.iv_close, R.id.tv_filter_conditions, R.id.tv_encounter, R.id.ll_search_friends, R.id.gift})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dropOutNiceMeet();
                finish();
                break;
            case R.id.tv_filter_conditions://筛选

                break;
            case R.id.tv_encounter://遇到的人
                GetToast.useString(cnt, "遇到的人");
                startActivity(new Intent(cnt, EncounterActivity.class));
                break;
            case R.id.ll_search_friends://搜索
                GetToast.useString(cnt, "搜索......");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject.put("fromMemberName", UserModel.getUserModel().getRealname());
                    jsonObject.put("fromHeadImgUrl", UserModel.getUserModel().getImgUrl());
                    jsonObject.put("fromGender", UserModel.getUserModel().getGender());
                    jsonObject.put("searchGender", "2");
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("latitude", latitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MerriUtils.getApp().socket.emit("search", jsonObject.toString());//发射搜索事件
                Log.e("@@@", "发射搜索事件......");
                MerriUtils.getApp().socket.on("searchResult", MerriUtils.getApp().searchResult);//监听搜索结果

                rlShaiXuan.setVisibility(View.GONE);
                llSearchTime.setVisibility(View.VISIBLE);
                isSearch = false;
                tvSearch.setText("停止");
                handler.postDelayed(runnable, 1000);
                break;


            case R.id.gift:
                FragmentGiftDialog.newInstance().setOnGridViewClickListener(4, new FragmentGiftDialog.OnGridViewClickListener() {
                    @Override
                    public void click(Gift gift) {
                        giftView.setGift(gift,true);
                        giftView.addNum(1);
                    }
                }).show(getSupportFragmentManager(), "dialog");
                break;
        }
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            tvTime.setText("" + FormatMiss(recLen));
            handler.postDelayed(this, 1000);
        }
    };


    //-------------------------------------

    private List<Gift> gifts = new ArrayList<>();

    // handler类接收数据
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                topicDetailTouchListener.startAnimation(900, 1500);  //动画出现的位置
            }
        }

        ;
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(400);
                    Message msg = new Message();
                    msg.what = 1;
                    handler2.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getGifts() {
        ApiManager.apiService(WebApiService.class).findGift()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<GiftListsMode>() {
                    @Override
                    public void onNext(GiftListsMode giftListsMode) {
                        if (giftListsMode.success) {
                            if (giftListsMode.data.size() > 0) {
                                gifts = giftListsMode.data;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NiceMeetActivity.this, "没有网络了，检查一下吧！", Toast.LENGTH_LONG).show();

                    }
                });


    }
//-------------------------------------


    public String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((MerriApp) getApplication()).locationService;
        locationService.registerListener(merriLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(merriLocationListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    private BDLocationListener merriLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude() + "";
            latitude = location.getLatitude() + "";
        }
    };

    @Override
    public void onBackPressed() {
        dropOutNiceMeet();
        super.onBackPressed();
    }


    /**
     * 退出美遇，发射事件
     */
    private void dropOutNiceMeet() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MerriUtils.getApp().socket.emit("beautifulExit", jsonObject.toString());
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();

        if (threadStarAnim.isAlive()) {
            threadStarAnim.destroy();
        }

    }

}
