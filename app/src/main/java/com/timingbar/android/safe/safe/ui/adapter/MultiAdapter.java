package com.timingbar.android.safe.safe.ui.adapter;

import android.content.Context;
import com.timingbar.android.safe.safe.modle.entity.User;
import com.timingbar.safe.library.view.recyclerview.adapter.MultiItemTypeAdapter;

import java.util.List;

/**
 * MultiAdapter
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/1
 */

public class MultiAdapter extends MultiItemTypeAdapter<User> {
    public MultiAdapter(Context context, List<User> datas) {
        super (context, datas);
    }
}
