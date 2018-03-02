package com.timingbar.safe.library.util;

import android.os.Message;
import org.greenrobot.eventbus.EventBus;

import static com.timingbar.safe.library.integration.AppManager.APP_EXIT;
import static com.timingbar.safe.library.integration.AppManager.KILL_ALL;

/**
 * UIUtil
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/2/28
 */

public class UiUtil {
    /**
     * 关闭所有activity
     */
    public static void killAll() {
        Message message = new Message ();
        message.what = KILL_ALL;
        EventBus.getDefault ().post (message);
    }

    /**
     * 退出app
     */
    public static void exitApp() {
        Message message = new Message ();
        message.what = APP_EXIT;
        EventBus.getDefault ().post (message);
    }
}
