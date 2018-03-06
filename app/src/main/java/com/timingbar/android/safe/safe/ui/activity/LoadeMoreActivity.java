package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
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
 * LoadeMoreActivity
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 分页点击加载更多
 *
 * @author rqmei on 2018/3/6
 */

public class LoadeMoreActivity extends BaseActivity {
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    LoadMoreWrapper mLoadMoreWrapper;
    List<User> users = new ArrayList<> ();

    @Override
    public int getLayoutResId() {
        return R.layout.test1;
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
                if (recyclerView.getScrollState () == RecyclerView.SCROLL_STATE_IDLE) {
                    mLoadMoreWrapper.notifyDataSetChanged ();
                }
            }

            @Override
            public void onLoadMore() {
                //                User user = new User ("李四-->loadMore");
                //                users.add (user);
                //                if (recyclerView.getScrollState () == RecyclerView.SCROLL_STATE_IDLE) {
                //                    mLoadMoreWrapper.notifyDataSetChanged ();
                //                }
                //mLoadMoreWrapper.showLoadComplete ();
                //                mLoadMoreWrapper.showLoadMore ();
                mLoadMoreWrapper.showLoadError ();
            }
        });
        recyclerView.setAdapter (mLoadMoreWrapper);
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

}
