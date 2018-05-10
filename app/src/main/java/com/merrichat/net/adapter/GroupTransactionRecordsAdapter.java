package com.merrichat.net.adapter;

import android.content.Context;
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

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by amssy on 18/1/19.
 * 群交易记录adapter
 */

public class GroupTransactionRecordsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<TradeItemOfMonthModel> transactionHistoryModelList;

    /**
     * 选中条目
     * 0 余额
     * 1 金币
     * 2 通话费
     * 默认为1
     */
    private int checkedMoney = 0;


    /**
     * 账户余额
     */
    private String cashBalance = "";

    /**
     * 讯美币余额
     */
    private String giftBalance = "";

    private LayoutInflater inflater;
    private OnMyItemClickListener listener;
    private Context mContext;

    public GroupTransactionRecordsAdapter(Context context, List<TradeItemOfMonthModel> list, int checkedMoney, String cashBalance, String giftBalance) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.transactionHistoryModelList = list;
        this.checkedMoney = checkedMoney;
        this.cashBalance = cashBalance;
        this.giftBalance = giftBalance;
    }


    public int getCheckedMoney() {
        return checkedMoney;
    }

    public void setCheckedMoney(int checkedMoney) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group_transaction, null);
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

        viewHolder.tv_title.setText(tradeItemOfMonthModel.getNickName());
//        Glide.with(mContext).load(tradeItemOfMonthModel.getImgUrl()).into(viewHolder.iv_header);
        Glide.with(mContext).load(tradeItemOfMonthModel.getImgUrl()).error(R.mipmap.ic_preloading).into(viewHolder.iv_header);
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

        TradeItemOfMonthModel historyModel = transactionHistoryModelList.get(position);

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


    public void notifyChange(int checkedMoney) {
        setCheckedMoney(checkedMoney);
        notifyDataSetChanged();
    }
}
