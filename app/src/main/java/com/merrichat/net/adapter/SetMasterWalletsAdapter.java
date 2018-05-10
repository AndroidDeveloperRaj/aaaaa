package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MasterWalletsModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/19.
 * 群主钱包设置adapter
 */

public class SetMasterWalletsAdapter extends RecyclerView.Adapter<SetMasterWalletsAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    private SwitchButtonOnclickListener switchButtonOnclickListener;

    private Context context;

    private List<MasterWalletsModel> list;

    private String cid;

    public SetMasterWalletsAdapter(Context context, List<MasterWalletsModel> list,String cid) {
        this.context = context;
        this.list = list;
        this.cid = cid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_wallets, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MasterWalletsModel model = list.get(position);
        Glide.with(context).load(model.getMasterImgUrl()).error(R.mipmap.ic_preloading).into(holder.cvPhoto);
        holder.tvName.setText(model.getMasterName());
        if (model.getIsShowWallet() == 0) {
            holder.sbWalletPermissions.setCheckedImmediatelyNoEvent(false);
        } else if (model.getIsShowWallet() == 1) {
            holder.sbWalletPermissions.setCheckedImmediatelyNoEvent(true);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(pos);
                }
            }
        });

        holder.sbWalletPermissions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int isShowWallet;
                if (isChecked){
                    isShowWallet = 1;
                }else {
                    isShowWallet = 0;
                }
                isAllowSeeWallet(model.getMasterId(), isShowWallet,position);
            }
        });
    }


    /**
     * 设置是否允许查看钱包
     */
    private void isAllowSeeWallet(String masterId,int isShowWallet,final int position){
        OkGo.<String>post(Urls.isAllowSeeWallet)
                .params("cid",cid)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("masterId",masterId)
                .params("isShowWallet",isShowWallet)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")){
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")){

                                }else {
                                    noChangeStateButtonStatus(position);
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)){
                                        GetToast.useString(context,message);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            noChangeStateButtonStatus(position);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        noChangeStateButtonStatus(position);
                    }
                });

    }

    private void noChangeStateButtonStatus(int position){
        switchButtonOnclickListener.onClickListener(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }



    public void setSwitchButtonClickListener(SwitchButtonOnclickListener listener) {
        this.switchButtonOnclickListener = listener;
    }


    public interface SwitchButtonOnclickListener {
        void onClickListener(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 头像
         */
        @BindView(R.id.cv_photo)
        CircleImageView cvPhoto;

        /**
         * 名字
         */
        @BindView(R.id.tv_name)
        TextView tvName;

        /**
         * 能否查看钱包权限的按钮
         */
        @BindView(R.id.sb_wallet_permissions)
        SwitchButton sbWalletPermissions;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
