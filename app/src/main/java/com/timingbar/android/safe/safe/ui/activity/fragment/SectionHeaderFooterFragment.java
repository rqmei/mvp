package com.timingbar.android.safe.safe.ui.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.ui.section.HeaderFooterSection;
import com.timingbar.safe.library.base.BaseFragment;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.view.recyclerview.section.SectionedRecyclerViewAdapter;

/**
 * SectionHeaderFootrFragment
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/9
 */

public class SectionHeaderFooterFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Context mContext;
    SectionedRecyclerViewAdapter sectionAdapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity ();
        return inflater.inflate (R.layout.recycler_view, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        sectionAdapter = new SectionedRecyclerViewAdapter ();
        sectionAdapter.addSection (new HeaderFooterSection (mContext, 0));
        sectionAdapter.addSection (new HeaderFooterSection (mContext, 1));
        sectionAdapter.addSection (new HeaderFooterSection (mContext, 2));
        recyclerView.setLayoutManager (new LinearLayoutManager (mContext));
        recyclerView.setAdapter (sectionAdapter);
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

}