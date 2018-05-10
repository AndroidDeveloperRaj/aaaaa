package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.ComplaintOthersAty;
import com.merrichat.net.activity.his.HisZiLiaoSettingAty;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.EncounterPersonModel;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.view.ReportDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EncounterBodyAdapter extends PagerAdapter {

    private HisHomeModel.DataBean hisHomeModelData;
    private HisHomeModel.DataBean.HisMemberInfoBean infoBean;

    private List<EncounterPersonModel.EncounterBody> listEncounterBody;// 遇到的人列表
    private Context context;

//    ReportDialog reportDialog;

    /**
     * list 遇到的人列表
     *
     * @param listEncounterBody
     * @param context
     */
    public EncounterBodyAdapter(List<EncounterPersonModel.EncounterBody> listEncounterBody, Context context) {
        this.listEncounterBody = listEncounterBody;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listEncounterBody.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        View relativeLayout = (View) LayoutInflater.from(context).inflate(R.layout.item_view_page, null);
        viewHolder = new ViewHolder(relativeLayout);
        final EncounterPersonModel.EncounterBody encounterBody = listEncounterBody.get(position);
        if (encounterBody.memberName != null) {
            viewHolder.meat_name.setText(encounterBody.memberName);
        } else {
            viewHolder.meat_name.setText("未设置姓名");
        }

        viewHolder.meat_far.setText(encounterBody.distance);
        Glide.with(context)
                .load(encounterBody.headImgUrl)
                .priority(Priority.HIGH)
                .into(viewHolder.meat_ico);
        viewHolder.meat_constellation.setText(encounterBody.constellations);

        if (encounterBody.gender.toString().equals("1.0")) {
            viewHolder.image_gender_label.setVisibility(View.VISIBLE);
            viewHolder.image_gender_label.setBackground(context.getResources().getDrawable(R.mipmap.ic_gender_boy));
            viewHolder.meat_years.setText(encounterBody.age + "");
        } else if (encounterBody.gender.toString().equals("2.0")) {
            viewHolder.image_gender_label.setVisibility(View.VISIBLE);
            viewHolder.image_gender_label.setBackground(context.getResources().getDrawable(R.mipmap.ic_gender_girl));
            viewHolder.meat_years.setText(encounterBody.age + "");
        } else {
            viewHolder.image_gender_label.setVisibility(View.GONE);
            viewHolder.meat_years.setText("" + encounterBody.age);
        }
        viewHolder.meat_introduce.setText(encounterBody.signature);

        viewHolder.lay_jubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                hisHomeModelData = new HisHomeModel.DataBean();
                infoBean = new HisHomeModel.DataBean.HisMemberInfoBean();
                infoBean.setMemberId(encounterBody.memberId + "");
                infoBean.setNickName(encounterBody.memberName);
                infoBean.setMobile(encounterBody.mobile);
                hisHomeModelData.setHisMemberInfo(infoBean);

                Bundle bundle = new Bundle();
                bundle.putSerializable("hisHomeModelData", hisHomeModelData);
                RxActivityTool.skipActivity(context, ComplaintOthersAty.class, bundle);


            }
        });


        container.addView(relativeLayout);
        return relativeLayout;
    }


    private ViewHolder viewHolder;

    class ViewHolder {
        public ImageView meat_ico;  // 当前人物的头像
        public TextView meat_name;
        public TextView meat_far;
        public TextView meat_constellation;
        public TextView meat_years;
        public TextView meat_introduce;
        public TextView image_gender_label;
        public ImageView lay_jubao;

        public ViewHolder(View view) {
            lay_jubao = (ImageView) view.findViewById(R.id.lay_jubao);
            meat_ico = (ImageView) view.findViewById(R.id.meat_ico);
            meat_name = (TextView) view.findViewById(R.id.meat_name);
            meat_far = (TextView) view.findViewById(R.id.meat_far);
            meat_constellation = (TextView) view.findViewById(R.id.meat_constellation);
            meat_years = (TextView) view.findViewById(R.id.meat_years);
            meat_introduce = (TextView) view.findViewById(R.id.meat_introduce);
            image_gender_label = (TextView) view.findViewById(R.id.image_gender_label);

        }
    }


}
