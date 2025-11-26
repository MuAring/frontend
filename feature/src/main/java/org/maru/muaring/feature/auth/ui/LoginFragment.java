package org.maru.muaring.feature.auth.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.auth.navigation.LoginNavigator;
import com.kakao.sdk.user.*;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private LoginNavigator navigator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // HiltViewModel 가져오기
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 카카오 로그인
        Button kakaoLoginBtn = view.findViewById(R.id.btnKakaoLogin);
        kakaoLoginBtn.setOnClickListener(v -> {
            UserApiClient.getInstance().loginWithKakaoTalk(requireActivity(), (token, error) -> {
                if (error != null) {
                    // 카톡 앱 로그인 실패 -> 웹 로그인
                    UserApiClient.getInstance().loginWithKakaoAccount(requireActivity(), (accountToken, accountError) -> {
                        if (accountToken != null) {
                            loginViewModel.loginWithKakao(accountToken.getAccessToken(), requireContext());
                        }
                        return null;
                    });
                } else if (token != null) {
                    loginViewModel.loginWithKakao(token.getAccessToken(), requireContext());
                }
                return null;
            });
        });

        // 스포티파이 로그인
        Button spotifyLoginBtn = view.findViewById(R.id.btnSpotifyLogin);
        spotifyLoginBtn.setOnClickListener(v -> {
            loginViewModel.startSpotifyLogin(requireContext());
        });

        observeLoginState();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // LoginActivity가 LoginNavigator를 구현하고 있나?
        if (context instanceof LoginNavigator) {
            // 구현했다면 Fragment 내부에서 navigator로 사용할 수 있게 저장
            navigator = (LoginNavigator) context;
        } else {
            throw new IllegalStateException("LoginActivity는 LoginNavigator을 구현하고 있어야 합니다.");
        }
    }

    private void observeLoginState() {
        loginViewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {

            if (state instanceof LoginState.Loading) {
                // TODO: 로딩 progress bar 표시 (컴포넌트 이용 예정)
            }
            else if (state instanceof LoginState.Success) {
                // 로그인된 메인화면으로 이동
                navigator.navigateToMain();

                // 로그인 화면 Activity 종료
                requireActivity().finish();
            }
            else if (state instanceof  LoginState.SpotifyUrl) {
                String url = ((LoginState.SpotifyUrl) state).url;
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(requireContext(), Uri.parse(url));
            }
            else if (state instanceof LoginState.Error) {
                String msg = ((LoginState.Error) state).message;
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
