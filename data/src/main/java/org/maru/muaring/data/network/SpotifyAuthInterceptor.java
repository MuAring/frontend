package org.maru.muaring.data.network;

import android.content.Context;

import org.maru.muaring.core.TokenManager;
import org.maru.muaring.data.api.AuthApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.SpotifyTokenRefreshResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class SpotifyAuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;
    private final AuthApi authApi;
    private final Context context;

    public SpotifyAuthInterceptor(TokenManager tokenManager,
                                  AuthApi authApi,
                                  Context context) {
        this.tokenManager = tokenManager;
        this.authApi = authApi;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        String spotifyAccess = tokenManager.getSpotifyAccessToken();

        Request original = chain.request();
        Request requestWithToken = original.newBuilder()
                .header("Authorization", "Bearer " + spotifyAccess)
                .build();

        Response response = chain.proceed(requestWithToken);

        if (response.code() == 401) {
            response.close();

            String newAccess = refreshSpotifyAccessTokenFromBackend();
            if (newAccess == null) {
                // 실패하면 그냥 401 반환
                return response;
            }

            // 새 토큰으로 다시 시도
            Request retryRequest = original.newBuilder()
                    .header("Authorization", "Bearer " + newAccess)
                    .build();

            return chain.proceed(retryRequest);
        }

        return response;
    }

    private String refreshSpotifyAccessTokenFromBackend() {
        try {
            Call<ApiResponse<SpotifyTokenRefreshResponse>> call =
                    authApi.refreshSpotifyAccessToken();

            retrofit2.Response<ApiResponse<SpotifyTokenRefreshResponse>> res = call.execute();
            if (res.isSuccessful() && res.body() != null) {
                String newSpotifyAccessToken = res.body().getData().spotifyAccessToken;

                TokenManager.saveSpotifyAccess(context, newSpotifyAccessToken);
                return newSpotifyAccessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
