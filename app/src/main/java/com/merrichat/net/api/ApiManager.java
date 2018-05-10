package com.merrichat.net.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.callback.MyHttpLoggingInterceptor;
import com.merrichat.net.http.UrlConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiManager
 * Created by justin on 16/3/8.
 */
public class ApiManager {

    private static ApiManager apiManager;
    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .build();
        }
    };
    Authenticator mAuthenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response)
                throws IOException { // 遇上401之后的refreshtoken
            //Your.sToken = service.refreshToken();
            return response.request().newBuilder()
                    .addHeader("Authorization", "newAccessToken")
                    .build();
        }
    };
    private HashMap<Class, Retrofit> SERVICE_RETROFIT_BIND = new HashMap<>();
    private Retrofit retrofit_webapi;
    private Retrofit retrofit_getsocket;
    private ConcurrentHashMap<Class, Object> cachedApis = new ConcurrentHashMap<>();


    public ApiManager() {
        MyHttpLoggingInterceptor loggingInterceptor = new MyHttpLoggingInterceptor(MerriApp.TAG_HTTP, MerriApp.isDebug);
        loggingInterceptor.setPrintLevel(MyHttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.SEVERE);                               //log颜色级别，决定了log在控制台显示的颜色
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)  //添加log监听工具
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)   //超时时间
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addNetworkInterceptor(new StethoInterceptor())
                .authenticator(mAuthenticator);
        OkHttpClient client = builder
                .build();

        retrofit_webapi = new Retrofit.Builder()
                .baseUrl(UrlConfig.getLifeUrl())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit_getsocket = new Retrofit.Builder()
                .baseUrl(UrlConfig.getLifeUrl())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SERVICE_RETROFIT_BIND.put(WebApiService.class, retrofit_getsocket);
        SERVICE_RETROFIT_BIND.put(WebApiService.class, retrofit_webapi);


    }

    public static ApiManager getApiManager() {
        if (apiManager == null) {
            synchronized (ApiManager.class) {
                if (apiManager == null) {
                    apiManager = new ApiManager();
                }
            }
        }
        return apiManager;
    }

    /**
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T apiService(Class<T> clz) {
        return getApiManager().getService(clz);
    }

    /**
     * @param clz
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> clz) {
        Object obj = cachedApis.get(clz);
        if (obj != null) {
            return (T) obj;
        } else {
            Retrofit retrofit = SERVICE_RETROFIT_BIND.get(clz);
            if (retrofit != null) {
                T service = retrofit.create(clz);
                cachedApis.put(clz, service);
                return service;
            } else {
                return null;
            }
        }
    }

}
