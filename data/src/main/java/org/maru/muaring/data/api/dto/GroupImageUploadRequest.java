package org.maru.muaring.data.api.dto;

public class GroupImageUploadRequest {

    public Long groupId;
    public String s3Key;
    public String fileName;
    public String fileType;
    public Long fileSize;

    private GroupImageUploadRequest(Long groupId, String s3Key, String fileName, String fileType, Long fileSize) {
        this.groupId = groupId;
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public static GroupImageUploadRequest create(Long groupId, String s3Key, String fileName, String fileType, Long fileSize) {
        return new GroupImageUploadRequest(groupId, s3Key, fileName, fileType, fileSize);
    }
}