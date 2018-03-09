package com.timingbar.android.safe.safe.ui.activity.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.timingbar.android.safe.R;
import com.timingbar.safe.library.base.BaseFragment;
import com.timingbar.safe.library.mvp.IPresenter;

/**
 * SectionHeaderFootrFragment
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/9
 */

public class SectionHeaderFootrFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate (R.layout.recycler_view, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

}