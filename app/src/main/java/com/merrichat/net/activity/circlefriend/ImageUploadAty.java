package com.merrichat.net.activity.circlefriend;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.ossfile.STSGetter;
import com.merrichat.net.utils.RxTools.RxToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/23.
 */

public class ImageUploadAty extends BaseActivity {

    public static final int UPLOAD_SUC = 3;
    public static final int UPLOAD_Fail = 4;
    public static final int UPLOAD_PROGRESS = 5;
    private static final int RESULT_LOAD_IMAGE = 6;
    private String picturePath = "";
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.dele)
    Button dele;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.select)
    Button select;
    @BindView(R.id.et)
    EditText et;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:
                    tvProgress.setText("0");
                    Toast.makeText(ImageUploadAty.this, "upload_suc", Toast.LENGTH_SHORT).show();
                    handled = true;
                    break;
                case UPLOAD_Fail:
                    tvProgress.setText("0");
                    handled = true;
                    break;
                case UPLOAD_PROGRESS:
                    Bundle data = msg.getData();
                    long currentSize = data.getLong("currentSize");
                    long totalSize = data.getLong("totalSize");
                    OSSLog.logDebug("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                    tvProgress.setText("进度：" + (int) ((currentSize * 100) / totalSize));
                    handled = true;
                    break;
                case 1000:
                    RxToast.showToast("删除成功");
                    break;
            }

            return handled;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        ButterKnife.bind(this);


    }

//    @OnClick(R.id.btn)
//    public void onViewClicked() {

//    }

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        Toast.makeText(ImageUploadAty.this, "init Samples fail", Toast.LENGTH_SHORT).show();
        return false;
    }

    @OnClick({R.id.select, R.id.btn, R.id.dele})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.btn:
                PutObjectSamples putObjectSamples = new PutObjectSamples(getApplicationContext(), Config.bucket, et.getText().toString(), picturePath);
                if (checkNotNull(putObjectSamples)) {
                    putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                            handler.sendEmptyMessage(UPLOAD_SUC);
                        }

                        @Override
                        public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                            handler.sendEmptyMessage(UPLOAD_Fail);
                        }

                        @Override
                        public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                            Message msg = Message.obtain();
                            msg.what = UPLOAD_PROGRESS;
                            Bundle data = new Bundle();
                            data.putLong("currentSize", currentSize);
                            data.putLong("totalSize", totalSize);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    });
                }
                break;
            case R.id.dele:
                dele();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.d("PickPicture", picturePath);
            cursor.close();
        }
    }


    private void dele() {
        OSSCredentialProvider credentialProvider;
        credentialProvider = new STSGetter(Config.STSSERVER);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(ImageUploadAty.this, Config.endpoint, credentialProvider, conf);

        // 创建删除请求
        DeleteObjectRequest delete = new DeleteObjectRequest(Config.bucket, "1578.jpg");
        // 异步删除
        OSSAsyncTask deleteTask = oss.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.d("asyncCopyAndDelObject", "success!");
                Message msg = Message.obtain();
                msg.what = 1000;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }

        });
    }
}
