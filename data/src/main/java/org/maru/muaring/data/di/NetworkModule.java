package org.maru.muaring.data.di;

import org.maru.muaring.data.api.LikeApi;
import org.maru.muaring.data.api.NearbyApi;
import org.maru.muaring.data.api.RecommendationApi;
import org.maru.muaring.data.network.AuthInterceptor;
import org.maru.muaring.data.api.AuthApi;
import org.maru.muaring.data.api.FollowApi;
import org.maru.muaring.data.api.GroupApi;
import org.maru.muaring.data.api.HistoryApi;
import org.maru.muaring.data.api.ImageApi;
import org.maru.muaring.data.api.LocationApi;
import org.maru.muaring.data.api.LibraryApi;
import org.maru.muaring.data.api.MemberApi;
import org.maru.muaring.data.api.PostApi;
import org.maru.muaring.data.api.UploadApi;
import org.maru.muaring.data.repository.AuthRepository;
import org.maru.muaring.data.repository.AuthRepositoryImpl;
import org.maru.muaring.data.repository.FollowRepository;
import org.maru.muaring.data.repository.FollowRepositoryImpl;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.data.repository.GroupRepositoryImpl;
import org.maru.muaring.data.repository.HistoryRepository;
import org.maru.muaring.data.repository.HistoryRepositoryImpl;
import org.maru.muaring.data.repository.ImageRepository;
import org.maru.muaring.data.repository.ImageRepositoryImpl;
import org.maru.muaring.data.repository.LocationRepository;
import org.maru.muaring.data.repository.LocationRepositoryImpl;
import org.maru.muaring.data.repository.LibraryRepository;
import org.maru.muaring.data.repository.LibraryRepositoryImpl;
import org.maru.muaring.data.repository.LikeRepository;
import org.maru.muaring.data.repository.LikeRepositoryImpl;
import org.maru.muaring.data.repository.MemberRepository;
import org.maru.muaring.data.repository.MemberRepositoryImpl;
import org.maru.muaring.data.repository.NearbyRepository;
import org.maru.muaring.data.repository.NearbyRepositoryImpl;
import org.maru.muaring.data.repository.PostRepository;
import org.maru.muaring.data.repository.PostRepositoryImpl;
import org.maru.muaring.data.repository.RecommendationRepository;
import org.maru.muaring.data.repository.RecommendationRepositoryImpl;
import org.maru.muaring.data.repository.UploadRepository;
import org.maru.muaring.data.repository.UploadRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)   // 모든 요청에 자동 토큰 첨부
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

    @Provides
    @Singleton
    public ImageApi provideImageApi(Retrofit retrofit) {
        return retrofit.create(ImageApi.class);
    }

    @Provides
    @Singleton
    public ImageRepository provideImageRepository(ImageApi api) {
        return new ImageRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public MemberApi provideMemberApi(Retrofit retrofit) {
        return retrofit.create(MemberApi.class);
    }

    @Provides
    @Singleton
    public MemberRepository provideMemberRepository(MemberApi api) {
        return new MemberRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public PostApi providePostApi(Retrofit retrofit) {
        return retrofit.create(PostApi.class);
    }

    @Provides
    @Singleton
    public PostRepository providePostRepository(PostApi api) {
        return new PostRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public HistoryApi provideHistoryApi(Retrofit retrofit) {
        return retrofit.create(HistoryApi.class);
    }

    @Provides
    @Singleton
    public HistoryRepository provideHistoryRepository(HistoryApi api) {
        return new HistoryRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public LocationApi provideLocationApi(Retrofit retrofit) {
        return retrofit.create(LocationApi.class);
    }

    @Provides
    @Singleton
    public LocationRepository provideLocationRepository(LocationApi api) {
        return new LocationRepositoryImpl(api);
    }
    @Provides
    @Singleton
    public LibraryApi provideLibraryApi(Retrofit retrofit) {
        return retrofit.create(LibraryApi.class);
    }

    @Provides
    @Singleton
    public LibraryRepository provideLibraryRepository(LibraryApi api) {
        return new LibraryRepositoryImpl(api);
    }


    @Provides
    @Singleton
    public UploadApi provideUploadApi(Retrofit retrofit) {
        return retrofit.create(UploadApi.class);
    }

    @Provides
    @Singleton
    public UploadRepository provideMusicRepository(UploadApi api) {
        return new UploadRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public FollowApi provideFollowApi(Retrofit retrofit) {
        return retrofit.create(FollowApi.class);
    }

    @Provides
    @Singleton
    public FollowRepository provideFollowRepository(FollowApi api) {
        return new FollowRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public LikeApi provideLikeApi(Retrofit retrofit) {
        return retrofit.create(LikeApi.class);
    }

    @Provides
    @Singleton
    public LikeRepository provideLikeRepository(LikeApi api) {
        return new LikeRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public RecommendationApi provideRecommendationApi(Retrofit retrofit) {
        return retrofit.create(RecommendationApi.class);
    }

    @Provides
    @Singleton
    public RecommendationRepository provideRecommendationRepository(RecommendationApi api) {
        return new RecommendationRepositoryImpl(api);
    }

    @Provides
    @Singleton
    public NearbyApi provideNearbyApi(Retrofit retrofit) {
        return retrofit.create(NearbyApi.class);
    }

    @Provides
    @Singleton
    public NearbyRepository provideNearbyRepository(NearbyApi api) {
        return new NearbyRepositoryImpl(api);
    }

}
