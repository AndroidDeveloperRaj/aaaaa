package com.merrichat.net.activity.video.complete;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.merrifunction.RecordVideoAty;
import com.merrichat.net.activity.message.SendLocationAty;
import com.merrichat.net.activity.picture.SelectorLabelAty;
import com.merrichat.net.activity.video.editor.VideoEditorActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupLocationModel;
import com.merrichat.net.model.SelectorLabelModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoReleaseModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.ossfile.STSGetter;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxImageTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.common.internal.ByteStreams.copy;
import static com.merrichat.net.ossfile.Config.endpoint;
import static com.merrichat.net.utils.FileUtils.getCurrentDate;

/**
 * 发布视频
 * Created by amssy on 17/11/7.
 */
public class VideoReleaseActivity extends BaseActivity {
    public final static int activityId = MiscUtil.getActivityId();
    /**
     * 取消
     */
    @BindView(R.id.tv_left)
    TextView tvClose;
    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitle;
    /**
     * 保存草稿
     */
    @BindView(R.id.tv_right)
    TextView tvFinish;
    /**
     * 浏览效果
     */
    @BindView(R.id.tv_show)
    TextView tvShow;
    /**
     * 标题
     */
    @BindView(R.id.edit_title)
    EditText editTitle;

    @BindView(R.id.lin_top)
    LinearLayout linTop;
    /**
     * 内容
     */
    @BindView(R.id.edit_content)
    EditText editContent;
    /**
     * 地址
     */
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    /**
     * 标签
     */
    @BindView(R.id.rel_add_label)
    RelativeLayout relAddLabel;
    /**
     * 底部完成按钮
     */
    @BindView(R.id.rel_complete)
    RelativeLayout relComplete;
    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.imageView_cover)
    ImageView imageViewCover;
    /**
     * 权限选择
     */
    @BindView(R.id.radio_public)
    RadioButton radioPublic;
    @BindView(R.id.radio_privacy)
    RadioButton radioPrivacy;
    @BindView(R.id.radio_friend)
    RadioButton radioFriend;
    @BindView(R.id.radio_stranger)
    RadioButton radioStranger;
    /**
     * 同步分享
     */
    @BindView(R.id.radio_share_circle)
    CheckBox radioShareCircle;
    @BindView(R.id.radio_share_friend)
    CheckBox radioShareFriend;
    @BindView(R.id.radio_share_sina)
    CheckBox radioShareSina;
    @BindView(R.id.radio_share_qq)
    CheckBox radioShareQq;
    @BindView(R.id.checkBox_location)
    CheckBox checkBoxLocation;
    @BindView(R.id.rel_cover)
    RelativeLayout relCover;

    /**
     * title剩余字数
     */
    @BindView(R.id.tv_title_num)
    TextView tvTitleNum;
    @BindView(R.id.cover)
    SimpleDraweeView sdcover;
    private String videoPath;
    private String address;
    private String longitude = "0";//经度
    private String latitude = "0";//纬度
    private int RESULT_LABEL_POSITION = 1;
    private String title = "";//标题
    private String content = "";//日志内容 json [{url:url(图片或视频地址),type:1(0图片 1视频),text:'文字内容',flag:0(日志是否为封面 0是 1不是)}]
    private String phone = "";
    private String cover = "";//日志封面信息json {url:url(图片或视频地址),type:1(0图片 1视频),text:'文字内容',flag:1(该照片微封面),width:12(宽),height:12(高)}
    private String classifystr;//日志分类(标签) 日志分类 （"1,2,3"）
    private String memberId;
    private String gender;//日志原创人性别 0 男 1女 2不限
    private String musicUrl;
    private String videoUrl;
    private Double position;//位置信息
    private int jurisdiction = 1;//查看权限 0私密 1公开 2 好友 3陌生人
    private int flag;//日志标识 1图文专辑 2视频 3照片 4录像
    private int isBlack;//是否举报 -1:未举报 ,0:举报待审核 ,1:举报通过 ,2:举报失败
    private int isDelete;//是否删除 0否 1是
    private String pictureUrls;//上传照片的URL ["assd","asdaxxx","cdfsdd"]
    private ArrayList<SelectorLabelModel.DataBean> selectorLabelList;
    private String label = "";
    private Bitmap bitmap;
    private String outFile = Environment.getExternalStorageDirectory() + "/MerriChat/";
    private String name;//保存图片到本地的路径名
    private String imageUrl;//图片封面链接
    private int width;//封面图的宽
    private int height;//封面图的高
    private String memberName = "";//日志原创人姓名
    private String memberImage = "";//头像

    private int shareTo = 1;
    private ProgressDialog dialog_load;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //显示图片
                    imageViewCover.setImageURI(Uri.fromFile(new File(name)));
                    break;
                case 2:
                    imageViewCover.setImageURI(Uri.fromFile(new File(name)));
                    break;
                case 3:
                    if (dialog_load != null) {
                        RxToast.showToast("发布视频失败,请重试");
                        dialog_load.dismiss();
                    }
                    break;
                case 4:
                    int progress = (int) msg.obj;
                    if (progress < 99) {
                        dialog_load.setMessage("正在发布视频..." + progress + "%");
                    } else if (progress == 100) {
                        dialog_load.setMessage("正在发布视频..." + "98%");
                    } else if (progress == 101) {
                        dialog_load.setMessage("正在发布视频..." + "99%");
                    } else if (progress == 102) {
                        dialog_load.setMessage("正在发布视频..." + "100%");
                    }
                    break;
                case 5:
                    Uri uri = Uri.parse(imageUrl);
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                            .build();
                    sdcover.setController(draweeController);
                    break;
            }
        }
    };
    private int isReleaseFailure = 1;

    private LocationService locationService;
    //定位回调，根据需求获取所需要的参数，下面是所有参数
    private BDLocationListener merriLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                if (TextUtils.isEmpty(location.getCity()) && TextUtils.isEmpty(location.getDistrict()) && TextUtils.isEmpty(location.getStreet())) {
                    address = "";
                } else {
                    address = location.getCity() + location.getDistrict() + location.getStreet();
                }
            }
            //RxToast.showToast("定位失败，请打开权限管理开启定位权限");

        }
    };
    private int REQUEST_CODE_LOCATION = 0x012;
    private Bitmap[] list = new Bitmap[6];
    private List<String> listUri = new ArrayList<>();
    private String mStorePath;
    private CommomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_video_release);
        ButterKnife.bind(this);
        //设置TITLE
        initTitle();
        initGetIntent();
        initView();
        initRadioCheck();
    }

    private void initRadioCheck() {
        //朋友圈
        radioShareCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shareTo = 1;
                    radioShareCircle.setChecked(true);
                    radioShareFriend.setChecked(false);
                    radioShareSina.setChecked(false);
                    radioShareQq.setChecked(false);
                }
            }
        });
        //好友
        radioShareFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shareTo = 2;
                    radioShareFriend.setChecked(true);
                    radioShareCircle.setChecked(false);
                    radioShareSina.setChecked(false);
                    radioShareQq.setChecked(false);
                }
            }
        });
        //微博
        radioShareSina.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shareTo = 3;
                    radioShareSina.setChecked(true);
                    radioShareQq.setChecked(false);
                    radioShareCircle.setChecked(false);
                    radioShareFriend.setChecked(false);
                }
            }
        });
        //QQ
        radioShareQq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shareTo = 4;
                    radioShareQq.setChecked(true);
                    radioShareSina.setChecked(false);
                    radioShareCircle.setChecked(false);
                    radioShareFriend.setChecked(false);
                }
            }
        });

        //公开
        radioPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jurisdiction = 1;
                }
            }
        });
        //私密
        radioPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jurisdiction = 0;
                }
            }
        });
        //好友
        radioFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jurisdiction = 2;
                }
            }
        });
        //陌生人
        radioStranger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jurisdiction = 3;
                }
            }
        });
        //定位
