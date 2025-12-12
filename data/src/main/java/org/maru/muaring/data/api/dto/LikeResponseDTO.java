package org.maru.muaring.data.api.dto;

public class LikeResponseDTO {

    private Long postId;
    private int numOfLikes;
    private boolean liked;

    public Long getPostId() {
        return postId;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public boolean isLiked() {
        return liked;
    }
}
