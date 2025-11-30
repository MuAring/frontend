package org.maru.muaring.data.api.dto;

public class ImageUploadRequest {

    public String fileName;
    public String fileType;
    public String imageType;
    public Long fileSize;
    public Long targetId;

    private ImageUploadRequest(String fileName, String fileType, String imageType, Long fileSize, Long targetId) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.imageType = imageType;
        this.fileSize = fileSize;
        this.targetId = targetId;
    }

    public static ImageUploadRequest create(String fileName, String fileType, String imageType, Long fileSize, Long targetId) {
        return new ImageUploadRequest(fileName, fileType, imageType, fileSize, targetId);
    }
}