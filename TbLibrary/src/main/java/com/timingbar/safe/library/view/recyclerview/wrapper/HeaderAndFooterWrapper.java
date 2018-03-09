package com.timingbar.safe.library.view.recyclerview.wrapper;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.timingbar.safe.library.view.recyclerview.adapter.MultiItemTypeAdapter;
import com.timingbar.safe.library.view.recyclerview.base.ViewHolder;
import com.timingbar.safe.library.view.recyclerview.utils.WrapperUtils;

/**
 * HeaderAndFooterWrapper
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/28
 */

public class HeaderAndFooterWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    //头部View
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<> ();
    //底部View
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<> ();

    private RecyclerView.Adapter mInnerAdapter;
    private RecyclerView.Adapter mNotifyAdapter;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get (viewType) != null) {
            ViewHolder holder = ViewHolder.createViewHolder (mHeaderViews.get (viewType));
            return holder;

        } else if (mFootViews.get (viewType) != null) {
            ViewHolder holder = ViewHolder.createViewHolder (mFootViews.get (viewType));
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder (parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos (position)) {
            return mHeaderViews.keyAt (position);
        } else if (isFooterViewPos (position)) {
            return mFootViews.keyAt (position - getHeadersCount () - getRealItemCount ());
        }
        return mInnerAdapter.getItemViewType (position - getHeadersCount ());
    }

    private int getRealItemCount() {
        return mInnerAdapter.getItemCount ();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos (position)) {
            return;
        }
        if (isFooterViewPos (position)) {
            return;
        }
        mInnerAdapter.onBindViewHolder (holder, position - getHeadersCount ());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount () + getFootersCount () + getRealItemCount ();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mNotifyAdapter = recyclerView.getAdapter ();
        WrapperUtils.onAttachedToRecyclerView (mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback () {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType (position);
                if (mHeaderViews.get (viewType) != null) {
                    return layoutManager.getSpanCount ();
                } else if (mFootViews.get (viewType) != null) {
                    return layoutManager.getSpanCount ();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize (position);
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow (holder);
        int position = holder.getLayoutPosition ();
        if (isHeaderViewPos (position) || isFooterViewPos (position)) {
            WrapperUtils.setFullSpan (holder);
        }
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount ();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount () + getRealItemCount ();
    }


    public void addHeaderView(View view) {
        int key = findHeaderKeyByView (view);
        if (key == -1) {
            mHeaderViews.put (mHeaderViews.size () + BASE_ITEM_TYPE_HEADER, view);
            if (mNotifyAdapter != null)
                mNotifyAdapter.notifyDataSetChanged ();

            if (mInnerAdapter instanceof MultiItemTypeAdapter) {
                ((MultiItemTypeAdapter) mInnerAdapter).offset += 1;
            }
        }
    }

    public void addFootView(View view) {
        mFootViews.put (mFootViews.size () + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return mHeaderViews.size ();
    }

    public int getFootersCount() {
        return mFootViews.size ();
    }

    public void deleteHeaderView(View view) {
        int key = findHeaderKeyByView (view);
        if (key != -1) {
            mHeaderViews.remove (key);
            if (mInnerAdapter instanceof MultiItemTypeAdapter) {
                ((MultiItemTypeAdapter) mInnerAdapter).offset -= 1;
            }
            if (mNotifyAdapter != null)
                mNotifyAdapter.notifyDataSetChanged ();
        }
    }

    public int findHeaderKeyByView(View view) {
        for (int i = 0; i < mHeaderViews.size (); i++) {
            int key = mHeaderViews.keyAt (i);
            if (mHeaderViews.get (key) == view) {
                return key;
            }
        }

        return -1;
    }
}