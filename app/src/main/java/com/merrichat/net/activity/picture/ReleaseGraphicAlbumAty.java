package com.merrichat.net.activity.picture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.gson.Gson;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.compress.CompressImageUtil;
import com.jph.takephoto.model.TImage;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.activity.message.ChatAmplificationActivity;
import com.merrichat.net.activity.message.SendLocationAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.editor.VideoMusicActivity;
import com.merrichat.net.adapter.PhotoVideoAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.DraftDaoModel;
import com.merrichat.net.model.DraftModel;
import com.merrichat.net.model.GroupLocationModel;
import com.merrichat.net.model.MemberDraftModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.SelectorLabelModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoReleaseModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MediaFileUtil;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.NoScrollRecyclerView;
import com.merrichat.net.view.PhotoVideoPoPView;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.utils.RxTools.RxActivityTool.activityStack;

/**
 * Created by AMSSY1 on 2017/11/14.
 * <p>
 * 发布图文专辑
 */

public class ReleaseGraphicAlbumAty extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    public final static int activityId = MiscUtil.getActivityId();
    private static final int UPLOAD_SUC = 0x007;
    private static final int UPLOAD_Fail = 0x006;
    private static final int UPLOAD_PROGRESS = 0x005;
    private final int TOUP_TYPE = 0x0001;//上移item标志
    private final int TODOWN_TYPE = 0x002;//下移
    private final int FROM_EDITTEXT = 0x003;//编辑文字内容
    private final int REQUEST_CODE_CHOOSE = 0x004;//添加图片
    private final int REQUEST_CODE_CHOOSE_EDIT = 0x0016;//添加图片
    private final int REQUEST_CODE_MUSIC = 0x0013;
    private final int FROM_ADD_TEXTCONTENT = 0x0015;//添加文字内容
    private final int SHOW_DIALOG = 0x013;//开启导入图片进度弹窗
    private final int CLOSE_DIALOG = 0x014;//关闭导入图片进度弹窗
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    /**
     * title剩余字数
     */
    @BindView(R.id.tv_title_num)
    TextView tvTitleNum;
    /**
     * 位置
     */
    @BindView(R.id.tv_address)
    TextView tvAddress;
    /**
     * 预览图片
     */
    @BindView(R.id.cvv_videoview)
    ImageView cvvVideoview;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_biaoqian_content)
    TextView tvBiaoqianContent;
    @BindView(R.id.rl_biaoqian)
    RelativeLayout rlBiaoqian;
    @BindView(R.id.tv_share_pengyouquan)
    TextView tvSharePengyouquan;
    @BindView(R.id.tv_weixin_haoyou)
    TextView tvWeixinHaoyou;
    @BindView(R.id.dtv_toshare)
    DrawableCenterTextView dtvToshare;
    @BindView(R.id.tv_yinyue_content)
    TextView tvYinyueContent;
    @BindView(R.id.rl_yinyue)
    RelativeLayout rlYinyue;
    @BindView(R.id.rv_receclerView)
    NoScrollRecyclerView rvRececlerView;
    @BindView(R.id.rl_quanxian)
    LinearLayout rlQuanxian;
    String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    @BindView(R.id.tv_gong_kai)
    DrawableCenterTextView tvGongKai;
    @BindView(R.id.tv_hao_you)
    DrawableCenterTextView tvHaoYou;
    @BindView(R.id.tv_si_mi)
    DrawableCenterTextView tvSiMi;
    @BindView(R.id.sv_scrollview)
    ScrollView svScrollView;
    /**
     * 仅陌生
     */
    @BindView(R.id.tv_mo_sheng)
    DrawableCenterTextView tvMoSheng;
    private PhotoVideoAdapter photoVideoAdapter;
    private ArrayList<PhotoVideoModel> photoVideoList;
    private int position = 0;
    private String authority;
    private ArrayList<String> imgPathLists;
    private ArrayList<String> urlsNameList;//图片上传成功后 文件名的集合
    private String fileSuffix;
    private ArrayList<SelectorLabelModel.DataBean> label_list = new ArrayList<>();
    private double latitude;
    private double longitude;
    private String address;
    private boolean noGetLocation = true;
    //定位回调，根据需求获取所需要的参数，下面是所有参数
    private BDLocationListener merriLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                address = location.getCity() + location.getDistrict() + location.getStreet();


                Log.d("LogTest", "纬度：" + latitude + "经度：" + longitude + "地址：" + address);

                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                    noGetLocation = true;
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                    noGetLocation = true;
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                    noGetLocation = true;
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    getLocationFailed();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    getLocationFailed();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    getLocationFailed();
                }
                if (noGetLocation) {
                    //llDingWei.setVisibility(View.VISIBLE);
                    //checkBoxLocation.setChecked(true);
                }
//                tvDingweiAddress.setText(" " + address);//地址
                Logger.e(sb.toString());
            }
        }
    };
    /**
     * 百度定位
     */
    private LocationService locationService;
    private Gson gson;
    private int jurisdiction = 1;//查看权限 0私密 1公开 2 私密 3陌生
    private int quanXianFlag = 0;
    private String classifystr = "";
    private LoadingDialog loadingDialog;
    private String tabText;
    private int editDraftOfPosition;//编辑草稿的位置
    private int activityIdFrom;// 从编辑草稿进入的标致
    private String cover;
    private String videoMusicPath;
    private String audioFileSuffix;//音乐文件后缀名
    private String toShareImage = "";//用于分享的图片Url
    private int REQUEST_CODE_LOCATION = 0x012;
    private int ADD_CONTENT_FROM = -1;//1 item添加 2 footerView 添加
    private CommomDialog dialogPic;
    private CommomDialog dialogMic;
    private int currentNum = 0;
    private int totalNum = 0;
    private LoadingDialog loadingImgDialog;
    private ExecutorService singleExecutor;
    private int loadingDialogTotal = 0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:

                    String title = etTitle.getText().toString().trim();
                    String describe = etContent.getText().toString().trim();
                    String content = "";
                    String coverJson = "";
                    String musicUrl = "";
                    String pictureUrls = "";
                    if (!RxDataTool.isNullString(audioFileSuffix)) {
                        musicUrl = publicImgUrl + audioFileSuffix;
                    }
                    ArrayList<PhotoVideoModel> contentList = new ArrayList<>();
                    for (PhotoVideoModel photoVideoModel : photoVideoList
                            ) {
                        PhotoVideoModel contentModel = new PhotoVideoModel();
                        contentModel.setFlag(photoVideoModel.getFlag());
                        contentModel.setUrl(photoVideoModel.getUrl());
                        contentModel.setText(photoVideoModel.getText());
                        contentModel.setHeight(photoVideoModel.getHeight());
                        contentModel.setWidth(photoVideoModel.getWidth());
                        contentModel.setType(photoVideoModel.getType());
                        contentList.add(contentModel);
                    }
                    for (PhotoVideoModel model : contentList) {
                        String url = model.getUrl();
                        if (!RxDataTool.isNullString(url)) {
                            model.setUrl(publicImgUrl + url.substring(url.lastIndexOf("/") + 1));
                        }
                        if (model.getFlag() == 1) {
                            coverJson = gson.toJson(model);
                        }
                    }
                    content = gson.toJson(contentList);
                    if (urlsNameList.size() > 0) {
                        for (int i = 0; i < urlsNameList.size(); i++) {
                            pictureUrls += "\"" + urlsNameList.get(i) + "\"" + ",";
                        }
                        pictureUrls = "[" + pictureUrls.substring(0, pictureUrls.length() - 1) + "]";
                    }
                    beautyPublish(title, content, describe, coverJson, classifystr, musicUrl, pictureUrls);

                    handled = true;
                    break;
                case UPLOAD_Fail:
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    RxToast.showToast("上传失败，请重试！");
                    handled = true;
                    break;
                case UPLOAD_PROGRESS:
