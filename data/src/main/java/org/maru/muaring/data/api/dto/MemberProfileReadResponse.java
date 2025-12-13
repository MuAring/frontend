package org.maru.muaring.data.api.dto;

import lombok.Getter;

@Getter
public class MemberProfileReadResponse {

    private String nickname;
    private String imageUrl;
    private boolean isMe;
    private boolean isPublic;
    private boolean isFollowing;
    private Long sharedMusicCount;
    private Long followerCount;
    private Long followeeCount;
    private Long joinedGroupCount;
}
