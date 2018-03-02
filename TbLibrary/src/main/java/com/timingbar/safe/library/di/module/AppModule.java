package com.timingbar.safe.library.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.timingbar.safe.library.mvp.IRepositoryManager;
import com.timingbar.safe.library.mvp.RepositoryManagerImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Map;

/**
 * AppModule
 * -----------------------------------------------------------------------------------------------------------------------------------
 * Modules类里面的方法专门提供依赖，所以我们定义一个类，用@Module注解，这样Dagger在构造类的实例的时候，就知道从哪里去找到需要的 依赖。
 * modules的一个重要特征是它们设计为分区并组合在一起（比如说，在我们的app中可以有多个组成在一起的modules)
 * Module其实是一个简单工厂模式，Module里面的方法基本都是创建类实例的方法。
 * 属于Component的实例端的（连接各种目标类依赖实例的端）
 * <p>
 * <p>
 * Module中的创建类实例方法用Provides进行标注，Component在搜索到目标类中用Inject注解标注的属性后，
 * Component就会去Module中去查找用Provides标注的对应的创建类实例方法，这样就可以解决第三方类库用dagger2实现依赖注入了。
 *
 * @author rqmei on 2018/1/29
 */
@Module
public class AppModule {
    private Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Gson provideGson(Application application, @Nullable GsonConfiguration configuration) {
        GsonBuilder builder = new GsonBuilder ();
        if (configuration != null)
            configuration.configGson (application, builder);
        return builder.create ();
    }

    @Singleton
    @Provides
    public IRepositoryManager provideRepositoryManager(RepositoryManagerImpl repositoryManager) {
        return repositoryManager;
    }

    @Singleton
    @Provides
    public Map<String, Object> provideExtras() {
        return new ArrayMap<> ();
    }


    public interface GsonConfiguration {
        void configGson(Context context, GsonBuilder builder);
    }

}
