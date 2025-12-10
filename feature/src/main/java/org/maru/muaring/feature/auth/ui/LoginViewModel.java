package org.maru.muaring.feature.auth.ui;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.TokenManager;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.repository.AuthRepository;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<LoginState> loginState =
            new MutableLiveData<>(new LoginState.Idle());

    @Inject
    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void loginWithKakao(String token, Context context) {
        loginState.setValue(new LoginState.Loading());

        repository.loginWithKakao(token, new Callback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                try {
                    TokenManager.save(context, response.getAccessToken(), response.getRefreshToken(), response.getSpotifyAccessToken());
                    loginState.postValue(new LoginState.Success(response));
                } catch (Exception e) {
                    loginState.postValue(new LoginState.Error("토큰 저장 실패: " + e.getMessage()));
                }
            }

            @Override
            public void onError(Exception e) {
                // UI에 실패 알림
                loginState.postValue(new LoginState.Error(e.getMessage()));
            }
        });
    }

    // ⚪ Spotify 로그인 flow 시작 → authorize URL 받기
    public void startSpotifyLogin(Context context) {
        loginState.setValue(new LoginState.Loading());

        repository.getSpotifyAuthorizeUrl(new Callback<>() {
            @Override
            public void onSuccess(String authorizeUrl) {
                loginState.postValue(new LoginState.SpotifyUrl(authorizeUrl));
            }

            @Override
            public void onError(Exception e) {
                loginState.postValue(new LoginState.Error("스포티파이 Authorized URL 요청에 실패했습니다. : " + e.getMessage()));
            }
        });
    }

    // ⚪ redirect 후 받은 code 로 서버 로그인
    public void loginWithSpotifyCode(String code, Context context) {
        loginState.setValue(new LoginState.Loading());

        repository.loginWithSpotifyCode(code, new Callback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                // 토큰 저장
                TokenManager.save(context, response.getAccessToken(), response.getRefreshToken(), response.getSpotifyAccessToken());
                loginState.postValue(new LoginState.Success(response));
            }

            @Override
            public void onError(Exception e) {
                loginState.postValue(new LoginState.Error("로그인 실패: " + e.getMessage()));
            }
        });
    }
}
