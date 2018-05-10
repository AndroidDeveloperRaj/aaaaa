package com.merrichat.net.activity.my;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxDeviceTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/28.
 * <p>
 * 男神女神认证
 */

public class MenAndWomenVerificationAty extends BaseActivity {
    private static final int UPLOAD_SUC = 0x007;
    private static final int UPLOAD_Fail = 0x006;
    private static final int UPLOAD_PROGRESS = 0x005;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.webView_bang_dan)
    WebView webViewBangDan;
    String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    private String uri = "http://igomopub.igomot.net/clip-pub/boyOrGirl/index.html";
    private Uri fileUri;
    private int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 0x001;
    private String videoPath = "";
    private int status = 1;//0表示的是查看审核状态,1:表示的是提交男/女神的申请
    private ProgressDialog mProgressDialog;
    private int starStatus;// 男神女神认证状态
    private String fileSuffix = "";
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:
                    handled = true;
                    applyStar();


                    break;
                case UPLOAD_Fail:
                    mProgressDialog.dismiss();
                    RxToast.showToast("提交失败！");

                    handled = true;
                    break;
                case UPLOAD_PROGRESS:
                    Bundle data = msg.getData();
                    long currentSize = data.getLong("currentSize");
                    long totalSize = data.getLong("totalSize");
//                    OSSLog.logDebug("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                    Logger.e("Video Progress", currentSize + "/" + totalSize);
                    mProgressDialog.setProgress((int) ((currentSize * 100) / totalSize));


//                    mProgressDialog.setText("进度：" + (int) ((currentSize * 100) / totalSize));
                    handled = true;
                    break;
            }

            return handled;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_dan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("男神女神认证");
        starStatus = getIntent().getIntExtra("starStatus", -1);
        initProgressDialog();

        webViewBangDan.loadUrl(uri);
        //启用支持javascript
        WebSettings settings = webViewBangDan.getSettings();
        settings.setJavaScriptEnabled(true);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webViewBangDan.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //showErrorPage();//显示错误页面
            }
        });

        webViewBangDan.setWebChromeClient(new WebChromeClient() {
            /**
             * 当WebView加载之后，返回 HTML 页面的标题 Title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                    //showErrorPage();//显示错误页面
                }
            }
        });
        //添加js对象(必要)
        webViewBangDan.addJavascriptInterface(new JsOperation(), "Android");
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在上传视频，请稍后……");
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 创建保存录制得到的视频文件
     *
     * @return
     * @throws IOException
     */
    private File createMediaFile() throws IOException {
        if (RxFileTool.sdCardIsAvailable()) {
            File mediaStorageDir = new File(ConstantsPath.pic2videoPath, "Merri_Video");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Logger.e("create directory:::", "failed to create directory");
                    return null;
                }
            }
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp;
            String suffix = ".mp4";
            fileSuffix = imageFileName + suffix;
            videoPath = mediaStorageDir + File.separator + fileSuffix;
            File mediaFile = new File(videoPath);
            return mediaFile;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Logger.e("Video saved to:\n" +
                        data.getData());
                Logger.e("Video Path:\n" +
                        videoPath);
                if (data != null) {
                    ossUpload();
                }
                //Display the video
            } else if (resultCode == RESULT_CANCELED) {
                RxToast.showToast("录制视频取消！");
                // User cancelled the video capture
            } else {
                RxToast.showToast("录制视频失败！");
                // Video capture failed, advise user
            }
        }
    }

    /**
     * 阿里云OSS上传（默认是异步多文件上传）
     */
    private void ossUpload() {
        mProgressDialog.setProgress(0);
        mProgressDialog.show();

        File file = new File(videoPath);
        if (null == file || !file.exists()) {
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            return;
        }
        // 文件后缀
        if (file.isFile()) {
            // 获取文件后缀名
            fileSuffix = file.getName();
            Logger.e("fileSuffix：：：：", fileSuffix);
        }
        // 文件标识符objectKey
        final String objectKey = "alioss_" + System.currentTimeMillis() + fileSuffix;
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, fileSuffix, videoPath);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Logger.e("onSuccess：：：：", "onFailure::上传成功！");
                    handler.sendEmptyMessage(UPLOAD_SUC);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    Logger.e("onFailure：：：：", "onFailure::上传失败！");
                    handler.sendEmptyMessage(UPLOAD_Fail);

                }

                @Override
                public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                    Message msg = new Message();
                    msg.what = UPLOAD_PROGRESS;
                    Bundle bundle = new Bundle();
                    bundle.putLong("currentSize", currentSize);
                    bundle.putLong("totalSize", totalSize);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
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

    /**
     * 男神女神申请
     */
    private void applyStar() {
        File file = new File(ConstantsPath.pic2videoPath, "1.txt");//创建的空的文件
        if (!RxFileTool.isFileExists(file)) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OkGo.<String>post(Urls.APPLY_STAR)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("file", publicImgUrl + fileSuffix)
                .params("status", status)
                .params("deviceType", RxDeviceTool.getIMEI(MenAndWomenVerificationAty.this))
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {

                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    mProgressDialog.dismiss();
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        starStatus = 0;
                                        RxToast.showToast(data.optString("data"));
                                    } else {
                                        RxToast.showToast("提交失败，请重试！");
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
                        mProgressDialog.dismiss();
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private class JsOperation {
        //Html调用此方法传递数据，注解一定要留着否则会出错
        @JavascriptInterface
        public void shoot() {
            if (starStatus == 0) {
                RxToast.showToast("已提交，请等待审核！");
                return;
            } else if (starStatus == 1) {
                RxToast.showToast("审核已通过，无需再次提交审核！");
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            try {
                fileUri = Uri.fromFile(createMediaFile()); // create a file to save the video
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
            // 调用前置摄像头
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            intent.putExtra("autofocus", true); // 自动对焦
            // start the Video Capture Intent
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        }
    }

}
