/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.merrichat.net.http;

/**
 * 接口 Url
 */
public class Urls {
    public static final String SERVER_URL = UrlConfig.getLifeUrl();
    public static final String SERVER = "http://server.jeasonlzy.com/OkHttpUtils/";
    //    public static final String SERVER = "http://192.168.1.121:8080/OkHttpUtils/";
    public static final String URL_METHOD = SERVER + "method";
    public static final String URL_CACHE = SERVER + "cache";
    public static final String URL_IMAGE = SERVER + "image";
    public static final String URL_JSONOBJECT = SERVER + "jsonObject";
    public static final String URL_JSONARRAY = SERVER + "jsonArray";
    public static final String URL_FORM_UPLOAD = SERVER + "upload";
    public static final String URL_TEXT_UPLOAD = SERVER + "uploadString";
    public static final String URL_DOWNLOAD = SERVER + "download";
    public static final String URL_REDIRECT = SERVER + "redirect";
    public static final String URL_GANK_BASE = "http://gank.io/api/data/";

    public static final String URL_COLS = SERVER_URL + "productController/queryCols";
    /**
     * 我的主页
     */
    public static final String MY_HOME = SERVER_URL + "oneInfo/myHome";
    /**
     * 个人信息
     */
    public static final String ONE_INFO = SERVER_URL + "oneInfo/oneInfo";

    /**
     * 插入/更新教育经历
     */
    public static final String INSERT_EDUCATION = SERVER_URL + "oneInfo/insertEducation";

    /**
     * 插入/更新工作经历
     */
    public static final String INSERT_WORK = SERVER_URL + "oneInfo/insertWork";

    /**
     * 删除教育/工作经历
     */
    public static final String deleteExperience = SERVER_URL + "oneInfo/deleteExperience";
    /**
     * 头像上传
     */
    public static final String uploadPicMember = SERVER_URL + "personal/uploadPicMember";


    /**
     * 登录验证
     */
    public static final String MERRI_LOGIN = SERVER_URL + "okdiLifeResiterValid/login";
    /**
     * 登录验证手机号是否注册
     */
    public static final String MERRI_LOGIN_PHONE = SERVER_URL + "okdiLifeResiter/validMob";
    /**
     * 登录记录
     */
    public static final String MERRI_LOGIN_RECORD = SERVER_URL + "mobDeviceRecord/record";
    /**
     * 登录保存设备登录信息
     */
    public static final String MERRI_LOGIN_SAVE = SERVER_URL + "mobMemberLogin/doAddMobMemberLogin";
    /**
     * 微信登录取CODE
     */
    public static final String WX_LOGIN_CODE = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 微信登录取用户信息
     */
    public static final String WX_LOGIN_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 微信登录请求
     */
    public static final String WX_LOGIN = SERVER_URL + "okdilifeValid/check_bing";
    /**
     * 登录绑定微信
     */
    public static final String WX_BOUND = SERVER_URL + "okdiLifeResiterValid/wx/bound";

    /**
     * 发送验证码
     */
    public static final String SEND_SMS = SERVER_URL + "okdiLifeResiterValid/sendSmsRegV2";
    /**
     * 验证验证码是否正确
     */
    public static final String VALID_REGSMS = SERVER_URL + "okdiLifeResiter/validRegSms";
    /**
     * 验证验证码是否正确(找回密码)
     */
    public static final String VALID_FIND_REGSMS = SERVER_URL + "okdiLifeResiter/validFindPwdSms";
    /**
     * 注册
     */
    public static final String REGISTER = SERVER_URL + "okdiLifeResiterValid/regist";
    /**
     * 修改密码
     */
    public static final String UPDATE_PWD = SERVER_URL + "okdiLifeResiter/updatepwdFindPwd";
    /**
     * 设置--修改密码
     */
    public static final String UPDATE_MYPWD = SERVER_URL + "okdiLifeResiter/updateMyPwd";
    /**
     * 绑定性别-出生日期
     */
    public static final String BOUND_SEX_BIRTH = SERVER_URL + "oneInfo/setOneInfo";

