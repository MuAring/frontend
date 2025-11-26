package org.maru.muaring.feature.auth.ui;

import org.maru.muaring.data.api.dto.LoginResponse;

public abstract class LoginState {

    public static class Idle extends LoginState {}

    public static class Loading extends LoginState {}

    public static class Success extends LoginState {
        public final LoginResponse response;

        public Success(LoginResponse response) {
            this.response = response;
        }
    }

    public static class Error extends LoginState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class SpotifyUrl extends LoginState {
        public final String url;
        public SpotifyUrl(String url) {
            this.url = url;
        }
    }
}