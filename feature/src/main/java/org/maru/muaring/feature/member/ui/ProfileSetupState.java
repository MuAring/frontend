package org.maru.muaring.feature.member.ui;

public abstract class ProfileSetupState {
    public static class Idle extends ProfileSetupState {}
    public static class Loading extends ProfileSetupState {}
    public static class Success extends ProfileSetupState {}
    public static class Error extends ProfileSetupState {
        public final String message;
        public Error(String message) { this.message = message; }
    }
    public static class NicknameAvailable extends ProfileSetupState {
        public final String nickname;
        public NicknameAvailable(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class NicknameUnAvailable extends ProfileSetupState {
        public final String nickname;
        public NicknameUnAvailable(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class ImageUploaded extends ProfileSetupState {
        public final String s3Key;
        public final String fileName;
        public final String fileType;
        public final Long fileSize;

        public ImageUploaded(String s3Key, String fileName, String fileType, Long fileSize) {
            this.s3Key = s3Key;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }
    }

    public static final class ProfileCreated extends ProfileSetupState {
        public final String nickname;
        public ProfileCreated(String nickname) {
            this.nickname = nickname;
        }
    }
}