package com.timingbar.safe.library.mvp;

import io.rx_cache2.internal.RxCache;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RepositoryManager
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 管理网络请求相关仓库实例对象的具体实现
 *
 * @author rqmei on 2018/1/29
 */

public class RepositoryManagerImpl implements IRepositoryManager {
    //用于创建代理对象
    private Retrofit mRetrofit;
    //缓存管理
    private RxCache mRxCache;
    //保存已实例化的仓库对象
    private final Map<String, IModel> mRepositoryCache = new LinkedHashMap<> ();
    //保存Retrofit创建的代理对象
    private final Map<String, Object> mRetrofitServiceCache = new LinkedHashMap<> ();
    //保存缓存配置对应的缓存接口的对象集合
    private final Map<String, Object> mCacheServiceCache = new LinkedHashMap<> ();

    @Inject
    public RepositoryManagerImpl(Retrofit retrofit, RxCache rxCache) {
        this.mRetrofit = retrofit;
        this.mRxCache = rxCache;
    }

    /**
     * 根据传入的Class创建对应的仓库
     *
     * @param repository
     * @param <T>
     * @return 网络请求仓库集合
     */
    @Override
    public <T extends IModel> T createRepository(Class<T> repository) {
        T repositoryInstance;
        synchronized (mRepositoryCache) {
            repositoryInstance = (T) mRepositoryCache.get (repository.getName ());
            if (repositoryInstance == null) {
                Constructor<? extends IModel> constructor = findConstructorForClass (repository);
                try {
                    //将构造函数实例化并获取其对象
                    repositoryInstance = (T) constructor.newInstance (this);
                } catch (InstantiationException e) {
                    throw new RuntimeException ("Unable to invoke " + constructor, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException ("Unable to invoke " + constructor, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException ("create repository error", e);
                }
                //将实例化的对象保存到激活中
                mRepositoryCache.put (repository.getName (), repositoryInstance);
            }
        }
        return repositoryInstance;
    }

    /**
     * 根据传入的Class创建对应的Retrofit service
     *
     * @param service 被用于创建代理对象的service
     * @param <T>
     * @return 代理对象集合
     */
    @Override
    public <T> T createRetrofitService(Class<T> service) {
        T retrofitService;
        synchronized (mRetrofitServiceCache) {
            retrofitService = (T) mRetrofitServiceCache.get (service.getName ());
            if (retrofitService == null) {
                //用Retrofit创建一个service的代理对象
                retrofitService = mRetrofit.create (service);
                //将获取的代理对象保存到我们的集合变量中        
                mRetrofitServiceCache.put (service.getName (), retrofitService);
            }
        }
        return retrofitService;
    }

    /**
     * 根据传入的Class创建对应的RxCache service
     *
     * @param cache 缓存配置
     * @param <T>
     * @return 缓存配置对应的缓存接口相关对象集合
     */
    @Override
    public <T> T createCacheService(Class<T> cache) {
        T cacheService;
        synchronized (mCacheServiceCache) {
            cacheService = (T) mCacheServiceCache.get (cache.getName ());
            if (cacheService == null) {
                //缓存配置对应的缓存接口
                cacheService = mRxCache.using (cache);
                mCacheServiceCache.put (cache.getName (), cacheService);
            }
        }
        return cacheService;
    }

    /**
     * 通过反射的方式获取对应类中所有的构造函数
     *
     * @param cls 将被反射的class
     * @return 被反射的class中所有的构造函数
     */
    private static Constructor<? extends IModel> findConstructorForClass(Class<?> cls) {
        Constructor<? extends IModel> bindingCtor;
        String clsName = cls.getName ();
        try {
            //获得所有构造函数
            bindingCtor = (Constructor<? extends IModel>) cls.getConstructor (IRepositoryManager.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException ("Unable to find constructor for " + clsName, e);
        }

        return bindingCtor;
    }
}
