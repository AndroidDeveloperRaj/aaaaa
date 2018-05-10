package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TImage;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ResonModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.LoginKeyboardUtil;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by amssy on 17/9/2.
 * 举报
 */

public class InformActivity extends BaseActivity {
    public static final int activityId = MiscUtil.getActivityId();
    private static final int UPLOAD_SUC = 0x00001;
    private final int REQUEST_CODE_CHOOSE = 0x0001;
    @BindView(R.id.lin_inform_group)
    LinearLayout linInformGroup;//举报原因
    @BindView(R.id.rel_des)
    RelativeLayout relDes;
    @BindView(R.id.editText_inform)
    EditText editTextInform;
    @BindView(R.id.tv_inform)
    TextView tvInform;
    @BindView(R.id.lin_image_group)
    LinearLayout linImageGroup;
    @BindView(R.id.lin_inform)
    LinearLayout linInform;
    @BindView(R.id.btn_submit_inform)
    Button btnSubmitInform;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.ll_login_root)
    LinearLayout llLoginRoot;
    @BindView(R.id.lin_content)
    LinearLayout linContent;
    private List<String> reason;
    private ImageView[] imageViews;
    private TextView[] textViews;
    private String strReason = "";
    private ArrayList<String> list_imgUrl;
    //选择的图片的路径
    private ArrayList<String> selectPhotoList;
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    private int MAX_PIC_SIZE = 100 * 1024;
    private int position = -1;
    private String contentId;
    private String authority;
    private String toMemberId;
    private String toMemberPhone;
    private String toMemberNick;
    private String toShowBarTitle;
    private int reportType = 1;
    private List<String> picList;
    private ResonModel resonModel;
    private String fileSuffix;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handle = false;
            switch (msg.what) {
                case UPLOAD_SUC://图片上传成功
                    reportLog();
                    break;
            }
            return handle;
        }
    });
    private ArrayList<String> resultStringList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        ButterKnife.bind(this);
        //滑动返回关闭
        authority = MerriApp.getFileProviderName(this);
        initGetIntent();
        initAddGridView();
        queryTypeAll();
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            contentId = intent.getStringExtra("contentId");
            toMemberId = intent.getStringExtra("memberId");
            toMemberNick = intent.getStringExtra("memberName");
            toMemberPhone = intent.getStringExtra("memberPhone");
            toShowBarTitle = intent.getStringExtra("title");
        }
        tvTitleText.setText("举报");

        keepLoginBtnNotOver(llLoginRoot, linContent);

        //触摸外部，键盘消失
        llLoginRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LoginKeyboardUtil.closeKeyboard(InformActivity.this);
                return false;
            }
        });
    }

    /**
     * 保持登录按钮始终不会被覆盖
     *
     * @param root
     * @param subView
     */
    private void keepLoginBtnNotOver(final View root, final View subView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                // 获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                // 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                // 若不可视区域高度大于200，则键盘显示,其实相当于键盘的高度
                if (rootInvisibleHeight > 200) {
                    // 显示键盘时
                    int srollHeight = rootInvisibleHeight - (root.getHeight() - subView.getHeight()) - LoginKeyboardUtil.getNavigationBarHeight(root.getContext());
                    if (srollHeight > 0) {//当键盘高度覆盖按钮时
                        root.scrollTo(0, srollHeight);
                    }
                } else {
                    // 隐藏键盘时
                    root.scrollTo(0, 0);
                }
            }
        });
    }

    private void initAddGridView() {
        selectPhotoList = new ArrayList<>();
        list_imgUrl = new ArrayList<>();
        initaddImageLinGroup();
    }

    private void initaddImageLinGroup() {
        linImageGroup.removeAllViews();
        for (int i = 0; i < list_imgUrl.size(); i++) {
            View view = LayoutInflater.from(cnt).inflate(R.layout.item_inform, null);
            SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.imageView_inform);
            File file = new File(list_imgUrl.get(i));
            imageView.setImageURI(Uri.fromFile(file));
            //图片的点击事件
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExitScreenDialog(InformActivity.this, finalI);
                }
            });
            linImageGroup.addView(view);
        }
    }

    private void showExitScreenDialog(Context context, final int position) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("确定删除该张图片吗?")//
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
                                //处理监听事件
                                list_imgUrl.remove(position);
                                resultStringList.remove(position);
                                linImageGroup.removeAllViews();
                                initaddImageLinGroup();
                            }
                        });
                    }
                });
    }

    @OnClick({R.id.lin_inform, R.id.btn_submit_inform, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.lin_inform://添加图片证据
                if (list_imgUrl.size() < 9) {
                    selectPhotoList.clear();
                    MultiImageSelector selector = MultiImageSelector.create(InformActivity.this);
                    selector.showCamera(true);
                    selector.count(9);
                    selector.multi();
                    selector.origin(resultStringList);
                    selector.start(InformActivity.this, REQUEST_CODE_CHOOSE);
                   /* Matisse.from(InformActivity.this)
                            .choose(MimeType.ofImage(), false)
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
                            .forResult(REQUEST_CODE_CHOOSE);*/
                } else {
                    RxToast.showToast("图片证据最多9张喔～");
                }
                break;
            case R.id.btn_submit_inform://提交
                if (position == -1) {
                    RxToast.showToast("请选择举报原因");
                } else if (TextUtils.equals(editTextInform.getText().toString(), "")) {
                    RxToast.showToast("请输入举报原因");
                } else {
                    if (list_imgUrl.size() > 0) {
                        ossUpload(list_imgUrl);
                    } else {
                        reportLog();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            resultStringList = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);

            if (resultStringList != null) {
                compressImg(resultStringList);
            }

        }
       /* if (requestCode == REQUEST_CODE_CHOOSE && data != null) {

            List<String> resultStringList = Matisse.obtainPathResult(data);
            if (resultStringList != null) {
                compressImg((ArrayList<String>) resultStringList);
            }
        }*/
    }

    /**
     * 举报原因
     */
    private void initView() {
        imageViews = new ImageView[reason.size()];
        textViews = new TextView[reason.size()];
        final RelativeLayout[] relativeLayouts = new RelativeLayout[reason.size()];
        for (int i = 0; i < reason.size(); i++) {
            View view = LayoutInflater.from(cnt).inflate(R.layout.layout_inform_reason, null);
            RelativeLayout rel_inform = (RelativeLayout) view.findViewById(R.id.rel_inform);
            TextView textView = (TextView) view.findViewById(R.id.tv_reason);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_inform_check);
            View v = view.findViewById(R.id.view_inform);

            textViews[i] = textView;
            textViews[i].setText("" + reason.get(i));
            textViews[i].setTextColor(getResources().getColor(R.color.black_new_two));
            if (i == reason.size() - 1) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }

            imageViews[i] = imageView;
            imageViews[i].setId(i);

            final int finalI = i;
            relativeLayouts[i] = rel_inform;
            relativeLayouts[i].setId(i);

            linInformGroup.addView(view);
        }

        for (int i = 0; i < reason.size(); i++) {
            relativeLayouts[i].setTag(relativeLayouts);
            relativeLayouts[i].setOnClickListener(new TabOnClick());
        }

    }

    @OnTextChanged(value = R.id.editText_inform, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged() {
        int length = editTextInform.getText().length();
        tvInform.setText(length + "/200");
        if (length == 200) {
            RxToast.showToast("投诉原因最多200个字！");
        }
    }

    /**
     * 查询举报原因列表
     */
    private void queryTypeAll() {
        OkGo.<String>get(Urls.QUERY_TYPE_ALL)
                .tag(this)
                //.params("id", contentId)//帖子ID
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    resonModel = JSON.parseObject(response.body(), ResonModel.class);
                                    reason = new ArrayList<>();
                                    for (int i = 0; i < resonModel.getData().size(); i++) {
                                        reason.add(resonModel.getData().get(i).getReasonText());
                                    }
                                    initView();
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

    /**
     * 举报帖子
     */
    private void reportLog() {
        String picList = "";
        for (int i = 0; i < selectPhotoList.size(); i++) {
            if (picList.equals("")) {
                picList = selectPhotoList.get(i);
            } else {
                picList = picList + "," + selectPhotoList.get(i);
            }
        }
        OkGo.<String>get(Urls.REPORT_LOG)
                .tag(this)
                .params("fromMemberPhone", UserModel.getUserModel().getMobile())//举报人的电话
                .params("fromMemberId", UserModel.getUserModel().getMemberId())//举报人的id
                .params("fromMemberName", UserModel.getUserModel().getRealname())//举报人姓名
                .params("toMemberId", toMemberId)//被举报人的id
                .params("toMemberPhone", toMemberPhone)//被举报人的电话
                .params("toMemberNick", toMemberNick)//被举报人的昵称
                .params("reportId", contentId)//日志ID
                .params("toShowBarTitle", toShowBarTitle)//日志title
                .params("reasonId", resonModel.getData().get(position).getId())//举报原因id
                .params("reasonText", resonModel.getData().get(position).getReasonText())//举报原因
                .params("reportRemark", editTextInform.getText().toString())//举报备注
                .params("reportType", reportType)//举报类型
                .params("picList", picList)//举报图片
                .execute(new StringDialogCallback(this, "正在提交举报资料...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast(data.optJSONObject("data").optString("message"));
                                    finish();
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                    finish();
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
                list_imgUrl.clear();
                for (int i = 0; i < images.size(); i++) {
                    list_imgUrl.add(images.get(i).getCompressPath());
                }
                initaddImageLinGroup();
            }

            @Override
            public void onCompressFailed(ArrayList<TImage> images, String msg) {
                Logger.e("onCompressFailed", msg);

            }
        }).compress();
    }

    private ArrayList<TImage> getTImagesWithUris(ArrayList<String> urls, TImage.FromType fromType) {
        ArrayList<TImage> tImages = new ArrayList();
        for (String url : urls) {
            TImage tImage = TImage.of(url, fromType);
            tImage.setCompressPath(url);
            tImages.add(tImage);
        }
        return tImages;
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
        // 文件后缀
        fileSuffix = "";
        if (file.isFile()) {
            // 获取文件后缀名
            fileSuffix = file.getName();
            Logger.e("fileSuffix：：：：", fileSuffix);
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
                    selectPhotoList.add(publicImgUrl + fileSuffix);
                    if (urls.size() == 0) {
                        handler.sendEmptyMessage(UPLOAD_SUC);
                    }
                    ossUpload(urls);// 递归同步效果
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    Logger.e("onFailure：：：：", "onFailure::上传失败！");
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
        Toast.makeText(this, "init Samples fail", Toast.LENGTH_SHORT).show();
        return false;
    }

    class TabOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (relDes.getVisibility() == View.GONE) {
                relDes.setVisibility(View.VISIBLE);
            }
            RelativeLayout[] linearLayouts = (RelativeLayout[]) v.getTag();
            RelativeLayout linearLayout = (RelativeLayout) v;
            for (int i = 0; i < linearLayouts.length; i++) {
                if (linearLayout.equals(linearLayouts[i])) {
                    imageViews[i].setVisibility(View.VISIBLE);
                    textViews[i].setTextColor(getResources().getColor(R.color.normal_red));
                    strReason = reason.get(i);
                    position = i;
                } else {
                    textViews[i].setTextColor(getResources().getColor(R.color.black_new_two));
                    imageViews[i].setVisibility(View.GONE);
                }
            }
        }
    }

}
