package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.modle.entity.User;
import com.timingbar.android.safe.safe.ui.adapter.SingleAdapter;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.view.recyclerview.wrapper.LoadMoreWrapper;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * PullToLoadeMoreActivity
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 上下拉刷新
 *
 * @author rqmei on 2018/3/7
 */

public class PullToLoadeMoreActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    LoadMoreWrapper mLoadMoreWrapper;
    List<User> users = new ArrayList<> ();

    @Override
    public int getLayoutResId() {
        return R.layout.pull_to_loade_more;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        for (int i = 0; i < 10; i++) {
            User user = new User ("张三------->" + i);
            users.add (user);
        }
        SingleAdapter adapter = new SingleAdapter (this, appComponent.imageLoader (), users);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        mLoadMoreWrapper = new LoadMoreWrapper (adapter);
        mLoadMoreWrapper.setLoadMoreView (R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadListener (new LoadMoreWrapper.OnLoadListener () {
            @Override
            public void onRetry() {
                Timber.i ("LoadeMoreActivity onRetry------>");
                User user = new User ("李四-->loadMore");
                users.add (user);
                mLoadMoreWrapper.showLoadComplete ();
            }

            @Override
            public void onLoadMore() {
                mLoadMoreWrapper.showLoadMore ();
                User user = new User ("李四-->loadMore");
                users.add (user);
                mLoadMoreWrapper.notifyDataSetChanged ();
            }
        });
        recyclerView.setAdapter (mLoadMoreWrapper);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipeRefresh.setDistanceToTriggerSync (300);
        swipeRefresh.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh() {
                mLoadMoreWrapper.setShowFootView (false);
                swipeRefresh.setRefreshing (false);
                users.clear ();
                for (int i = 0; i < 8; i++) {
                    User user = new User ("王五-->onRefresh" + i);
                    users.add (user);
                }
                mLoadMoreWrapper.notifyDataSetChanged ();
            }
        });
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }
}
