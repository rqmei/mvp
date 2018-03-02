package com.timingbar.android.safe.safe.modle;

import com.timingbar.android.safe.app.utils.BaseJson;
import com.timingbar.android.safe.safe.control.UserControl;

import com.timingbar.android.safe.safe.modle.api.service.CommonService;
import com.timingbar.android.safe.safe.modle.entity.VersionCode;
import com.timingbar.safe.library.mvp.IRepositoryManager;
import io.reactivex.Observable;


/**
 * CommonRepository 仓库
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 必须实现IModel
 * 可以根据不同的业务逻辑划分多个Repository类,多个业务逻辑相近的页面可以使用同一个Repository类
 * 无需每个页面都创建一个独立的Repository
 * 通过{@link com.timingbar.safe.library.mvp.IRepositoryManager#createRepository(Class)}获得的Repository实例,为单例对象
 *
 * @author rqmei on 2018/2/5
 */

public class CommonRepository implements UserControl.Modle {
    IRepositoryManager manager;

    @Override
    public void onDestory() {

    }

    /**
     * 必须含有一个接收IRepositoryManager接口的构造函数,否则会报错
     *
     * @param manager
     */
    public CommonRepository(IRepositoryManager manager) {
        this.manager = manager;
    }

    /**
     * 获取服务器的版本号
     *
     * @return
     */
    public Observable<BaseJson<VersionCode>> getVersionCode() {
        Observable<BaseJson<VersionCode>> refreshRecord = manager.createRetrofitService (CommonService.class).getVersionCode ("1");
        return refreshRecord;
    }
}
