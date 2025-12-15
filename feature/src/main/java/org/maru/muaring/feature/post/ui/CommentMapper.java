package org.maru.muaring.feature.post.ui;

import org.maru.muaring.data.api.dto.CommentReadResponse;
import org.maru.muaring.data.api.dto.ReplyReadResponse;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static List<CommentItem> toCommentItems(
            List<CommentReadResponse> responses
    ) {
        List<CommentItem> items = new ArrayList<>();
        if (responses == null) return items;

        for (CommentReadResponse comment : responses) {

            // 부모 댓글
            items.add(new CommentItem(
                    comment.getCommentId(),
                    false,
                    comment.getMemberNickname(),
                    comment.getProfileImgUrl(),                       // 프로필 URL 없으면 null
                    comment.getContent()
            ));

            // 답글
            if (comment.getReplies() != null) {
                for (ReplyReadResponse reply : comment.getReplies()) {
                    items.add(new CommentItem(
                            reply.getReplyId(),
                            true,
                            reply.getMemberNickname(),
                            reply.getProfileImgUrl(),
                            reply.getContent()
                    ));
                }
            }
        }
        return items;
    }
}
