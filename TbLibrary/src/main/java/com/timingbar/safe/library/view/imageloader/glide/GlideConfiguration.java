package com.timingbar.safe.library.view.imageloader.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.timingbar.safe.library.base.delegate.IApp;
import com.timingbar.safe.library.di.component.IAppComponent;
import com.timingbar.safe.library.http.GlideOkHttpUrlLoader;
import com.timingbar.safe.library.util.DataHelper;

import java.io.File;
import java.io.InputStream;

/**
 * GlideConfiguration
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 重写glide图片加载的相关配置信息
 * <p/>
 * 在AndroidManifest.xml中的<application>标签下定义<meta-data>，这样Glide才能知道我们定义了这么一个类，
 * 其中android:name是我们自定义的GlideModule的完整路径，而android:value就固定写死GlideModule。
 *
 * @author rqmei on 2018/1/25
 */
public class GlideConfiguration implements GlideModule {
    public static final int IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024;//图片缓存文件最大值为100Mb

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //通过builder.setXXX进行配置.
        //设置磁盘缓存，需要实现DiskCache.Factory,默认实现是InternalCacheDiskCacheFactory
        builder.setDiskCache (() -> {
            // Careful: the external cache directory doesn't enforce permissions
            IAppComponent appComponent = ((IApp) context.getApplicationContext ()).getAppComponent ();
            return DiskLruCacheWrapper.get (DataHelper.makeDirs (new File (appComponent.cacheFile (), "Glide")), IMAGE_DISK_CACHE_MAX_SIZE);
        });

        MemorySizeCalculator calculator = new MemorySizeCalculator (context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize ();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize ();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
        //设置内存缓存的大小
        builder.setMemoryCache (new LruResourceCache (customMemoryCacheSize));
        //设置Bitmap的缓存池，用来重用Bitmap
        builder.setBitmapPool (new LruBitmapPool (customBitmapPoolSize));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //通过glide.register进行配置.
        //Glide默认使用HttpURLConnection做网络请求,在这切换成okhttp请求
        IAppComponent appComponent = ((IApp) context.getApplicationContext ()).getAppComponent ();
        glide.register (GlideUrl.class, InputStream.class, new GlideOkHttpUrlLoader.Factory (appComponent.okHttpClient ()));
    }
}
