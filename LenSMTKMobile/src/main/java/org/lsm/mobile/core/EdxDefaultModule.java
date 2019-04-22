package org.lsm.mobile.core;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import org.lsm.mobile.authentication.LoginService;
import org.lsm.mobile.course.CourseService;
import org.lsm.mobile.discussion.DiscussionService;
import org.lsm.mobile.discussion.DiscussionTextUtils;
import org.lsm.mobile.http.provider.RetrofitProvider;
import org.lsm.mobile.http.util.CallUtil;
import org.lsm.mobile.http.provider.OkHttpClientProvider;
import org.lsm.mobile.http.serialization.ISO8601DateTypeAdapter;
import org.lsm.mobile.http.serialization.JsonPageDeserializer;
import org.lsm.mobile.model.Page;
import org.lsm.mobile.model.course.BlockData;
import org.lsm.mobile.model.course.BlockList;
import org.lsm.mobile.model.course.BlockType;
import org.lsm.mobile.module.db.IDatabase;
import org.lsm.mobile.module.db.impl.IDatabaseImpl;
import org.lsm.mobile.module.download.IDownloadManager;
import org.lsm.mobile.module.download.IDownloadManagerImpl;
import org.lsm.mobile.module.notification.DummyNotificationDelegate;
import org.lsm.mobile.module.notification.NotificationDelegate;
import org.lsm.mobile.module.storage.IStorage;
import org.lsm.mobile.module.storage.Storage;
import org.lsm.mobile.user.UserService;
import org.lsm.mobile.util.AppStoreUtils;
import org.lsm.mobile.util.BrowserUtil;
import org.lsm.mobile.util.Config;
import org.lsm.mobile.util.MediaConsentUtils;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class EdxDefaultModule extends AbstractModule {
    //if your module requires a context, add a constructor that will be passed a context.
    private Context context;

    //with RoboGuice 3.0, the constructor for AbstractModule will use an `Application`, not a `Context`
    public EdxDefaultModule(Context context) {
        this.context = context;
    }

    @Override
    public void configure() {
        Config config = new Config(context);

        bind(IDatabase.class).to(IDatabaseImpl.class);
        bind(IDownloadManager.class).to(IDownloadManagerImpl.class);

        bind(NotificationDelegate.class).to(DummyNotificationDelegate.class);

        bind(IEdxEnvironment.class).to(EdxEnvironment.class);

        bind(LinearLayoutManager.class).toProvider(LinearLayoutManagerProvider.class);

        bind(EventBus.class).toInstance(EventBus.getDefault());

        bind(Gson.class).toInstance(new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(ISO8601DateTypeAdapter.FACTORY)
                .registerTypeAdapter(Page.class, new JsonPageDeserializer())
                .registerTypeAdapter(BlockData.class, new BlockData.Deserializer())
                .registerTypeAdapter(BlockType.class, new BlockType.Deserializer())
                .registerTypeAdapter(BlockList.class, new BlockList.Deserializer())
                .serializeNulls()
                .create());

        bind(OkHttpClientProvider.class).to(OkHttpClientProvider.Impl.class);
        bind(RetrofitProvider.class).to(RetrofitProvider.Impl.class);
        bind(OkHttpClient.class).toProvider(OkHttpClientProvider.Impl.class).in(Singleton.class);
        bind(Retrofit.class).toProvider(RetrofitProvider.Impl.class).in(Singleton.class);

        bind(LoginService.class).toProvider(LoginService.Provider.class).in(Singleton.class);
        bind(CourseService.class).toProvider(CourseService.Provider.class).in(Singleton.class);
        bind(DiscussionService.class).toProvider(DiscussionService.Provider.class).in(Singleton.class);
        bind(UserService.class).toProvider(UserService.Provider.class).in(Singleton.class);

        bind(IStorage.class).to(Storage.class);

        requestStaticInjection(CallUtil.class, BrowserUtil.class, MediaConsentUtils.class,
                DiscussionTextUtils.class, AppStoreUtils.class);
    }
}
