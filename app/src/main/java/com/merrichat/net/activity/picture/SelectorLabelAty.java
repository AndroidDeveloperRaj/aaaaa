package com.merrichat.net.activity.picture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.SelectorLabelAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.SelectorLabelModel;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/20.
 * <p>
 * 选择标签
 */

public class SelectorLabelAty extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.et_label)
    EditText etLabel;
    @BindView(R.id.rv_receclerView)
    RecyclerView rvRececlerView;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    private ArrayList<SelectorLabelModel.DataBean> selectorLabelList;
    private SelectorLabelAdapter selectorLabelAdapter;
    private ArrayList<SelectorLabelModel.DataBean> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectorlabel);
        ButterKnife.bind(this);
        initTitle();
        initView();
        queryLabel();
    }

    private void initView() {
        selectorLabelList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRececlerView.setLayoutManager(gridLayoutManager);
        selectorLabelAdapter = new SelectorLabelAdapter(R.layout.item_selectorlable, selectorLabelList);
        rvRececlerView.setAdapter(selectorLabelAdapter);
        selectorLabelAdapter.setOnItemClickListener(this);

        etLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 20) {
                    RxToast.showToast("标签最多20个字");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 20) {

                    String titleText = etLabel.getText().toString().trim();
                    etLabel.setText(titleText.substring(0, 20));
                    etLabel.setSelection(etLabel.getText().toString().trim().length());
                }
            }
        });
    }

    private void initTitle() {
        tvTitleText.setText("选择标签");
    }

    @OnClick({R.id.iv_back, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sure:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("label_list", selectorLabelList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < selectorLabelList.size(); i++) {
            if (i == position) {
                if (selectorLabelList.get(i).isChecked()) {
                    selectorLabelList.get(i).setChecked(false);
                    selectedList.remove(selectorLabelList.get(i));
                } else {
                    if (selectedList.size() < 3) {
                        selectorLabelList.get(i).setChecked(true);
                        selectedList.add(selectorLabelList.get(i));
                    } else {
                        RxToast.showToast("最多可选3个标签");
                    }
                }
            }
        }
        selectorLabelAdapter.notifyDataSetChanged();
    }

    /**
     * 获取所有标签
     */
    private void queryLabel() {
        OkGo.<String>get(Urls.QUERY_LABEL)
                .tag(this)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    SelectorLabelModel selectorLabelModel = JSON.parseObject(response.body(), SelectorLabelModel.class);
                                    selectorLabelList.addAll(selectorLabelModel.getData());
                                    for (int i = 0; i < selectorLabelList.size(); i++) {
                                        SelectorLabelModel.DataBean dataBean = selectorLabelList.get(i);
                                        dataBean.setChecked(false);
                                    }
                                    selectorLabelAdapter.notifyDataSetChanged();
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
