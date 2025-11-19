package org.maru.muaring.data.repository;

import org.maru.muaring.data.api.dto.LoginResponse;

public interface AuthRepository {

//    interface LoginCallback {
//        void onSuccess(LoginResponse response);
//        void onError(String message);
//    }

    LoginResponse loginWithKakao(String kakaoToken) throws Exception;
}