    /**
     * 分配长连接地址, 建立长连接之前请求接口获取地址
     */
    public static final String CIM_ALLOCATE = SERVER_URL + "im/allocate";
    /**
     * 通讯录好友
     */
    public static final String getBookFriendList = SERVER_URL + "linkman/getBookFriendList";
    /**
     * 同意加好友
     */
    public static final String agreeFriendsRequest = SERVER_URL + "linkman/agreeFriendsRequest";
    /**
     * 添加好友
     */
    public static final String addGoodFriends = SERVER_URL + "linkman/addGoodFriends";
    /**
     * 判断是否首次登录
     */
    public static final String firstLogin = SERVER_URL + "mobDeviceRecord/firstLogin";

    /**
     * 删除好友请求
     */
    public static final String deleteFriendsRequest = SERVER_URL + "linkman/deleteFriendsRequest";
    /**
     * 我的好友列表
     */
    public static final String getGoodFriendsList = SERVER_URL + "linkman/getGoodFriendsList";
    /**
     * 好友列表
     */
    public static final String getMyAllGoodFriendsList = SERVER_URL + "linkman/getMyAllGoodFriendsList";
    /**
     * 新的好友邀请记录
     */
    public static final String inviteFriendsRecord = SERVER_URL + "linkman/inviteFriendsRecord";
    /**
     * - 意见反馈
     */
    public static final String ADD_FEEDBACK = SERVER_URL + "feedback/addFeedBack";
    /**
     * - 个人信息--身份信息的增改查
     */
    public static final String FINDUP_IDENTITY = SERVER_URL + "oneInfo/findAndupIdentity";
    /**
     * - - 他的影集--他的影集
     */
    public static final String hisAlbum = SERVER_URL + "oneInfo/hisAlbum";
    /**
     * - 个人信息--查询/隐藏朋友圈状态
     */
    public static final String hideFriendCircle = SERVER_URL + "oneInfo/hideFriendCircle";

    /**
     * 视频聊天分配长连接地址
     */
    public static final String IM_VIDEO_CHAT = SERVER_URL + "im/videoChat";


    public static final String IM_VIDEO_CHATNew = "http://59.110.12.50:24396/videochat/register/allocate";


    /**
     * 查询附近的人
     */
    public static final String NEAR_PEOPLE = SERVER_URL + "nearbyPeople/queryListNearbyPeople";
    /**
     * 删除好友
     */
    public static final String deleteGoodFriend = SERVER_URL + "linkman/deleteGoodFriend";

    /**
     * 拉取离线消息
     */
    public static final String offline = SERVER_URL + "im/offline";


    /**
     * 查询举报列表
     */
    public static final String queryReportingTypes = SERVER_URL + "oneInfo/queryReportingTypes";
    /**
     * 举报人
     */
    public static final String reportingAudit = SERVER_URL + "oneInfo/reportingAudit";
    /**
     * 邀请赚钱
     */
    public static final String MY_PEER = SERVER_URL + "oneInfo/myPeer";
    /**
     * 奖励报表
     */
    public static final String GET_BONUSREPORTLIST = SERVER_URL + "bonus/getBonusReportList";
    /**
     * - 奖励记录
     */
    public static final String GET_BONUSRECORDLIST = SERVER_URL + "bonus/getBonusRecordList";
    /**
     * 获取所有标签
     */
    public static final String QUERY_LABEL = SERVER_URL + "beautyRelease/queryLabel";
    /**
     * 查询/设置隐私
     */
    public static final String QUERY_SETPRIVACY = SERVER_URL + "oneInfo/queryAndSetPrivacy";
    /**
     * 更改视频勿扰模式
     */
    public static final String SET_MV_STATUS = SERVER_URL + "oneInfo/setMVStatus";
    /**
     * 发布图文专辑
     */
    public static final String BEAUTY_PULISH = SERVER_URL + "beautyRelease/beautyPublish";
    /**
     * 查询帖子详情
     */
    public static final String QUERY_BEAUTY_LOG = SERVER_URL + "beautyRelease/queryBeautyLogID";
    /**
     * 添加影集评论
     */
    public static final String ADD_COMMENT = SERVER_URL + "showBar/addComment";
    /**
     * 朋友圈好友
     */
    public static final String QUERY_BEAUTY_LOG_FRIEND = SERVER_URL + "beautyRelease/queryBeautyLogFirendMemberIds";
    /**
     * 查询附近帖子
     */
    public static final String FIND_CIRCLE_NEAR = SERVER_URL + "beautyRelease/findCircleNear";
    /**
     * 朋友圈推荐
     */
    public static final String FRIENDS_RECOMEND = SERVER_URL + "beautyRelease/queryPubBeautyLog";
    /**
     * 更新点赞数
     */
    public static final String UPDATE_LIKES = SERVER_URL + "beautyRelease/updateLikes";
    /**
     * 帖子回复评论
     */
    public static final String REPLY_DYNAMIC = SERVER_URL + "dynamicNotice/replyDynamic";

