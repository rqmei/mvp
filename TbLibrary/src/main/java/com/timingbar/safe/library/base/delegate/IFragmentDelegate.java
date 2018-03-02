package com.timingbar.safe.library.base.delegate;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

/**
 * IFragmentDelegate
 * -----------------------------------------------------------------------------------------------------------------------------------
 * Fragment各生命周期方法相关的接口
 *
 * @author rqmei on 2018/1/30
 */

public interface IFragmentDelegate extends Parcelable {
    String FRAGMENT_DELEGATE = "fragment_delegate";

    void onAttach(Context context);

    void onCreate(Bundle savedInstanceState);

    void onCreateView(View view, Bundle savedInstanceState);

    void onActivityCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroyView();

    void onDestroy();

    void onDetach();

    /**
     * Return true if the fragment is currently added to its activity.
     */
    boolean isAdded();
}
