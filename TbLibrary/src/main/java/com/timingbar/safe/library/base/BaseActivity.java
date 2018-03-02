package com.timingbar.safe.library.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.timingbar.safe.library.base.delegate.IActivity;
import com.timingbar.safe.library.base.delegate.IApp;
import com.timingbar.safe.library.di.component.IAppComponent;
import com.timingbar.safe.library.mvp.IPresenter;

/**
 * BaseActivity
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/5
 */

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity<P> {
    protected P mPresenter;
    protected Unbinder unbinder;
    protected IAppComponent appComponent;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView (name, context, attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (getLayoutResId ());
        unbinder = ButterKnife.bind (this);
        initView (savedInstanceState);
        initData (savedInstanceState);
        initListener ();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);
        if (mPresenter == null) {
            mPresenter = obtainPresenter ();
        }
        if (appComponent == null) {
            appComponent = ((IApp) this.getApplication ()).getAppComponent ();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = obtainPresenter ();
        }
        if (appComponent == null) {
            appComponent = ((IApp) this.getApplication ()).getAppComponent ();
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void setPresenter(P presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        if (unbinder != null)
            unbinder.unbind ();
    }
}
