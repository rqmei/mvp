package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.modle.entity.User;
import com.timingbar.android.safe.safe.ui.adapter.SingleAdapter;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.mvp.IPresenter;
import com.timingbar.safe.library.view.recyclerview.adapter.MultiItemTypeAdapter;
import com.timingbar.safe.library.view.recyclerview.divider.RecycleViewDivider;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * Test2Activity
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/27
 */

public class Test2Activity extends BaseActivity {
    @BindView(R.id.rv_test1)
    RecyclerView rvTest1;
    SingleAdapter singleAdapter;
    List<User> users = new ArrayList<> ();

    @Override
    public int getLayoutResId() {
        return R.layout.test2;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        for (int i = 0; i < 10; i++) {
            User user = new User ("张三------->" + i);
            users.add (user);
        }
        singleAdapter = new SingleAdapter (this, appComponent.imageLoader (), users);
        //注意 必须设置layoutManager,不设置会导致不显示数据
        rvTest1.setLayoutManager (new LinearLayoutManager (this));
        //设置横向分割线
        rvTest1.addItemDecoration (new RecycleViewDivider (this, LinearLayoutManager.HORIZONTAL));
        Timber.i ("test2Activity----" + users.size () + "," + singleAdapter.getItemCount ());
        rvTest1.setAdapter (singleAdapter);
        singleAdapter.setOnItemClickListener (new MultiItemTypeAdapter.OnItemClickListener () {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                Toast.makeText (Test2Activity.this, "singleAdapter item 单击事件====" + ((User) o).getUserName (), Toast.LENGTH_SHORT).show ();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                Toast.makeText (Test2Activity.this, "singleAdapter item 长按事件====", Toast.LENGTH_SHORT).show ();
                return false;
            }
        });
    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

}
