package com.merrichat.net.api;

import com.merrichat.net.model.AboutLogModel;
import com.merrichat.net.model.AddGoodFriendsModel;
import com.merrichat.net.model.AllocateModel;
import com.merrichat.net.model.ApplyReturnModel;
import com.merrichat.net.model.ApplyStarModel;
import com.merrichat.net.model.BeReportModel;
import com.merrichat.net.model.ClipOrderPayModel;
import com.merrichat.net.model.CreateGroupOfModel;
import com.merrichat.net.model.DeleteOrderModel;
import com.merrichat.net.model.EncounterPersonModel;
import com.merrichat.net.model.GetCommunicationFeeModel;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.GiftListsMode;
import com.merrichat.net.model.OrderSignByOrderIdModel;
import com.merrichat.net.model.PresentGiftModel;
import com.merrichat.net.model.QueryAllMagicTypeModel;
import com.merrichat.net.model.QueryGoodFriendRequestModel;
import com.merrichat.net.model.QueryMagicListModel;
import com.merrichat.net.model.QueryReportingTypesModel;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.Response;
import com.merrichat.net.model.UploadGroupOfImgModel;
import com.merrichat.net.model.UploadModel;
import com.merrichat.net.model.WaiteGroupBuyDetailModel;
import com.merrichat.net.model.WaiteGroupBuyModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface WebApiService {

    /**
     * 获取list礼物列表
     *
     * @return
     */
    @GET("redGift/findGift")
    Observable<GiftListsMode> findGift();


    /**
     * 获取遇到的人列表
     *
     * @param memberId 当前本人的memberId
     * @return
     */
    @FormUrlEncoded
    @POST("im/encounterPerson")
    Observable<EncounterPersonModel> encounterPerson(@Field("memberId") String memberId);


    /**
     * 送礼物
     *
     * @param memberId
     * @param outMemberId  出账 memberId 等同于 memberId
     * @param outAccountId 出账 account
     * @param inMemberId   入账memberId
     * @param inAccountId  入账 account
     * @param amount       金额
     * @param title        标题   （打赏，赠送礼物，视频）
     * @param type         （1，视频分成  2，语音  3，礼物  4，打赏）
     * @param remark       备注
     * @return
     */
    @FormUrlEncoded
    @POST("order/clipOrderPay")
    Observable<ClipOrderPayModel> clipOrderPay(@Field("memberId") String memberId,
                                               @Field("outMemberId") String outMemberId,
                                               @Field("outAccountId") String outAccountId,
                                               @Field("inMemberId") String inMemberId,
                                               @Field("inAccountId") String inAccountId,
                                               @Field("amount") int amount,
                                               @Field("title") String title,
                                               @Field("type") int type,
                                               @Field("remark") String remark,
                                               @Field("tieId") String tieId);

    /**
     * 男神女神申请
     *
     * @param memberId   本人的memberId
     * @param status     0表示的是查看审核状态,1:表示的是提交男/女神的申请
     * @param deviceType 设备类型
     * @return
     */
    @FormUrlEncoded
    @POST("igomoRelation/applyStar")
    Observable<ApplyStarModel> applyStar(@Field("memberId") String memberId,
                                         @Field("status") int status,
                                         @Field("deviceType") String deviceType);


    /**
     * @return 视频聊天分配长连接地址---美遇
     */
    @FormUrlEncoded
    @POST("im/videoChat")
    Observable<AllocateModel> IM_VIDEO_CHAT(@Field("memberId") String memberId);


    /**
     * @param accountId 账户 ID
     * @param maxMoney  订单总价格（可选）
     * @param uid       生活传 memberId 传 shopId
     * @return 生活端查询余额 红包和金币
     */
    @FormUrlEncoded
    @POST("billInfo/queryWalletInfo")
    Observable<QueryWalletInfoModel> queryWalletInfo(@Field("accountId") String accountId,
                                                     @Field("maxMoney") String maxMoney,
                                                     @Field("uid") String uid);


    /**
     * @param memberId     用户id
     * @param memberName   用户名称 realname
     * @param memberUrl    用户头像
     * @param toMemberId   好友id
     * @param toMemberName 好友名称
     * @param toMemberUrl  好友头像
     * @param source       来源 （默认为0）
     * @return 添加好朋友
     */
    @FormUrlEncoded
    @POST("linkman/addGoodFriends")
    Observable<AddGoodFriendsModel> addGoodFriends(@Field("memberId") String memberId,
                                                   @Field("memberName") String memberName,
                                                   @Field("memberUrl") String memberUrl,
                                                   @Field("toMemberId") String toMemberId,
                                                   @Field("toMemberName") String toMemberName,
                                                   @Field("toMemberUrl") String toMemberUrl,
                                                   @Field("source") String source);

    /**
     * 美遇筛选条件
     *
     * @param memberId 用户id
     * @return
     */
    @FormUrlEncoded
    @POST("bonus/getCommunicationFee")
    Observable<GetCommunicationFeeModel> getCommunicationFee(@Field("memberId") String memberId,
                                                             @Field("type") String type,
                                                             @Field("flag") String flag);


    /**
     * 举报筛选条件
     *
     * @param currentPage 当前页
     * @param pageSize    每页数
     * @return
     */
    @FormUrlEncoded
    @POST("oneInfo/queryReportingTypes")
    Observable<QueryReportingTypesModel> queryReportingTypes(@Field("currentPage") String currentPage,
                                                             @Field("pageSize") String pageSize);

    /**
     * @param memberId
     * @param toMemberId
     * @param giftId
     * @return 赠送礼物完毕时要通知服务器
     */
    @FormUrlEncoded
    @POST("oneInfo/presentGift")
    Observable<PresentGiftModel> presentGift(@Field("memberId") String memberId,
                                             @Field("toMemberId") String toMemberId,
                                             @Field("giftId") long giftId);


    /**
     * 判断当前账户是否被举报
     *
     * @param memberId 用户id
     * @return
     */
    @FormUrlEncoded
    @POST("oneInfo/beReport")
    Observable<BeReportModel> beReport(@Field("memberId") String memberId);


    /**
     * 上传群头像接口
     *
     * @param file file文件
     * @return
     */
    @Multipart
    @POST("groupOfRelate/uploadGroupOfImg")
    Observable<UploadGroupOfImgModel> uploadGroupOfImg(@Part("multipartFile") RequestBody description,
                                                       @Part MultipartBody.Part file);


    /**
     * 创建群聊
     * <p>
     *
     * @return
     */
    @FormUrlEncoded
    @POST("groupOfRelate/createGroupOf")
    Observable<CreateGroupOfModel> createGroupOf(@FieldMap Map<String, Object> params);

    /**
     * 查询 待成团/待发货/待收货/已结束/退款/查询订单详情 列表 订单
     *
     * @param jsObject
     * @return
     */
    @FormUrlEncoded
    @POST("mxOrder/queryOrder")
    Observable<WaiteGroupBuyModel> queryOrder(@Field("jsObject") String jsObject);


    /**
     * @param
     * @return 查询 待成团/待发货/待收货/已结束/退款/查询订单详情 订单
     */
    @FormUrlEncoded
    @POST("mxOrder/queryOrder")
    Observable<WaiteGroupBuyDetailModel> queryOrderDetail(@Field("jsObject") String jsObject);


    /**
     * 确认收货
     *
     * @param memberId 用户id
     * @param orderId  订单id
     * @return
     */
    @FormUrlEncoded
    @POST("mxOrder/orderSignByOrderId")
    Observable<OrderSignByOrderIdModel> orderSignByOrderId(@Field("memberId") String memberId,
                                                           @Field("orderId") String orderId);


    /**
     * 申请退款/退货/申请仲裁
     * <p>
     * 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     *
     * @param orderId   订单id
     * @param flag      1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     * @param orderJson 退款信息
     * @return
     */
    @FormUrlEncoded
    @POST("mxOrder/applyReturn")
    Observable<ApplyReturnModel> applyReturn(@Field("orderId") String orderId,
                                             @Field("flag") String flag,
                                             @Field("orderJson") String orderJson);

    /**
     * 申请退款/退货/申请仲裁
     * <p>
     * 1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     *
     * @param orderId   订单id
     * @param flag      1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
     * @param orderJson 仲裁信息，包括仲裁图片和仲裁理由
     * @return
     */
    @FormUrlEncoded
    @POST("mxOrder/applyReturn")
    Observable<ApplyReturnModel> applyReturn2(@Field("orderId") String orderId,
                                              @Field("flag") String flag,
                                              @Field("orderJson") String orderJson);

    /**
     * 取消订单
     *
     * @param memberId
     * @param orderId     订单ID
     * @param flag        0:买家 1:表示是卖家
     * @param orderStatus 0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 ,8仲裁
     * @return
     */
    @FormUrlEncoded
    @POST("mxOrder/deleteOrder")
    Observable<DeleteOrderModel> deleteOrder(@Field("memberId") String memberId,
                                             @Field("orderId") String orderId,
                                             @Field("flag") String flag,
                                             @Field("orderStatus") String orderStatus,
                                             @Field("key") String key);


    /**
     * 个人信息--上传图片综合接口(申请仲裁和退货等)
     *
     * @param description
     * @return
     */
    @Multipart
    @POST("uploadImageController/upload")
    Observable<UploadModel> upload(@Part("multipartFile") RequestBody description,
                                   @Part() List<MultipartBody.Part> parts,
                                   @PartMap Map<String, RequestBody> map);


    /**
     * 相关帖子
     *
     * @param id          帖子Id
     * @param pageSize
     * @param pageNum
     * @param classifystr 帖子分类
     * @return
     */
    @FormUrlEncoded
    @POST("beautyRelease/queryBeautyAboutLog")
    Observable<AboutLogModel> queryBeautyAboutLog(@Field("id") String id,
                                                  @Field("pageSize") int pageSize,
                                                  @Field("pageNum") int pageNum,
                                                  @Field("classifystr") String classifystr);


    /**
     * 查询是否是好友
     *
     * @param memberId   当前用户的memberId
     * @param toMemberId 对方的 memberId
     * @return
     */
    @FormUrlEncoded
    @POST("linkman/queryGoodFriendRequest")
    Observable<QueryGoodFriendRequestModel> queryGoodFriendRequest(@Field("memberId") String memberId,
                                                                   @Field("toMemberId") String toMemberId);


    /**
     * 获取list礼物列表
     *
     * @return
     */
    @GET("mxOrder/getNets")
    Observable<GetNetsModel> getNets();


//    clip-pub/linkman/insertInviteFriendsRecord


    /**
     * 批量插入通讯录好友
     *
     * @param memberId   用户id
     * @param phone      用户手机
     * @param memberJson 通讯录好友
     * @return
     */
    @FormUrlEncoded
    @POST("linkman/insertInviteFriendsRecord")
    Observable<UploadModel> insertInviteFriendsRecord(@Field("memberId") String memberId,
                                                      @Field("phone") String phone,
                                                      @Field("memberJson") String memberJson);


    /**
     * 批量插入通讯录好友
     *
     * @param memberId   用户id
     * @param phone      用户手机
     * @param memberJson 通讯录好友
     * @return
     */
    @FormUrlEncoded
    @POST("linkman/synchroAddressBook")
    Observable<UploadModel> synchroAddressBook(@Field("memberId") String memberId,
                                               @Field("phone") String phone,
                                               @Field("uploadFlag") String uploadFlag,
                                               @Field("memberJson") String memberJson);


    /**
     * 获取魔拍列类型 列表
     *
     * @return
     */
    @GET("magicShot/queryAllMagicType")
    Observable<QueryAllMagicTypeModel> queryAllMagicType();


    /**
     * 查询魔拍列表
     *
     * @param magicType 魔拍类型
     * @param pageNum   页号
     * @param pageSize  每页多少条信息
     * @return
     */
    @FormUrlEncoded
    @POST("magicShot/queryMagicList")
    Observable<QueryMagicListModel> queryMagicList(@Field("magicType") String magicType,
                                                   @Field("pageNum") int pageNum,
                                                   @Field("pageSize") int pageSize);


}
