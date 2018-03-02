package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.control.UserControl;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.mvp.Message;
import com.timingbar.safe.library.util.UiUtil;
import org.greenrobot.eventbus.EventBus;

/**
 * A001Activity
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/27
 */

public class Test1Activity extends BaseActivity implements UserControl.View {

    @BindView(R.id.btn1)
    Button btn1;

    @Override
    public int getLayoutResId() {
        return R.layout.test1;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void handleMessage(Message message) {

    }

    @OnClick(R.id.btn1)
    public void onViewClicked(View v) {
        switch (v.getId ()) {
            case R.id.btn1://
                appComponent.appManager ().startActivity (Test2Activity.class);
                finish ();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
    }

    @Override
    public void onBackPressed() {
        finish ();
    }

}
