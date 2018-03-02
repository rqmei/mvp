package com.timingbar.android.safe.safe.presenter;

import com.timingbar.android.safe.app.utils.BaseJson;
import com.timingbar.android.safe.safe.modle.CommonRepository;
import com.timingbar.android.safe.safe.modle.entity.VersionCode;
import com.timingbar.safe.library.di.component.IAppComponent;
import com.timingbar.safe.library.mvp.BasePresenter;
import com.timingbar.safe.library.mvp.Message;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import timber.log.Timber;

/**
 * CommonPresenter
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 存放通用的网络请求相关的业务逻辑处理
 *
 * @author rqmei on 2018/2/5
 */

public class CommonPresenter extends BasePresenter<CommonRepository> {
    private RxErrorHandler mErrorHandler;
    IAppComponent iAppComponent;

    public CommonPresenter(IAppComponent appComponent) {
        super (appComponent.repositoryManager ().createRepository (CommonRepository.class));
        this.mErrorHandler = appComponent.rxErrorHandler ();
        this.iAppComponent = appComponent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        this.mErrorHandler = null;
    }

    /**
     * 获取服务器版本号
     *
     * @param msg
     */
    public void getVersionCode(final Message msg) {
        mModel.getVersionCode ()
                .subscribeOn (Schedulers.io ())
                .retryWhen (new RetryWithDelay (3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe (disposable -> {
                    //在订阅时必须调用这个方法,不然Activity退出时可能内存泄漏
                    addDispose (disposable);
                    //发送请求，显示加载框
                    msg.getTarget ().showLoading ();
                }).subscribeOn (AndroidSchedulers.mainThread ())
                .observeOn (AndroidSchedulers.mainThread ())
                .doAfterTerminate (() -> {
                    //请求完成隐藏加载框
                    msg.getTarget ().hideLoading ();
                }).subscribe (new ErrorHandleSubscriber<BaseJson<VersionCode>> (mErrorHandler) {
            @Override
            public void onNext(@NonNull BaseJson<VersionCode> loginResultInfo) {
                Timber.i ("进入onNext...." + loginResultInfo.getData ().getName ());
                if (loginResultInfo.isSuccess ()) {
                    //服务器返回标识成功
                    msg.what = 2;
                    msg.obj = loginResultInfo.getData ().toString ();
                    msg.HandleMessageToTargetUnrecycle ();
                } else {
                    //服务器返回标识失败
                    msg.what = 3;
                    msg.HandleMessageToTargetUnrecycle ();
                }
            }

        });
    }

}
