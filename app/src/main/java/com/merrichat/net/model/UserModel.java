package com.merrichat.net.model;


import com.merrichat.net.model.dao.UserModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

@Entity
public class UserModel {

    @Id(autoincrement = true)
    private Long ID;

    @Property(nameInDb = "MEMBER_ID")
    String memberId = "";// 用户ID  对应memberId


    @Property(nameInDb = "ACCOUNT_ID")
    String accountId = "";

    /**
     * 性别  0未绑定 1男 2女
     */
    @Property(nameInDb = "GENDER")
    String gender = "";

    /**
     * 头像
     */
    @Property(nameInDb = "IMG_URL")
    String imgUrl = "";

    /**
     * 是否绑定微信
     */
    @Property(nameInDb = "BIN_DING")
    String binding = "";

    /**
     * 手机号
     */
    @Property(nameInDb = "MOBILE")
    String mobile = "";

    /**
     * 昵称
     */
    @Property(nameInDb = "REAL_NAME")
    String realname = "";

    /**
     *
     */
    @Property(nameInDb = "STATUS")
    String status = "";

    /**
     *
     */
    @Property(nameInDb = "USER_FLAG")
    String userFlag = "";

    /**
     * 微信AccountId
     */
    @Property(nameInDb = "WX_ACCOUNT_ID")
    String weixinAccountId = "";

    /**
     * 是否登录
     */
    @Property(nameInDb = "IS_LOGIN")
    boolean isLogin = false;

    /**
     * 登录ID
     */
    @Property(nameInDb = "MOB_MEMBER_LOGIN_ID")
    String mobMemberLoginId = "";

    /**
     *
     */
    @Property(nameInDb = "SIGNATURE")
    String signature = "";

    /**
     * 生日
     */
    @Property(nameInDb = "BIRTHDAY")
    String birthday = "";
    /**
     * 推荐人
     */
    @Property(nameInDb = "TUIJIANREN")
    String tuijianren = "";
    /**
     * 注册时间
     */
    @Property(nameInDb = "REGIST_TIME")
    String registTime = "";
    /**
     * accessToken
     */
    @Property(nameInDb = "ACCESS_TOKEN")
    String accessToken = "";
    /**
     * refreshToken
     */
    @Property(nameInDb = "REFRESH_TOKEN")
    String refreshToken = "";

    /**
     * uploadFlag
     */
    @Property(nameInDb = "UPLOADFLAG")
    String uploadFlag = "";

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Generated(hash = 531558855)
    public UserModel(Long ID, String memberId, String accountId, String gender, String imgUrl,
            String binding, String mobile, String realname, String status, String userFlag,
            String weixinAccountId, boolean isLogin, String mobMemberLoginId, String signature,
            String birthday, String tuijianren, String registTime, String accessToken,
            String refreshToken, String uploadFlag) {
        this.ID = ID;
        this.memberId = memberId;
        this.accountId = accountId;
        this.gender = gender;
        this.imgUrl = imgUrl;
        this.binding = binding;
        this.mobile = mobile;
        this.realname = realname;
        this.status = status;
        this.userFlag = userFlag;
        this.weixinAccountId = weixinAccountId;
        this.isLogin = isLogin;
        this.mobMemberLoginId = mobMemberLoginId;
        this.signature = signature;
        this.birthday = birthday;
        this.tuijianren = tuijianren;
        this.registTime = registTime;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.uploadFlag = uploadFlag;
    }

    @Generated(hash = 782181818)
    public UserModel() {
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBinding() {
        return this.binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserFlag() {
        return this.userFlag;
    }

    public void setUserFlag(String userFlag) {
        this.userFlag = userFlag;
    }

    public String getWeixinAccountId() {
        return this.weixinAccountId;
    }

    public void setWeixinAccountId(String weixinAccountId) {
        this.weixinAccountId = weixinAccountId;
    }

    public String getTuijianren() {
        return tuijianren;
    }

    public void setTuijianren(String tuijianren) {
        this.tuijianren = tuijianren;
    }

    public boolean getIsLogin() {
        return this.isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 获取UserModel对象
     *
     * @return
     */
    public static UserModel getUserModel() {
        UserModelDao userModelDao = GreenDaoManager.getInstance().getSession().getUserModelDao();
        List<UserModel> list = userModelDao.queryBuilder().list();
        UserModel userModel = list.size() > 0 ? list.get(0) : new UserModel();
        return userModel;
    }


    /**
     * 设置或更改UserModel的对象的值
     *
     * @param userModel
     */
    public static void setUserModel(UserModel userModel) {
        if (userModel.getID() != null && userModel.getID() > 0) {
            updateUserModel(userModel);
        } else {
            UserModelDao userModelDao = GreenDaoManager.getInstance().getSession().getUserModelDao();
            userModelDao.insert(userModel);
        }
    }


    /**
     * 更新UserModel对象的信息
     *
     * @param userModel
     */
    public static void updateUserModel(UserModel userModel) {
        UserModelDao userModelDao = GreenDaoManager.getInstance().getSession().getUserModelDao();
        userModelDao.update(userModel);
    }

    /**
     * 删除
     *
     * @param
     */
    public static void deleteUserModel(UserModel userModel) {
        UserModelDao userModelDao = GreenDaoManager.getInstance().getSession().getUserModelDao();
        userModelDao.deleteByKey(userModel.getID());
    }

    public String getMobMemberLoginId() {
        return this.mobMemberLoginId;
    }

    public void setMobMemberLoginId(String mobMemberLoginId) {
        this.mobMemberLoginId = mobMemberLoginId;
    }

    public String getRegistTime() {
        return this.registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(String uploadFlag) {
        this.uploadFlag = uploadFlag;
    }
}
