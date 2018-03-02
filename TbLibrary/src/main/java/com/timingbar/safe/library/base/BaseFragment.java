package com.timingbar.safe.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.timingbar.safe.library.base.delegate.IFragment;
import com.timingbar.safe.library.mvp.IPresenter;

/**
 * BaseFragment
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 因为java只能单继承,所以如果有需要继承特定Fragment的三方库,那你就需要自己自定义Fragment
 * 继承于这个特定的Fragment,然后按照将BaseFragment的格式,复制过去,记住一定要实现{@link IFragment}
 *
 * @author rqmei on 2018/1/30
 */

public abstract class BaseFragment<P extends IPresenter> extends Fragment implements IFragment<P> {
    protected P mPresenter;

    public BaseFragment() {
        //必须确保在Fragment实例化时setArguments()
        setArguments (new Bundle ());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView (inflater, container, savedInstanceState);
    }

    @Override
    public void setPresenter(P presenter) {
        this.mPresenter = presenter;
    }

    /**
     * 每次新建Fragment都会发生.
     * 它并不是实例状态恢复的方法, 只是一个View状态恢复的回调.
     * 在onActivityCreated()和onStart()之间调用
     *
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored (savedInstanceState);
        if (mPresenter == null) {
            mPresenter = obtainPresenter ();
        }
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     */
    @Override
    public boolean useEventBus() {
        return true;
    }
}
