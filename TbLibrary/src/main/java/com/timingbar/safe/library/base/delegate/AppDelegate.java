package com.timingbar.safe.library.base.delegate;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import com.timingbar.safe.library.di.component.DaggerIAppComponent;
import com.timingbar.safe.library.di.component.IAppComponent;
import com.timingbar.safe.library.di.module.AppModule;
import com.timingbar.safe.library.di.module.ClientModule;
import com.timingbar.safe.library.di.module.GlobalConfigModule;
import com.timingbar.safe.library.integration.ActivityLifecycle;
import com.timingbar.safe.library.integration.IConfigModule;
import com.timingbar.safe.library.integration.ManifestParser;
import com.timingbar.safe.library.view.imageloader.glide.GlideImageConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * AppDelegate
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 实现application中相关的配置信息
 *
 * @author rqmei on 2018/1/29
 */

public class AppDelegate implements IApp {
    private Application mApplication;
    //公开的 module与View的连接组建
    private IAppComponent mAppComponent;
    /**
     * {@link ActivityLifecycle}用于对应用中Activity 和 fragment的生命周期的追踪和回调
     */
    @Inject
    protected ActivityLifecycle mActivityLifecycle;
    /**
     * {@link IConfigModule} 框架公用的一些配置信息
     */
    private final List<IConfigModule> mModules;
    /**
     * {@link Lifecycle} 是一个持有组件（比如 activity 或者 fragment）生命周期状态信息的类，并且允许其它对象观察这个状态。
     */
    private List<Lifecycle> mAppLifecycles = new ArrayList<> ();
    private List<Application.ActivityLifecycleCallbacks> mActivityLifecycles = new ArrayList<> ();
    /**
     * {@link ComponentCallbacks2}主要作用就是在内存状态变化的时候通知应用中的组件，让应用对其所占用的资源进行适当的释放，来降低被系统杀死的概率
     */
    private ComponentCallbacks2 mComponentCallback;

    public AppDelegate(Application application) {
        this.mApplication = application;
        this.mModules = new ManifestParser (mApplication).parse ();
        for (IConfigModule module : mModules) {
            module.injectAppLifecycle (mApplication, mAppLifecycles);
            module.injectActivityLifecycle (mApplication, mActivityLifecycles);
        }
    }

    @Override
    public IAppComponent getAppComponent() {
        return mAppComponent;
    }

    public void onCreate() {
        mAppComponent = DaggerIAppComponent
                .builder ()
                .appModule (new AppModule (mApplication))//提供application
                .clientModule (new ClientModule ())//用于提供okhttp和retrofit的单例
                .globalConfigModule (getGlobalConfigModule (mApplication, mModules))//全局配置
                .build ();
        mAppComponent.inject (this);

        mAppComponent.extras ().put (IConfigModule.class.getName (), mModules);

        mApplication.registerActivityLifecycleCallbacks (mActivityLifecycle);

        for (Application.ActivityLifecycleCallbacks lifecycle : mActivityLifecycles) {
            //activity从创建到销毁的生命周期的回调方法
            mApplication.registerActivityLifecycleCallbacks (lifecycle);
        }

        for (Lifecycle lifecycle : mAppLifecycles) {
            lifecycle.onCreate (mApplication);
        }

        mComponentCallback = new AppComponentCallbacks (mApplication, mAppComponent);
        //注册检测应用是否被切换到了后台/前台（进程管理）
        mApplication.registerComponentCallbacks (mComponentCallback);

    }

    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明{@link IConfigModule}的实现类,和Glide的配置方式相似
     *
     * @return
     */
    private GlobalConfigModule getGlobalConfigModule(Application context, List<IConfigModule> modules) {
        GlobalConfigModule.Builder builder = GlobalConfigModule.builder ();
        for (IConfigModule module : modules) {
            module.applyOptions (context, builder);
        }
        return builder.build ();
    }

    /**
     * 清空配置数据
     */
    public void onTerminate() {
        if (mActivityLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks (mActivityLifecycle);
        }
        //注销内存变化的监听
        if (mComponentCallback != null) {
            mApplication.unregisterComponentCallbacks (mComponentCallback);
        }
        if (mActivityLifecycles != null && mActivityLifecycles.size () > 0) {
            for (Application.ActivityLifecycleCallbacks lifecycle : mActivityLifecycles) {
                mApplication.unregisterActivityLifecycleCallbacks (lifecycle);
            }
        }
        if (mAppLifecycles != null && mAppLifecycles.size () > 0) {
            for (Lifecycle lifecycle : mAppLifecycles) {
                lifecycle.onTerminate (mApplication);
            }
        }
        this.mAppComponent = null;
        this.mActivityLifecycle = null;
        this.mActivityLifecycles = null;
        this.mComponentCallback = null;
        this.mAppLifecycles = null;
        this.mApplication = null;
    }

    /**
     * app生命周期管理
     */
    public interface Lifecycle {
        /**
         * 创建
         *
         * @param application
         */
        void onCreate(Application application);

        /**
         * 内存泄漏处理
         *
         * @param application
         */
        void onTerminate(Application application);
    }

    /**
     * 应用内存变化的监听（内存回收管理）
     * 响应onTrimMemory回调：开发者的app会直接受益，有利于用户体验，系统更有可能让app存活的更持久。
     * 不响应onTrimMemory回调：系统更有可能kill 进程
     */
    private static class AppComponentCallbacks implements ComponentCallbacks2 {
        private Application mApplication;
        private IAppComponent mAppComponent;

        public AppComponentCallbacks(Application application, IAppComponent appComponent) {
            this.mApplication = application;
            this.mAppComponent = appComponent;
        }

        /**
         * @param level 这个值代表着当前系统可用内存的状态
         */
        @Override
        public void onTrimMemory(int level) {
            if (level == TRIM_MEMORY_RUNNING_MODERATE) {
                //设备开始运行缓慢，当前app正在运行，不会被kill
            } else if (level == TRIM_MEMORY_RUNNING_LOW) {
                //设备运行更缓慢了，当前app正在运行，不会被kill。但是请回收unused资源，以便提升系统的性能。
            } else if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
                //设备运行特别慢，当前app还不会被杀死，但是如果此app没有释放资源，系统将会kill后台进程
            } else if (level == TRIM_MEMORY_UI_HIDDEN) {
                //当前应用的UI已不再可见(进入后台)，可以回收大个资源
            } else if (level == TRIM_MEMORY_BACKGROUND) {
                //系统运行慢，并且进程位于LRU list的上端。尽管app不处于高风险被kill。当前app应该释放那些容易恢复的资源
            } else if (level == TRIM_MEMORY_MODERATE) {
                //系统运行缓慢，当前进程已经位于LRU list的中部，如果系统进一步变慢，便会有被kill的可能
            } else if (level == TRIM_MEMORY_COMPLETE) {
                //系统运行慢，当前进程是第一批将被系统kill的进程。此app应该释放一切可以释放的资源。低于api 14的，用户可以使用onLowMemory回调。
            }

        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {
            //内存不足时清理图片请求框架的内存缓存
            mAppComponent.imageLoader ().clear (mApplication, GlideImageConfig
                    .builder ()
                    .isClearMemory (true)
                    .build ());
        }
    }

}
