package com.timingbar.android.safe.safe.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.BindView;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.ui.activity.fragment.SectionHeaderFootrFragment;
import com.timingbar.android.safe.safe.ui.activity.fragment.SectionHeaderFragment;
import com.timingbar.safe.library.base.BaseActivity;
import com.timingbar.safe.library.mvp.IPresenter;

/**
 * SectionActivity
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/8
 */

public class SectionActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    public int getLayoutResId() {
        return R.layout.section;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView (savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener (toggle);
        toggle.syncState ();

        navView.setNavigationItemSelectedListener (this);

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public IPresenter obtainPresenter() {
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();
        switch (id) {
            case R.id.nav_section_header:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_section_header_footer:
                replaceFragment (new SectionHeaderFootrFragment ());
                break;
            case R.id.nav_example3:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_example4:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_example5:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_example6:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_example7:
                replaceFragment (new SectionHeaderFragment ());
                break;
            case R.id.nav_example8:
                replaceFragment (new SectionHeaderFragment ());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        drawer.closeDrawer (GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();

        transaction.replace (R.id.fragment_container, fragment);

        transaction.commit ();
    }
}
