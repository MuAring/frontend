package org.maru.muaring.feature.auth.ui;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
                    TokenManager.save(context, response.getAccessToken(), response.getRefreshToken());
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
}
