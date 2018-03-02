package com.timingbar.safe.library.integration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.timingbar.safe.library.base.delegate.*;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ActivityLifecycle
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 用于对应用中Activity 和 fragment的生命周期的追踪和回调
 * 使用ActivityLifecycleCallbacks可实现:
 * 1 判断App是否在后台运行
 * 2 关闭该应用所有Activity
 *
 * @author rqmei on 2018/1/29
 */

public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private AppManager mAppManager;//activity对象管理类
    private Application mApplication;
    private Map<String, Object> mExtras;
    private FragmentLifecycle mFragmentLifecycle;//fragment的生命周期
    private List<FragmentManager.FragmentLifecycleCallbacks> mFragmentLifecycles;

    @Inject
    public ActivityLifecycle(AppManager appManager, Application application, Map<String, Object> extras) {
        this.mAppManager = appManager;
        this.mApplication = application;
        this.mExtras = extras;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w (activity + "--onActivityCreated");
        //如果intent包含了此字段,并且为true说明不加入到list
        // 默认为false,如果不需要管理(比如不需要在退出所有activity(killAll)时，退出此activity就在intent加此字段为true)
        boolean isNotAdd = false;
        if (activity.getIntent () != null)
            isNotAdd = activity.getIntent ().getBooleanExtra (AppManager.IS_NOT_ADD_ACTIVITY_LIST, false);

        if (!isNotAdd)
            mAppManager.addActivity (activity);

        //配置ActivityDelegate
        if (activity instanceof IActivity && activity.getIntent () != null) {
            IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
            if (activityDelegate == null) {
                activityDelegate = new ActivityDelegateImpl (activity);
                activity.getIntent ().putExtra (IActivityDelegate.ACTIVITY_DELEGATE, activityDelegate);
            }
            activityDelegate.onCreate (savedInstanceState);
        }

        /**
         * 给每个Activity配置Fragment的监听,Activity可以通过 {@link IActivity#useFragment()} 设置是否使用监听
         * 如果这个Activity返回false的话,这个Activity将不能使用{@link FragmentDelegate},意味着 {@link com.jess.arms.base.BaseFragment}也不能使用
         */
        boolean useFragment = activity instanceof IActivity ? ((IActivity) activity).useFragment () : true;
        if (activity instanceof FragmentActivity && useFragment) {
            if (mFragmentLifecycle == null) {
                mFragmentLifecycle = new FragmentLifecycle ();
            }

            ((FragmentActivity) activity).getSupportFragmentManager ().registerFragmentLifecycleCallbacks (mFragmentLifecycle, true);
            if (mFragmentLifecycles == null) {
                mFragmentLifecycles = new ArrayList<> ();
                List<IConfigModule> modules = (List<IConfigModule>) mExtras.get (IConfigModule.class.getName ());
                for (IConfigModule module : modules) {
                    module.injectFragmentLifecycle (mApplication, mFragmentLifecycles);
                }
            }

            for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles) {
                ((FragmentActivity) activity).getSupportFragmentManager ().registerFragmentLifecycleCallbacks (fragmentLifecycle, true);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.w (activity + "--onActivityStarted");
        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onStart ();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.w (activity + "--onActivityResumed");
        mAppManager.setCurrentActivity (activity);
        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onResume ();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.w (activity + "--onActivityPaused");
        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onPause ();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.w (activity + "--onActivityStopped");
        if (mAppManager.getCurrentActivity () == activity) {
            mAppManager.setCurrentActivity (null);
        }
        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onStop ();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.w (activity + "--onActivitySaveInstanceState");
        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onSaveInstanceState (outState);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.w (activity + "--onActivityDestroyed");
        mAppManager.removeActivity (activity);
        boolean useFragment = activity instanceof IActivity ? ((IActivity) activity).useFragment () : true;
        if (activity instanceof FragmentActivity && useFragment) {
            if (mFragmentLifecycle != null) {
                ((FragmentActivity) activity).getSupportFragmentManager ().unregisterFragmentLifecycleCallbacks (mFragmentLifecycle);
            }
            if (mFragmentLifecycles != null && mFragmentLifecycles.size () > 0) {
                for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles) {
                    ((FragmentActivity) activity).getSupportFragmentManager ().unregisterFragmentLifecycleCallbacks (fragmentLifecycle);
                }
            }
        }

        IActivityDelegate activityDelegate = fetchActivityDelegate (activity);
        if (activityDelegate != null) {
            activityDelegate.onDestroy ();
            activity.getIntent ().removeExtra (IActivityDelegate.ACTIVITY_DELEGATE);
        }
    }

    private IActivityDelegate fetchActivityDelegate(Activity activity) {
        IActivityDelegate activityDelegate = null;
        if (activity instanceof IActivity && activity.getIntent () != null) {
            activityDelegate = activity.getIntent ().getParcelableExtra (IActivityDelegate.ACTIVITY_DELEGATE);
        }
        return activityDelegate;
    }

    static class FragmentLifecycle extends FragmentManager.FragmentLifecycleCallbacks {
        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached (fm, f, context);
            Timber.w (f.toString () + " - onFragmentAttached");
            if (f instanceof IFragment && f.getArguments () != null) {
                IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
                if (fragmentDelegate == null || !fragmentDelegate.isAdded ()) {
                    fragmentDelegate = new FragmentDelegateImpl (fm, f);
                    f.getArguments ().putParcelable (IFragmentDelegate.FRAGMENT_DELEGATE, fragmentDelegate);
                }
                fragmentDelegate.onAttach (context);
            }
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated (fm, f, savedInstanceState);
            Timber.w (f.toString () + " - onFragmentCreated");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onCreate (savedInstanceState);
            }
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated (fm, f, v, savedInstanceState);
            Timber.w (f.toString () + " - onFragmentViewCreated");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onCreateView (v, savedInstanceState);
            }
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentActivityCreated (fm, f, savedInstanceState);
            Timber.w (f.toString () + " - onFragmentActivityCreated");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onActivityCreate (savedInstanceState);
            }
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted (fm, f);
            Timber.w (f.toString () + " - onFragmentStarted");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onStart ();
            }
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            super.onFragmentResumed (fm, f);
            Timber.w (f.toString () + " - onFragmentResumed");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onResume ();
            }
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            super.onFragmentPaused (fm, f);
            Timber.w (f.toString () + " - onFragmentPaused");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onPause ();
            }
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped (fm, f);
            Timber.w (f.toString () + " - onFragmentStopped");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onStop ();
            }
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentViewDestroyed (fm, f);
            Timber.w (f.toString () + " - onFragmentViewDestroyed");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onDestroyView ();
            }
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentDestroyed (fm, f);
            Timber.w (f.toString () + " - onFragmentDestroyed");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onDestroy ();
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached (fm, f);
            Timber.w (f.toString () + " - onFragmentDetached");
            IFragmentDelegate fragmentDelegate = fetchFragmentDelegate (f);
            if (fragmentDelegate != null) {
                fragmentDelegate.onDetach ();
                f.getArguments ().clear ();
            }
        }

        private IFragmentDelegate fetchFragmentDelegate(Fragment fragment) {
            if (fragment instanceof IFragment) {
                return fragment.getArguments () == null ? null : fragment.getArguments ().getParcelable (IFragmentDelegate.FRAGMENT_DELEGATE);
            }
            return null;
        }
    }

}
