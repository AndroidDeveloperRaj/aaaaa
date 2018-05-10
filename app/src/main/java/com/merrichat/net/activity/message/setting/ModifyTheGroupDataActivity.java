package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CircleImageView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/18.
 * 修改群资料
 */

public class ModifyTheGroupDataActivity extends MerriActionBarActivity implements TakePhoto.TakeResultListener, InvokeListener {

    /**
     * 修改群头像
     */
    @BindView(R.id.rl_modify_group_avatar)
    RelativeLayout rlModifyGroupAvatar;

    /**
     * 修改群名字
     */
    @BindView(R.id.rl_modify_group_name)
    RelativeLayout rlModifyGroupName;

    /**
     * 修改群地址
     */
    @BindView(R.id.rl_modify_group_locations)
    RelativeLayout rlModifyGroupLocations;

    /**
     * 修改群公告
     */
    @BindView(R.id.rl_modify_group_introduction)
    RelativeLayout rlModifyGroupIntroduction;


    /**
     * 群头像
     */
    @BindView(R.id.civ_group_photo)
    CircleImageView civGroupPhoto;

    /**
     * 群类型
     */
    @BindView(R.id.tv_group_type)
    TextView tvGroupType;

    /**
     * 实物佣金比例
     */
    @BindView(R.id.tv_shiwu_percent)
    TextView tvShiWuPercent;
    /**
     * 虚拟佣金比例
     */
    @BindView(R.id.tv_xuni_percent)
    TextView tvXuNiPercent;

    private static final int HEADER_WIDTH = 184;//头像的宽
    private static final int HEADER_HEIGHT = 184;//头像的高
    private static final int MAX_PIC_SIZE = 50 * 1024;//上传图片的最大值

    /**
     * 群logo右面的群名字
     */
    @BindView(R.id.tv_group_logo)
    TextView tvGroupLogo;
    @BindView(R.id.iv_group_logo)
    ImageView ivGroupLogo;