//        checkBoxLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        if (locationService != null) {
            locationService.unregisterListener(merriLocationListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        locationService = ((MerriApp) getApplication()).locationService;
        locationService.registerListener(merriLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    private void initView() {
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 20) {
                    RxToast.showToast("标题最多20个字");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 20) {
                    String titleText = editTitle.getText().toString().trim();
                    editTitle.setText(titleText.substring(0, 20));
                    editTitle.setSelection(editTitle.getText().toString().trim().length());
                }
                tvTitleNum.setText((20 - editTitle.getText().toString().trim().length()) + "");

            }
        });
        //默认选中（公开）
        radioPublic.setChecked(true);
        //默认分享（朋友圈）
        radioShareCircle.setChecked(true);
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            int activityId = intent.getIntExtra("activityId", -1);
            videoPath = intent.getStringExtra("videoPath");//视频路径

            if (activityId == VideoEditorActivity.activityId || activityId == RecordVideoAty.activityId) {
                name = intent.getStringExtra("cover");
                int[] imageWidthHeight = RxImageTool.getImageWidthHeight(name);
                //需要计算宽高让图片填充满
                width = imageWidthHeight[0];
                height = imageWidthHeight[1];
                handler.sendEmptyMessage(1);
            } else {
                name = intent.getStringExtra("cover");
                int[] imageWidthHeight = RxImageTool.getImageWidthHeight(name);
                width = imageWidthHeight[0];
                height = imageWidthHeight[1];
                handler.sendEmptyMessage(2);
            }

        }
    }

    /**
     * 设置TITLE
     */
    private void initTitle() {
        tvClose.setText("返回");
        tvTitle.setText("发布视频");
        tvFinish.setText("发布");
        tvFinish.setTextColor(getResources().getColor(R.color.normal_red));
    }

    private void showExitScreenDialog(Context context) {
        dialog = new CommomDialog(cnt, R.style.dialog, "是否放弃发布视频?", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    finish();
                }
            }
        }).setTitle("温馨提示");
        dialog.show();
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.iv_back, R.id.tv_right, R.id.rl_address, R.id.rel_complete, R.id.rel_add_label, R.id.rel_cover})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rl_address://添加位置
                Bundle addressBundle = new Bundle();
                addressBundle.putInt("activityId", activityId);
                RxActivityTool.skipActivityForResult(this, SendLocationAty.class, addressBundle, REQUEST_CODE_LOCATION);
                break;
            case R.id.iv_back://返回
                //提示是否生成草稿
                showExitScreenDialog(VideoReleaseActivity.this);
                break;
            case R.id.tv_right://保存草稿
                if (NetUtils.isNetworkAvailable(VideoReleaseActivity.this)) {
                    if (UserModel.getUserModel().getIsLogin()) {
                        //获取title
                        title = editTitle.getText().toString();
                        //电话
                        phone = UserModel.getUserModel().getMobile();
                        //memberId
                        memberId = UserModel.getUserModel().getMemberId();
                        //性别
                        gender = UserModel.getUserModel().getGender();
                        //发布人姓名
                        memberName = UserModel.getUserModel().getRealname();
                        //头像
                        memberImage = UserModel.getUserModel().getImgUrl();
                        //权限(查看权限 0私密 1公开)
                        //jurisdiction = 1;
                        //日志标识 1图文专辑 2视频 3照片 4录像
                        flag = 2;
                        //是否举报 -1:未举报 ,0:举报待审核 ,1:举报通过 ,2:举报失败
                        isBlack = -1;
                        //是否删除 0否 1是
                        isDelete = 0;

//                        if (TextUtils.isEmpty(classifystr)) {
//                            RxToast.showToast("请选择视频标签");
//                        } else {
                        if (TextUtils.isEmpty(editTitle.getText().toString())) {
                            RxToast.showToast("请输入视频标题");
                            return;
                        }

                        if (TextUtils.isEmpty(editContent.getText().toString())){
                            dialog = new CommomDialog(cnt, R.style.dialog, "写点内容能够获得更多点赞，赚更多现金", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        showSoftInputFromWindow(VideoReleaseActivity.this,editContent);
                                        return;
                                    }else {
                                        if (TextUtils.isEmpty(classifystr)) {
                                            classifystr = "-1";
                                        }
                                        //发布视频
                                        //setData();
                                        dialog_load = new ProgressDialog(VideoReleaseActivity.this);
                                        dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog_load.setCanceledOnTouchOutside(false);
                                        dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        dialog_load.setMessage("正在发布视频...");
                                        dialog_load.show();
                                        switch (isReleaseFailure) {
                                            case 1:
                                                String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
                                                setVideoURL(videoName);
                                                break;
                                            case 2:
                                                String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                                                setImageURL(imageName);//上传图片
                                                break;
                                            case 3:
                                                videoRelease();
                                                break;
                                        }
                                    }
                                }
                            }).setTitle("温馨提示");
                            dialog.setPositiveButton("现在写");
                            dialog.setNegativeButton("懒得写");
                            dialog.show();
                        }else {
                            if (TextUtils.isEmpty(classifystr)) {
                                classifystr = "-1";
                            }
                            //发布视频
                            //setData();
                            dialog_load = new ProgressDialog(VideoReleaseActivity.this);
                            dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog_load.setCanceledOnTouchOutside(false);
                            dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog_load.setMessage("正在发布视频...");
                            dialog_load.show();
                            switch (isReleaseFailure) {
                                case 1:
                                    String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
                                    setVideoURL(videoName);
                                    break;
                                case 2:
                                    String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                                    setImageURL(imageName);//上传图片
                                    break;
                                case 3:
                                    videoRelease();
                                    break;
                            }
                        }

