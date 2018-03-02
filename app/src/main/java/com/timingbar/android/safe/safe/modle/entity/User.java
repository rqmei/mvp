package com.timingbar.android.safe.safe.modle.entity;

import java.io.Serializable;

/**
 * User
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/1
 */

public class User implements Serializable {
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
