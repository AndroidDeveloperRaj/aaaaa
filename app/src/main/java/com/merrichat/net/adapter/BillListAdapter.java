package com.merrichat.net.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.TradeItemOfMonthModel;
import com.merrichat.net.model.TransactionHistoryModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhui.guo on 2016/4/23.
 * 钱包交易记录adapter
 */
public class BillListAdapter extends TradeListAdapter{
    private Context mContext;
    private List<TransactionHistoryModel> transactionHistoryModelList;

    /**
     * 选中条目
     * 0 余额
     * 1 金币
     * 2 通话费
     * 默认为1
     */
    private int checkedMoney = 1;


    /**
     * 账户余额
     */
    private String cashBalance = "";

    /**
     * 讯美币余额
     */
    private String giftBalance = "";

    /**
     * 通讯余额
     */
    private String couponBalance = "";


    public BillListAdapter(Context mContext, List<TransactionHistoryModel> list,int checkedMoney,String cashBalance,String giftBalance,String couponBalance) {
        this.mContext = mContext;
        this.transactionHistoryModelList = list;
        this.checkedMoney = checkedMoney;
        this.cashBalance = cashBalance;
        this.giftBalance = giftBalance;
        this.couponBalance = couponBalance;
    }


    @Override
    public Object getItem(int section, int position) {
        return transactionHistoryModelList.get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return transactionHistoryModelList.size();
    }

    @Override
    public int getCountForSection(int section) {
        return transactionHistoryModelList.get(section).getList_item().size();
    }



    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(ArrayList<TransactionHistoryModel> list,int checkedMoney,String cashBalance,String giftBalance,String couponBalance) {
        this.transactionHistoryModelList = list;
        this.checkedMoney = checkedMoney;
        this.cashBalance = cashBalance;
        this.giftBalance = giftBalance;
        this.couponBalance = couponBalance;
        notifyDataSetChanged();
    }


    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final TradeItemOfMonthModel tradeItemOfMonthModel = transactionHistoryModelList.get(section).getList_item().get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_record_list, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_income_or_expenditure = (TextView) convertView.findViewById(R.id.tv_income_or_expenditure);
            viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolder.iv_header = (CircleImageView) convertView.findViewById(R.id.iv_header);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_title.setText(tradeItemOfMonthModel.getUser_name());
        Glide.with(mContext).load(tradeItemOfMonthModel.getImgUrl()).into(viewHolder.iv_header);
        Glide.with(mContext).load(tradeItemOfMonthModel.getImgUrl()).error(R.mipmap.ic_preloading).into(viewHolder.iv_header);
        viewHolder.tv_time.setText(tradeItemOfMonthModel.getCreated());
        if ("0".equals(tradeItemOfMonthModel.getTrade_type())){//收入
            viewHolder.tv_income_or_expenditure.setText("+￥"+tradeItemOfMonthModel.getJour_value());
        }else if ("1".equals(tradeItemOfMonthModel.getTrade_type())){//支出
            viewHolder.tv_income_or_expenditure.setText("-￥"+tradeItemOfMonthModel.getJour_value());
        }else {
            viewHolder.tv_income_or_expenditure.setText("￥"+tradeItemOfMonthModel.getJour_value());
        }
        viewHolder.tv_type.setText(tradeItemOfMonthModel.getTitle());

        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_record_framlayout, null);
            // TODO 添加该句话避免；Null指针
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            viewHolder = new GroupViewHolder();
            viewHolder.tvYearMonth = (TextView) convertView.findViewById(R.id.tv_year_month);
            viewHolder.tvBalance = (TextView) convertView.findViewById(R.id.tv_balance);
            viewHolder.ivTag = (ImageView) convertView.findViewById(R.id.iv_tag);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        if (transactionHistoryModelList.size() <= section) {
            return convertView;
        }
        final TransactionHistoryModel historyModel = transactionHistoryModelList.get(section);

        viewHolder.tvYearMonth.setText(historyModel.getTrademonth());

        if (checkedMoney==0){//余额
            viewHolder.tvBalance.setText("现金余额: ￥"+cashBalance);
        }else if (checkedMoney==1){//金币
            viewHolder.tvBalance.setText("讯美币余额: "+giftBalance);
        }else if (checkedMoney==2){//通话费
            viewHolder.tvBalance.setText("我的积分"+couponBalance+"分");
        }else {
            viewHolder.tvBalance.setText("");
        }
        return convertView;
    }

    final static class ViewHolder {

        /**
         * 头像
         */
        public CircleImageView iv_header;


        /**
         * 名字
         */
        public TextView tv_title;


        /**
         * 时间
         */
        public TextView tv_time;


        /**
         * 金额
         */
        public TextView tv_income_or_expenditure;

        /**
         * 类型
         */
        public TextView tv_type;
    }

    final static class GroupViewHolder {

        /**
         * 月份
         */
        public TextView tvYearMonth;
        public ImageView ivTag;

        /**
         * 余额
         */
        public TextView tvBalance;

    }

    public checkData checkData;

    public void setCheckData(checkData checkData){
        this.checkData = checkData;
    }

    public interface checkData{
        void onCheckDataClick();
    }
}
