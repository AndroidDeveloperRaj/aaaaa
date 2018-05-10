package com.merrichat.net.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmanage.ApplyArbitramentActivity;
import com.merrichat.net.activity.groupmanage.ApplyRefundActivity;
import com.merrichat.net.activity.groupmanage.GroupItemClickListener;
import com.merrichat.net.activity.groupmarket.PinTuanShareActivity;
import com.merrichat.net.activity.grouporder.ChaKanWuLiuActivity;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.DeleteOrderModel;
import com.merrichat.net.model.OrderSignByOrderIdModel;
import com.merrichat.net.model.QueryOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WaiteGroupBuyModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BuyOrderAdapter extends BaseQuickAdapter<WaiteGroupBuyModel.Data, BaseViewHolder> {

    private Fragment mFragment;
    private int currentPage;  //当前页面的订单状态  几个页面共同复用这一个adapter 包括待成团（0），待发货（1），待收货（2），已结束（3），退款（4）
    private Context context;
    private GroupItemClickListener mListener;


    public BuyOrderAdapter(int viewId, List<WaiteGroupBuyModel.Data> list, Fragment fragment, int currentPage, GroupItemClickListener mListener) {
        super(viewId,list);
        this.context = fragment.getContext();
        mFragment = fragment;
        this.currentPage = currentPage;
        this.mListener = mListener;
    }


    @Override
    protected void convert(BaseViewHolder holder,final WaiteGroupBuyModel.Data data) {
        switch (currentPage) {
            case 0: // 待成团
                holder.setText(R.id.tv_sell_state,"等待成团").
                setText(R.id.tv_order_state,"邀请好友");
                holder.getView(R.id.tv_order_state).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        Intent intent = new Intent(mFragment.getContext(), PinTuanShareActivity.class);

                        final long time = 24 * 3600 * 1000 - data.remainTime;
                        intent.putExtra("currentTimeMillis", time);
                        intent.putExtra("orderId", Long.valueOf(data.orderId));
                        intent.putExtra("sum", (data.orderItemList.get(0).salesMemberSum - data.orderItemList.size()));
                        intent.putExtra("shareImage", data.orderItemList.get(0).img);
                        mFragment.startActivity(intent);
                    }
                });
                break;
            case 1:  //待发货
                holder.setText(R.id.tv_sell_state,"已付款等待发货").
                setText(R.id.tv_order_state,"取消订单");
                holder.getView(R.id.tv_order_state).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        CommomDialog dialog = new CommomDialog(context, R.style.dialog, "是否取消订单？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    deleteOrder(data.orderId);
                                }
                            }
                        }).setTitle("取消订单");
                        dialog.show();
                    }
                });
                break;
            case 2: // 待收货
                holder.setText(R.id.tv_sell_state,"已发货等待收货").
                setText(R.id.tv_order_state,"确认收货");
                holder.getView(R.id.tv_order_state).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        CommomDialog dialog = new CommomDialog(context, R.style.dialog, "是否确认收货？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    orderSignByOrderId(data.orderId + "");  //  确认收货 等待调试
                                }
                            }
                        }).setTitle("确认收货");
                        dialog.show();
                    }
                });
                holder.setGone(R.id.tv_check_trans_map,true);
                holder.getView(R.id.tv_check_trans_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }

                        switch (data.sendType) {
                            case "1":
                                Intent intent = new Intent(context, ChaKanWuLiuActivity.class);
                                intent.putExtra("orderId", data.orderId + "");
                                context.startActivity(intent);
                                break;
                            case "2":
                                RxToast.showToast("此订单自取！");
                                break;
                            case "3":
                                RxToast.showToast("此订单送货上门！");
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case 3: // 已结束
                holder.setText(R.id.tv_sell_state,"交易成功");
                if (data.iquidateStatus == 1){
                    holder.setGone(R.id.tv_order_state,false);
                }else {
                    holder.setGone(R.id.tv_order_state,true);
                }

                holder.setText(R.id.tv_order_state,"申请退款");
                holder.getView(R.id.tv_order_state).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        QueryOrderModel.QueryOrderRequestParams queryOrderRequestParams = new QueryOrderModel.QueryOrderRequestParams();
                        queryOrderRequestParams.orderId = data.orderId + "";
                        queryOrderRequestParams.orderStatus = "" + 4;
                        Intent intent = new Intent(context, ApplyRefundActivity.class);
                        intent.putExtra("queryOrderRequestParams", new Gson().toJson(queryOrderRequestParams));
                        mFragment.startActivityForResult(intent, currentPage);
                    }
                });
                break;
            case 4: //退款
                holder.setText(R.id.tv_sell_state,data.reStatusText);
                holder.setText(R.id.tv_order_state,"申请仲裁");

                if (data.reStatus == 3 || data.reStatus == 4 || data.reStatus == 5 || data.reStatus == 6 || data.iquidateStatus == 1) {
                    holder.setGone(R.id.tv_order_state,false);
                } else if (data.reStatus == 2) {
                    holder.setGone(R.id.tv_order_state,true);
                    holder.setText(R.id.tv_order_state,"申请退款");
                } else {
                    holder.setGone(R.id.tv_order_state,true);
                }
                holder.getView(R.id.tv_order_state).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (data.reStatus == 2) {
                            QueryOrderModel.QueryOrderRequestParams queryOrderRequestParams = new QueryOrderModel.QueryOrderRequestParams();
                            queryOrderRequestParams.orderId = data.orderId + "";
                            queryOrderRequestParams.orderStatus = "" + 7;
                            Intent intent = new Intent(context, ApplyRefundActivity.class);
                            intent.putExtra("queryOrderRequestParams", new Gson().toJson(queryOrderRequestParams));
                            mFragment.startActivityForResult(intent, currentPage);
                        } else {
                            Intent intent3 = new Intent(mFragment.getActivity(), ApplyArbitramentActivity.class);
                            intent3.putExtra("orderId", data.orderId);
                            mFragment.startActivityForResult(intent3, currentPage);
                        }
                    }
                });
                break;
        }

        holder.getView(R.id.tv_call_seller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(context, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", data.shopMemberId);
                intent.putExtra("receiverHeadUrl", data.shopUrl);
                intent.putExtra("activityId", MiscUtil.getActivityId());
                intent.putExtra("receiverName", data.shopMemberName);
                context.startActivity(intent);
            }
        });

        holder.setText(R.id.tv_sell_content_title,data.orderItemList.get(0).productName);
        holder.setText(R.id.tv_sell_content_discripe,data.orderItemList.get(0).productPropertySpecValue);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        holder.setText(R.id.tv_order_pay,Html.fromHtml(" 共<font color='#FF3D6F'>" + data.orderItemList.get(0).productNum + "</font>件商品合计<font color='#FF3D6F'>" + data.totalAmount + "</font>(含运费" + df.format(data.deliveryFee) + ")"));

        if (data.shopMemberName == null) {
            holder.setText(R.id.tv_sell_name,"卖家昵称：");
        } else {
            holder.setText(R.id.tv_sell_name,"卖家昵称：" + data.shopMemberName);
        }

        Glide.with(context).load(data.orderItemList.get(0).img).centerCrop().into( (ImageView) holder.getView(R.id.iv_sell_content_url));

    }

    /**
     * 确认收货
     *
     * @param orderId 账单id
     */
    private void orderSignByOrderId(String orderId) {
        ApiManager.getApiManager().getService(WebApiService.class).orderSignByOrderId(UserModel.getUserModel().getMemberId(), orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<OrderSignByOrderIdModel>() {
                    @Override
                    public void onNext(OrderSignByOrderIdModel orderIdModel) {
                        if (orderIdModel.success) {
                            GetToast.useString(context, "确认收货成功～！");
                            mListener.clickListener(currentPage);
                        } else {
                            Toast.makeText(context, orderIdModel.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 取消订单
     *
     * @param orderId 账单id
     */
    private void deleteOrder(String orderId) {
        ApiManager.getApiManager().getService(WebApiService.class).deleteOrder(UserModel.getUserModel().getMemberId(),
                orderId,
                "0",
                "2",
                "buy")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<DeleteOrderModel>() {
                    @Override
                    public void onNext(DeleteOrderModel orderIdModel) {
                        if (orderIdModel.success) {
                            GetToast.useString(context, "取消订单成功～！");
                            mListener.clickListener(currentPage);
                        } else {
                            Toast.makeText(context, orderIdModel.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
