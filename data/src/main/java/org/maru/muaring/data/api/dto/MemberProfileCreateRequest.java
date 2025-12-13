package org.maru.muaring.data.api.dto;

public class MemberProfileCreateRequest {

    private String nickname;
    private ImageCreateRequest imageRequest;

    private MemberProfileCreateRequest(String nickname, ImageCreateRequest request) {
        this.nickname = nickname;
        this.imageRequest = request;
    }

    public static MemberProfileCreateRequest create(String nickname, ImageCreateRequest request) {
        return new MemberProfileCreateRequest(nickname, request);
    }
}