//                        }
                    } else {
                        RxActivityTool.skipActivity(VideoReleaseActivity.this, LoginActivity.class);
                    }
                } else {
                    RxToast.showToast("当前网络不可用,请检查网络后重试");
                }
                break;
            case R.id.rel_complete:
                if (NetUtils.isNetworkAvailable(VideoReleaseActivity.this)) {
                    if (UserModel.getUserModel().getIsLogin()) {
                        //获取title
                        title = editTitle.getText().toString();
                        //电话
                        phone = UserModel.getUserModel().getMobile();
                        //memberId
                        memberId = UserModel.getUserModel().getMemberId();
                        //性别
                        gender = UserModel.getUserModel().getGender();
                        //发布人姓名
                        memberName = UserModel.getUserModel().getRealname();
                        //头像
                        memberImage = UserModel.getUserModel().getImgUrl();
                        //权限(查看权限 0私密 1公开)
                        //jurisdiction = 1;
                        //日志标识 1图文专辑 2视频 3照片 4录像
                        flag = 2;
                        //是否举报 -1:未举报 ,0:举报待审核 ,1:举报通过 ,2:举报失败
                        isBlack = -1;
                        //是否删除 0否 1是
                        isDelete = 0;
//                        if (TextUtils.isEmpty(editTitle.getText().toString())){
//                            RxToast.showToast("请输入视频标题");
//                        } else if (TextUtils.isEmpty(classifystr)) {
//                            RxToast.showToast("请选择视频标签");
//                        } else {
                        if (TextUtils.isEmpty(classifystr)) {
                            classifystr = "-1";
                        }

                        if (TextUtils.isEmpty(editTitle.getText().toString())) {
                            RxToast.showToast("请输入视频标题");
                            return;
                        }

                        if (TextUtils.isEmpty(editContent.getText().toString())){
                            dialog = new CommomDialog(cnt, R.style.dialog, "写点内容能够获得更多点赞，赚更多现金", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        showSoftInputFromWindow(VideoReleaseActivity.this,editContent);
                                        return;
                                    }else {
                                        //setData();
                                        dialog_load = new ProgressDialog(VideoReleaseActivity.this);
                                        dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog_load.setCanceledOnTouchOutside(false);
                                        dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        dialog_load.setMessage("正在发布视频...");
                                        dialog_load.show();
                                        switch (isReleaseFailure) {
                                            case 1:
                                                String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
                                                setVideoURL(videoName);
                                                break;
                                            case 2:
                                                String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                                                setImageURL(imageName);//上传图片
                                                break;
                                            case 3:
                                                videoRelease();
                                                break;
                                        }
                                    }
                                }
                            }).setTitle("温馨提示");
                            dialog.setPositiveButton("现在写");
                            dialog.setNegativeButton("懒得写");
                            dialog.show();
                        }else {
                            //setData();
                            dialog_load = new ProgressDialog(VideoReleaseActivity.this);
                            dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog_load.setCanceledOnTouchOutside(false);
                            dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog_load.setMessage("正在发布视频...");
                            dialog_load.show();
                            switch (isReleaseFailure) {
                                case 1:
                                    String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
                                    setVideoURL(videoName);
                                    break;
                                case 2:
                                    String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                                    setImageURL(imageName);//上传图片
                                    break;
                                case 3:
                                    videoRelease();
                                    break;
                            }
                        }