//                    Bundle data = msg.getData();
//                    long currentSize = data.getLong("currentSize");
//                    long totalSize = data.getLong("totalSize");
//                    OSSLog.logDebug("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                    tvProgress.setText("进度：" + (int) ((currentSize * 100) / totalSize));
                    loadingDialog.setLoadingText("音视频文件较大，努力上传中，请稍等~" + "\n" + msg.arg1 + "/" + loadingDialogTotal + "共");

                    handled = true;
                    break;
                case SHOW_DIALOG:
                    loadingImgDialog = new LoadingDialog(ReleaseGraphicAlbumAty.this).setInterceptBack(true);
                    loadingImgDialog.setLoadingText(currentNum + "/" + (totalNum + "共"));
                    loadingImgDialog.show();
                    break;
                case CLOSE_DIALOG:
                    Logger.e("onCompressSuccess……CLOSE_DIALOG", currentNum + "");
                    loadingImgDialog.setLoadingText("正在导入" + currentNum + "/" + (totalNum + "共"));
                    if (currentNum == totalNum) {
                        loadingImgDialog.close();
                        photoVideoAdapter.notifyDataSetChanged();
                    }
                    break;
            }

            return handled;
        }
    });
    private CommomDialog etContentDialog;

    /**
     * 获取图片宽高
     *
     * @param path
     * @return
     */
    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 将Uri集合转换成TImage集合
     *
     * @param urls
     * @return
     */
    public static ArrayList<TImage> getTImagesWithUris(ArrayList<String> urls, TImage.FromType fromType) {
        ArrayList<TImage> tImages = new ArrayList();
        for (String url : urls) {
            TImage tImage = TImage.of(url, fromType);
            tImage.setCompressPath(url);
            tImages.add(tImage);
        }
        return tImages;
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovediotoshare);
        ButterKnife.bind(this);
        authority = MerriApp.getFileProviderName(this);
        EventBus.getDefault().register(this);
        initView();
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 返回键的监听
     */
    @Override
    public void onBackPressed() {
        showExitScreenDialog(ReleaseGraphicAlbumAty.this);
    }

    private void getLocationFailed() {
        noGetLocation = false;
//        RxToast.showToast("定位失败，请重试！");
        address = "";
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        //发布图文 微信分享成功后 返回推荐
        if (myEventBusModel.COLSE_GRAPHICALBUM_ACTIVITY) {
            if (activityStack != null && activityStack.size() > 0) {
                for (int i = 0; i < activityStack.size(); i++) {
                    if (null != activityStack.get(i) && !activityStack.get(i).getLocalClassName().equals("MainActivity")) {
                        activityStack.get(i).finish();
                    }
                }
            }
            activityStack.clear();
            MyEventBusModel mEventBusModel = new MyEventBusModel();
            mEventBusModel.REFRESH_MINE_CIRCLER = true;
            mEventBusModel.REFRESH_MINE_CIRCLER_SHARE = false;
            EventBus.getDefault().post(mEventBusModel);

        }
    }

    private void initView() {
        initTitle();

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 20) {
                    RxToast.showToast("标题最多20个字！");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 20) {
                    String titleText = etTitle.getText().toString().trim();
                    etTitle.setText(titleText.substring(0, 20));
                    etTitle.setSelection(etTitle.getText().toString().trim().length());
                }
                tvTitleNum.setText((20 - etTitle.getText().toString().trim().length()) + "");

            }
        });
        tvGongKai.setSelected(true);
        gson = new Gson();
        photoVideoList = new ArrayList<>();
        urlsNameList = new ArrayList<>();
        Intent intent = getIntent();
        activityIdFrom = intent.getIntExtra("activityId", -1);
        editDraftOfPosition = intent.getIntExtra("editDraftOfPosition", -1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerView.setLayoutManager(layoutManager);
        photoVideoAdapter = new PhotoVideoAdapter(R.layout.item_photovideo, photoVideoList);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(photoVideoAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rvRececlerView);
        rvRececlerView.setAdapter(photoVideoAdapter);
        photoVideoAdapter.setOnItemChildClickListener(this);
        rvRececlerView.setNestedScrollingEnabled(false);
        photoVideoAdapter.addFooterView(getFooterView(), -1, LinearLayout.VERTICAL);
        // 开启拖拽
        photoVideoAdapter.enableDragItem(itemTouchHelper);
        photoVideoAdapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

            }
        });
        if (activityIdFrom == MyDynamicsAty.activityId) {// 我的-动态--草稿
            DraftModel draftContent = (DraftModel) intent.getSerializableExtra("draftContent");
            setDataFromMyDynamicsAty(draftContent);
        } else {
            imgPathLists = intent.getStringArrayListExtra("imgPathList");
//            handler.sendEmptyMessage(SHOW_DIALOG);
            totalNum = imgPathLists.size();
            loadingImgDialog = new LoadingDialog(ReleaseGraphicAlbumAty.this).setInterceptBack(true);
            loadingImgDialog.setLoadingText(currentNum + "/" + (totalNum + "共"));
            loadingImgDialog.setLoadSpeed(LoadingDialog.Speed.SPEED_TWO);
            loadingImgDialog.show();
            singleImgCompress(0, imgPathLists, MerriCameraFunctionAty.activityId);

//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    if (imgPathLists != null && imgPathLists.size() > 0) {
//                        handler.sendEmptyMessage(SHOW_DIALOG);
//                        ArrayList<String> tempList = new ArrayList<>();
//                        totalNum = imgPathLists.size();
//                        for (int i = 0; i < imgPathLists.size(); i++) {
//                            singleImgCompress(imgPathLists.get(i));
////                            tempList.clear();
////                            tempList.add(imgPathLists.get(i));
////                            if (i == 0) {
////                                compressImg(i, tempList, MerriCameraFunctionAty.activityId);
////                            }
////                            compressImg(-1, tempList, MerriCameraFunctionAty.activityId);
////
//                        }
////                compressImg(imgPathLists, MerriCameraFunctionAty.activityId);
//                    }
//                }
//            };
//            handler.postDelayed(runnable, 500);
            //启动图片下载线程
//            runOnQueue(runnable);


/*
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (imgPathLists != null && imgPathLists.size() > 0) {

                        ArrayList<String> tempList = new ArrayList<>();
                        totalNum = imgPathLists.size();
                        handler.sendEmptyMessage(SHOW_DIALOG);
                        for (int i = 0; i < imgPathLists.size(); i++) {
                            tempList.clear();
                            tempList.add(imgPathLists.get(i));
                            if (i == 0) {
                                compressImg(i, tempList, MerriCameraFunctionAty.activityId);
                            }
                            compressImg(-1, tempList, MerriCameraFunctionAty.activityId);

                        }
//                compressImg(imgPathLists, MerriCameraFunctionAty.activityId);
                    }

                }
            }, 500);
*/

        }
    }

    /**
     * 执行单线程列队执行
     */
    public void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    private View getFooterView() {
        View view = getLayoutInflater().inflate(R.layout.item_graphic_album_footer, (ViewGroup) rvRececlerView.getParent(), false);
        final ImageView iv_add_photovideo = (ImageView) view.findViewById(R.id.iv_add_photovideo);
        final LinearLayout ll_add_content = (LinearLayout) view.findViewById(R.id.ll_add_content);
        ImageView iv_add_textcontent = (ImageView) view.findViewById(R.id.iv_add_textcontent);
        ImageView iv_add_piccontent = (ImageView) view.findViewById(R.id.iv_add_piccontent);
        iv_add_photovideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ll_add_content.setVisibility(View.VISIBLE);
                ADD_CONTENT_FROM = 2;

                showPopup(iv_add_photovideo);
            }
        });
        return view;
    }

    /**
     * 从 "我的-动态--草稿"进入 初始化界面
     *
     * @param draftContent
     */
    private void setDataFromMyDynamicsAty(DraftModel draftContent) {
        if (draftContent != null) {
            etTitle.setText(draftContent.getTitle());
            etContent.setText(draftContent.getContenText());
            Glide.with(this).load(draftContent.getCover()).into(cvvVideoview);
            String tabTextMD = draftContent.getTabText();
            classifystr = draftContent.getClassifystr();
            if (tabTextMD != null && tabTextMD.length() > 0) {
                tvBiaoqianContent.setText(tabTextMD.substring(0, tabTextMD.length() - 1));
            }
            jurisdiction = draftContent.getJurisdiction();
            if (jurisdiction == 1) {
                setQuanXian(tvGongKai);
            } else if (jurisdiction == 2) {
                setQuanXian(tvHaoYou);
            } else if (jurisdiction == 0) {
                setQuanXian(tvSiMi);
            } else if (jurisdiction == 3) {
                setQuanXian(tvMoSheng);
            }
            ArrayList<PhotoVideoModel> contents = draftContent.getContent();
            if (contents != null && contents.size() > 0) {
                photoVideoList.addAll(contents);
            }
        }
    }

    private void initTitle() {
        tvTitleText.setText("发布图文专辑");
        tvRight.setVisibility(View.GONE);
        tvRight.setText("保存草稿");
        tvRight.setTextColor(Color.parseColor("#888888"));
    }

    /**
     * 保存草稿箱的 数据格式
     * [{"memberDraftList":[{"classifystr":"","contenText":"","content":[{"type":"0","text":"通途","height":0,"flag":0,"width":0},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514374863594.jpg","type":"0","height":820,"flag":1,"width":820}],"cover":"/data/data/com.merrichat.net/cache/takephoto_cache/1514374863594.jpg","date":"1515122905670","title":"","tabText":"添加标签,","jurisdiction":1,"type":0},{"classifystr":"321495724056576","contenText":"路上注我家3","content":[{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514376992068.jpg","type":"0","text":"口头","height":820,"flag":1,"width":820},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1513406411122.jpg","type":"0","height":770,"flag":0,"width":770},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1513394097886.jpg","type":"0","height":820,"flag":0,"width":820},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1513405770550.jpg","type":"0","height":820,"flag":0,"width":820}],"cover":"/data/data/com.merrichat.net/cache/takephoto_cache/1514376992068.jpg","date":"1515123012420","title":"继续7","tabText":"流行,时尚,","jurisdiction":2,"type":0}],"memberId":"316069722243072"},{"memberDraftList":[{"classifystr":"321495724056576","contenText":"讨厌我","content":[{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514374863594.jpg","type":"0","height":820,"flag":0,"width":820},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514376958339.jpg","type":"0","height":820,"flag":1,"width":820}],"cover":"/data/data/com.merrichat.net/cache/takephoto_cache/1514376958339.jpg","date":"1515122422220","title":"扣图空","tabText":"流行,时尚,","jurisdiction":1,"type":0},{"classifystr":"321495724056576","contenText":"你以为9","content":[{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1513406999017.jpg","type":"0","height":820,"flag":1,"width":820},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514374863594.jpg","type":"0","height":820,"flag":0,"width":820},{"url":"/data/data/com.merrichat.net/cache/takephoto_cache/1514377276041.jpg","type":"0","text":"你现在是","height":820,"flag":0,"width":820}],"cover":"/data/data/com.merrichat.net/cache/takephoto_cache/1513406999017.jpg","date":"1515122511380","title":"利物浦8","tabText":"流行,时尚,","jurisdiction":2,"type":0}],"memberId":"315917552893952"}]
     * <p>
     * 1、 判断数据库中是否存有草稿
     * 2.如果没有，创建MemberDraftModel加入memberDraftList中；如果有判断是否存在当前登录人的草稿 {（1）如果不存在 则创建草稿列表DraftModel加入contentList中
     * （2）存在则将原来的草稿draftModelList全部加入contentList中}
     *
     * @param view
     */
    @OnClick({R.id.iv_back, R.id.tv_right, R.id.rl_biaoqian, R.id.rl_quanxian, R.id.rl_address, R.id.cvv_videoview,
            R.id.rl_yinyue, R.id.tv_share_pengyouquan, R.id.tv_weixin_haoyou, R.id.dtv_toshare,
            R.id.tv_gong_kai, R.id.tv_hao_you, R.id.tv_si_mi, R.id.tv_mo_sheng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_address://添加位置
                Bundle addressBundle = new Bundle();
                addressBundle.putInt("activityId", activityId);
                RxActivityTool.skipActivityForResult(this, SendLocationAty.class, addressBundle, REQUEST_CODE_LOCATION);
                break;
            case R.id.iv_back:
                showExitScreenDialog(ReleaseGraphicAlbumAty.this);
                break;
            case R.id.tv_right:
                String corverUrl = "";//封面路径
                String editMemberId = "";//编辑该登录人的memberId
                int editMemberDraftPositon = 0;//编辑该登录人的位置
                for (PhotoVideoModel photoVideoModel :
                        photoVideoList) {
                    if (photoVideoModel.getFlag() == 1) {
                        corverUrl = photoVideoModel.getUrl();
                    }
                }
                ArrayList<DraftModel> contentList = new ArrayList<>();
                ArrayList<MemberDraftModel> memberDraftList = new ArrayList<>();

                DraftDaoModel draftDaoModel = DraftDaoModel.getDraftDaoModel();
                String tuWenDraftJson = draftDaoModel.getTuWenDraft();
                List<MemberDraftModel> memberDraftDaolList = JSON.parseArray(tuWenDraftJson, MemberDraftModel.class);
                MemberDraftModel memberDraftModel = new MemberDraftModel();
                memberDraftModel.setMemberId(UserModel.getUserModel().getMemberId());
                if (memberDraftDaolList != null && memberDraftDaolList.size() > 0) {
                    memberDraftList.addAll(memberDraftDaolList);

                    for (int i = 0; i < memberDraftDaolList.size(); i++) {
                        MemberDraftModel memberDraftDaoModel = memberDraftDaolList.get(i);
                        if (memberDraftDaoModel.getMemberId().equals(UserModel.getUserModel().getMemberId())) {//判断是否存在当前登录人的草稿
                            editMemberDraftPositon = i;
                            editMemberId = memberDraftDaoModel.getMemberId();
                            ArrayList<DraftModel> draftModelList = memberDraftDaoModel.getMemberDraftList();
                            if (draftModelList != null) {
                                contentList.addAll(draftModelList);
                            }
                            DraftModel draftModel = getDraftModel(corverUrl);
                            if (activityIdFrom == MyDynamicsAty.activityId) {//已有草稿，编辑时替换草稿
                                contentList.set(editDraftOfPosition, draftModel);
                            } else {// 新增草稿
                                contentList.add(0, draftModel);
                            }
                        }
                    }
                }
                if (editMemberId.equals(UserModel.getUserModel().getMemberId())) {//为当前登录用户，在编辑时替换草稿
                    memberDraftModel.setMemberDraftList(contentList);
                    memberDraftList.set(editMemberDraftPositon, memberDraftModel);
                } else {// 新增草稿
                    DraftModel draftModel = getDraftModel(corverUrl);
                    contentList.add(draftModel);
                    memberDraftModel.setMemberDraftList(contentList);
                    memberDraftList.add(0, memberDraftModel);
                }

                String tuWenJson = gson.toJson(memberDraftList);
                draftDaoModel.setTuWenDraft(tuWenJson);
                DraftDaoModel.setDraftDaoModel(draftDaoModel);
                RxToast.showToast("已保存草稿！");
                if (activityIdFrom == MyDynamicsAty.activityId) {
                    finish();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("activityId", activityId);
                    RxActivityTool.skipActivityAndFinish(this, MyDynamicsAty.class, bundle);
                }
                break;
            case R.id.cvv_videoview:
                ArrayList<String> corverUrlList = new ArrayList<>();
                for (int i = 0; i < photoVideoList.size(); i++) {
                    PhotoVideoModel photoVideoModel = photoVideoList.get(i);
                    if (photoVideoModel.flag == 1) {
                        String coverString = photoVideoModel.getUrl();
                        corverUrlList.add(coverString);
                    }

                }
                if (corverUrlList.size() > 0) {
                    Intent intent = new Intent(this, ChatAmplificationActivity.class);
                    intent.putStringArrayListExtra("imgUrl", corverUrlList);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                }
                break;
            case R.id.rl_yinyue://添加音乐
                Bundle bundle = new Bundle();
                bundle.putInt("activityId", ReleaseGraphicAlbumAty.activityId);
                RxActivityTool.skipActivityForResult(this, VideoMusicActivity.class, bundle, REQUEST_CODE_MUSIC);
                break;
            case R.id.rl_biaoqian:
                RxActivityTool.skipActivityForResult(this, SelectorLabelAty.class, ReleaseGraphicAlbumAty.activityId);
                break;
            case R.id.tv_share_pengyouquan:
                break;
            case R.id.tv_weixin_haoyou:
                break;
            case R.id.dtv_toshare:
                /*if (!noGetLocation) {
                    RxToast.showToast("定位失败，请重新定位");
                    return;
                }
                if (RxDataTool.isNullString(classifystr)) {
                    RxToast.showToast("请选择标签");
                    return;
                }*/
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                String titleString = etTitle.getText().toString().trim();
                if (TextUtils.isEmpty(titleString)) {
                    RxToast.showToast("请输入标题后发布！");
                    return;
                }
                String etContentString = etContent.getText().toString().trim();

                if (TextUtils.isEmpty(etContentString)) {
                    etContentDialog = new CommomDialog(cnt, R.style.dialog, "写点内容能够获得更多点赞，赚更多现金", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                showSoftInputFromWindow(ReleaseGraphicAlbumAty.this, etContent);

                                return;
                            } else {
                                if (photoVideoList != null && photoVideoList.size() == 0) {
                                    RxToast.showToast("请编辑内容后发布！");
                                    return;
                                }
                                if (NetUtils.isNetworkAvailable(ReleaseGraphicAlbumAty.this)) {
                                    if (UserModel.getUserModel().getIsLogin()) {
                                        if (photoVideoList.size() == 1) {
                                            //弹出提示框
                                            if (dialogPic != null) {
                                                dialogPic.show();
                                            } else {
                                                dialogPic = new CommomDialog(cnt, R.style.dialog, "一次发多张图效果更好,确定只发一张图吗？", new CommomDialog.OnCloseListener() {
                                                    @Override
                                                    public void onClick(Dialog dialog, boolean confirm) {
                                                        if (confirm) {
                                                            dialog.dismiss();
                                                            toRelease();
                                                        }
                                                    }
                                                }).setNegativeButton("继续编辑").setPositiveButton("直接发布").setTitle("提示");
                                                dialogPic.show();
                                            }
                                        } else if (RxDataTool.isNullString(videoMusicPath)) {
                                            //弹出提示框
                                            if (dialogMic != null) {
                                                dialogMic.show();
                                            } else {
                                                dialogMic = new CommomDialog(cnt, R.style.dialog, "图片是记忆,音乐是灵魂,配上音乐效果才震撼", new CommomDialog.OnCloseListener() {
                                                    @Override
                                                    public void onClick(Dialog dialog, boolean confirm) {
                                                        if (confirm) {
                                                            dialog.dismiss();
                                                            toRelease();
                                                        } else {
                                                            Bundle bundle = new Bundle();
                                                            bundle.putInt("activityId", ReleaseGraphicAlbumAty.activityId);
                                                            RxActivityTool.skipActivityForResult(ReleaseGraphicAlbumAty.this, VideoMusicActivity.class, bundle, REQUEST_CODE_MUSIC);
                                                        }
                                                    }
                                                }).setNegativeButton("添加音乐").setPositiveButton("直接发布").setTitle("提示");
                                                dialogMic.show();
                                            }
                                        } else {
                                            toRelease();
                                        }
                                    } else {
                                        RxActivityTool.skipActivity(ReleaseGraphicAlbumAty.this, LoginActivity.class);
                                    }
                                } else {
                                    RxToast.showToast("当前网络不可用,请检查网络后重试");
                                }
                            }
                        }
                    }).setTitle("温馨提示");
                    etContentDialog.setPositiveButton("现在写");
                    etContentDialog.setNegativeButton("懒得写");
                    etContentDialog.show();
                } else {
                    if (photoVideoList != null && photoVideoList.size() == 0) {
                        RxToast.showToast("请编辑内容后发布！");
                        return;
                    }
                    if (NetUtils.isNetworkAvailable(ReleaseGraphicAlbumAty.this)) {
                        if (UserModel.getUserModel().getIsLogin()) {
                            if (photoVideoList.size() == 1) {
                                //弹出提示框
                                if (dialogPic != null) {
                                    dialogPic.show();
                                } else {
                                    dialogPic = new CommomDialog(cnt, R.style.dialog, "一次发多张图效果更好,确定只发一张图吗？", new CommomDialog.OnCloseListener() {
                                        @Override
                                        public void onClick(Dialog dialog, boolean confirm) {
                                            if (confirm) {
                                                dialog.dismiss();
                                                toRelease();
                                            }
                                        }
                                    }).setNegativeButton("继续编辑").setPositiveButton("直接发布").setTitle("提示");
                                    dialogPic.show();
                                }
                            } else if (RxDataTool.isNullString(videoMusicPath)) {
                                //弹出提示框
                                if (dialogMic != null) {
                                    dialogMic.show();
                                } else {
                                    dialogMic = new CommomDialog(cnt, R.style.dialog, "图片是记忆,音乐是灵魂,配上音乐效果才震撼", new CommomDialog.OnCloseListener() {
                                        @Override
                                        public void onClick(Dialog dialog, boolean confirm) {
                                            if (confirm) {
                                                dialog.dismiss();
                                                toRelease();
                                            } else {
                                                Bundle bundle = new Bundle();
                                                bundle.putInt("activityId", ReleaseGraphicAlbumAty.activityId);
                                                RxActivityTool.skipActivityForResult(ReleaseGraphicAlbumAty.this, VideoMusicActivity.class, bundle, REQUEST_CODE_MUSIC);
                                            }
                                        }
                                    }).setNegativeButton("添加音乐").setPositiveButton("直接发布").setTitle("提示");
                                    dialogMic.show();
                                }
                            } else {
                                toRelease();
                            }
                        } else {
                            RxActivityTool.skipActivity(ReleaseGraphicAlbumAty.this, LoginActivity.class);
                        }
                    } else {
                        RxToast.showToast("当前网络不可用,请检查网络后重试");
                    }
                }
                break;
            case R.id.rl_quanxian:
                break;
            case R.id.tv_gong_kai:
                jurisdiction = 1;
                setQuanXian(tvGongKai);
                break;
            case R.id.tv_hao_you:
                jurisdiction = 2;
                setQuanXian(tvHaoYou);
                break;
            case R.id.tv_si_mi:
                jurisdiction = 0;
                setQuanXian(tvSiMi);
                break;
            case R.id.tv_mo_sheng://仅陌生
                jurisdiction = 3;
                setQuanXian(tvMoSheng);
                break;
        }

    }

    /**
     * 调上传文件接口
     */
    private void toRelease() {

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < photoVideoList.size(); i++) {
            urls.add(photoVideoList.get(i).getUrl());
            if (photoVideoList.get(i).getFlag() == 1) {
                cover = photoVideoList.get(i).getUrl();
            }
        }

        urlsNameList.clear();
        if (!RxDataTool.isNullString(videoMusicPath)) {
            urls.add(videoMusicPath);
        }
