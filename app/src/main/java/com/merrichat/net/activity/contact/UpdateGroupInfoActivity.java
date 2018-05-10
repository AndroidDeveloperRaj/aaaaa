package com.merrichat.net.activity.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
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
import com.merrichat.net.R;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.SelectFriendAty;
import com.merrichat.net.activity.message.SendLocationAty;
import com.merrichat.net.activity.message.setting.SetGroupAnnouncementActivity;
import com.merrichat.net.activity.my.ChangeNickNameActivity;
import com.merrichat.net.activity.my.PersonalInfoActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.model.CreateGroupOfModel;
import com.merrichat.net.model.GroupLocationModel;
import com.merrichat.net.model.UploadGroupOfImgModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.GetToast;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by wangweiwei on 2018/1/23.
 * 上传群资料
 */

public class UpdateGroupInfoActivity extends MerriActionBarActivity implements TakePhoto.TakeResultListener, InvokeListener {

    private final int requestCode1 = 1;
    private final int requestCode2 = 2;
    private final int requestCode3 = 3;

    private SimpleDraweeView mIconGroup;
    private RelativeLayout mEnterGroupName;  //输入群名称
    private RelativeLayout mEnterGroupInfo;     //输入群公告
    private RelativeLayout mEnterGroupPosition;    //选择群地点

    private TextView mGroupName; //群名称
    private TextView mGroupInfo;  //群公告
    private TextView mGroupAddress;  //群创建地址

    private GroupLocationModel locationModel = new GroupLocationModel();  // 群地址包括location 详细地址
    private String groupImageUrl = "";  //群头像地址
    private String communityNotice;  //群公告

    private static final String TAG = UpdateGroupInfoActivity.class.getName();
    //takephoto相关
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Context context;

    private String memberJson;

    private RelativeLayout mGroupType1;
    private RelativeLayout mGroupType2;
    private RelativeLayout mGroupType3;

    private TextView mCheckType;  //被选择的选择按钮 交流群
    private TextView mCheckType1;//被选择的选择按钮 微商群
    private TextView mCheckType2;//被选择的选择按钮 集市群

    private int mGroupType = 0;   //交易群类型

    private double mRealCommission = 0;  //真实物品佣金比例
    private double mVirtualCommission = 0;  //虚拟物品佣金比例

    private RelativeLayout mRlRealCommission;   //真实物品佣金比例选择
    private RelativeLayout mRlVirtualCommission;  //虚拟物品佣金比例选择按钮

    private OptionsPickerView pvCustomOptions;
    private TextView mRealPercent;
    private TextView mVirtualPercent;
    private TextView mLabel;  //

