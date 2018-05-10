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
import com.merrichat.net.model.ListOfGossipModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/31.
 * 禁言名单adapter
 */

public class ListOfGossipAdapter extends RecyclerView.Adapter<ListOfGossipAdapter.ViewHolder> {


    /**
     * 事件回调监听
     */
    private DesignateGagAdapter.OnItemClickListener onItemClickListener;

    private Context context;
    private List<ListOfGossipModel> list;

    /**
     * 群id
     */
    private String groupId;


    public ListOfGossipAdapter(Context context, List<ListOfGossipModel> list,String groupId) {
        this.context = context;
        this.list = list;
        this.groupId = groupId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_of_gossip, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ListOfGossipModel model = list.get(position);
        Glide.with(context).load(model.getHeadImgUrl()).error(R.mipmap.ic_preloading).into(holder.civGossipView);
        holder.tvGossipName.setText(model.getMemberName());
        if (model.getIsBanned()==0){
            holder.sbGossipButton.setCheckedImmediatelyNoEvent(false);
        }else if (model.getIsBanned()==1){
            holder.sbGossipButton.setCheckedImmediatelyNoEvent(true);
        }
       holder.sbGossipButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               JSONArray jsonArray = new JSONArray();
               JSONObject jsonObject = new JSONObject();
               try {
                   jsonObject.put("id",model.getMemberId());
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               jsonArray.put(jsonObject);
               if (isChecked){
                   specifyDisSendMsg(jsonArray.toString());
                }else {
                    cancelBanned(jsonArray.toString());
                }
           }
       });
    }


    /**
     * 指定禁言
     */
    private void specifyDisSendMsg(String jsonMember){
        OkGo.<String>post(Urls.specifyDisSendMsg)
                .params("cid",groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("jsonMember",jsonMember)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {

                                }
                            }else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(context, message);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 取消禁言
     * @param memJson
     */
    private void cancelBanned(String memJson){
        OkGo.<String>post(Urls.cancelBanned)
                .params("cid",groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memJson",memJson)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {

                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(context, message);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
    public void setOnItemClickListener(DesignateGagAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.civ_gossip_view)
        CircleImageView civGossipView;


        @BindView(R.id.tv_gossip_name)
        TextView tvGossipName;


        @BindView(R.id.sb_gossip_button)
        SwitchButton sbGossipButton;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
