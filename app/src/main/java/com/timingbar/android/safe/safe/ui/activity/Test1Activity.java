package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.control.UserControl;
import com.timingbar.android.safe.safe.modle.entity.NewsItem;
import com.timingbar.android.safe.safe.ui.adapter.MultiAdapter;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.mvp.Message;
import com.timingbar.safe.library.view.recyclerview.divider.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * A001Activity
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/27
 */

public class Test1Activity extends BaseActivity implements UserControl.View {

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getLayoutResId() {
        return R.layout.test1;
    }

    List<NewsItem> newsItems = new ArrayList<> ();
    MultiAdapter multiAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        for (int i = 0; i < 12; i++) {
            int type = 1;
            if (i > 5) {
                type = 2;
            }
            NewsItem newsItem = new NewsItem ("新闻标题" + i, "2018030" + (i + 1), type);
            newsItems.add (newsItem);
        }
        multiAdapter = new MultiAdapter (this, appComponent.imageLoader (), newsItems);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        recyclerView.addItemDecoration (new RecycleViewDivider (this, LinearLayout.VERTICAL));
        recyclerView.setAdapter (multiAdapter);
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

    @Override
    public void onSuccess() {
        
    }
}
