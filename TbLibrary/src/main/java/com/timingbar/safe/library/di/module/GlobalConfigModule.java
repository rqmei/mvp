package com.timingbar.safe.library.di.module;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.timingbar.safe.library.http.IGlobalHttpHandler;
import com.timingbar.safe.library.util.DataHelper;
import com.timingbar.safe.library.view.imageloader.IBaseImageLoaderStrategy;
import com.timingbar.safe.library.view.imageloader.glide.GlideImageLoaderStrategy;
import dagger.Module;
import dagger.Provides;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;

import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * GlobalConfigModule
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/1/29
 */
@Module
public class GlobalConfigModule {
    private HttpUrl mApiUrl;
    private IBaseImageLoaderStrategy mLoaderStrategy;//图片加载
    private IGlobalHttpHandler mHandler;//处理http响应结果
    private List<Interceptor> mInterceptors;
    private ResponseErrorListener mErrorListener; //针对于Rxjava的错误处理库,可在网络链接出现错误时自动重试
    private File mCacheFile;
    private ClientModule.RetrofitConfiguration mRetrofitConfiguration;
    private ClientModule.OkhttpConfiguration mOkhttpConfiguration;
    private ClientModule.RxCacheConfiguration mRxCacheConfiguration;
    private AppModule.GsonConfiguration mGsonConfiguration;

    /**
     * @description: 设置baseurl
     */
    private GlobalConfigModule(Builder builder) {
        this.mApiUrl = builder.apiUrl;
        this.mLoaderStrategy = builder.loaderStrategy;
        this.mHandler = builder.handler;
        this.mInterceptors = builder.interceptors;
        this.mErrorListener = builder.responseErrorListener;
        this.mCacheFile = builder.cacheFile;
        this.mRetrofitConfiguration = builder.retrofitConfiguration;
        this.mOkhttpConfiguration = builder.okhttpConfiguration;
        this.mRxCacheConfiguration = builder.rxCacheConfiguration;
        this.mGsonConfiguration = builder.gsonConfiguration;
    }

    public static Builder builder() {
        return new Builder ();
    }


    @Singleton
    @Provides
    @Nullable
    List<Interceptor> provideInterceptors() {
        return mInterceptors;
    }


    @Singleton
    @Provides
    HttpUrl provideBaseUrl() {
        return mApiUrl == null ? HttpUrl.parse ("https://api.github.com/") : mApiUrl;
    }


    @Singleton
    @Provides
    IBaseImageLoaderStrategy provideImageLoaderStrategy() {//图片加载框架默认使用glide
        return mLoaderStrategy == null ? new GlideImageLoaderStrategy () : mLoaderStrategy;
    }


    @Singleton
    @Provides
    @Nullable
    IGlobalHttpHandler provideGlobalHttpHandler() {
        return mHandler;//处理Http请求和响应结果
    }


    /**
     * 提供缓存文件
     */
    @Singleton
    @Provides
    File provideCacheFile(Application application) {
        return mCacheFile == null ? DataHelper.getCacheFile (application) : mCacheFile;
    }


    /**
     * 提供处理Rxjava错误的管理器的回调
     *
     * @return
     */
    @Singleton
    @Provides
    ResponseErrorListener provideResponseErrorListener() {
        return mErrorListener == null ? ResponseErrorListener.EMPTY : mErrorListener;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.RetrofitConfiguration provideRetrofitConfiguration() {
        return mRetrofitConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.OkhttpConfiguration provideOkhttpConfiguration() {
        return mOkhttpConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.RxCacheConfiguration provideRxCacheConfiguration() {
        return mRxCacheConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    AppModule.GsonConfiguration provideGsonConfiguration() {
        return mGsonConfiguration;
    }

    public static final class Builder {
        private HttpUrl apiUrl;
        private IBaseImageLoaderStrategy loaderStrategy;
        private IGlobalHttpHandler handler;
        private List<Interceptor> interceptors;
        private ResponseErrorListener responseErrorListener;
        private File cacheFile;
        private ClientModule.RetrofitConfiguration retrofitConfiguration;
        private ClientModule.OkhttpConfiguration okhttpConfiguration;
        private ClientModule.RxCacheConfiguration rxCacheConfiguration;
        private AppModule.GsonConfiguration gsonConfiguration;


        private Builder() {
        }

        /**
         * 基础url
         *
         * @param baseurl
         * @return
         */
        public Builder baseurl(String baseurl) {
            if (TextUtils.isEmpty (baseurl)) {
                throw new IllegalArgumentException ("baseurl can not be empty");
            }
            this.apiUrl = HttpUrl.parse (baseurl);
            return this;
        }

        /**
         * 用来请求网络图片
         *
         * @param loaderStrategy
         * @return
         */
        public Builder imageLoaderStrategy(IBaseImageLoaderStrategy loaderStrategy) {
            this.loaderStrategy = loaderStrategy;
            return this;
        }

        /**
         * 处理Http请求和响应结果
         *
         * @param handler
         * @return
         */
        public Builder globalHttpHandler(IGlobalHttpHandler handler) {
            this.handler = handler;
            return this;
        }

        /**
         * 动态添加任意个interceptor
         *
         * @param interceptor
         * @return
         */
        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null)
                interceptors = new ArrayList<> ();
            this.interceptors.add (interceptor);
            return this;
        }

        /**
         * 用来提供处理所有Rxjava的onError逻辑，rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效
         *
         * @param listener
         * @return
         */
        public Builder responseErrorListener(ResponseErrorListener listener) {
            this.responseErrorListener = listener;
            return this;
        }

        /**
         * 缓存文件
         *
         * @param cacheFile
         * @return
         */
        public Builder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        /**
         * 这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象
         *
         * @param retrofitConfiguration
         * @return
         */
        public Builder retrofitConfiguration(ClientModule.RetrofitConfiguration retrofitConfiguration) {
            this.retrofitConfiguration = retrofitConfiguration;
            return this;
        }

        /**
         * 这里可以自己自定义配置Okhttp的参数
         *
         * @param okhttpConfiguration
         * @return
         */
        public Builder okhttpConfiguration(ClientModule.OkhttpConfiguration okhttpConfiguration) {
            this.okhttpConfiguration = okhttpConfiguration;
            return this;
        }

        /**
         * 这里可以自己自定义配置RxCache的参数
         *
         * @param rxCacheConfiguration
         * @return
         */
        public Builder rxCacheConfiguration(ClientModule.RxCacheConfiguration rxCacheConfiguration) {
            this.rxCacheConfiguration = rxCacheConfiguration;
            return this;
        }

        /**
         * 这里可以自己自定义配置Gson的参数
         *
         * @param gsonConfiguration
         * @return
         */
        public Builder gsonConfiguration(AppModule.GsonConfiguration gsonConfiguration) {
            this.gsonConfiguration = gsonConfiguration;
            return this;
        }


        public GlobalConfigModule build() {
            return new GlobalConfigModule (this);
        }


    }
}
