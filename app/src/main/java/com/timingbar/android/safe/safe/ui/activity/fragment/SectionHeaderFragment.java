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
import com.timingbar.android.safe.safe.ui.section.HeaderSection;
import com.timingbar.safe.library.base.BaseFragment;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.view.recyclerview.section.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Example1Fragment
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/8
 */

public class SectionHeaderFragment extends BaseFragment {
    SectionedRecyclerViewAdapter sectionAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Context mContext;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity ();
        return inflater.inflate (R.layout.recycler_view, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        sectionAdapter = new SectionedRecyclerViewAdapter ();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            List<String> contacts = getContactsWithLetter (alphabet);
            if (contacts.size () > 0) {
                sectionAdapter.addSection (new HeaderSection (mContext, String.valueOf (alphabet), contacts));
            }
        }
        recyclerView.setLayoutManager (new LinearLayoutManager (getContext ()));
        recyclerView.setAdapter (sectionAdapter);
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

    /**
     * 根据手写字母获取array中的值
     *
     * @param letter
     * @return
     */
    public List<String> getContactsWithLetter(char letter) {
        List<String> contacts = new ArrayList<> ();
        for (String contact : getResources ().getStringArray (R.array.names)) {
            if (contact.charAt (0) == letter) {
                contacts.add (contact);
            }
        }

        return contacts;
    }
}
