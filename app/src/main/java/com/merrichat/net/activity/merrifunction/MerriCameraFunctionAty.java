package com.merrichat.net.activity.merrifunction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ForceOfflineDialogAty;
import com.merrichat.net.activity.circlefriend.ImageUploadAty;
import com.merrichat.net.activity.my.InviteToMakeMoneyAty;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.activity.video.editor.VideoEditorActivity;
import com.merrichat.net.activity.video.editor.VideoMusicActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by amssy on 17/11/4.
 * 中间大按钮
 */

public class MerriCameraFunctionAty extends Activity {
    public static int activityId = MiscUtil.getActivityId();
    private final int REQUEST_CODE_IMG_CHOOSE = 0x0002;
    @BindView(R.id.lay_take_picture)
    LinearLayout layTakePicture;
    @BindView(R.id.lay_camera)
    LinearLayout layCamera;
    @BindView(R.id.lay_btn)
    LinearLayout layBtn;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.lay_xiangce)
    LinearLayout layXiangce;
    @BindView(R.id.lay_make_video)
    LinearLayout layMakeVideo;
    @BindView(R.id.lay_make_graphic_album)
    LinearLayout layMakeGraphicAlbum;
    @BindView(R.id.tv_fun)
    TextView tv_fun;
    private ArrayList<String> videoList;
    private int REQUEST_CODE_VIDEO_CHOOSE = 1;
    private int REQUEST_IMAGE = 0x003;
    private boolean isBlack = false;//是否被加入黑名单 true 在黑名单中 false 不在黑名单中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merri_function);
        ButterKnife.bind(this);
        isBlack();
        RxActivityTool.addActivity(this);
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.lay_make_money, R.id.lay_take_picture, R.id.lay_camera, R.id.iv_close, R.id.lay_xiangce, R.id.lay_make_video, R.id.lay_make_graphic_album})
    public void onViewClicked(View view) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {

            case R.id.lay_make_money://邀请赚钱
                if (!StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                startActivity(new Intent(MerriCameraFunctionAty.this, ImageUploadAty.class));
//                RxActivityTool.skipActivity(MerriCameraFunctionAty.this, InviteToMakeMoneyAty.class);
                break;
            case R.id.lay_take_picture://拍照
                if (StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    if (isBlack) {
                        RxToast.showToast("您已被加入黑名单，无法使用该功能");
                        return;
                    }
                    startActivity(new Intent(MerriCameraFunctionAty.this, TakePictureAty.class));
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.lay_camera://录像
                if (StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    if (isBlack) {
                        RxToast.showToast("您已被加入黑名单，无法使用该功能");
                        return;
                    }
                    startActivity(new Intent(MerriCameraFunctionAty.this, VideoMusicActivity.class).putExtra("activityId", activityId));
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.iv_close:
                onBackPressed();

                break;
            case R.id.lay_xiangce://相册
                if (StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    if (isBlack) {
                        RxToast.showToast("您已被加入黑名单，无法使用该功能");
                        return;
                    }
//                    调用相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.lay_make_video://视频制作
                if (StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    if (isBlack) {
                        RxToast.showToast("您已被加入黑名单，无法使用该功能");
                        return;
                    }
                    Matisse.from(MerriCameraFunctionAty.this)
                            .choose(MimeType.ofVideo(), false)
                            .showSingleMediaType(true)//只显示图片或视频一种类型
                            .capture(true)
                            .captureStrategy(
                                    new CaptureStrategy(true, MerriApp.getFileProviderName(this)))
                            .maxSelectable(99)
                            .gridExpectedSize(
                                    getResources().getDimensionPixelSize(R.dimen.dp120))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            .theme(R.style.Matisse_MerriChat)

                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(REQUEST_CODE_VIDEO_CHOOSE);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.lay_make_graphic_album://图文制作
                if (StringUtil.isLogin(MerriCameraFunctionAty.this)) {
                    if (isBlack) {
                        RxToast.showToast("您已被加入黑名单，无法使用该功能");
                        return;
                    }
                    Matisse.from(MerriCameraFunctionAty.this)
                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP, MimeType.WEBP), false)
                            .showSingleMediaType(true)//只显示图片或视频一种类型
                            .capture(true)
                            .captureStrategy(
                                    new CaptureStrategy(true, MerriApp.getFileProviderName(this)))
                            .maxSelectable(99)
                            .gridExpectedSize(
                                    getResources().getDimensionPixelSize(R.dimen.dp120))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            .theme(R.style.Matisse_MerriChat)

                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(REQUEST_CODE_IMG_CHOOSE);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
        }
    }

    private void testToken() {
        OkGo.<String>post(Urls.queryPromoWordsList)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 查询用户是否在黑名单中
     */
    private void isBlack() {
        OkGo.<String>post(Urls.isBlack)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    isBlack = jsonObjectEx.optBoolean("data");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VIDEO_CHOOSE) {
            if (resultCode == RESULT_OK) {
                videoList = new ArrayList<>();
                videoList.addAll(Matisse.obtainPathResult(data));

                Bundle bundle = new Bundle();
                bundle.putSerializable("videoList", videoList);
                RxActivityTool.skipActivity(MerriCameraFunctionAty.this, VideoEditorActivity.class, bundle);
            }
        }
        if (requestCode == REQUEST_CODE_IMG_CHOOSE) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> result = Matisse.obtainPathResult(data);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("imgPathList", (ArrayList<String>) result);
                RxActivityTool.skipActivity(this, ReleaseGraphicAlbumAty.class, bundle);
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {

                Uri originalUri = data.getData();        //获得图片的uri

                String[] proj = {MediaStore.Images.Media.DATA};

                //好像是android多媒体数据库的封装接口，具体的看Android文档

                Cursor cursor = managedQuery(originalUri, proj, null, null, null);

                //按我个人理解 这个是获得用户选择的图片的索引值

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                //将光标移至开头 ，这个很重要，不小心很容易引起越界

                cursor.moveToFirst();

                //最后根据索引值获取图片路径

                String path = cursor.getString(column_index);
                ArrayList<String> result = new ArrayList<>();
                result.add(path);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("imgPathList", result);
                RxActivityTool.skipActivity(this, ReleaseGraphicAlbumAty.class, bundle);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_VIDEO_ACTIVITY) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
