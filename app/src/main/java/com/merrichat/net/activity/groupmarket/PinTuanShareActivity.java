package com.merrichat.net.activity.groupmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.TimeTools;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 邀请好友拼团界面
 * Created by amssy on 18/2/4.
 */

public class PinTuanShareActivity extends MerriActionBarActivity {
    /**
     * 时间
     */
    @BindView(R.id.tv_time)
    TextView tvTime;
    /**
     * 人数
     */
    @BindView(R.id.tv_num)
    TextView tvNum;
    /**
     * 微信
     */
    @BindView(R.id.imageView_weChat)
    ImageView imageViewWeChat;

    UMShareAPI umShareAPI = null;
    private Context mContext;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台
    //分享链接
    private String shareUrl = "";
    //分享封面图
    private String shareImage = "";
    //分享title
    private String title = "";
    //分享描述
    private String cotent = "";
    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享取消");
        }
    };
    private long currentTimeMillis;//倒计时时间
    private CountDownTimer countDownTimer;
    private long orderId;//订单ID
    private int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pin_tuan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            currentTimeMillis = intent.getLongExtra("currentTimeMillis", 0);
            orderId = intent.getLongExtra("orderId",0);
            sum = intent.getIntExtra("sum",0);
            shareImage = intent.getStringExtra("shareImage");
            //时间倒计时
            if (currentTimeMillis > 0) {
                countDownTimer = new CountDownTimer(currentTimeMillis, 1000) {

                    public void onTick(long millisUntilFinished) {
                        String countTimeByLong = TimeTools.getCountTimeByLong(millisUntilFinished);
                        tvTime.setText(countTimeByLong);
                    }
                    public void onFinish() {
                        tvTime.setText("拼团已结束");
                    }
                }.start();
            } else {
                tvTime.setText("拼团已结束");
            }

            tvNum.setText(""+sum);

        }
        umShareAPI = UMShareAPI.get(this);
        setLeftBack();
        setTitle("邀请好友拼团");
    }

    @OnClick(R.id.imageView_weChat)
    public void onViewClicked() {
        getPromoQRcode();
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(PinTuanShareActivity.this, shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        web.setDescription("" + cotent);
        web.setTitle("" + title);
        new ShareAction(PinTuanShareActivity.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    /**
     * 分享回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    private void getPromoQRcode() {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "6")//分享来源 6为拼团
                .params("orderId", orderId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    shareUrl = data.optJSONObject("data").getString("url");
                                    shareImage = "";
                                    title = "我正在拼团,赶紧来一起拼团吧";
                                    cotent = "还差"+sum+"人拼团成功";
                                    //分享到微信
                                    if (umShareAPI.isInstall(PinTuanShareActivity.this, SHARE_MEDIA.WEIXIN)) {
                                        ShareWeb(shareMedia);
                                    } else {
                                        RxToast.showToast("未安装微信客户端...");
                                    }
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
}
