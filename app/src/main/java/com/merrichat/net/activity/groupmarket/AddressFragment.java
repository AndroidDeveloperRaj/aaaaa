package com.merrichat.net.activity.groupmarket;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.AddressAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AddressJsonModel;
import com.merrichat.net.model.AddressModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.DrawableCenterTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by amssy on 18/1/23.
 * <p>
 * 实物地址
 */

public class AddressFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
    /**
     * 点击回调
     */
    public onAddressOnClicklinster addressOnClicklinster;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    private View view;
    private Unbinder unbinder;
    private AddressAdapter adapter;
    private ArrayList<AddressModel> addressList = new ArrayList<>();
    private String key = "rec";//vir:表示的虚拟的收货地址, rec:表示的实物的收货地址
    private String type = "";//0:表示删除, type:1:表示的是查询单个信息,查询时传空
    private CommomDialog dialog;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_address, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    private void initView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AddressAdapter(key, R.layout.item_address, addressList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (addressList.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                //获取地址列表
                quDelRecAddr("", 0);
            }
        }
    }

    /**
     * 个人信息--查询收货地址列表/查询单个收货地址/删除收货地址
     *
     * @param type     //0:表示删除, type:1:表示的是查询单个信息,查询时传空
     * @param position 删除条目position
     */
    private void quDelRecAddr(final String type, final int position) {
        AddressJsonModel addressJsonModel = new AddressJsonModel();
        String memberId = UserModel.getUserModel().getMemberId();
        if (RxDataTool.isNullString(memberId)) {
            RxToast.showToast("请登录后查看！");
            return;
        }
        if (type.equals("0")) {
            addressJsonModel.setId(addressList.get(position).getId());
            addressJsonModel.setType(type);
        }
        addressJsonModel.setMemberId(memberId);
        addressJsonModel.setKey(key);
        String jsonStr = JSON.toJSONString(addressJsonModel);
        OkGo.<String>post(Urls.quDelRecAddr)//
                .tag(this)//
                .params("jsonStr", jsonStr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (type.equals("0")) {//删除地址
                                        if (data.optBoolean("success")) {
                                            RxToast.showToast("已删除!");
                                            addressList.remove(position);
                                        }
                                    } else {
                                        if (data != null && data.optBoolean("success")) {
                                            addressList.clear();
                                            JSONArray dataBean = data.optJSONArray("data");
                                            if (dataBean != null) {
                                                List<AddressModel> addressModelList = JSON.parseArray(dataBean.toString(), AddressModel.class);
                                                if (addressModelList != null && addressModelList.size() > 0) {
                                                    addressList.addAll(addressModelList);
                                                }
                                            }
                                        } else {
                                            RxToast.showToast(R.string.connect_to_server_fail);
                                        }
                                    }
                                    if (addressList.size() > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();

                                } else {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvEmpty.setVisibility(View.VISIBLE);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        switch (view.getId()) {
            case R.id.tv_editor://编辑按钮
                Intent intent = new Intent(getActivity(), EditorAddressActivity.class);
                intent.putExtra("type", "editor");
                intent.putExtra("addressModel", addressList.get(position));
                startActivity(intent);
                break;
            case R.id.tv_delete://删除按钮
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "是否删除该地址吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                type = "0";
                                quDelRecAddr(type, position);
                            }
                        }
                    }).setTitle("确认删除");
                    dialog.show();
                }
                break;
        }
    }

    /**
     * Item的点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Log.e("-->>>>  onItemClick",new Gson().toJson(addressList.get(position)));
        //收货人
        String addresseeName = addressList.get(position).getName();
        //电话
        String addresseePhone = addressList.get(position).getMobile();
        //地址ID
        String addresseeTownId = addressList.get(position).getId();
        //省市区
        String addresseeTownName = addressList.get(position).getRecAddress();
        //详细地址
        String addresseeAddress = addressList.get(position).getDetAddress();
        int addressType;//地址类型 0虚拟 1实际
        if (key.equals("rec")) {//vir:表示的虚拟的收货地址, rec:表示的实物的收货地址
            addressType = 1;
        } else {
            addressType = 0;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addresseeName", addresseeName);
            jsonObject.put("addresseePhone", addresseePhone);
            jsonObject.put("addresseeTownId", addresseeTownId);
            jsonObject.put("addresseeTownName", addresseeTownName);
            jsonObject.put("addressType", addressType);
            jsonObject.put("addresseeAddress", addresseeAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (addressOnClicklinster != null) {
            addressOnClicklinster.onAddress(jsonObject.toString());
        }
    }

    public void setAddressOnClicklinster(onAddressOnClicklinster addressOnClicklinster) {
        this.addressOnClicklinster = addressOnClicklinster;
    }

    @Subscribe
    public void OnEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_RECEIVING_ADDRESS_REC) {
            quDelRecAddr("", 0);
        }
    }

    @OnClick(R.id.tv_empty)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty:
                quDelRecAddr("", 0);
                break;
        }
    }

    public interface onAddressOnClicklinster {
        void onAddress(String addresseeJson);
    }
}

