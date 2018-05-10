package com.merrichat.net.activity.message.setting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.TImage;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.ChatAmplificationActivity;
import com.merrichat.net.activity.message.ProductListAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.ImageAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GoodsTradingModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.weidget.RecycleViewDivider;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by amssy on 18/1/25.
 * 修改群名称/添加商品
 */

public class ModifyGroupNameActivity extends MerriActionBarActivity {
    private static final int REQUEST_IMAGE = 1;
    private static final int UPLOAD_SUC = 2;
    private static final int UPLOAD_Fail = 3;
    public static final int activityId = MiscUtil.getActivityId();
    private String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    @BindView(R.id.et_group_name)
    ClearEditText etGroupName;
    /**
     * 添加图片
     */
    @BindView(R.id.tv_add_picture)
    TextView tvAddPicture;
    /**
     * 图片列表
     */
    @BindView(R.id.rv_img)
    RecyclerView rvImg;
    private String title = "";
    private int sourceFlag = 0;

    /**
     * 群名称
     */
    private String groupId = "";
    private ArrayList<String> imgUrlList;//选择的本地图片
    private ArrayList<String> upImgList;//图片上传成功后 文件名的集合
    private ImageAdapter adapter;
    private LoadingDialog loadingDialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:
                    Intent intent = new Intent();
                    intent.putExtra("productName", etGroupName.getText().toString());
                    intent.putExtra("productImgUrlList", upImgList);
                    setResult(RESULT_OK, intent);
                    finish();
                    handled = true;
                    break;
                case UPLOAD_Fail:
                    handled = true;
                    closeDialog();
                    break;
            }
            return handled;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_group_name);
        ButterKnife.bind(this);
        initView();
        ininData();
    }

    private void ininData() {
        imgUrlList = new ArrayList<>();
        upImgList = new ArrayList<>();
        loadingDialog = new LoadingDialog(this).setLoadingText("上传图片中...").setInterceptBack(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvImg.setLayoutManager(layoutManager);
        rvImg.addItemDecoration(new RecycleViewDivider(cnt, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.line_grayD9)));
        rvImg.setNestedScrollingEnabled(false);
        adapter = new ImageAdapter(R.layout.item_img, imgUrlList);
        rvImg.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(cnt, ChatAmplificationActivity.class);
                intent.putStringArrayListExtra("imgUrl", imgUrlList);
                cnt.startActivity(intent);
                ((ModifyGroupNameActivity) cnt).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleItemDialog(position);
                return false;
            }
        });
    }

    private void showDeleItemDialog(final int index) {
        final NormalDialog dialog = new NormalDialog(cnt);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("确定要删除此张照片吗?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(null)//
                .dismissAnim(null)//
                .show();
        dialog.setCanceledOnTouchOutside(true);
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
                        imgUrlList.remove(index);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initView() {
        if (getIntent().getFlags() == SetGoodsTradingActivity.activityId) {
            sourceFlag = 1;
            title = "添加商品";
            rvImg.setVisibility(View.VISIBLE);
            tvAddPicture.setVisibility(View.VISIBLE);
        } else {
            sourceFlag = 0;
            title = "修改群名称";
            groupId = getIntent().getStringExtra("groupId");
            rvImg.setVisibility(View.GONE);
            tvAddPicture.setVisibility(View.GONE);
        }
        setTitle(title);
        setLeftBack();

        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = etGroupName.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
                //如果是汉字则为两个字符
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，
                    //如果在这个范围内则算一个字符，
                    //如果不在这个范围比如是汉字的话就是两个字符
                    if (charAt >= 32 && charAt <= 122) {
                        mTextMaxlenght++;
                    } else {
                        mTextMaxlenght += 2;
                    }
                    // 当最大字符大于32时，进行字段的截取，并进行提示字段的大小
                    if (mTextMaxlenght > 32) {
                        // 截取最大的字段
                        String newStr = str.substring(0, i);
                        etGroupName.setText(newStr);
                        // 得到新字段的长度值
                        editable = etGroupName.getText();
                        int newLen = editable.length();
                        if (selEndIndex > newLen) {
                            selEndIndex = editable.length();
                        }
                        // 设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setRightText("保存", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etGroupName.getText().toString())) {
                    if (sourceFlag == 1) {
                        GetToast.useString(cnt, "请输入商品名称");
                    } else {
                        GetToast.useString(cnt, "请输入群名称");
                    }
                    return;
                }
                if (sourceFlag == 1) {

                    if (imgUrlList.size() > 0) {
                        if (loadingDialog != null) {
                            loadingDialog.show();
                        } else {
                            loadingDialog = new LoadingDialog(cnt).setLoadingText("上传图片中...").setInterceptBack(true);
                            loadingDialog.show();
                        }
                        compressImg(imgUrlList);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("productName", etGroupName.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    String temp = etGroupName.getText().toString().trim();
                    if (temp.contains("讯") && temp.contains("美") || temp.contains("merrichat") || temp.contains("m e r r i c h a t")) {
                        RxToast.showToast("输入的群昵称不合法，请重新输入");
                        return;
                    }
                    updateGroupOfName();
                }
            }
        });
    }


    @OnClick({R.id.tv_add_picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_picture://添加图片
                selectPic();
                break;
        }
    }


    /**
     * 添加图片
     */
    private void selectPic() {
        MultiImageSelector selector = MultiImageSelector.create(cnt);
        selector.showCamera(true);
        selector.count(9);
        selector.multi();
        selector.origin(imgUrlList);
        selector.start((Activity) cnt, REQUEST_IMAGE);
    }

    /**
     * 批量压缩图片
     *
     * @param urlsList
     */
    private void compressImg(final ArrayList<String> urlsList) {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(200 * 1024)
                .enableReserveRaw(true)
                .create();
        CompressImageImpl.of(this, config, getTImagesWithUris(urlsList, TImage.FromType.OTHER), new CompressImage.CompressListener() {
            @Override
            public void onCompressSuccess(ArrayList<TImage> images) {
                ossUpload(images);
            }

            @Override
            public void onCompressFailed(ArrayList<TImage> images, String msg) {
                Logger.e("onCompressFailed", msg);
                closeDialog();
            }
        }).compress();
    }


    /**
     * 阿里云OSS上传（默认是异步多文件上传）
     *
     * @param images
     */
    private void ossUpload(final ArrayList<TImage> images) {
        if (images.size() <= 0) {
            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
            return;// 这个return必须有，否则下面报越界异常，原因自己思考下哈
        }
        final String url = images.get(0).getCompressPath();
        if (TextUtils.isEmpty(url)) {
            images.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(images);
            return;
        }

        File file = new File(url);
        if (null == file || !file.exists()) {
            images.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(images);
            return;
        }

        final String fileSuffix = "MerriChat_Product_" + FileUtils.getCurrentDate() + ".jpg";
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, fileSuffix, url);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    images.remove(0);
                    Logger.e("onSuccess：：：：", "onSuccess::上传成功！");
                    upImgList.add(publicImgUrl + fileSuffix);
                    if (images.size() == 0) {
                        handler.sendEmptyMessage(UPLOAD_SUC);
                    }
                    ossUpload(images);// 递归同步效果
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

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        return false;
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
     * 修改群名字
     */
    private void updateGroupOfName() {
        OkGo.<String>post(Urls.updateGroupOfName)
                .params("cid", groupId)
                .params("communityName", etGroupName.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.optJSONObject("data").optBoolean("success")) {
                                    GetToast.useString(cnt, "修改成功");
                                    MyEventBusModel eventBusModel = new MyEventBusModel();
                                    eventBusModel.REFRESH_GROUP_DATA_NAME = true;
                                    EventBus.getDefault().post(eventBusModel);
                                    finish();
                                } else {
                                    String message = jsonObject.optJSONObject("data").optString("message");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                imgUrlList.clear();
                imgUrlList.addAll(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialog();
    }

    private void closeDialog() {
        if (loadingDialog != null) {
            loadingDialog.close();
            loadingDialog = null;
        }
    }
}
