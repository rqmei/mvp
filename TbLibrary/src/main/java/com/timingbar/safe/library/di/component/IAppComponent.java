package com.timingbar.safe.library.di.component;

import android.app.Application;
import com.google.gson.Gson;
import com.timingbar.safe.library.base.BaseApplication;
import com.timingbar.safe.library.base.delegate.AppDelegate;
import com.timingbar.safe.library.di.module.AppModule;
import com.timingbar.safe.library.di.module.ClientModule;
import com.timingbar.safe.library.di.module.GlobalConfigModule;
import com.timingbar.safe.library.mvp.IRepositoryManager;
import com.timingbar.safe.library.view.imageloader.ImageLoader;
import dagger.Component;
import com.timingbar.safe.library.integration.AppManager;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import okhttp3.OkHttpClient;

import javax.inject.Singleton;
import java.io.File;
import java.util.Map;

/**
 * AppComponent
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 为连接器(component),连接module和将要注入的class
 * 注入AndroidInjectionModule：在应用程序的ApplicationComponent中，注入AndroidInjectionModule，
 * 以确保Android的类(Activity、Fragment、Service、BroadcastReceiver及ContentProvider等)可以绑定
 *
 * @author rqmei on 2018/1/29
 */
@Singleton
@Component(modules = {AndroidInjectionModule.class, AndroidSupportInjectionModule.class, AppModule.class, ClientModule.class, GlobalConfigModule.class})
public interface IAppComponent extends AndroidInjector<BaseApplication> {
    //全局单例app
    Application application();

    //Rxjava错误处理管理类
    RxErrorHandler rxErrorHandler();

    //glide加载网络图片的请求框架
    OkHttpClient okHttpClient();

    //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    ImageLoader imageLoader();

    //gson
    Gson gson();

    //用于管理所有activity
    AppManager appManager();

    //用于管理所有仓库(网络请求层),以及数据缓存层
    IRepositoryManager repositoryManager();

    //缓存文件根目录(RxCache和Glide的的缓存都已经作为子文件夹在这个目录里),应该将所有缓存放到这个根目录里,便于管理和清理,可在GlobeConfigModule里配置
    File cacheFile();

    //用来存取一些整个App公用的数据,切勿大量存放大容量数据
    Map<String, Object> extras();

    void inject(AppDelegate delegate);
}