    /**
     * 评论点赞  取消点赞
     */
    public static final String LIKE_SHOW_COMMENT = SERVER_URL + "igmoShowBar/likeShowComment";
    /**
     * 评论列表
     */
    public static final String GET_COMMENT_LIST = SERVER_URL + "showBar/getCommentList";

    /**
     * 查询余额
     */
    public static final String queryWalletInfo = SERVER_URL + "billInfo/queryWalletInfo";


    /**
     * 获取应缴税额
     */
    public static final String getTaxAmountByMonth = SERVER_URL + "order/getTaxAmountByMonth";


    /**
     * 充值业务--微信
     */
    public static final String billInfoRecharge = SERVER_URL + "billInfo/recharge";
    /**
     * 我的动态
     */
    public static final String MY_MOVIEINFO = SERVER_URL + "oneInfo/myMovieInfo";

    /**
     * 查询是否设置支付密码
     */
    public static final String checkAccountPwdIsExist = SERVER_URL + "passwordAndPay/checkAccountPwdIsExist";

    /**
     * 创建支付密码
     */
    public static final String setWalletPassword = SERVER_URL + "passwordAndPay/setWalletPassword";


    /**
     * 校验密码是否正确
     */
    public static final String validatePassword = SERVER_URL + "passwordAndPay/validatePassword";
    /**
     * 实名认证
     */
    public static final String realNameVerify = SERVER_URL + "verify/realNameVerify";


    /**
     * 修改支付密码
     */
    public static final String updateAccountPwd = SERVER_URL + "passwordAndPay/updateAccountPwd";

    /**
     * 查询我的粉丝 关注
     */
    public static final String QUERY_ATTENTIONRELATION = SERVER_URL + "attenRelation/queryAttentionRelation";
    /**
     * 查询他的粉丝 关注
     */
    public static final String queryAttentionOther = SERVER_URL + "attenRelation/queryAttentionOther";
    /**
     * 关注/取消关注
     */
    public static final String ADD_ATTENTIONRELATION = SERVER_URL + "attenRelation/addToAttentionRelation";
    /**
     * 更新（收藏和取消收藏）
     */
    public static final String UPDATE_COLLECTIONS = SERVER_URL + "beautyRelease/updateCollections";
    /**
     * 查询关注的人的帖子
     */
    public static final String QUERY_BEAUTY_LOG_ATTENTION = SERVER_URL + "beautyRelease/queryBeautyLogAttentionRelation";
    /**
     * 查询举报原因
     */
    public static final String QUERY_TYPE_ALL = SERVER_URL + "beautyRelease/queryTypeAll";
    /**
     * 举报日志
     */
    public static final String REPORT_LOG = SERVER_URL + "beautyRelease/reportLog";
    /**
     * 删除日志
     */
    public static final String DELETE_LOG = SERVER_URL + "beautyRelease/deleteLog";

    /**
     * 查询钱包交易记录
     */
    public static final String queryTradebytypePage = SERVER_URL + "order/query_tradebytype_page";

