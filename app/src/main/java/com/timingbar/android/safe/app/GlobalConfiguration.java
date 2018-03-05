package com.timingbar.android.safe.app;

import android.app.Application;
import android.content.Context;
import android.net.ParseException;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.timingbar.safe.library.base.delegate.AppDelegate;
import com.timingbar.safe.library.di.module.GlobalConfigModule;
import com.timingbar.safe.library.http.IGlobalHttpHandler;
import com.timingbar.safe.library.http.RequestInterceptor;
import com.timingbar.safe.library.integration.ActivityLifecycle;
import com.timingbar.safe.library.integration.IConfigModule;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.HttpException;
import timber.log.Timber;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * GlobalConfiguration
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 配置框架的自定义属性
 * GlobalConfigModule使用建造者模式将App的全局配置信息封装进Module(使用Dagger注入到需要配置信息的地方),可以配置CacheFile,Interceptor等,
 * 甚至于Retrofit,Okhttp,RxCache都可以自定义配置,因为使用的是建造者模式所以如你有其他配置信息需要使用Dagger注入,直接就可以添加进Builder并且不会影响到其他地方
 *
 * @author rqmei on 2018/1/29
 */

public class GlobalConfiguration implements IConfigModule {
    GlobalConfigModule.Builder builder;
    ActivityLifecycle activityLifecycle;

    /**
     * 使用builder可以为框架配置一些配置信息
     *
     * @param context
     * @param builder
     */
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        builder.baseurl (ApiConfig.HOSTSERVER)
                .globalHttpHandler (new IGlobalHttpHandler () {// 这里可以提供一个全局处理Http请求和响应结果的处理类,
                    // 这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                    @Override
                    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                        // 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后重新请求token,并重新执行请求
                        try {
                            if (!TextUtils.isEmpty (httpResult) && RequestInterceptor.isJson (response.body ())) {
                                JSONObject object = new JSONObject (httpResult);
                                String success = object.getString ("success");
                                Log.e ("success", "success=" + success);
                                //                        if (StringHelper.checkStrIsNotNull (success)) {
                                //                            UiUtils.snackbarText ("后台接口数据错误！");
                                //                        }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                            return response;
                        }
                     /* 这里如果发现token过期,可以先请求最新的token,然后在拿新的token放入request里去重新请求
                        注意在这个回调之前已经调用过proceed,所以这里必须自己去建立网络请求,如使用okhttp使用新的request去请求
                        create a new request and modify it accordingly using the new token
                        Request newRequest = chain.request().newBuilder().header("token", newToken).build();
                        retry the request
                        response.body().close();
                        如果使用okhttp将新的请求,请求成功后,将返回的response  return出去即可
                        如果不需要返回新的结果,则直接把response参数返回出去 */
                        return response;
                    }

                    //这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
                    @Override
                    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                        /* 如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的requeat如增加header,不做操作则直接返回request参数
                           return chain.request().newBuilder().header("token", tokenId).build(); */
                        return request;
                    }
                }).responseErrorListener ((context1, t) -> {
                    /* 用来提供处理所有错误的监听，rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效 */
            Timber.tag ("Catch-Error").w (t.getMessage ());
            //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
            String msg = "未知错误";
            if (t instanceof UnknownHostException) {
                msg = "网络不可用";
            } else if (t instanceof SocketTimeoutException) {
                msg = "请求网络超时";
            } else if (t instanceof HttpException) {
                HttpException httpException = (HttpException) t;
                msg = convertStatusCode (httpException);
            } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException || t instanceof JsonIOException) {
                msg = "数据解析错误";
            }

            //  UiUtils.snackbarText (msg);
        }).gsonConfiguration ((context1, gsonBuilder) -> {
            gsonBuilder.serializeNulls ()//支持序列化null的参数
                    .enableComplexMapKeySerialization ();//支持将序列化key为object的map,默认只能序列化key为string的map
        }).retrofitConfiguration ((context1, retrofitBuilder) -> {
            //                    retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson
        }).okhttpConfiguration ((context1, okhttpBuilder) -> {
            okhttpBuilder.writeTimeout (10, TimeUnit.SECONDS);
        }).rxCacheConfiguration ((context1, rxCacheBuilder) -> {
            rxCacheBuilder.useExpiredDataIfLoaderNotAvailable (true);
        });
        this.builder = builder;
    }

    /**
     * 向Application的生命周期中注入一些自定义逻辑
     *
     * @param context
     * @param lifecycles
     */
    @Override
    public void injectAppLifecycle(Context context, List<AppDelegate.Lifecycle> lifecycles) {
        // AppDelegate.Lifecycle 的所有方法都会在基类Application对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        lifecycles.add (new AppDelegate.Lifecycle () {
            @Override
            public void onCreate(Application application) {
                Timber.plant (new Timber.DebugTree ());
                //                                if (BuildConfig.LOG_DEBUG) {//Timber日志打印
                //                                    Timber.plant (new Timber.DebugTree ());
                //                                }
                //leakCanary内存泄露检查
                // ((IApp) application).getAppComponent ().extras ().put (RefWatcher.class.getName (), BuildConfig.USE_CANARY ? LeakCanary.install (application) : RefWatcher.DISABLED);
            }

            @Override
            public void onTerminate(Application application) {
                //内存泄露检查
            }
        });

    }

    /**
     * 向Activity的生命周期中注入一些自定义逻辑
     *
     * @param context
     * @param lifecycles
     */
    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
       /* lifecycles.add (new Application.ActivityLifecycleCallbacks () {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });*/
    }

    /**
     * 向Fragment的生命周期中注入一些自定义逻辑
     *
     * @param context
     * @param lifecycles
     */
    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        //        lifecycles.add (new FragmentManager.FragmentLifecycleCallbacks () {
        //            @Override
        //            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        //                super.onFragmentCreated (fm, f, savedInstanceState);
        //            }
        //
        //            @Override
        //            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        //                super.onFragmentDestroyed (fm, f);
        //            }
        //        });
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code () == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code () == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code () == 400) {
            msg = "移动端和服务端接口参数不对";
        } else if (httpException.code () == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code () == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message ();
        }
        return msg;
    }
}
