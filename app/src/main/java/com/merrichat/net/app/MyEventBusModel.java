package com.merrichat.net.app;

import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.VideoReleaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/11/13.
 * EventBus传递对象标记
 */

public class MyEventBusModel implements Serializable {
    /**
     * 我的收藏--收藏时刷新
     */
    public boolean FRESH_MY_COLLECTION = false;
    /**
     * 刷新他的主页-关注状态值
     */
    public int REFRESH_ATTENTION_STATUS = -1;
    public boolean REFRESH_PRAISE_COMMENT = false;
    /**
     * 刷新推荐页面
     */
    public boolean REFRESH_RECOMMENT_FRAGMENT = false;
    /**
     * 发布图文 微信分享成功后 返回推荐
     */
    public boolean COLSE_GRAPHICALBUM_ACTIVITY = false;
    /**
     * 销售订单--刷新退款
     */
    public boolean REFRESH_REFUND = false;
    /**
     * 销售订单--刷新待待成团
     */
    public boolean REFRESH_PENDING_REGIMENT = false;
    /**
     * 销售订单--刷新待发货
     */
    public boolean REFRESH_PENDING_DELIVERY = false;
    /**
     * 收货地址--实物地址
     */
    public boolean REFRESH_RECEIVING_ADDRESS_REC = false;
    /**
     * 收货地址--虚拟地址
     */
    public boolean REFRESH_RECEIVING_ADDRESS_VIR = false;
    /**
     * 发布图文后 回到朋友圈
     */
    public boolean REFRESH_MINE_CIRCLER = false;
    /**
     * 是否弹出分享框
     */
    public boolean REFRESH_MINE_CIRCLER_SHARE = false;
    public String id = "";
    public String title = "";
    public String content = "";
    public String imageUrl = "";
    public int shareTo = 0;

    /**
     * 编辑个人资料 刷新个人信息
     */
    public boolean REFRESH_PERSON_INFO_EPX = false;
    /**
     * 刷新他的主页
     */
    public boolean REFRESH_HIS_HOME = false;
    /**
     * 刷新我的主页
     */
    public boolean REFRESH_MINE_HOME = false;
    /**
     * 刷新个人信息界面
     */
    public boolean REFRESH_PERSON_INFO = false;
    /**
     * 刷新好友列表
     */
    public boolean REFRESH_FRIENDS_LIST = false;
    /**
     * 预览相册界面点击完成 关闭相册列表界面 同时刷新发动态界面
     */
    public boolean CLOSE_PHOTO_LIST = false;
    /**
     * 预览相册界面点击回退家 刷新相册界面
     */
    public boolean REFRESH_PHOTO_LIST = false;
    /**
     * 跳转到消息页面
     */
    public boolean MESSAGE_IS_T0_CHAT = false;
    /**
     * 消息列表刷新的广播
     */
    public boolean MESSAGE_IS_REFRESH = false;

    /**
     * 刷新消息MainActivity里面的消息的未读数量
     */
    public boolean MESSAGE_IS_MAIN_MESSAGE_NUM = false;

    /**
     * 刷新消息列表----快递通知是否显示
     */
    public boolean MESSAGE_IS_NUMBER_REFRESH = false;
    /**
     * 相册界面点击完成 刷新发动态界面
     */
    public boolean PHOTO_LIST_CLICK_COMPLETE = false;
    /**
     * 单聊界面刷新数据的广播
     */
    public boolean SINGLE_MESSAGE_IS_REFRESH = false;
    /**
     * 当日榜单
     */
    public boolean DANG_RI_BANG_DAN = false;
    /**
     * 本周榜单
     */
    public boolean BEN_ZHOUI_BANG_DAN = false;


    /**
     * 讯美币充值成功，发广播关闭充值页面
     */
    public boolean REFRESH_WECHAT_MEIBI = false;
    /**
     * 发红包微信支付成功，发广播关闭页面
     */
    public boolean REFRESH_WECHAT_FAHONGBAO = false;
    /**
     * 全部评论
     */
    public boolean COMMENT_EVALUATE1 = false;
    public boolean COMMENT_EVALUATE2 = false;
    public boolean COMMENT_EVALUATE3 = false;
    public boolean COMMENT_EVALUATE4 = false;
    /**
     * 删除帖子
     */
    public boolean DELEATE_LOG1 = false;
    public boolean DELEATE_LOG2 = false;
    public boolean DELEATE_LOG3 = false;
    public boolean DELEATE_LOG4 = false;

    /**
     * 刷新钱包
     */
    public boolean REFRESH_MY_WALLET = false;


    /**
     * 刷新搜索页面
     * 退出搜索
     */
    public boolean REFRESH_NICE_MEET_ONE = false;


    /**
     * 刷新搜索页面
     * 重新搜索
     */
    public boolean REFRESH_NICE_MEET_TWO = false;


    //-------------------  语音聊天相关事件
    /**
     * 语音聊天应答事件 ANSWER
     */
    public boolean REFRESH_VOICE_CHAT_ANSWER = false;

    /**
     * 语音聊天应答事件对方相关信息  ANSWER_CALLINFO
     */
    public String VOICE_CHAT_ANSWER_CALLINFO = "";

    /**
     * 视频聊天拨打事件   CALL
     */
    public boolean REFRESH_VIDEO_CHAT_CALL = false;

    /**
     * 视频聊天拨打事件对方相关信息   CALL_CALLINFO
     */
    public String VIDEO_CHAT_CALL_CALLINFO = "";


    /**
     * 视频聊天拨打事件   CALL
     */
    public boolean MAGIC_TACK_STYLE = false;