    /**
     * 绑定支付宝账户
     */
    public static final String addAliPaySign = SERVER_URL + "okdilifeValid/addAliPaySign";
    /**
     * 转账
     */
    public static final String turnAccount = SERVER_URL + "hairRed/turnAccount";
    /**
     * 发红包
     */
    public static final String hairReds = SERVER_URL + "hairRed/hairReds";
    /**
     * 红包状态
     */
    public static final String redEnState = SERVER_URL + "hairRed/redEnState";
    /**
     * 收红包
     */
    public static final String collectRed = SERVER_URL + "hairRed/collectRed";
    /**
     * 密码是否正确
     */
    public static final String inputPassword = SERVER_URL + "hairRed/inputPassword";


    /**
     * 绑定支付宝账户
     */
    public static final String aliPaybound = SERVER_URL + "okdilifeValid/aliPaybound";

    /**
     * 查询是否绑定支付宝账户
     */
    public static final String getAliPayAccountId = SERVER_URL + "okdilifeValid/getAliPayAccountId";


    /**
     * 查询是否绑定了微信
     */
    public static final String getWeixinAccountId = SERVER_URL + "okdilifeValid/getWeixinAccountId";


    /**
     * 现金余额提现申请
     */
    public static final String withdrawCreate = SERVER_URL + "withdraw/create";
    /**
     * 金额验证
     */
    public static final String checkWithdrawStatus = SERVER_URL + "withdraw/checkWithdrawStatus";
    /**
     * 查询认证状态
     */
    public static final String queryRealNameVerfyStatus = SERVER_URL + "verify/queryRealNameVerfyStatus";
    /**
     * 打赏红包
     */
    public static final String CLIP_ORDER_PAY = SERVER_URL + "order/clipOrderPay";
    /**
     * 查询帖子打赏详情
     */
    public static final String GET_REWARD_LOG = SERVER_URL + "order/getRewardLog";
    /**
     * 男神女神申请
     */
    public static final String APPLY_STAR = SERVER_URL + "igomoRelation/applyStar";
    /**
     * 退出登录
     */
    public static final String deleteMobMemberLogin = SERVER_URL + "mobMemberLogin/deleteMobMemberLogin";


    /**
     * 联系人--消息---查询通知列表
     */
    public static final String oneInfoNotice = SERVER_URL + "oneInfo/notice";


    /**
     * 联系人--消息---删除通知
     */
    public static final String deleteNotice = SERVER_URL + "oneInfo/deleteNotice";


    /**
     * 图形验证码
     */
    public static String VERIFYCODE = SERVER_URL + "okdiLifeResiterValid/queryVerifyCode";
    /**
     * 查询相关日志
     */
    public static String QUERY_ABOUT_LOG = SERVER_URL + "beautyRelease/queryBeautyAboutLog";
    /**
     * 赞和评论
     */
    public static String queryNoticeList = SERVER_URL + "igomoMessage/queryNoticeList";

    /**
     * 获取全部权限
     */
    public static String QUERY_ALL_JURISDICTION = SERVER_URL + "beautyRelease/getAllJurisdictions";
    /**
     * 修改权限
     */
    public static String UPDATE_LOG_JURISDICTION = SERVER_URL + "beautyRelease/updateLogJurisdictionById";
    /**
     * 天降红包
     */
    public static String ACTIVITY_POPUP = SERVER_URL + "igomoPromo/activityPopup";
    /**
     * 查询音乐列表
     */
    public static String QUERY_MUSIC = SERVER_URL + "music/queryMusic";

    /**
     * 查询是否是男神女神
     */
    public static String okdiLifeResiterQueryStar = SERVER_URL + "okdiLifeResiter/queryStar";

    /**
     * 查询应用版本号
     */
    public static String queryAppVersionInfoAndroid = SERVER_URL + "appVersion/queryAppVersionInfoAndroid";

    /**
     * 发送礼物
     */
    public static String giftMessage = SERVER_URL + "hairRed/giftMessage";


    /**
     * 查询是否有好友请求
     */
    public static String queryGoodFriendRequest = SERVER_URL + "linkman/queryGoodFriendRequest";


