package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.OnClick;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.control.UserControl;
import com.timingbar.android.safe.safe.presenter.CommonPresenter;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.base.delegate.IApp;
import com.timingbar.safe.library.mvp.Message;
import com.timingbar.safe.library.util.DataHelper;
import com.timingbar.safe.library.view.imageloader.ImageLoader;
import com.timingbar.safe.library.view.imageloader.glide.GlideImageConfig;
import timber.log.Timber;


public class MainActivity extends BaseActivity<CommonPresenter> implements UserControl.View {
    @BindView(R.id.main)
    LinearLayout main;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.bg1)
    RelativeLayout bg1;
    @BindView(R.id.bg2)
    LinearLayout bg2;
    @BindView(R.id.bg3)
    Button bg3;
    @BindView(R.id.bg4)
    ImageView bg4;
    @BindView(R.id.bg5)
    EditText bg5;
    @BindView(R.id.tv_show_msg)
    TextView tvShowMsg;
    ImageLoader imageLoader;

    @Override
    public int getLayoutResId() {
        return R.layout.main;
    }

    @Override
    public CommonPresenter obtainPresenter() {
        return new CommonPresenter (appComponent);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        imageLoader = appComponent.imageLoader ();
        imageLoader.loadImage (this, GlideImageConfig.builder ().url ("http://pic14.nipic.com/20110607/6776092_111031284000_2.jpg").bgView (main).transformationType (3).placeholder (R.mipmap.ic_launcher).build ());
        imageLoader.loadImage (this, GlideImageConfig.builder ().url (DataHelper.getAssetsImg ("image/1.jpg")).imageView (image).build ());
        imageLoader.loadImage (this, GlideImageConfig.builder ().imageView (bg4).placeholder (R.mipmap.ic_launcher).transformationType (1).url ("http://ww1.sinaimg.cn/mw600/6345d84ejw1dvxp9dioykg.gif").build ());
        if (mPresenter != null) {
            mPresenter.getVersionCode (this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        imageLoader.clear (this, GlideImageConfig.builder ().imageView (image).bgView (main).isClearDiskCache (true).isClearMemory (true).build ());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        // bg4.setVisibility (View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Timber.i ("main showMessage=" + message);
        tvShowMsg.setText (message);
    }

    @Override
    public void handleMessage(Message message) {
        Timber.i ("main handleMessage=" + message.obj);
    }

    @OnClick({R.id.bg4, R.id.image, R.id.tv_show_msg, R.id.bg3})
    public void onViewClicked(View v) {
        switch (v.getId ()) {
            case R.id.bg4://
                appComponent.appManager ().startActivity (Test1Activity.class);
                break;
            case R.id.image:
                appComponent.appManager ().startActivity (LoadeMoreActivity.class);
                break;
            case R.id.tv_show_msg:
                appComponent.appManager ().startActivity (PullToLoadeMoreActivity.class);
                break;
            case R.id.bg3:
                appComponent.appManager ().startActivity (SectionActivity.class);
                break;
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText (this, "main onSuccess......", Toast.LENGTH_SHORT).show ();
    }
}