    /**
     * 视频聊天拨打事件对方相关信息   CALL_CALLINFO
     */
    public String MAGIC_TACK_STYLE_INFO = "";


    //-----------------


    /**
     * 美遇应答事件
     */
    public boolean REFRESH_MEET_NICE_ANSWER = false;

    /**
     * 美遇--应答事件---对方的相关信息
     */
    public String NICE_MEET_ANSWER_CALLINFO = "";


    /**
     * 美遇---呼叫事件
     */
    public boolean REFRESH_MEET_NICE_CALL = false;

    /**
     * 美遇---呼叫事件---对方的应答信息
     */
    public String NICE_MEET_CALL_ANSWERINFO = "";


    /**
     * 充值成功，关闭钱包余额页面
     */
    public boolean FINISH_WALLET_BALANCE = false;
    /**
     * 刷新我的--陌生视频开关
     */
    public boolean REFRESH_MINE_MV = false;


    /**
     * 登录状态发生变化时重新发送个人信息到服务器(推送用)
     */
    public boolean login_type = false;
    /**
     * 登录状态发生变化的广播（登录成功或退出登录）
     */
    public boolean Login_type_change = false;

    /**
     * 退出登录时保存的上一个用户的memberId
     */
    public long LOGIN_EXIT_BEFORE_MEMBERID = 0;

    /**
     * 首页接受数据 发布帖子
     */
    public boolean FRIEND_RELEASE = false;
    public boolean FRIEND_RELEASE_TUWEN = false;
    public boolean FRIEND_RELEASE_SUCCESS = false;
    public VideoReleaseModel videoReleaseModel;


    /***
     * 退出当前账号需要关闭设置页面
     ***/
    public boolean CLOSE_MYACTIVITY = false;

    /**
     * 筛选性别
     */
    public boolean CHOOSE_SEX1 = false;
    public boolean CHOOSE_SEX2 = false;
    public boolean CHOOSE_SEX3 = false;

    /**
     * 关闭订单支付之前的界面
     */
    public boolean CLOSE_ORDER = false;
    public boolean CLOSE_NO_ORDER = false;
    /**
     * 微信支付成功
     */
    public boolean WXPAY_SUCCESS = false;
    /**
     * 支付失败或者取消
     */
    public boolean PAY_CANCEL = false;
    /**
     * 商品下架
     */
    public boolean SHOP_SOLD_OUT = false;


    /**
     * 刷新群设置数据
     */
    public boolean REFRESH_GROUP_SETTING = false;

    public boolean REFRESH_GROUP_SETTING_NAME = false;


    /**
     * 刷新群组列表数据
     */
    public boolean REFRESH_GROUP_LIST = false;
    /**
     * 刷新通知列表
     */
    public boolean REFRESH_NOTICE_ATY = false;


    /**
     * 刷新修改群地址
     */
    public boolean REFRESH_GROUP_DATA = false;

    /**
     * 刷新修改群名字
     */
    public boolean REFRESH_GROUP_DATA_NAME = false;

    /**
     * 刷新群主钱包数据
     */
    public boolean REFRESH_GROUP_WALLET = false;

    /**
     * 刷新群消息列表数据
     */
    public boolean REFRESH_GROUP_MESSAGE = false;

    /**
     * 关闭群聊页面
     */
    public boolean FINIIS_GROUP_CHAT = false;

    public boolean REFRESH_GROUP_MESSAGE_NAME = false;
    public String REFRESH_GROUP_MESSAGE_NAME_STRING = "";


    /**
     * 关闭群订单详情页面
     */
    public boolean FINISH_GROUP_ORDER_DETAILS = false;

    /**
     * 刷新群订单列表---仲裁退款列表
     */
    public boolean REFRESH_GROUP_ORDER_LIST = false;

    /**
     * 进入的登录界面，登录成功刷新
     */
    public boolean REFRESH_LOGIN_SUCESS_ENTER_LOGIN = false;


    public boolean REFRESH_NEW_FRIENDS_NUM = false;

    /**
     * 取消选择音乐
     */
    public boolean CANCEL_CHOOSE_MUSIC = false;

    /**
     * 刷新朋友圈列表
     */
    public boolean FRESH_LIST_DATA = false;
    public String income;
    public int likeCounts;
    public boolean isLike;

    /**
     * 关闭拍摄相关界面
     */
    public boolean CLOSE_VIDEO_ACTIVITY = false;

    /**
     * 帖子被删除或者被拉黑
     */
    public boolean REMOVE_LOG = false;

    /**
     * 帖子评论
     */
    //外层回复
    public boolean COMMENT_LOG = false;
    //里层回复
    public boolean COMMENT_LOG_1 = false;

    public String replyMemberId;
    public String replyCommentId;
    public List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply;
    public String personId;
    public String item_nickName;

    /**
     * 打赏成功回调
     */
    public boolean VIDEO_DASH_SUCCESS = false;
    /**
     * 评论成功 刷新公分
     */
    public boolean VIDEO_COMMENT_SUCCESS = false;
    public int commentNumber = 0;
    /**
     * 刷新他的主页-关注状态
     */
    public boolean REFRESH_ATTENTION_STATUS_FLAG = false;

    /**
     * 手机网络播放视频
     */
    public boolean VIDEO_PAUSE = false;
    public boolean VIDEO_START = false;
    /**
     * 筛选附近的群
     */
    public boolean FILTER_NEAR_GROUP = false;
    /**
     * 筛选附近的群 -type标志
     */
    public String FILTER_NEAR_GROUP_TYPE ;
    /**
     * 我的收藏-取消收藏
     */
    public boolean FRESH_MY_CANCLE_COLLECTION=false;
}