//                setData(urls);
        loadingDialogTotal = urls.size();
        loadingDialog = new LoadingDialog(this).setLoadingText("音视频文件较大，努力上传中，请稍等~" + "\n" + "0/" + loadingDialogTotal + "共").setInterceptBack(true);
        loadingDialog.show();
        ossUpload(urls);
    }

    private void setData(ArrayList<String> urls) {
        VideoReleaseModel videoReleaseModel = new VideoReleaseModel(cover, "", etTitle.getText().toString(), "", UserModel.getUserModel().getMobile(), "", classifystr, UserModel.getUserModel().getMemberId(), UserModel.getUserModel().getGender(), UserModel.getUserModel().getRealname(), address, UserModel.getUserModel().getImgUrl(), "", "", String.valueOf(longitude), String.valueOf(latitude), jurisdiction, 1, -1, 0, "", 0, 0, etContent.getText().toString(), 0, urls, photoVideoList, videoMusicPath);
        //发送广播 刷新评论数据
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.FRIEND_RELEASE_TUWEN = true;
        myEventBusModel.videoReleaseModel = videoReleaseModel;
        EventBus.getDefault().post(myEventBusModel);
        startActivity(new Intent(ReleaseGraphicAlbumAty.this, MainActivity.class));
    }

    /**
     * 获取 草稿model
     *
     * @param corverUrl
     * @return
     */
    private DraftModel getDraftModel(String corverUrl) {
        DraftModel draftModel = new DraftModel();
        draftModel.setTitle(etTitle.getText().toString().trim());
        draftModel.setContenText(etContent.getText().toString().trim());
        draftModel.setClassifystr(classifystr);
        draftModel.setTabText(tvBiaoqianContent.getText().toString().trim() + ",");
        draftModel.setJurisdiction(jurisdiction);
        draftModel.setContent(photoVideoList);
        draftModel.setDate(System.currentTimeMillis() + "");
        draftModel.setCover(corverUrl);
        return draftModel;
    }

    private void showExitScreenDialog(Context context) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("是否放弃发布图文?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                RxFileTool.cleanExternalCache(ReleaseGraphicAlbumAty.this);//发布成功后，清除压缩后的产生的缓存
                                //处理监听事件
                                finish();
                            }
                        });
                    }
                });
    }

    private void setQuanXian(View view) {
        tvGongKai.setSelected(false);
        tvHaoYou.setSelected(false);
        tvSiMi.setSelected(false);
        tvMoSheng.setSelected(false);
        view.setSelected(true);
    }

    /**
     * 阿里云OSS上传（默认是异步多文件上传）
     *
     * @param urls
     */
    private void ossUpload(final List<String> urls) {

        if (urls.size() <= 0) {
            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
            return;// 这个return必须有，否则下面报越界异常，原因自己思考下哈
        }
        final String url = urls.get(0);
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(urls);
            return;
        }

        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(urls);
            return;
        }
        boolean audioFileType = MediaFileUtil.isAudioFileType(url);//判断是否是音频文件

        // 文件后缀
        fileSuffix = "";
        if (file.isFile()) {
            // 获取文件后缀名
            if (audioFileType) {
                audioFileSuffix = file.getName();
                fileSuffix = audioFileSuffix;
                Logger.e("audioFileSuffix：：：：", audioFileSuffix);

            } else {
                fileSuffix = file.getName();
                Logger.e("imgFileSuffix：：：：", fileSuffix);
            }

        }
        // 文件标识符objectKey
        final String objectKey = "alioss_" + System.currentTimeMillis() + fileSuffix;
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, fileSuffix, url);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    urls.remove(0);
                    Logger.e("onSuccess：：：：", "onSuccess::上传成功！");
                    if (audioFileSuffix == null || !fileSuffix.equals(audioFileSuffix)) {
                        urlsNameList.add(publicImgUrl + fileSuffix);
                    }
                    if (!RxDataTool.isNullString(cover)) {
                        File file = new File(cover);
                        if (file.isFile()) {
                            // 获取文件后缀名
                            toShareImage = publicImgUrl + file.getName();
                        }
                    }
                    Message message = new Message();
                    message.arg1 = loadingDialogTotal - urls.size();
                    message.what = UPLOAD_PROGRESS;
                    handler.sendMessage(message);
                    if (urls.size() == 0) {
                        handler.sendEmptyMessage(UPLOAD_SUC);
                    }
                    ossUpload(urls);// 递归同步效果
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    Logger.e("onFailure：：：：", "onFailure::上传失败！");
                    handler.sendEmptyMessage(UPLOAD_Fail);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                }
            });
        }
    }

    /**
     * 发布图文专辑
     */
    private void beautyPublish(String title, String content, final String describe, String coverJson, String classifystr,
                               String musicUrl, String pictureUrls) {
        OkGo.<String>post(Urls.BEAUTY_PULISH)
                .tag(this)
                .params("title", title)
                .params("content", content)
                .params("describe", describe)
                .params("phone", UserModel.getUserModel().getMobile())
                .params("cover", coverJson)
                .params("classifystr", classifystr)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("gender", UserModel.getUserModel().getGender())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("address", address)
                .params("memberImage", UserModel.getUserModel().getImgUrl())
                .params("musicUrl", musicUrl)
                .params("videoUrl", "")
                .params("longitude", longitude)
                .params("latitude", latitude)
                .params("jurisdiction", jurisdiction)
                .params("flag", 1)//日志标识 1图文专辑 2视频 3照片 4录像
                .params("isBlack", -1)//是否举报 -1:未举报 ,0:举报待审核 ,1:举报通过 ,2:举报失败
                .params("isDelete", 0)//是否删除 0否 1是
//                .params("createTimes", createTimes)
//                .params("likeCounts", likeCounts)
//                .params("shareCounts", shareCounts)
//                .params("commentCounts", commentCounts)
//                .params("collectCounts", collectCounts)
                .params("pictureUrls", pictureUrls)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (loadingDialog != null) {
                            loadingDialog.close();
                        }
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null) {
                                        if (data.optBoolean("result")) {
                                            String id = data.optString("id");
                                            RxToast.showToast("发布成功！");
                                            loadingDialog.close();
                                            //发送广播 关闭界面
                                            MyEventBusModel myEventBusModel = new MyEventBusModel();
                                            //关闭拍摄界面
                                            myEventBusModel.CLOSE_VIDEO_ACTIVITY = true;
                                            //分享需要的参数（CircleFriendsFragment需要）
                                            myEventBusModel.REFRESH_MINE_CIRCLER = true;
                                            //弹出分享框
                                            myEventBusModel.REFRESH_MINE_CIRCLER_SHARE = true;
                                            myEventBusModel.id = id;
                                            myEventBusModel.title = etTitle.getText().toString();
                                            myEventBusModel.content = etContent.getText().toString();
                                            myEventBusModel.imageUrl = toShareImage;
                                            EventBus.getDefault().post(myEventBusModel);
                                            RxActivityTool.skipActivity(ReleaseGraphicAlbumAty.this, MainActivity.class);
                                            finish();
//                                            getPromoQRcode(id, UserModel.getUserModel().getMemberId());

                                        } else {
                                            RxToast.showToast(data.optString("msg"));
                                        }
                                    } else {
                                        RxToast.showToast("发布失败！");
                                    }
                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (loadingDialog != null) {
                            loadingDialog.close();
                        }
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcode(String id, String toMemberId) {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", id)
                .params("atlMemberId", toMemberId)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    String shareUrl = data.optJSONObject("data").getString("url");
                                    Intent intent = new Intent(ReleaseGraphicAlbumAty.this, ShareToActivity.class);
                                    intent.putExtra("ShareUrl", "" + shareUrl);
                                    intent.putExtra("shareImage", "" + toShareImage);
                                    intent.putExtra("activityId", "" + activityId);
                                    intent.putExtra("title", "" + etTitle.getText().toString().trim());
                                    intent.putExtra("content", "" + etContent.getText().toString().trim());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_dialog_bottom_in, 0);
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        Toast.makeText(this, "init Samples fail", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        this.position = position;
        switch (view.getId()) {
            case R.id.iv_delete_item:// 删除后 重新设置封面
//                photoVideoAdapter.remove(position);
//                photoVideoAdapter.notifyItemRemoved(position);
                int flag = photoVideoList.get(position).getFlag();//删除的是否是封面的标记 1 封面 0 不是封面
                photoVideoList.remove(position);
                if (flag == 1) {
                    if (photoVideoList.size() > 0) {
                        for (int i = 0; i < photoVideoList.size(); i++) {
                            if (!RxDataTool.isNullString(photoVideoList.get(i).getUrl())) {
                                photoVideoList.get(i).setFlag(1);
                                Glide.with(ReleaseGraphicAlbumAty.this).load(photoVideoList.get(i).getUrl()).into(cvvVideoview);
                                break;
                            }
                        }
                    }
                }

                photoVideoAdapter.notifyDataSetChanged();
                break;
            case R.id.rl_img://编辑图片
                Matisse.from(ReleaseGraphicAlbumAty.this)
                        .choose(MimeType.ofImage(), false)
                        .showSingleMediaType(true)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, authority))
                        .maxSelectable(1)
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.dp120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .theme(R.style.Matisse_MerriChat)

                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE_EDIT);
                break;
            case R.id.iv_add_photovideo:
                View llAddContent = photoVideoAdapter.getViewByPosition(rvRececlerView, position, R.id.iv_add_photovideo);
                ADD_CONTENT_FROM = 1;
                showPopup(llAddContent);