//                        }
                    } else {
                        RxActivityTool.skipActivity(VideoReleaseActivity.this, LoginActivity.class);
                    }
                } else {
                    RxToast.showToast("当前网络不可用,请检查网络后重试");
                }
                break;
            case R.id.rel_add_label://标签
                startActivityForResult(new Intent(VideoReleaseActivity.this, SelectorLabelAty.class), RESULT_LABEL_POSITION);
                break;
            case R.id.rel_cover://浏览视频效果
                startActivity(new Intent(VideoReleaseActivity.this, VideoActivity.class).putExtra("videoPath", "" + videoPath));
                break;
        }
    }

    private void setData() {
        VideoReleaseModel videoReleaseModel = new VideoReleaseModel(name, videoPath, title, content, phone, cover, classifystr, memberId, gender, memberName, address, memberImage, musicUrl, videoUrl, longitude, latitude, jurisdiction, flag, isBlack, isDelete, pictureUrls, width, height, editContent.getText().toString(), shareTo);
        //发送广播
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.FRIEND_RELEASE = true;
        myEventBusModel.REFRESH_MINE_CIRCLER = true;
        myEventBusModel.videoReleaseModel = videoReleaseModel;
        EventBus.getDefault().post(myEventBusModel);
        startActivity(new Intent(VideoReleaseActivity.this, MainActivity.class).putExtra("activityId", activityId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LABEL_POSITION) {
            if (resultCode == RESULT_OK) {
                //返回标签
                label = "";
                classifystr = "";
                selectorLabelList = new ArrayList<>();
                selectorLabelList.addAll((Collection<? extends SelectorLabelModel.DataBean>) data.getExtras().getSerializable("label_list"));
                for (int i = 0; i < selectorLabelList.size(); i++) {
                    if (selectorLabelList.get(i).isChecked()) {
                        if (label.equals("")) {
                            label = selectorLabelList.get(i).getLable();//显示标签
                            classifystr = "" + selectorLabelList.get(i).getLableId();//上传标签
                        } else {
                            label = label + "，" + selectorLabelList.get(i).getLable();
                            classifystr = classifystr + "," + selectorLabelList.get(i).getLableId();
                        }
                    }
                }
                tvLabel.setText(label);
            }
        }
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK && data != null) {
                GroupLocationModel locationModel = (GroupLocationModel) data.getSerializableExtra("locationModel");
                tvAddAddress.setText(locationModel.address);
                latitude = locationModel.currentLatitude + "";
                longitude = locationModel.currentLongitude + "";
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        return false;
    }

    /**
     * 上传视频到阿里云
     */
    private void setVideoURL(final String videoName) {
        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, videoName, videoPath);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    getUrl(videoName);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    isReleaseFailure = 1;
                    handler.sendEmptyMessage(3);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    //android.util.Log.d("LogTest", "进度：" + (int) ((currentSize * 100) / totalSize));
                    int arg1 = (int) ((currentSize * 100) / totalSize);
                    Message message = new Message();
                    message.what = 4;
                    message.obj = arg1;
                    handler.sendMessage(message);
                }
            });
        }
    }

    private void getUrl(final String videoName) {
        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        credentialProvider = new STSGetter(Config.STSSERVER);

        final OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    videoUrl = oss.presignConstrainedObjectURL(Config.bucket, videoName, 30 * 60);
                    videoUrl = "http://okdi.oss-cn-beijing.aliyuncs.com/" + videoName;
                    JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
                    JSONObject jsonObj = new JSONObject();//pet对象，json形式
                    try {
                        jsonObj.put("url", videoUrl);//视频链接
                        jsonObj.put("type", 1);//1是为视频
                        jsonObj.put("text", editContent.getText().toString());
                        jsonObj.put("flag", 1);//1不是封面
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 把每个数据当作一对象添加到数组里
                    jsonarray.put(jsonObj);
                    content = jsonarray.toString();

                    //删除本地合成的视频
                    FileUtils.delFile(videoPath);
                    String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                    setImageURL(imageName);//上传图片
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 上传图片到阿里云
     *
     * @param imageName
     */
    private void setImageURL(final String imageName) {
        if (TextUtils.isEmpty(name)){
            getImageUrl(imageName);
        }else {
            PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, imageName, name);
            if (checkNotNull(putObjectSamples)) {
                putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        getImageUrl(imageName);
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                        isReleaseFailure = 2;
                        handler.sendEmptyMessage(3);
                    }

                    @Override
                    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                        Message message = new Message();
                        message.what = 4;
                        message.obj = 101;
                        handler.sendMessage(message);
                    }
                });
            }
        }
    }

    private void getImageUrl(final String imageName) {
        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        credentialProvider = new STSGetter(Config.STSSERVER);

        final OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);

        imageUrl = "http://okdi.oss-cn-beijing.aliyuncs.com/" + imageName;
        JSONObject jsonObj = new JSONObject();//pet对象，json形式
        try {
            jsonObj.put("url", imageUrl);//视频链接
            jsonObj.put("type", 0);//1是为视频 0图片
            jsonObj.put("text", editContent.getText().toString());
            jsonObj.put("flag", 1);//1不是封面
            if (TextUtils.isEmpty(name)){
                jsonObj.put("width", 720);
                jsonObj.put("height", 1280);
            }else {
                jsonObj.put("width", width);
                jsonObj.put("height", height);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cover = jsonObj.toString();
        pictureUrls = "[\"" + imageUrl + "\"]";
        //删除本地封面图
        FileUtils.delFile(name);
        videoRelease();
    }

    /**
     * 发布视频
     */
    private void videoRelease() {
        OkGo.<String>get(Urls.BEAUTY_PULISH)
                .tag(this)
                .params("title", title)
                .params("content", content)
                .params("describe", editContent.getText().toString())
                .params("phone", phone)
                .params("cover", cover)
                .params("classifystr", classifystr)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("gender", gender)
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("address", address)
                .params("memberImage", UserModel.getUserModel().getImgUrl())
                .params("musicUrl", musicUrl)
                .params("videoUrl", videoUrl)
                .params("longitude", longitude)
                .params("latitude", latitude)
                .params("jurisdiction", jurisdiction)
                .params("flag", flag)
                .params("isBlack", isBlack)
                .params("isDelete", isDelete)
                .params("pictureUrls", pictureUrls)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (data.optJSONObject("data").optBoolean("result")) {
                                        Message message = new Message();
                                        message.what = 4;
                                        message.obj = 102;
                                        handler.sendMessage(message);

                                        RxToast.showToast("发布视频成功");

                                        String id = data.optJSONObject("data").optString("id");

                                        //发送广播 关闭界面
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        //关闭拍摄界面
                                        myEventBusModel.CLOSE_VIDEO_ACTIVITY = true;
                                        //分享需要的参数（CircleFriendsFragment需要）
                                        myEventBusModel.REFRESH_MINE_CIRCLER = true;
                                        //弹出分享框
                                        myEventBusModel.REFRESH_MINE_CIRCLER_SHARE = true;
                                        myEventBusModel.id = id;
                                        myEventBusModel.title = editTitle.getText().toString();
                                        myEventBusModel.content = editContent.getText().toString();
                                        myEventBusModel.imageUrl = imageUrl;
                                        myEventBusModel.shareTo = shareTo;
                                        EventBus.getDefault().post(myEventBusModel);
                                        /**
                                         * 视频发布成功  删除本地视频文件夹及文件
                                         */
                                        if (RxFileTool.isDir(outFile + "video")) {
                                            FileUtils.deleteDir(outFile + "video");
                                        }
                                        if (RxFileTool.isDir(outFile + "video_edit")) {
                                            FileUtils.deleteDir(outFile + "video_edit");
                                        }
                                        startActivity(new Intent(VideoReleaseActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        RxToast.showToast(data.optJSONObject("data").optString("msg"));
                                    }
                                } else {
                                    if (dialog_load != null) {
                                        dialog_load.dismiss();
                                        RxToast.showToast(data.optString("message"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        isReleaseFailure = 3;
                        handler.sendEmptyMessage(3);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 监听返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitScreenDialog(VideoReleaseActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    /**
//     * 异步分割图片
//     */
//    class MyTaskImage extends AsyncTask<Void, Integer, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            //getBitmapsFromVideo(videoPath);
//            // 网络图片转 bitmap
//            for (int i = 0; i < listUri.size(); i++) {
//                bitmap = GetLocalOrNetBitmap(listUri.get(i));
//                list[i] = bitmap;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            /**
//             * 生成的gif每一帧的时间间隔(ms),默认500.
//             */
//            int mDelayTime = 300;
//
//            int mQuality = 10;
//
//            int mColor = 256;
//
//            final Gifflen mGiffle = new Gifflen.Builder()
//                    .color(mColor)          //色域范围是2~256,且必须是2的整数次幂.
//                    .delay(mDelayTime) //每相邻两帧之间播放的时间间隔.
//                    .quality(mQuality) //色彩量化时的quality值.
//                    .width(360)          //生成Gif文件的宽度(像素).
//                    .height(640)          //生成Gif文件的高度(像素).
//                    .listener(new Gifflen.OnEncodeFinishListener() {  //创建完毕的回调
//                        @Override
//                        public void onEncodeFinish(String path) {
//                            RxToast.showToast("已保存gif到" + path);
//                            Logger.e("已保存gif到:"+ path);
//                            //setImageURL1("video_" + getCurrentDate() + ".gif");
//                            Uri uri = Uri.parse("file://"+path);
//                            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                                    .setUri(uri)
//                                    .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
//                                    .build();
//                            sdcover.setController(draweeController);
//                        }
//                    })
//                    .build();
//            mStorePath = FileUtils.createVideoCoverFileNameGIF();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mGiffle.encode(mStorePath, list);
//                }
//            }).start();
//            super.onPostExecute(aBoolean);
//        }
//
//    }
//
//    /**
//     * 分割视频 1秒1帧
//     *
//     * @param dataPath
//     */
//    public void getBitmapsFromVideo(String dataPath) {
//        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(dataPath);
//        // 得到每一秒时刻的bitmap比如第一秒,第二秒
//        for (int i = 1; i <= 3; i++) {
//            Logger.e("开始制作" + i);
//            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
//            list[i - 1] = bitmap;
//        }
//    }
//
//    private void setImageURL1(final String imageName) {
//        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, imageName, mStorePath);
//        if (checkNotNull(putObjectSamples)) {
//            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
//                @Override
//                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                    getImageUrl1(imageName);
//                }
//
//                @Override
//                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
//
//                }
//
//                @Override
//                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//
//                }
//            });
//        }
//    }
//
//    private void getImageUrl1(final String imageName) {
//        OSSCredentialProvider credentialProvider;
//        //使用自己的获取STSToken的类
//        credentialProvider = new STSGetter(Config.STSSERVER);
//
//        final OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                imageUrl = "http://okdi.oss-cn-beijing.aliyuncs.com/" + imageName;
//                Logger.e("gif图：" + imageUrl);
//                handler.sendEmptyMessage(5);
//            }
//        }).start();
//    }
//
//    public static Bitmap GetLocalOrNetBitmap(String url)
//    {
//        Bitmap bitmap = null;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//        try
//        {
//            in = new BufferedInputStream(new URL(url).openStream(), 1024);
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, 1024);
//            copy(in, out);
//            out.flush();
//            byte[] data = dataStream.toByteArray();
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            data = null;
//            return bitmap;
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
