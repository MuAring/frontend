package org.maru.muaring.core;

import android.content.Context;
import android.content.SharedPreferences;

// ✨ SharedPreferences(앱 내부 저장소)에 AccessToken / RefreshToken 을 읽고 쓰는 전역 유틸리티 클래스
public class TokenManager {

    /* SharedPreferences 파일 이름
    앱 내부 저장 경로에 muaring_pref.xml 파일로 모든 토큰이 저장됨 */
    private static final String PREF = "muaring_pref";

    // AccessToken을 저장할 key 값
    private static final String ACCESS = "access";

    // RefreshToken을 저장할 key 값
    private static final String REFRESH = "refresh";

    public static void save(Context context, String access, String refresh) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(ACCESS, access)
                .putString(REFRESH, refresh)
                .apply();
    }

    public static String getAccessToken(Context context) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(ACCESS, null);
    }
}
