package com.merrichat.net.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.grouporder.ChaKanWuLiuActivity;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.model.WaiteGroupBuyModel;
import com.merrichat.net.view.MyListView;

import org.akita.ui.adapter.AkBaseAdapter;

/**
 * Created by amssy on 18/1/23.
 * 群订单管理adapter
 */

public class GroupBuyManagementAdapter extends AkBaseAdapter<WaiteGroupBuyModel.Data> {
    private Fragment mFragment;
    private int currentPage;  //当前页面的订单状态  几个页面共同复用这一个adapter 包括待成团（0），待发货（1），待收货（2），已结束（3），仲裁退款（4）
    private Context context;

    private BuyerListAdapter buyerListAdapter;

    public GroupBuyManagementAdapter(Context context, Fragment fragment, int currentPage) {
        super(fragment.getActivity());
        this.context = context;
        mFragment = fragment;
        this.currentPage = currentPage;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = (mFragment.getActivity().getLayoutInflater()).inflate(
                    R.layout.item_group_buy_managment, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WaiteGroupBuyModel.Data data = mData.get(position);

        Log.e("------>>>", new Gson().toJson(data));


        holder.tvBuyerAddress.setText("买家地址：" + data.addresseeDetailed);
        holder.tvBuyerNickname.setText("买家昵称：" + data.memberName);
        holder.tvBuyerName.setText("买家姓名:" + data.addresseeName);

        Glide.with(context).load(data.orderItemList.get(0).img).centerCrop().into(holder.ivSellContentUrl);

        holder.tvSellContentTitle.setText(data.orderItemList.get(0).productName);
        holder.tvSellContentDiscripe.setText(data.orderItemList.get(0).productPropertySpecValue);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        holder.tvOrderPay.setText(Html.fromHtml(" 共<font color='#FF3D6F'>" + data.orderItemList.get(0).productNum + "</font>件商品合计<font color='#FF3D6F'>" + data.totalAmount + "</font>(含运费" + df.format(data.deliveryFee) + ")"));


        switch (currentPage) {
            case 0: // 待成团
                holder.rlBuyerAddress.setVisibility(View.GONE);
                holder.llBuyerInformation.setVisibility(View.GONE);
                holder.tvOrderPay.setVisibility(View.GONE);
                holder.tv_call_seller.setVisibility(View.GONE);

                holder.tv_contact_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.shopMemberId).putExtra("receiverName", data.shopName).putExtra("receiverHeadUrl", ""));
                    }
                });

                holder.llBuyerList.setVisibility(View.VISIBLE);

                break;
            case 1:  //待发货
                holder.tv_contact_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.shopMemberId).putExtra("receiverName", data.shopName));
                    }
                });

                holder.tv_call_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.memberId).putExtra("receiverName", data.memberName));
                    }
                });
                break;
            case 2: // 待收货
                holder.tv_contact_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.shopMemberId).putExtra("receiverName", data.shopName));
                    }
                });
                holder.tv_check_trans_map.setVisibility(View.VISIBLE);
                holder.tv_check_trans_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ChaKanWuLiuActivity.class).putExtra("orderId", data.orderId));
                    }
                });


                holder.tv_call_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.memberId).putExtra("receiverName", data.memberName));
                    }
                });

                break;
            case 3: // 已结束
                holder.tv_contact_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.shopMemberId).putExtra("receiverName", data.shopName));
                    }
                });

                holder.tv_call_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.memberId).putExtra("receiverName", data.memberName));
                    }
                });


                holder.tv_check_trans_map.setVisibility(View.VISIBLE);
                holder.tv_check_trans_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ChaKanWuLiuActivity.class).putExtra("orderId", data.orderId));
                    }
                });

                break;
            default: //仲裁
                holder.tv_contact_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.shopMemberId).putExtra("receiverName", data.shopName));
                    }
                });

                holder.tv_call_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SingleChatActivity.class).putExtra("receiverMemberId", data.memberId).putExtra("receiverName", data.memberName));
                    }
                });

                holder.tv_check_trans_map.setVisibility(View.VISIBLE);
                holder.tv_check_trans_map.setText("仲裁退款");
                holder.tv_check_trans_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "你点击了仲裁退款" + position, Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

        return convertView;
    }

    class ViewHolder {
        /**
         * 查看物流/仲裁退款
         */
        private TextView tv_check_trans_map;

        /**
         * 联系买家
         */
        private TextView tv_call_seller;

        /**
         * 联系卖家
         */
        private TextView tv_contact_seller;


        /**
         * 买家地址
         */
        private RelativeLayout rlBuyerAddress;
        private TextView tvBuyerAddress;


        /**
         * 买家信息
         */
        private LinearLayout llBuyerInformation;
        private TextView tvBuyerNickname;
        private TextView tvBuyerName;

        /**
         * 共计N件商品 N元
         */
        private TextView tvOrderPay;

        private MyListView myListView;

        //商品信息
        private ImageView ivSellContentUrl;
        private TextView tvSellContentTitle;  //tv_sell_content_title
        private TextView tvSellContentDiscripe;//tv_sell_content_discripe

        /**
         * 买家和卖家信息
         * 只在待成团列表展示
         */
        private LinearLayout llBuyerList;

        public ViewHolder(View convertView) {
            tv_contact_seller = (TextView) convertView.findViewById(R.id.tv_contact_seller);
            tv_call_seller = (TextView) convertView.findViewById(R.id.tv_call_seller);
            tv_check_trans_map = (TextView) convertView.findViewById(R.id.tv_check_trans_map);

            rlBuyerAddress = (RelativeLayout) convertView.findViewById(R.id.rl_buyer_address);
            tvBuyerAddress = (TextView) convertView.findViewById(R.id.tv_buyer_address);

            llBuyerInformation = (LinearLayout) convertView.findViewById(R.id.ll_buyer_information);
            tvBuyerNickname = (TextView) convertView.findViewById(R.id.tv_buyer_nickname);
            tvBuyerName = (TextView) convertView.findViewById(R.id.tv_buyer_name);
            ivSellContentUrl = (ImageView) convertView.findViewById(R.id.iv_sell_content_url);

            tvSellContentTitle = (TextView) convertView.findViewById(R.id.tv_sell_content_title);
            tvSellContentDiscripe = (TextView) convertView.findViewById(R.id.tv_sell_content_discripe);

            tvOrderPay = (TextView) convertView.findViewById(R.id.tv_order_pay);
            myListView = (MyListView) convertView.findViewById(R.id.my_listview);

            llBuyerList = (LinearLayout) convertView.findViewById(R.id.ll_buyer_list);
        }
    }
}