    private List<Double> yongJinList = getListData();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_info);
        context = this;
        setLeftBack();
        setTitle("上传群资料");
        memberJson = getIntent().getStringExtra("memberJson");
        setRightText("确定", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                GetToast.useString(cnt, "完成...");
                if (groupImageUrl.length() > 0) {
                    uploadGroupOfImg(groupImageUrl);
                } else {
                    GetToast.useString(cnt, "当前没有设置照片，选择一张吧～！");
                }
            }
        });

        initView();
    }

    public void initClickView() {
        mIconGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPicturePop();
            }
        });

        mEnterGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefAppStore.setGroupName(context, "");
                Intent intent = new Intent(UpdateGroupInfoActivity.this, UpdateGroupNameActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, requestCode1);

            }
        });
        mEnterGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefAppStore.setGroupInfo(context, "");
                Intent intent = new Intent(UpdateGroupInfoActivity.this, SetGroupAnnouncementActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, requestCode2);

            }
        });
        mEnterGroupPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateGroupInfoActivity.this, SendLocationAty.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, requestCode3);

            }
        });

        mGroupType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckType.setBackgroundResource(R.mipmap.xuanzhaopian_2x);
                mCheckType1.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mCheckType2.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mGroupType = 0;

                mRlRealCommission.setVisibility(View.GONE);
                mRlVirtualCommission.setVisibility(View.GONE);
                mLabel.setVisibility(View.GONE);
            }
        });
        mGroupType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckType.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mCheckType1.setBackgroundResource(R.mipmap.xuanzhaopian_2x);
                mCheckType2.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mGroupType = 1;
                mRlRealCommission.setVisibility(View.VISIBLE);
                mRlVirtualCommission.setVisibility(View.VISIBLE);
                mLabel.setVisibility(View.VISIBLE);

            }
        });

        mGroupType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckType.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mCheckType1.setBackgroundResource(R.mipmap.weixuanzhong_huise_2x);
                mCheckType2.setBackgroundResource(R.mipmap.xuanzhaopian_2x);
                mGroupType = 2;
                mRlRealCommission.setVisibility(View.GONE);
                mRlVirtualCommission.setVisibility(View.GONE);
                mLabel.setVisibility(View.GONE);

            }
        });

        mRlRealCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCustomOptionPicker(1);
                pvCustomOptions.show();

            }
        });
        mRlVirtualCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCustomOptionPicker(2);
                pvCustomOptions.show();

            }
        });
    }


    /**
     * @param cid               群id
     * @param groupName         群名称
     * @param groupOfImgUrl     群logo 地址
     * @param locationModel     地址model
     * @param communityName     群公告
     * @param memberJson        联系人json
     * @param groupType         群类型0：交流群，1：微商群（BTC） 2：集市群(CTC)
     * @param realCommission    实物佣金比例举例：0.20 按百分比传
     * @param virtualCommission 虚拟佣金比例举例：0.20 按百分比传
     */
    public void createGroupOf(final long cid,
                              final String groupName,
                              final String groupOfImgUrl,
                              final GroupLocationModel locationModel,
                              final String communityName,
                              final String memberJson,
                              final int groupType,
                              final double realCommission,
                              final double virtualCommission) {
        /**
         * 创建群聊
         * @param cid             群id
         * @param memberId        创建人memberId
         * @param memberName      创建人名称
         * @param groupOfImgUrl   群头像URl
         * @param communityName   群名字
         * @param communityNotice 群公告
         * @param address         群地址
         * @param longitude       经度
         * @param latitude        纬度
         * @param memberJson      邀请的成员格式，数组类型的json字符串[{"id":12345,"name":"张三","phone":"13412121212"},{"id":332323,"name":"李四","phone":"13412121212"}]
         * @param phone           创建人手机号
         * @return
         */

        Map<String, Object> map = new HashMap<>();
        map.put("cid", cid);
        map.put("memberId", UserModel.getUserModel().getMemberId());
        map.put("memberName", UserModel.getUserModel().getRealname());
        map.put("groupOfImgUrl", groupOfImgUrl);
        map.put("communityName", groupName);
        map.put("type", groupType);
        if (groupType == 1) {
            map.put("realCommission", realCommission);
            map.put("virtualCommission", virtualCommission);
        }
        if (communityName != null && communityName.length() > 0) {
            map.put("communityNotice", communityName);
        }
        map.put("memberJson", memberJson);
        map.put("phone", UserModel.getUserModel().getMobile());

        if (locationModel != null) {
            if (!TextUtils.isEmpty(locationModel.address)) {
                if (locationModel.address != null) {
                    map.put("address", locationModel.address);
                }
            }
            if (locationModel.currentLatitude != 0) {
                map.put("longitude", locationModel.currentLongitude);
            }
            if (locationModel.currentLatitude != 0) {
                map.put("latitude", locationModel.currentLatitude);
            }
        }


        ApiManager.getApiManager().getService(WebApiService.class).createGroupOf(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<CreateGroupOfModel>() {
                    @Override
                    public void onNext(CreateGroupOfModel createGroupOfModel) {
                        if (createGroupOfModel.success) {
                            if (createGroupOfModel.data.success) {
                                Intent intent = new Intent(cnt, GroupChattingAty.class);
                                intent.putExtra("groupId", createGroupOfModel.data.cid);
                                intent.putExtra("group", groupName);
                                intent.putExtra("groupLogoUrl", groupOfImgUrl);
                                startActivity(intent);
                                if (AppManager.getAppManager().hasActivity(SelectFriendAty.class)) {
                                    AppManager.getAppManager().finishActivity(SelectFriendAty.class);
                                }
                                finish();
                            }
                        } else {
                            GetToast.useString(cnt, createGroupOfModel.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(UpdateGroupInfoActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 上传群头像地址，返回群id
     *
     * @param groupImageUrl 头像地址
     */
    public void uploadGroupOfImg(String groupImageUrl) {

        if (mGroupType == 1) {
            if (mRealCommission == 0 || mRealCommission == 0) {
                GetToast.showToast(cnt, "请选择交易佣金比例～！");
                return;
            }


        }

        if (mGroupName.getText().length() > 0) {

            if (null == locationModel || TextUtils.isEmpty(locationModel.address)) {
                Toast.makeText(UpdateGroupInfoActivity.this, "请选择群地点", Toast.LENGTH_LONG).show();
                return;
            }

            File file = new File(groupImageUrl);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("multipartFile", file.getName(), requestFile);
            String descriptionString = "This is a description";
            RequestBody description =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), descriptionString);
            ApiManager.getApiManager().getService(WebApiService.class).uploadGroupOfImg(description, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscribe<UploadGroupOfImgModel>() {
                        @Override
                        public void onNext(UploadGroupOfImgModel uploadGroupOfImgModel) {
                            if (uploadGroupOfImgModel.success) {
                                if (!(uploadGroupOfImgModel.groupOfImgUrl != null && uploadGroupOfImgModel.groupOfImgUrl.length() > 0 && uploadGroupOfImgModel.id > 0)) {
                                    Toast.makeText(UpdateGroupInfoActivity.this, "请求返回异常", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                createGroupOf(uploadGroupOfImgModel.id,
                                        mGroupName.getText().toString(),
                                        uploadGroupOfImgModel.groupOfImgUrl,
                                        locationModel,
                                        PrefAppStore.getGroupInfo(context),
                                        memberJson,
                                        mGroupType,
                                        mRealCommission,
                                        mVirtualCommission);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            GetToast.showToast(UpdateGroupInfoActivity.this, "没有网络了，检查一下吧～！");
                        }
                    });
        } else {
            GetToast.showToast(UpdateGroupInfoActivity.this, "群名称不能为空，检查一下吧");
        }
    }


    public void initView() {  //绑定view
        mIconGroup = (SimpleDraweeView) findViewById(R.id.icon_group);
        mEnterGroupName = (RelativeLayout) findViewById(R.id.rl_enter_group_name);
        mEnterGroupInfo = (RelativeLayout) findViewById(R.id.rl_enter_group_info);
        mEnterGroupPosition = (RelativeLayout) findViewById(R.id.rl_enter_group_position);
        mGroupName = (TextView) findViewById(R.id.tv_group_name);
        mGroupInfo = (TextView) findViewById(R.id.tv_group_info);
        mGroupAddress = (TextView) findViewById(R.id.tv_group_address);

        mGroupType1 = (RelativeLayout) findViewById(R.id.group_type_1);
        mGroupType2 = (RelativeLayout) findViewById(R.id.group_type_2);
        mGroupType3 = (RelativeLayout) findViewById(R.id.group_type_3);

        mCheckType = (TextView) findViewById(R.id.check_type);
        mCheckType1 = (TextView) findViewById(R.id.check_type_1);
        mCheckType2 = (TextView) findViewById(R.id.check_type_2);

        mRealPercent = (TextView) findViewById(R.id.tv_real_percent);
        mVirtualPercent = (TextView) findViewById(R.id.tv_virtual_percent);

        mLabel = (TextView) findViewById(R.id.tv_label);
        mRlRealCommission = (RelativeLayout) findViewById(R.id.rl_real_commission);
        mRlVirtualCommission = (RelativeLayout) findViewById(R.id.rl_virtual_commission);

        initClickView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestCode1:  //修改群名称返回
                if (null != PrefAppStore.getGroupName(context) && PrefAppStore.getGroupName(context).length() > 0) {
                    mGroupName.setText(PrefAppStore.getGroupName(context));
                    PrefAppStore.setGroupName(context, "");
                }
                break;

            case requestCode2:   //设置群公告
                if (null != PrefAppStore.getGroupInfo(context) && PrefAppStore.getGroupInfo(context).length() > 0) {
                    mGroupInfo.setText(PrefAppStore.getGroupInfo(context));
                }
                break;

            case requestCode3:  //选择地址
                if (null != PrefAppStore.getGroupLocation(context) && PrefAppStore.getGroupLocation(context).length() > 0) {
                    locationModel = new Gson().fromJson(PrefAppStore.getGroupLocation(context), GroupLocationModel.class);
                    mGroupAddress.setText(locationModel.address);
                    PrefAppStore.setGroupLocation(context, "");
                }
                break;

        }

    }


    /**
     * 选择图片
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
                                setCompressConfig(PersonalInfoActivity.HEADER_WIDTH, PersonalInfoActivity.HEADER_HEIGHT, PersonalInfoActivity.MAX_PIC_SIZE);
                                //其他设置
                                TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                take_builder.setCorrectImage(true);//纠正拍照的照片旋转角度
                                takePhoto.setTakePhotoOptions(take_builder.create());
                                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(PersonalInfoActivity.HEADER_WIDTH, PersonalInfoActivity.HEADER_WIDTH));

                            }
                        });
                        holder.setOnClickListener(R.id.tv_second, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                setCompressConfig(PersonalInfoActivity.HEADER_WIDTH, PersonalInfoActivity.HEADER_HEIGHT, PersonalInfoActivity.MAX_PIC_SIZE);
                                //其他设置
                                TakePhotoOptions.Builder take_builder = new TakePhotoOptions.Builder();
                                take_builder.setWithOwnGallery(false);//使用TakePhoto自带相册
                                takePhoto.setTakePhotoOptions(take_builder.create());
                                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(PersonalInfoActivity.HEADER_WIDTH, PersonalInfoActivity.HEADER_WIDTH));


                            }
                        });
                    }

                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
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


    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, "操作被取消");
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }


    @Override
    public void takeSuccess(TResult result) {
        groupImageUrl = result.getImage().getCompressPath();
        mIconGroup.setImageURI(Uri.fromFile(new File(groupImageUrl)));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefAppStore.setGroupInfo(context, "");
    }


    private List<Double> getListData() {
        List<Double> list = new ArrayList<>();
        list.add(0.05);
        list.add(0.1);
        list.add(0.15);
        list.add(0.2);
        list.add(0.25);
        list.add(0.5);
        for (double i = 1; i < 31; i++) {
            list.add(i);
        }
        return list;
    }


    private void initCustomOptionPicker(final int popupType) {//条件选择器初始化，自定义布局
        pvCustomOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                switch (popupType) {
                    case 1:
                        Double xueXing = yongJinList.get(options1);
                        mRealCommission = xueXing / 100;
                        mRealPercent.setText(xueXing + "%");
                        break;
                    case 2:
                        Double shenGao = yongJinList.get(options1);
                        mVirtualCommission = shenGao / 100;
                        mVirtualPercent.setText(shenGao + "%");
                        break;
                }
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        TextView tvPopTitle = (TextView) v.findViewById(R.id.title);
                        final TextView tvOk = (TextView) v.findViewById(R.id.ok);
                        TextView tvCancel = (TextView) v.findViewById(R.id.cancel);
                        switch (popupType) {
                            case 1:
                                tvPopTitle.setText("实物佣金比例设置");
                                break;
                            case 2:
                                tvPopTitle.setText("虚拟佣金比例设置");
                                break;
                        }
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });
                    }
                })
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .isDialog(true)
                .build();
        switch (popupType) {
            case 1:
                pvCustomOptions.setPicker(yongJinList);//添加数据}
                break;
            case 2:
                pvCustomOptions.setPicker(yongJinList);//添加数据}
                break;

        }

    }

}
