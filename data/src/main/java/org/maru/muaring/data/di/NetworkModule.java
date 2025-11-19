package org.maru.muaring.data.di;

import org.maru.muaring.data.api.AuthApi;
import org.maru.muaring.data.api.GroupApi;
import org.maru.muaring.data.repository.AuthRepository;
import org.maru.muaring.data.repository.AuthRepositoryImpl;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.data.repository.GroupRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public AuthApi provideAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(AuthApi api) {
        return new AuthRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public GroupApi provideGroupApi(Retrofit retrofit) {
        return retrofit.create(GroupApi.class);
    }

    @Provides
    @Singleton
    public GroupRepository provideGroupRepository(GroupApi api) {
        return new GroupRepositoryImpl(api);
    }
}