    /**
     * 礼物；列表
     */
    public static String findGift = SERVER_URL + "redGift/findGift";
    /**
     * 查询用户是否在黑名单中
     */
    public static String isBlack = SERVER_URL + "beautyRelease/isBlack";


    /**
     * 消息列表---未读通知和评论数
     */
    public static String messageList = SERVER_URL + "oneInfo/messageList";

    /**
     * 个人信息--查询收货地址列表/查询单个收货地址/删除收货地址
     */
    public static String quDelRecAddr = SERVER_URL + "oneInfo/quDelRecAddr";

    /**
     * 个人信息--增加/更新 收货地址
     */
    public static String upAdRecAddr = SERVER_URL + "oneInfo/upAdRecAddr";

    /**
     * 群市场列表
     */
    public static String findTransInfoByGroupId = SERVER_URL + "transInfo/findTransInfoByGroupId";
    /**
     * 拼团列表
     */
    public static String getRegimentList = SERVER_URL + "mxOrder/getRegimentList";
    /**
     * 参团列表
     */
    public static String offeredList = SERVER_URL + "mxOrder/offeredList";
    /**
     * 群组列表
     */
    public static String communityList = SERVER_URL + "groupOfRelate/communityList";
    /**
     * 商品详情
     */
    public static String getTransInfoById = SERVER_URL + "transInfo/getTransInfoById";
    /**
     * 提交订单
     */
    public static String createOrder = SERVER_URL + "mxOrder/createOrder";

    /**
     * 修改群名字
     */
    public static String updateGroupOfName = SERVER_URL + "groupOfRelate/updateGroupOfName";

    /**
     * 上传群logo
     */
    public static String uploadGroupOfImg = SERVER_URL + "groupOfRelate/uploadGroupOfImg";


    /**
     * 修改群地址
     */
    public static String updateGroupOfAddress = SERVER_URL + "groupOfRelate/updateGroupOfAddress";


    /**
     * 确认发货
     */
    public static String confirmShipment = SERVER_URL + "mxOrder/confirmShipment";

    /**
     * 清空聊天记录
     */
    public static String truncLog = SERVER_URL + "groupOfRelate/truncLog";

    /**
     * 置顶群
     */
    public static String topCommunity = SERVER_URL + "groupOfRelate/topCommunity";


    /**
     * 群消息免打扰
     */
    public static String interruptCommunity = SERVER_URL + "groupOfRelate/interruptCommunity";


    /**
     * 是否保存到通讯录
     */
    public static String saveList = SERVER_URL + "groupOfRelate/saveList";


    /**
     * 加群设置
     */
    public static String joinSetUp = SERVER_URL + "groupOfRelate/joinSetUp";


    /**
     * 群禁言设置
     */
    public static String disableSendMsgSetUp = SERVER_URL + "groupOfRelate/disableSendMsgSetUp";

    /**
     * 发布交易
     */
    public static String createTransInfo = SERVER_URL + "transInfo/createTransInfo";
    /**
     * 分页查询群成员
     */
    public static String queryAllMember = SERVER_URL + "groupOfRelate/queryAllMember";

    /**
     * 修改群公告
     */
    public static String updateGroupOfNotice = SERVER_URL + "groupOfRelate/updateGroupOfNotice";

    /**
     * 查询附近群
     */
    public static String nearCommunity = SERVER_URL + "groupOfRelate/nearCommunity";

    /**
     * 搜索群列表加入群
     */
    public static String joinCommunity = SERVER_URL + "groupOfRelate/joinCommunity";

    /**
     * 订单支付
     */
    public static String orderInfoPay = SERVER_URL + "mxOrder/orderInfoPay";


    /**
     * 群主页
     */
    public static String communityHome = SERVER_URL + "groupOfRelate/communityHome";


    /**
     * 删除并退出群租
     */
    public static String deleteAndExit = SERVER_URL + "groupOfRelate/deleteAndExit";

    /**
     * 加群/群禁言页面
     */
    public static String joinOrBannedPage = SERVER_URL + "groupOfRelate/joinOrBannedPage";

