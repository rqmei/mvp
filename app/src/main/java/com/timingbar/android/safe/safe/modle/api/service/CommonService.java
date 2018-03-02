package com.timingbar.android.safe.safe.modle.api.service;

import com.timingbar.android.safe.app.ApiConfig;
import com.timingbar.android.safe.app.utils.BaseJson;
import com.timingbar.android.safe.safe.modle.entity.VersionCode;
import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * CommonService
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 存放通用的一些API
 *
 * @author rqmei on 2018/2/5
 */

public interface CommonService {
    /**
     * 获取最新学时记录
     *
     * @param type 终端类型（1：android）
     * @return
     */
    @POST(ApiConfig.GET_VERSION_CODE)
    Observable<BaseJson<VersionCode>> getVersionCode(@Query("type") String type);
}
