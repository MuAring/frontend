package org.maru.muaring.core;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;

// ✨ SharedPreferences(앱 내부 저장소)에 AccessToken / RefreshToken 을 읽고 쓰는 전역 유틸리티 클래스
public class TokenManager {

    private final SharedPreferences prefs;

    @Inject
    public TokenManager(@ApplicationContext Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    /* SharedPreferences 파일 이름
    앱 내부 저장 경로에 muaring_pref.xml 파일로 모든 토큰이 저장됨 */
    private static final String PREF = "muaring_pref";

    // AccessToken을 저장할 key 값
    private static final String ACCESS = "access";

    // RefreshToken을 저장할 key 값
    private static final String REFRESH = "refresh";

    // SpotifyAccessToken을 저장할 key 값
    private static final String SPOTIFY_ACCESS = "spotifyAccess";

    // SpotifyRefreshToken을 저장할 key 값
    private static final String SPOTIFY_REFRESH = "spotifyRefresh";

    public static void save(
            Context context,
            String access,
            String refresh,
            String spotifyAccess
    ) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(ACCESS, access)
                .putString(REFRESH, refresh)
                .putString(SPOTIFY_ACCESS, spotifyAccess)
                .apply();
    }

    public static void saveSpotifyAccess(Context context, String spotifyAccessToken) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(SPOTIFY_ACCESS, spotifyAccessToken)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS, null);
    }

    public String getSpotifyAccessToken() {
        return prefs.getString(SPOTIFY_ACCESS, null);
    }

    // 저장된 토큰 삭제하는 임시 로직
    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        pref.edit().clear().apply();
    }
}
