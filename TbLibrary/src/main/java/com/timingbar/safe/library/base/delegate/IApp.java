package com.timingbar.safe.library.base.delegate;

import com.timingbar.safe.library.di.component.IAppComponent;

/**
 * App
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 获取底层通用的连接器AppComponent对象
 * app通用的一些方法
 *
 * @author rqmei on 2018/1/29
 */

public interface IApp {
    IAppComponent getAppComponent();
}
