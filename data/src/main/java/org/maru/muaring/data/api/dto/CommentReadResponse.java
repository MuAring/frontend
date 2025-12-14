package org.maru.muaring.data.api.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class CommentReadResponse {
    private Long commentId;
    private String content;
    private Long memberId;
    private String memberNickname;
    private Boolean isDeleted;
    private String createdAt;
    private List<ReplyReadResponse> replies;
}
