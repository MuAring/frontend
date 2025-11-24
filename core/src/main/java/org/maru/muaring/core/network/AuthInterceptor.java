package org.maru.muaring.core.network;

import android.content.Context;

import org.maru.muaring.core.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager, Context context) {
        this.tokenManager = tokenManager;
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = TokenManager.getAccessToken(context);

        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}