    /**
     * 聚合接口, 这里的订单包括 群订单,销售订单,购买订单
     */
    public static String queryOrder = SERVER_URL + "mxOrder/queryOrder";

    /**
     * 同意或取消成团
     */
    public static String agreeRegiment = SERVER_URL + "mxOrder/agreeRegiment";

    /**
     * 申请退款/退货/申请仲裁
     */
    public static String applyReturn = SERVER_URL + "mxOrder/applyReturn";

    /**
     * 查询物流信息接口
     */
    public static String queryExpressTrack = SERVER_URL + "mxOrder/queryExpressTrack";


    /**
     * 获取群二维码
     */
    public static String inviteQRCode = SERVER_URL + "community/inviteQRCode";

    /**
     * 群钱包提现到个人钱包
     */
    public static String withdrawGroupWallent = SERVER_URL + "withdraw/withdrawGroupWallent";


    /**
     * 成员管理页设置是否禁言
     */
    public static String isBanned = SERVER_URL + "groupOfRelate/isBanned";


    /**
     * 成员管理页设置是否禁止发布交易
     */
    public static String isEmbargo = SERVER_URL + "groupOfRelate/isEmbargo";

    /**
     * 成员管理页面是否设为管理员
     */
    public static String isSetToAdministrator = SERVER_URL + "groupOfRelate/isSetToAdministrator";


    /**
     * 群成员管理页面
     */
    public static String memberManagementPage = SERVER_URL + "groupOfRelate/memberManagementPage";

    /**
     * 修改/查看群资料页面
     */
    public static String communityInfo = SERVER_URL + "groupOfRelate/communityInfo";


    /**
     * 群钱包设置页面
     */
    public static String communityWalletSetUp = SERVER_URL + "groupOfRelate/communityWalletSetUp";

    /**
     * 设置是否允许查看钱包
     */
    public static String isAllowSeeWallet = SERVER_URL + "groupOfRelate/isAllowSeeWallet";


    /**
     * 指定禁言
     */
    public static String specifyDisSendMsg = SERVER_URL + "groupOfRelate/specifyDisSendMsg";

    /**
     * 商品下架
     */
    public static String deleteTransInfoById = SERVER_URL + "transInfo/deleteTransInfoById";

    /**
     * 我的群保证金
     */
    public static String myCommunityMargin = SERVER_URL + "groupOfRelate/myCommunityMargin";


    /**
     * 禁言名单
     */
    public static String bannedMemberIdList = SERVER_URL + "groupOfRelate/bannedMemberIdList";

    /**
     * 确认/拒绝退款
     */
    public static String confirmProduct = SERVER_URL + "mxOrder/confirmProduct";

    /**
     * 群交易设置页面
     */
    public static String communityTradeSetUp = SERVER_URL + "groupOfRelate/communityTradeSetUp";

    /**
     * 交易商品设置页面
     */
    public static String tradeProductSetPage = SERVER_URL + "groupOfRelate/tradeProductSetPage";


    /**
     * 完成群交易设置
     */
    public static String finishTradeSetUp = SERVER_URL + "groupOfRelate/finishTradeSetUp";

    /**
     * 判断商家质保金是否充足
     */
    public static String checkProduct = SERVER_URL + "mxOrder/checkProduct";
    /**
     * 批量设置群管理员
     */
    public static String batchSetMaster = SERVER_URL + "groupOfRelate/batchSetMaster";

    /**
     * 群聊发红包
     */
    public static String hairGroupRedPge = SERVER_URL + "hairRed/hairGroupRedPge";
    /**
     * 群成员管理踢出群
     */
    public static String kickedOutCommunity = SERVER_URL + "groupOfRelate/kickedOutCommunity";


    /**
     * .群设置批量加人批量踢人(只能群主和管理员操作)
     */
    public static String addOrKickMember = SERVER_URL + "groupOfRelate/addOrKickMember";

    /**
     * 获取拼团链接
     */
    public static String getPromoQRcode = SERVER_URL + "igomoPromo/getPromoQRcode";

    /**
     * 进群调用查看群类型/群成员类型/是否被禁止发布交易和禁言
     */
    public static String isBannedOrTrade = SERVER_URL + "groupOfRelate/isBannedOrTrade";