    /**
     * 群名字
     */
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.iv_tv_group_name)
    ImageView ivTvGroupName;

    /**
     * 群地址
     */
    @BindView(R.id.tv_tv_group_adress)
    TextView tvTvGroupAdress;
    @BindView(R.id.iv_group_adress)
    ImageView ivGroupAdress;

    /**
     * 群公告
     */
    @BindView(R.id.tv_group_announcement)
    TextView tvGroupAnnouncement;

    @BindView(R.id.iv_group_announcement)
    ImageView ivGroupAnnouncement;


    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String path_header;//头像图片路径


    /****************以下是接口返回值************************/

    /**
     * 群头像url
     */
    private String communityImgUrl;

    /**
     * 群名字
     */
    private String communityName;


    /**
     * 群公告
     */
    private String communityNotice;

    /**
     * 群地址
     */
    private String communityAddress;


    /**
     * 群类型默认0：交流群，1：微商群（BTC） 2：集市群(CTC)
     */
    private int type;

    /**
     * 是否是管理员0：成员，1：管理员 2：群主
     */
    private int isMaster;


    /**
     * 实物佣金
     */
    private String realCommission;

    /**
     * 虚拟佣金
     */
    private String virtualCommission;


    /**
     * 经纬度
     */
    private String longitude;
    private String latitude;


    /**
     * 群id
     */
    private String groupId = "";

    private boolean isUpadateGroupName = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_the_group_data);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        setLeftBack();
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_DATA) {//刷新数据
            communityInfo();
        } else if (myEventBusModel.REFRESH_GROUP_DATA_NAME) {
            isUpadateGroupName = true;
            communityInfo();
        }
    }


    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        communityInfo();
    }


    /**
     * 修改/查看群资料
     */
    private void communityInfo() {
        OkGo.<String>post(Urls.communityInfo)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    communityImgUrl = data.optString("communityImgUrl");
                                    communityName = data.optString("communityName");
                                    communityNotice = data.optString("communityNotice");
                                    communityAddress = data.optString("communityAddress");
                                    type = data.optInt("type");
                                    isMaster = data.optInt("isMaster");
                                    realCommission = data.optString("realCommission");
                                    virtualCommission = data.optString("virtualCommission");
                                    longitude = data.optString("longitude");
                                    latitude = data.optString("latitude");

                                    setText();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 设置页面文本信息
     */
    private void setText() {
        if (isMaster == 2) {
            setTitle("修改群资料");
            ivGroupLogo.setVisibility(View.VISIBLE);
            ivTvGroupName.setVisibility(View.VISIBLE);
            ivGroupAdress.setVisibility(View.VISIBLE);
            ivGroupAnnouncement.setVisibility(View.VISIBLE);

        } else {
            setTitle("查看群资料");
        }

        if (TextUtils.isEmpty(communityNotice)) {
            tvGroupAnnouncement.setText("设置群公告");
        } else {
            tvGroupAnnouncement.setText(communityNotice);
        }

        tvGroupLogo.setText(communityName);
        tvGroupName.setText(communityName);
        if (TextUtils.isEmpty(communityAddress)) {
            tvTvGroupAdress.setText("设置群地点");
        } else {
            tvTvGroupAdress.setText(communityAddress);
        }


        Glide.with(cnt).load(communityImgUrl).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).error(R.mipmap.ic_preloading).into(civGroupPhoto);
        if (type == 0) {
            tvGroupType.setText("交流群");
        } else if (type == 1) {
            tvGroupType.setText("BTC群");
        } else if (type == 2) {
            tvGroupType.setText("CTC群");
        }
        tvShiWuPercent.setText(realCommission);
        tvXuNiPercent.setText(virtualCommission);
//        tvCommissionRate.setText("群佣金比例：实物商品" + realCommission + " 虚拟物品" + virtualCommission);
    }


    @OnClick({R.id.rl_modify_group_avatar, R.id.rl_modify_group_name, R.id.rl_modify_group_introduction, R.id.rl_modify_group_locations})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_modify_group_avatar://修改群头像
                if (isMaster == 2) {
                    showSelectPicturePop();
                }
                break;
            case R.id.rl_modify_group_name://修改群名字
                if (isMaster == 2) {
                    startActivity(new Intent(cnt, ModifyGroupNameActivity.class).putExtra("groupId", groupId));
                }
                break;
            case R.id.rl_modify_group_locations://修改群地址
                if (isMaster == 2) {
                    startActivity(new Intent(cnt, ModifyGroupLocationsActivity.class).putExtra("groupId", groupId));
                }
                break;
            case R.id.rl_modify_group_introduction://修改群公告
                if (isMaster == 2) {
//                    startActivity(new Intent(cnt, ModifyGroupIntroductionActivity.class).putExtra("groupId", groupId));
                    startActivity(new Intent(cnt, SetGroupAnnouncementActivity.class).putExtra("type", 3).putExtra("groupId", groupId));
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishThisActivity();
    }

    private void finishThisActivity() {
        if (isUpadateGroupName) {
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_GROUP_SETTING_NAME = true;
            EventBus.getDefault().post(eventBusModel);
        }
        finish();
    }

    /**
     * 修改群头像---选择照片或者拍照
     */
    private void showSelectPicturePop() {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        final Uri imageUri = Uri.fromFile(file);
        NiceDialog.init()
                .setLayoutId(R.layout.popup_normal)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_cancle, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.tv_first, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                setCompressConfig(HEADER_WIDTH, HEADER_HEIGHT, MAX_PIC_SIZE);
                                //其他设置
                                TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                take_builder.setCorrectImage(true);//纠正拍照的照片旋转角度
                                takePhoto.setTakePhotoOptions(take_builder.create());
                                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(HEADER_WIDTH, HEADER_WIDTH));
                            }
                        });
                        holder.setOnClickListener(R.id.tv_second, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                setCompressConfig(HEADER_WIDTH, HEADER_HEIGHT, MAX_PIC_SIZE);
                                //其他设置
                                TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                take_builder.setWithOwnGallery(false);//使用TakePhoto自带相册
                                takePhoto.setTakePhotoOptions(take_builder.create());
                                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(HEADER_WIDTH, HEADER_WIDTH));

                            }
                        });
                    }

                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }


    /**
     * 上传群logo
     */
    private void uploadGroupOfImg() {
        OkGo.<String>post(Urls.uploadGroupOfImg)
                .tag(this)
                .params("multipartFile", new File(path_header))
                .params("cid", groupId)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                String groupOfImgUrl = jsonObject.optString("groupOfImgUrl");
                                Glide.with(cnt).load(groupOfImgUrl).error(R.mipmap.ic_preloading).into(civGroupPhoto);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }


    /**
     * 压缩配置
     *
     * @param width
     * @param height
     * @param maxSize 压缩不会超过这个值
     */
    private void setCompressConfig(int width, int height, int maxSize) {
        //压缩配置
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .create();
        config.enableReserveRaw(false);//是否保存原图
        takePhoto.onEnableCompress(config, true);
    }

    /**
     * 裁切配置
     *
     * @param width
     * @param height
     * @return
     */
    private CropOptions getCropOptions(int width, int height) {
        //裁切配置
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(width).setAspectY(height);//比例 184x184
        builder.setWithOwnCrop(false);//裁切工具:false是第三方true是takephoto自带的
        return builder.create();
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.e("@@@", "takeSuccess：" + result.getImage().getCompressPath());
        path_header = result.getImage().getCompressPath();
//        civGroupPhoto.setImageURI(Uri.fromFile(new File(path_header)));
        uploadGroupOfImg();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.e("@@@", "takeFail:----失败");
    }

    @Override
    public void takeCancel() {
        Log.e("@@@", "takeCancel:----");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
