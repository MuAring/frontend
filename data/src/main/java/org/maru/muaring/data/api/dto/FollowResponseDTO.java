package org.maru.muaring.data.api.dto;

public class FollowResponseDTO {

    private Long followId;
    private Long followerId;
    private Long followeeId;
    private String status;
    private String createdAt;   // LocalDateTime으로 못 받아온다고 했던 것 가틈

    public Long getFollowId() {
        return followId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