    /**
     * 取消禁言
     */
    public static String cancelBanned = SERVER_URL + "groupOfRelate/cancelBanned";


    /**
     * 查询所有费用设置
     */
    public static String getAllOrderFee = SERVER_URL + "fee/getAllOrderFee";


    /**
     * 单聊入账
     */
    public static String enterAccount = SERVER_URL + "hairRed/enterAccount";


    /**
     * 审核仲裁信息
     */
    public static String checkArbitrateInfo = SERVER_URL + "uploadImageController/checkArbitrateInfo";


    /**
     * 发布交易查询商品类型和商品列表
     */
    public static String queryProductList = SERVER_URL + "groupOfRelate/queryProductList";


    /**
     * 单聊设置页面--是否置顶聊天是否消息免打扰
     */
    public static String chatSetting = SERVER_URL + "groupOfRelate/chatSetting";


    /**
     * 群聊领红包
     */
    public static String collarGroupRedPge = SERVER_URL + "hairRed/collarGroupRedPge";


    /**
     * 查询领红包记录
     */
    public static String queryRedPgeRecord = SERVER_URL + "hairRed/queryRedPgeRecord";


    /**
     * 完成群投诉
     */
    public static String communityComplaints = SERVER_URL + "groupOfRelate/communityComplaints";

    /**
     * 根据条件搜索好友
     */
    public static String queryAllPeople = SERVER_URL + "linkman/queryAllPeople";

    /**
     * 根据条件搜索所以注册好友
     */
    public static String queryAllUser = SERVER_URL + "linkman/queryAllUser";


    /**
     * 获取群拉人红包分享链接地址
     */
    public static String redPackage = SERVER_URL + "community/invite/redPackage";

    /**
     * 销售订单 取消订单
     */
    public static String deleteOrder = SERVER_URL + "mxOrder/deleteOrder";

    /**
     * 单独查询是否是勿扰模式
     */
    public static String queryVideoStatus = SERVER_URL + "oneInfo/queryVideoStatus";


    /**
     * 加群申请列表
     */
    public static String communityApplyFor = SERVER_URL + "groupOfRelate/communityApplyFor";

    /**
     * 是否同意加群申请
     */
    public static String isAgreeJoinRequest = SERVER_URL + "groupOfRelate/isAgreeJoinRequest";

    /**
     * 使用工分兑换美币
     */
    public static String exchangeMeiBi = SERVER_URL + "beautyRelease/exchangeMeiBi";

    /**
     * 查询个人工分
     */
    public static String queryCharmCounts = SERVER_URL + "beautyRelease/queryCharmCounts";

    /**
     * 查询短信通知列表
     */
    public static String queryMessage = SERVER_URL + "music/queryMessage";

    /**
     * 分享帖子 更新工分
     */
    public static String updateShare = SERVER_URL + "beautyRelease/updateShare";
    /**
     * 浏览帖子 更新工分
     */
    public static String updateBrowse = SERVER_URL + "beautyRelease/updateBrowse";
    /**
     * 查询帖子的预计收入
     */
    public static String beautyLogIncome = SERVER_URL + "beautyRelease/beautyLogIncome";

    /**
     * 支付回调
     */
    public static String updateOrderPayment = SERVER_URL + "paycallback/updateOrderPayment";

    /**
     * 查询魔拍是否是HOT状态
     */
    public static String queryIsHotStatus = SERVER_URL + "magicShot/queryIsHotStatus";

    /**
     * 判断群成员能否邀请人
     */
    public static String groupRedsJudge = SERVER_URL + "hairRed/groupRedsJudge";

    /**
     * 查询视频列表
     */
    public static String queryBeautyVideoLog = SERVER_URL + "beautyRelease/queryBeautyVideoLog";
    /**
     * 测试token接口
     */
    public static String queryPromoWordsList = SERVER_URL + "igomoPromo/queryPromoWordsList";

    /**
     * 测试token接口
     */
    public static String getAccessToken = SERVER_URL + "token/getAccessToken";


}

