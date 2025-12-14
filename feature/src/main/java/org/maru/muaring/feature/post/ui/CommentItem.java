package org.maru.muaring.feature.post.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentItem {

    private Long id;
    private boolean isReply;
    private String nickname;
    private String profileImageUrl; // 없으면 null
    private String content;
}

