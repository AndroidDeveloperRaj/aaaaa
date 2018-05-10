package com.merrichat.net.model;

import com.merrichat.net.model.dao.DraftDaoModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/3.
 * <p>
 * 草稿
 */

@Entity
public class DraftDaoModel {
    @Id(autoincrement = true)
    private Long ID;
    @Property(nameInDb = "TU_WEN_DRAFT")
    private String tuWenDraft;

    @Property(nameInDb = "VIDEO_DRAFT")
    private String videoDraft;

    @Generated(hash = 1502377004)
    public DraftDaoModel(Long ID, String tuWenDraft, String videoDraft) {
        this.ID = ID;
        this.tuWenDraft = tuWenDraft;
        this.videoDraft = videoDraft;
    }

    @Generated(hash = 1322363740)
    public DraftDaoModel() {
    }

    /**
     * 获取UserModel对象
     *
     * @return
     */
    public static DraftDaoModel getDraftDaoModel() {
        DraftDaoModelDao draftDaoModelDao = GreenDaoManager.getInstance().getSession().getDraftDaoModelDao();
        List<DraftDaoModel> list = draftDaoModelDao.queryBuilder().list();
        DraftDaoModel draftDaoModel = list.size() > 0 ? list.get(0) : new DraftDaoModel();
        return draftDaoModel;
    }

    /**
     * 设置或更改UserModel的对象的值
     *
     * @param draftDaoModel
     */
    public static void setDraftDaoModel(DraftDaoModel draftDaoModel) {
        if (draftDaoModel.getID() != null && draftDaoModel.getID() > 0) {
            updateDraftDaoModel(draftDaoModel);
        } else {
            DraftDaoModelDao draftDaoModelDao = GreenDaoManager.getInstance().getSession().getDraftDaoModelDao();
            draftDaoModelDao.insert(draftDaoModel);
        }
    }

    /**
     * 更新UserModel对象的信息
     *
     * @param draftDaoModel
     */
    public static void updateDraftDaoModel(DraftDaoModel draftDaoModel) {
        DraftDaoModelDao draftDaoModelDao = GreenDaoManager.getInstance().getSession().getDraftDaoModelDao();
        draftDaoModelDao.update(draftDaoModel);
    }

    /**
     * 删除
     *
     * @param
     */
    public static void deleteDraftDaoModel(DraftDaoModel draftDaoModel) {
        DraftDaoModelDao draftDaoModelDao = GreenDaoManager.getInstance().getSession().getDraftDaoModelDao();
        draftDaoModelDao.deleteByKey(draftDaoModel.getID());
    }

    public String getVideoDraft() {
        return this.videoDraft;
    }

    public void setVideoDraft(String videoDraft) {
        this.videoDraft = videoDraft;
    }

    public String getTuWenDraft() {
        return this.tuWenDraft;
    }

    public void setTuWenDraft(String tuWenDraft) {
        this.tuWenDraft = tuWenDraft;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }


}
