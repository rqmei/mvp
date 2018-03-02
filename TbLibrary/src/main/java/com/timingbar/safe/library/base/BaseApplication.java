package com.timingbar.safe.library.base;

import android.app.Application;
import android.content.Context;
import com.timingbar.safe.library.base.delegate.AppDelegate;
import com.timingbar.safe.library.base.delegate.IApp;
import com.timingbar.safe.library.di.component.IAppComponent;

/**
 * BaseApplication
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 基类application
 *
 * @author rqmei on 2018/1/29
 */

public class BaseApplication extends Application implements IApp {
    private AppDelegate mAppDelegate;

    @Override
    public void onCreate() {
        super.onCreate ();
        this.mAppDelegate = new AppDelegate (this);
        this.mAppDelegate.onCreate ();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext (base);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate ();
        if (mAppDelegate != null)
            this.mAppDelegate.onTerminate ();
    }

    /**
     * 将IAppComponent返回出去,供其它地方使用, IAppComponent接口中声明的方法返回的实例,在getAppComponent()拿到对象后都可以直接使用
     *
     * @return IAppComponent实例对象
     */
    @Override
    public IAppComponent getAppComponent() {
        return mAppDelegate.getAppComponent ();
    }

}
