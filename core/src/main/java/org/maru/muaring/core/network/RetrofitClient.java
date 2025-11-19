package org.maru.muaring.core.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // 백엔드 서버 주소
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // 에뮬레이터에서는 localhost말고 10.0.2.2 쓴다고 함

    private static Retrofit retrofit;

    // Retrofit 인스턴스 가져오기 (싱글톤 패턴)
    public static Retrofit getClient() {
        if (retrofit == null) {
            // HTTP 로깅 (개발 중에 요청/응답 확인용)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp 클라이언트 설정
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // 로깅 추가
                    .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
                    .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃
                    .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃
                    .build();

            // Retrofit 빌드
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // API 서비스 생성 헬퍼 메서드
    public static <T> T createService(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
}