package com.merrichat.net.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.model.TradeItemOfMonthModel;
import com.merrichat.net.view.CircleImageView;

import java.util.List;

import cn.dreamtobe.kpswitch.IFSPanelConflictLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by amssy on 18/3/8.
 */

public class MyStickyAdapter extends BaseAdapter implements StickyListHeadersAdapter{
    private List<TradeItemOfMonthModel> transactionHistoryModelList;

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
    private LayoutInflater inflater;
    private OnMyItemClickListener listener;
    private Context mContext;

    public MyStickyAdapter(Context context, List<TradeItemOfMonthModel> list, String cashBalance, String giftBalance, String couponBalance) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.transactionHistoryModelList = list;
        this.cashBalance = cashBalance;
        this.giftBalance = giftBalance;
        this.couponBalance = couponBalance;
    }

    public void setCheckedMoney(int checkedMoney){
        this.checkedMoney = checkedMoney;
    }

    @Override
    public int getCount() {
        return transactionHistoryModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionHistoryModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        final TradeItemOfMonthModel tradeItemOfMonthModel = transactionHistoryModelList.get(position);
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
        if (TextUtils.equals("提现到微信",tradeItemOfMonthModel.getTitle()) || TextUtils.equals("提现到支付宝",tradeItemOfMonthModel.getTitle())){
            viewHolder.tv_title.setText("钱包余额提现");
            viewHolder.iv_header.setImageResource(R.mipmap.icon_tixian);
        }else if (TextUtils.equals("讯美",tradeItemOfMonthModel.getNickName()) && !TextUtils.equals("提现到微信",tradeItemOfMonthModel.getTitle()) && !TextUtils.equals("提现到支付宝",tradeItemOfMonthModel.getTitle())){
            viewHolder.tv_title.setText(tradeItemOfMonthModel.getNickName());
            viewHolder.iv_header.setImageResource(R.mipmap.icon_system_header);
        }else if (TextUtils.isEmpty(tradeItemOfMonthModel.getNickName()) && TextUtils.equals(tradeItemOfMonthModel.getUser_account_id(),"100006")){//平台账户 其他类型
            viewHolder.tv_title.setText("讯美");
            viewHolder.iv_header.setImageResource(R.mipmap.ic_preloading);
        }else {
            viewHolder.tv_title.setText(tradeItemOfMonthModel.getNickName());
            if (!TextUtils.isEmpty(tradeItemOfMonthModel.getImgUrl())) {
                Glide.with(mContext).load(tradeItemOfMonthModel.getImgUrl()).into(viewHolder.iv_header);
            }else {
                viewHolder.iv_header.setImageResource(R.mipmap.ic_preloading);
            }
        }

        viewHolder.tv_time.setText(tradeItemOfMonthModel.getCreated());
        if ("0".equals(tradeItemOfMonthModel.getTrade_type())) {//收入
            viewHolder.tv_income_or_expenditure.setText("+￥" + tradeItemOfMonthModel.getJour_value());
        } else if ("1".equals(tradeItemOfMonthModel.getTrade_type())) {//支出
            viewHolder.tv_income_or_expenditure.setText("-￥" + tradeItemOfMonthModel.getJour_value());
        } else {
            viewHolder.tv_income_or_expenditure.setText("￥" + tradeItemOfMonthModel.getJour_value());
        }
        viewHolder.tv_type.setText(tradeItemOfMonthModel.getTitle());

        return convertView;
    }

    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
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

        final TradeItemOfMonthModel historyModel = transactionHistoryModelList.get(position);

        viewHolder.tvYearMonth.setText(historyModel.getMonthTitle());
        viewHolder.ivTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMyItemTitleClick(position);
            }
        });

        if (checkedMoney == 0) {//余额
            viewHolder.tvBalance.setText("现金余额: ￥" + cashBalance);
        } else if (checkedMoney == 1) {//金币
            viewHolder.tvBalance.setText("讯美币余额: " + giftBalance);
        } else if (checkedMoney == 2) {//通话费
            viewHolder.tvBalance.setText("我的积分" + couponBalance + "分");
        } else {
            viewHolder.tvBalance.setText("");
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return transactionHistoryModelList.get(position).getTitleId();
    }

    class GroupViewHolder {

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

    class ViewHolder {
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

    /**
     * 内部接口回调方法
     */
    public interface OnMyItemClickListener {
        void onMyItemTitleClick(int position);
    }

    /**
     * 设置监听方法
     *
     * @param listener
     */
    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.listener = listener;
    }

}
