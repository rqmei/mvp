package com.timingbar.safe.library.http;

import android.content.Context;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import java.io.InputStream;

/**
 * OkHttpUrlLoader
 * -----------------------------------------------------------------------------------------------------------------------------------
 * Modelloader和DataFetcher两者结合起来构成了Glide的数据加载核心。当缓存中给定的数据不存在的时候，
 * Glide就会通过指定的Modelloader和DataFetcher进行数据加载，这些数据可能来自文件、网络、byte数组等地方。
 * <p>
 * GlideUrl表示指定的数据源
 * InputStream表示数据加载后需要封装成的类型
 *
 * @author rqmei on 2018/1/29
 */

public class GlideOkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {
    private final Call.Factory client;

    public GlideOkHttpUrlLoader(Call.Factory client) {
        this.client = client;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        return new GlideOkHttpStreamFetcher (client, model);
    }

    /**
     * The default factory for {@link GlideOkHttpUrlLoader}s.
     * ModelLoader工厂，在向Glide注册自定义ModelLoader时使用到
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile Call.Factory internalClient;
        private Call.Factory client;

        /**
         * Constructor for a new Factory that runs requests using a static singleton client.
         */
        public Factory() {
            this (getInternalClient ());
        }

        /**
         * Constructor for a new Factory that runs requests using given client.
         *
         * @param client this is typically an instance of {@code OkHttpClient}.
         */
        public Factory(Call.Factory client) {
            this.client = client;
        }

        private static Call.Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = new OkHttpClient ();
                    }
                }
            }
            return internalClient;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new GlideOkHttpUrlLoader (client);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
