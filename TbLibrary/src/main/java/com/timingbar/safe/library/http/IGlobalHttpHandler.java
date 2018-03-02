package com.timingbar.safe.library.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * GlobalHttpHandler 网络请求
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 连接服务器的方式相关的公开接口定义
 *
 * @author rqmei on 2018/1/29
 */

public interface IGlobalHttpHandler {
    /**
     * 提前获取服务器返回的结果，并做自己想要的逻辑处理
     *
     * @param httpResult
     * @param chain
     * @param response
     * @return
     */
    Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response);

    /**
     * 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
     *
     * @param chain
     * @param request
     * @return
     */
    Request onHttpRequestBefore(Interceptor.Chain chain, Request request);

    IGlobalHttpHandler EMPTY = new IGlobalHttpHandler () {
        @Override
        public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
            return response;
        }

        @Override
        public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
            return request;
        }
    };
}
