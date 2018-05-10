package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.model.GoodsTradingModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/2/1.
 * 交易商品设置adapter
 */

public class GoodsTradingAdapter extends RecyclerView.Adapter<GoodsTradingAdapter.ViewHolder> {

    private Context context;
    private List<GoodsTradingModel> list;
    private onTextClick onTextClick;

    public GoodsTradingAdapter.onTextClick getOnTextClick() {
        return onTextClick;
    }

    public void setOnTextClick(GoodsTradingAdapter.onTextClick onTextClick) {
        this.onTextClick = onTextClick;
    }

    public GoodsTradingAdapter(Context context, List<GoodsTradingModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_trading, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GoodsTradingModel model = list.get(position);
        holder.tvProductName.setText(model.getProductName());
        holder.tvProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTextClick != null) {
                    onTextClick.onTvClick(model);
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSwipeListener != null) {
                    mOnSwipeListener.onDel(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.btnDelete)
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface onTextClick {
        void onTvClick(GoodsTradingModel goodsTradingModel);
    }

    public interface onSwipeListener {
        void onDel(int pos);
    }

    private onSwipeListener mOnSwipeListener;

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
}
