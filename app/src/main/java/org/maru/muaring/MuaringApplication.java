package org.maru.muaring;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;
import dagger.hilt.android.HiltAndroidApp;


// ✨전역 초기화를 담당하는 클래스
@HiltAndroidApp
public class MuaringApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // kakaoSDK를 사용하기 위한 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY);
    }
}
