package com.timingbar.safe.library.integration;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import com.timingbar.safe.library.base.delegate.AppDelegate;
import com.timingbar.safe.library.di.module.GlobalConfigModule;

import java.util.List;

/**
 * ConfigModule
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 用来给框架配置各种自定义属性和功能,需要实现类{@link GlobalConfigModule}实现后,并在AndroidManifest中通过meta-data声明该实现类
 *
 * @author rqmei on 2018/1/29
 */

public interface IConfigModule {
    /**
     * 使用{@link GlobalConfigModule.Builder}给框架配置一些配置参数
     *
     * @param context
     * @param builder
     */
    void applyOptions(Context context, GlobalConfigModule.Builder builder);

    /**
     * 使用{@link AppDelegate.Lifecycle}在Application的生命周期中注入一些操作
     *
     * @return
     */
    void injectAppLifecycle(Context context, List<AppDelegate.Lifecycle> lifecycles);


    /**
     * 使用{@link Application.ActivityLifecycleCallbacks}在Activity的生命周期中注入一些操作
     *
     * @param context
     * @param lifecycles
     */
    void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles);

    /**
     * 使用{@link FragmentManager.FragmentLifecycleCallbacks}在Fragment的生命周期中注入一些操作
     *
     * @param context
     * @param lifecycles
     */
    void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles);
}
