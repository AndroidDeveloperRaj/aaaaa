package com.merrichat.net.api;

/**
 * Created by wangweiwei on 2018/1/24.
 */

import com.merrichat.net.model.GetNetsModel;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface ExpressApiService {

    /**
     * 获取list礼物列表
     *
     * @return
     */
    @GET("expBasGateway/getNets")
    Observable<GetNetsModel> getNets();


}
