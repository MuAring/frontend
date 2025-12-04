package org.maru.muaring.data.api.dto;

public class MemberProfileUpdateRequest {

    private String nickname;
    private ImageCreateRequest imageCreateRequest;
    private Boolean isPublic;
    private Boolean isDiscoveryEnabled;

    public MemberProfileUpdateRequest(String nickname, ImageCreateRequest imageCreateRequest, Boolean isPublic, Boolean isDiscoveryEnabled) {
        this.nickname = nickname;
        this.imageCreateRequest = imageCreateRequest;
        this.isPublic = isPublic;
        this.isDiscoveryEnabled = isDiscoveryEnabled;
    }

    public static MemberProfileUpdateRequest create(
            String nickname, ImageCreateRequest imageCreateRequest, Boolean isPublic, Boolean isDiscoveryEnabled
    ) {
        return new MemberProfileUpdateRequest(nickname, imageCreateRequest, isPublic, isDiscoveryEnabled);
    }
}
