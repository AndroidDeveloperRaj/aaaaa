package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.MyAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CircleImageView;
import com.merrichat.net.view.SpaceItemDecorations;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AMSSY1 on 2017/11/7.
 */

public class MyActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.clv_header)
    CircleImageView clvHeader;
    @BindView(R.id.tv_my_nickname)
    TextView tvMyNickname;
    @BindView(R.id.tv_edit_info)
    TextView tvEditInfo;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    /**
     * 0 影集
     * 1 收藏
     * 2 草稿
     */
    private int YINGJI_FLAG = 0;
    private int COLLECTION_FLAG = 1;
    private int DRAFT_FLAG = 2;
    private ArrayList<Object> list;
    private MyAdapter myAdapter;
    private int SPAN_NUM = 3;
    private SpaceItemDecorations spaceItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initTitle();
        list = new ArrayList<>();
        spaceItemDecoration = new SpaceItemDecorations(30, SPAN_NUM);
        rlRecyclerview.addItemDecoration(spaceItemDecoration);

        myHomeInfo();
    }

    private void myHomeInfo() {
        OkGo.<String>post(Urls.MY_HOME)//
                .tag(this)//
//                        .headers("header1", "headerValue1")//
//                        .params("param1", "paramValue1")//
//                        .params("param2", "paramValue2")//
//                        .params("param3", "paramValue3")//
//                        .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        RxToast.showToast(response.body());
                    }
                });
    }

    private void initTitle() {
        tvTitleText.setText("我的");
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.wode_shezhi2x);
        mSwipeRefreshLayout.setEnabled(false);
    }

}
