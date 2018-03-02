package com.timingbar.safe.library.view.recyclerview.base;

/**
 * ItemViewDelegate
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/28
 */

public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}