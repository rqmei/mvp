package com.timingbar.safe.library.http;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * OkHttpStreamFetcher
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 从指定的数据源加载数据并转换为指定的类型
 *
 * @author rqmei on 2018/1/29
 */

public class GlideOkHttpStreamFetcher implements DataFetcher<InputStream> {
    private final Call.Factory client;
    private final GlideUrl url;
    private InputStream stream;
    private ResponseBody responseBody;
    private volatile Call call;

    public GlideOkHttpStreamFetcher(Call.Factory client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    /**
     * 用来从指定的数据源中进行数据的加载。需要注意的是：
     * 1. 如果指定的数据能够从缓存中找到，则没有必要使用这个方法进行数据加载
     * 2. 这个方法往往需要在后台线程中实现，因此实现类需要考虑该方法阻塞的情况，避免因为该方法阻塞导致anr等问题
     */
    @Override
    public InputStream loadData(Priority priority) throws Exception {
        Request.Builder requestBuilder = new Request.Builder ().url (url.toStringUrl ());

        for (Map.Entry<String, String> headerEntry : url.getHeaders ().entrySet ()) {
            String key = headerEntry.getKey ();
            requestBuilder.addHeader (key, headerEntry.getValue ());
        }
        Request request = requestBuilder.build ();

        Response response;
        call = client.newCall (request);
        response = call.execute ();
        responseBody = response.body ();
        if (!response.isSuccessful ()) {
            throw new IOException ("Request failed with code: " + response.code ());
        }

        long contentLength = responseBody.contentLength ();
        stream = ContentLengthInputStream.obtain (responseBody.byteStream (), contentLength);
        return stream;
    }

    /**
     * 清理加载并转换的resource资源
     */
    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close ();
            }
        } catch (IOException e) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody.close ();
        }
    }

    @Override
    public String getId() {
        return url.getCacheKey ();
    }

    /**
     * 用来取消不需要的数据加载请求，最好在数据加载之前就调用该方法，当然也可以在数据加载完成或者正在进行加载的时候调用，调用该方法并一定会马上结束
     * 正在进行加载的动作。该方法一般需要在主线程调用，因此不能有阻塞操作。
     */

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel ();
        }
    }
}
