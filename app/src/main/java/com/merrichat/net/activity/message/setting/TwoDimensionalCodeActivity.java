package com.merrichat.net.activity.message.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/19.
 * 群二维码
 */

public class TwoDimensionalCodeActivity extends MerriActionBarActivity {

    /**
     * 群头像
     */
    @BindView(R.id.civ_group_photo)
    CircleImageView civGroupPhoto;

    /**
     * 群名字
     */
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;

    /**
     * 二维码
     */
    @BindView(R.id.iv_erwei_code)
    ImageView ivErweiCode;

    /**
     * 群id
     */
    private String groupId = "";

    /**
     * 群头像
     */
    private String communityImgUrl;

    /**
     * 群名称
     */
    private String communityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twodimensional_code);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("群二维码");
        setLeftBack();
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        communityImgUrl = getIntent().getStringExtra("communityImgUrl");
        communityName = getIntent().getStringExtra("communityName");
        Glide.with(cnt).load(communityImgUrl).error(R.mipmap.ic_preloading).into(civGroupPhoto);
        tvGroupName.setText(communityName);
        inviteQRCode();
    }


    /**
     * 获取群二维码
     */
    private void inviteQRCode() {
        OkGo.<String>post(Urls.inviteQRCode)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityId", groupId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                String url = data.optString("url");
                                try {
                                    if (!TextUtils.isEmpty(url)) {
                                        Bitmap bitmap = FileUtils.createQRCode(url, 600);
                                        ivErweiCode.setImageBitmap(bitmap);
                                    }
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
