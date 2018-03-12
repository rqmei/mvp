package com.timingbar.android.safe.safe.ui.activity.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import com.timingbar.android.safe.safe.ui.section.HeaderSection;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.view.recyclerview.section.SectionedRecyclerViewAdapter;

import java.util.List;

/**
 * ExpandedFragment
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/12
 */

public class ExpandedFragment extends SectionHeaderFragment {


    @Override
    public void initData(Bundle savedInstanceState) {
        sectionAdapter = new SectionedRecyclerViewAdapter ();
        mContext = getActivity ();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            List<String> contacts = getContactsWithLetter (alphabet);
            if (contacts.size () > 0) {
                sectionAdapter.addSection (new HeaderSection (sectionAdapter, mContext, String.valueOf (alphabet), contacts));
            }
        }
        recyclerView.setLayoutManager (new LinearLayoutManager (mContext));
        recyclerView.setAdapter (sectionAdapter);
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

}
