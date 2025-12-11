package org.maru.muaring.data.network;

import org.maru.muaring.core.TokenManager;
import java.io.IOException;
import jakarta.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    @Inject
    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getAccessToken();

        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}

