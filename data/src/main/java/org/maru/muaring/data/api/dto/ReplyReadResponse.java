package org.maru.muaring.data.api.dto;

import lombok.Getter;

@Getter
public class ReplyReadResponse {

    private Long replyId;
    private String content;
    private Long memberId;
    private String memberNickname;
    private String profileImgUrl;
    private Boolean isDeleted;
    private String createdAt;
}
