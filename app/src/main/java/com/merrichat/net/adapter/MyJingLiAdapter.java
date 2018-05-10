package com.merrichat.net.adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisXiangXiInfoAty;
import com.merrichat.net.activity.my.PersonalInfoActivity;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.model.PersonalInfoModel;
import com.merrichat.net.utils.RxTools.RxTimeTool;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/18.
 */

public class MyJingLiAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    private final int type;

    public MyJingLiAdapter(int type, int viewId, List<T> myJingLiList) {
        super(viewId, myJingLiList);
        this.type = type;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper, T item) {
        if (type == PersonalInfoActivity.JIAOYU_TYPE) {
            PersonalInfoModel.DataBean.InfoBean.EduExperienceBean eduExperienceBean = (PersonalInfoModel.DataBean.InfoBean.EduExperienceBean) item;
            helper.setBackgroundRes(R.id.civ_icon, R.mipmap.xuexiao2x)
                    .setText(R.id.tv_name, eduExperienceBean.getSchool())
                    .setText(R.id.tv_detail, RxTimeTool.getDate(eduExperienceBean.getStartTime(), "yyyy-MM-dd") + "-" + RxTimeTool.getDate(eduExperienceBean.getEndTime(), "yyyy-MM-dd") + ","
                            + eduExperienceBean.getEduBackGround() + "," + eduExperienceBean.getProfession()).addOnClickListener(R.id.tv_edit);

        } else if (type == PersonalInfoActivity.GONGZUO_TYPE) {
            PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean workExperienceBean = (PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean) item;
            helper.setBackgroundRes(R.id.civ_icon, R.mipmap.danwei2x)
                    .setText(R.id.tv_name, workExperienceBean.getCompany())
                    .setText(R.id.tv_detail, RxTimeTool.getDate(workExperienceBean.getStartTime(), "yyyy-MM-dd") + "-" + RxTimeTool.getDate(workExperienceBean.getEndTime(), "yyyy-MM-dd") + ","
                            + workExperienceBean.getOccupation()).addOnClickListener(R.id.tv_edit);
        } else if (type == HisXiangXiInfoAty.HIS_JIAOYU_TYPE) {
            HisHomeModel.DataBean.HisMemberInfoBean.EduExperienceBean eduExperienceBean = (HisHomeModel.DataBean.HisMemberInfoBean.EduExperienceBean) item;
            helper.setBackgroundRes(R.id.civ_icon, R.mipmap.xuexiao2x)
                    .setText(R.id.tv_name, eduExperienceBean.getSchool())
                    .setText(R.id.tv_detail, RxTimeTool.getDate(eduExperienceBean.getStartTime(), "yyyy-MM-dd") + "-" + RxTimeTool.getDate(eduExperienceBean.getEndTime(), "yyyy-MM-dd") + ","
                            + eduExperienceBean.getEduBackGround() + "," + eduExperienceBean.getProfession()).setGone(R.id.tv_edit,false);

        } else if (type == HisXiangXiInfoAty.HIS_GONGZUO_TYPE) {
            HisHomeModel.DataBean.HisMemberInfoBean.WorkExperienceBean workExperienceBean = (HisHomeModel.DataBean.HisMemberInfoBean.WorkExperienceBean) item;
            helper.setBackgroundRes(R.id.civ_icon, R.mipmap.danwei2x)
                    .setText(R.id.tv_name, workExperienceBean.getCompany())
                    .setText(R.id.tv_detail, RxTimeTool.getDate(workExperienceBean.getStartTime(), "yyyy-MM-dd") + "-" + RxTimeTool.getDate(workExperienceBean.getEndTime(), "yyyy-MM-dd") + ","
                            + workExperienceBean.getOccupation()).setGone(R.id.tv_edit,false);
        }
    }
}
