package com.timingbar.safe.library.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import com.timingbar.safe.library.http.IGlobalHttpHandler;
import com.timingbar.safe.library.http.RequestInterceptor;
import com.timingbar.safe.library.util.DataHelper;
import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClientModule
 * -----------------------------------------------------------------------------------------------------------------------------------
 * Module其实是一个简单工厂模式，Module里面的方法基本都是创建类实例的方法。
 * 属于Component的实例端的（连接各种目标类依赖实例的端）
 *
 * @author rqmei on 2018/1/29
 */
@Module
public class ClientModule {
    private static final int TIME_OUT = 10;


    /**
     * 返回对应的Retrofit实例
     *
     * @param application
     * @param configuration
     * @param builder
     * @param client        设置okhttp
     * @param httpUrl       域名
     * @return Retrofit实例
     */
    @Singleton
    @Provides
    Retrofit provideRetrofit(Application application, @Nullable RetrofitConfiguration configuration, Retrofit.Builder builder, OkHttpClient client, HttpUrl httpUrl) {
        builder.baseUrl (httpUrl)//域名
                .client (client)//设置okhttp
                .addCallAdapterFactory (RxJava2CallAdapterFactory.create ())//使用rxjava
                .addConverterFactory (GsonConverterFactory.create ());//使用Gson
        if (configuration != null)
            configuration.configRetrofit (application, builder);
        return builder.build ();
    }

    /**
     * 提供OkhttpClient
     *
     * @param application
     * @param configuration
     * @param builder
     * @param intercept
     * @param interceptors
     * @param handler
     * @return
     */
    @Singleton
    @Provides
    OkHttpClient provideClient(Application application, @Nullable OkhttpConfiguration configuration, OkHttpClient.Builder builder, Interceptor intercept
            , @Nullable List<Interceptor> interceptors, @Nullable IGlobalHttpHandler handler) {
        builder.connectTimeout (TIME_OUT, TimeUnit.SECONDS)
                .readTimeout (TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor (intercept);

        if (handler != null)
            builder.addInterceptor (new Interceptor () {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed (handler.onHttpRequestBefore (chain, chain.request ()));
                }
            });

        if (interceptors != null) {//如果外部提供了interceptor的集合则遍历添加
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor (interceptor);
            }
        }

        if (configuration != null)
            configuration.configOkhttp (application, builder);
        return builder.build ();
    }


    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder ();
    }


    @Singleton
    @Provides
    OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder ();
    }


    @Singleton
    @Provides
    Interceptor provideInterceptor(RequestInterceptor intercept) {
        return intercept;//打印请求信息的拦截器
    }


    /**
     * 提供RXCache客户端
     *
     * @param configuration  缓存配置
     * @param cacheDirectory RxCache缓存路径
     * @return
     */
    @Singleton
    @Provides
    RxCache provideRxCache(Application application, @Nullable RxCacheConfiguration configuration, @Named("RxCacheDirectory") File cacheDirectory) {
        RxCache.Builder builder = new RxCache.Builder ();
        if (configuration != null)
            configuration.configRxCache (application, builder);
        return builder.persistence (cacheDirectory, new GsonSpeaker ());
    }


    /**
     * 需要单独给RxCache提供缓存路径
     * 提供RxCache缓存地址
     */
    @Singleton
    @Provides
    @Named("RxCacheDirectory")
    File provideRxCacheDirectory(File cacheDir) {
        File cacheDirectory = new File (cacheDir, "RxCache");
        return DataHelper.makeDirs (cacheDirectory);
    }

    /**
     * 提供处理Rxjava错误的管理器,比如可在网络链接出现错误时自动重试
     *
     * @return
     */
    @Singleton
    @Provides
    RxErrorHandler proRxErrorHandler(Application application, ResponseErrorListener listener) {
        return RxErrorHandler
                .builder ()
                .with (application)
                .responseErrorListener (listener)
                .build ();
    }

    public interface RetrofitConfiguration {
        void configRetrofit(Context context, Retrofit.Builder builder);
    }

    public interface OkhttpConfiguration {
        void configOkhttp(Context context, OkHttpClient.Builder builder);
    }

    public interface RxCacheConfiguration {
        void configRxCache(Context context, RxCache.Builder builder);
    }
}
