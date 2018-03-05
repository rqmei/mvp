package com.timingbar.android.safe.safe.control;

import com.timingbar.safe.library.mvp.IModel;
import com.timingbar.safe.library.mvp.IView;

/**
 * UserControl
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/10
 */

public interface UserControl {
    interface View extends IView {
        void onSuccess();
    }

    interface Modle extends IModel {
    }
}