//                View llAddContent = photoVideoAdapter.getViewByPosition(rvRececlerView,position, R.id.ll_add_content);
//                llAddContent.setVisibility(View.VISIBLE);
//                photoVideoAdapter.notifyItemChanged(position);
                break;
            case R.id.iv_toup:
                moveItem(TOUP_TYPE, position);
                break;
            case R.id.iv_todown:
                moveItem(TODOWN_TYPE, position);
                break;
            case R.id.tv_textcontent:
                Bundle bundle = new Bundle();
                bundle.putString("editContentText", photoVideoList.get(position).getText());
                RxActivityTool.skipActivityForResult(this, PhotoVideoEditTextAty.class, bundle, FROM_EDITTEXT);
                break;
            case R.id.iv_check_shefengmian:
                for (int i = 0; i < photoVideoList.size(); i++) {
                    if (i == position) {
                        if (RxDataTool.isNullString(photoVideoList.get(i).getUrl())) {
                            RxToast.showToast("无图片，请选择其他图片作为封面");
                            return;
                        }
                        if (photoVideoList.get(i).getFlag() == 0) {
                            photoVideoList.get(i).setFlag(1);
                        }
                        Glide.with(ReleaseGraphicAlbumAty.this).load(photoVideoList.get(i).getUrl()).into(cvvVideoview);
                    } else {
                        photoVideoList.get(i).setFlag(0);
                    }
                }
                photoVideoAdapter.notifyDataSetChanged();

                break;
        }
    }

    /**
     * 添加文字 图片弹框
     *
     * @param llAddContent
     */
    private void showPopup(View llAddContent) {
        final PhotoVideoPoPView titlePopup = new PhotoVideoPoPView(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.popupwindow_addcontent_photovideo);
        titlePopup.setPicContentOnclickListener(new PhotoVideoPoPView.PicContentOnclickListener() {
            @Override
            public void picContentOnclickListener() {
                Matisse.from(ReleaseGraphicAlbumAty.this)
                        .choose(MimeType.ofImage(), false)
                        .showSingleMediaType(true)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, authority))
                        .maxSelectable(9)
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.dp120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .theme(R.style.Matisse_MerriChat)

                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });
        titlePopup.setTextContentOnclickListener(new PhotoVideoPoPView.TextContentOnclickListener() {
            @Override
            public void textContentOnclickListener() {
                RxActivityTool.skipActivityForResult(ReleaseGraphicAlbumAty.this, PhotoVideoEditTextAty.class, FROM_ADD_TEXTCONTENT);
            }
        });

        titlePopup.show(llAddContent, 250);
    }

    /**
     * 上移下移item
     *
     * @param type
     * @param position
     */
    public void moveItem(int type, int position) {
        int from = position;
        int to;
        if (type == TOUP_TYPE) {
            to = position - 1;
        } else {
            to = position + 1;
        }

        if (inRange(from) && inRange(to)) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(photoVideoList, i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(photoVideoList, i, i - 1);
                }
            }
            photoVideoAdapter.notifyItemMoved(from, to);
        } else {
            if (type == TOUP_TYPE) {
                RxToast.showToast("已到最顶部！");
            } else {
                RxToast.showToast("已到最底部！");

            }
        }
    }

    private boolean inRange(int position) {
        return position >= 0 && position < photoVideoList.size();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_EDITTEXT) {// 编辑文字
            if (data != null) {
                String editContentText = data.getStringExtra("editContentText");
                photoVideoList.get(position).setText(editContentText);
                photoVideoAdapter.notifyItemChanged(position);
            }
        }
        if (requestCode == FROM_ADD_TEXTCONTENT) {//添加文字
            if (data != null) {
                String editContentText = data.getStringExtra("editContentText");
                if (ADD_CONTENT_FROM == 1) {
                    photoVideoList.add(position, new PhotoVideoModel("0", 0, editContentText));
                } else if (ADD_CONTENT_FROM == 2) {
                    photoVideoList.add(new PhotoVideoModel("0", 0, editContentText));
                }
                photoVideoAdapter.notifyDataSetChanged();
                if (ADD_CONTENT_FROM == 2) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            svScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        }
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {//添加图片
            List<String> result = Matisse.obtainPathResult(data);
            compressImg((ArrayList<String>) result, REQUEST_CODE_CHOOSE);
        }
        if (requestCode == REQUEST_CODE_CHOOSE_EDIT && data != null) {
            List<String> result = Matisse.obtainPathResult(data);
            compressImg((ArrayList<String>) result, REQUEST_CODE_CHOOSE_EDIT);


        }
        if (requestCode == ReleaseGraphicAlbumAty.activityId) {//选择标签
            if (data != null) {
                tabText = "";
                classifystr = "";
                label_list = (ArrayList<SelectorLabelModel.DataBean>) data.getSerializableExtra("label_list");
                if (label_list != null && label_list.size() > 0) {
                    for (int i = 0; i < label_list.size(); i++) {
                        if (label_list.get(i).isChecked()) {
                            tabText += label_list.get(i).getLable() + ",";
                            classifystr = "" + label_list.get(i).getLableId();//上传标签
                        }
                    }
                    if (tabText.length() > 0) {
                        tvBiaoqianContent.setText(tabText.substring(0, tabText.length() - 1));
                    }
                }

            }
        }
        if (requestCode == REQUEST_CODE_MUSIC) {//选择音乐、
            if (data != null) {
                videoMusicPath = data.getStringExtra("VideoMusic");
                String fileName = data.getStringExtra("fileName");
                if (!RxDataTool.isNullString(fileName)) {
                    tvYinyueContent.setText(fileName);
                }
            }
        }
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK && data != null) {
                GroupLocationModel locationModel = (GroupLocationModel) data.getSerializableExtra("locationModel");
                tvAddress.setText(locationModel.address);
                latitude = locationModel.currentLatitude;
                longitude = locationModel.currentLongitude;
            }
        }
    }

    private void singleImgCompress(final int type, final ArrayList<String> urlsList, final int fromType) {
        //图片压缩
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(250 * 1024)
                .setMaxPixel(800)
                .enableReserveRaw(true)
                .create();
        new CompressImageUtil(this, config).compress(urlsList.get(0), new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(String compressPath) {
                currentNum++;
                Logger.e("onCompressSuccess", compressPath);
                Logger.e("onCompressSuccess……currentNum", currentNum + "");
                int[] imageWidthHeight = getImageWidthHeight(urlsList.get(0));
                if (type == 0 && fromType == MerriCameraFunctionAty.activityId) {//如果从MerriCameraFunctionAty页面传递过来的图片 默认第一条为封面
                    photoVideoList.add(new PhotoVideoModel(compressPath, "0", 1, imageWidthHeight[0], imageWidthHeight[1]));
                    Glide.with(ReleaseGraphicAlbumAty.this).load(compressPath).into(cvvVideoview);

                } else if (fromType == REQUEST_CODE_CHOOSE) {//添加图片时
                    if (ADD_CONTENT_FROM == 1) {
                        photoVideoList.add(position, new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                    } else if (ADD_CONTENT_FROM == 2) {
                        photoVideoList.add(new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                    }
                } else if (fromType == REQUEST_CODE_CHOOSE_EDIT) {//编辑图片时
                    PhotoVideoModel photoVideoModel = photoVideoList.get(position);
                    if (photoVideoModel.getFlag() == 1) {//如果是封面，选择后重新设置预览效果
//                            Glide.with(ReleaseGraphicAlbumAty.this).load(compressPath).into(cvvVideoview);
                    }
                    photoVideoModel.setWidth(imageWidthHeight[0]);
                    photoVideoModel.setHeight(imageWidthHeight[1]);
                    photoVideoModel.setUrl(compressPath);

                } else {
                    photoVideoList.add(new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                }
                handler.sendEmptyMessage(CLOSE_DIALOG);
                urlsList.remove(0);
                if (imgPathLists.size() > 0) {
                    singleImgCompress(-1, urlsList, fromType);
                }


            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                Logger.e("onCompressFailed", imgPath);

            }
        });
    }

    /**
     * 批量压缩图片
     *
     * @param urlsList
     */
    private void compressImg(final ArrayList<String> urlsList, final int fromType) {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(200 * 1024)
                .enableReserveRaw(true)
                .create();
        CompressImageImpl.of(this, config, getTImagesWithUris(urlsList, TImage.FromType.OTHER), new CompressImage.CompressListener() {
            @Override
            public void onCompressSuccess(ArrayList<TImage> images) {
                ++currentNum;
                handler.sendEmptyMessage(CLOSE_DIALOG);
                for (int i = 0; i < urlsList.size(); i++) {
                    int[] imageWidthHeight = getImageWidthHeight(urlsList.get(i));
                    String compressPath = images.get(i).getCompressPath();
                    if (fromType == MerriCameraFunctionAty.activityId && i == 0) {//如果从MerriCameraFunctionAty页面传递过来的图片 默认第一条为封面
                        photoVideoList.add(new PhotoVideoModel(compressPath, "0", 1, imageWidthHeight[0], imageWidthHeight[1]));
                        Glide.with(ReleaseGraphicAlbumAty.this).load(compressPath).into(cvvVideoview);

                    } else if (fromType == REQUEST_CODE_CHOOSE) {//添加图片时
                        if (ADD_CONTENT_FROM == 1) {
                            photoVideoList.add(position, new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                        } else if (ADD_CONTENT_FROM == 2) {
                            photoVideoList.add(new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                        }
                    } else if (fromType == REQUEST_CODE_CHOOSE_EDIT) {//编辑图片时
                        PhotoVideoModel photoVideoModel = photoVideoList.get(position);
                        if (photoVideoModel.getFlag() == 1) {//如果是封面，选择后重新设置预览效果
//                            Glide.with(ReleaseGraphicAlbumAty.this).load(compressPath).into(cvvVideoview);
                        }
                        photoVideoModel.setWidth(imageWidthHeight[0]);
                        photoVideoModel.setHeight(imageWidthHeight[1]);
                        photoVideoModel.setUrl(compressPath);

                    } else {
                        photoVideoList.add(new PhotoVideoModel(compressPath, "0", 0, imageWidthHeight[0], imageWidthHeight[1]));
                    }
                    Logger.e("onCompressSuccess", compressPath);
                }
                photoVideoAdapter.notifyDataSetChanged();
                if (ADD_CONTENT_FROM == 2) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            svScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }

            }

            @Override
            public void onCompressFailed(ArrayList<TImage> images, String msg) {
                Logger.e("onCompressFailed", msg);

            }
        }).compress();
    }
}
