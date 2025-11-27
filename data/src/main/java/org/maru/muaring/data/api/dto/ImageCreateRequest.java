package org.maru.muaring.data.api.dto;

import lombok.Builder;

@Builder
public class ImageCreateRequest {

    public String fileName;
    public String fileType;
    public String imageType;
    public Long fileSize;
    public String s3Key;

    public static ImageCreateRequest create(String fileName, String fileType, String imageType, long fileSize, String s3Key) {
        return ImageCreateRequest.builder()
                .fileName(fileName)
                .fileType(fileType)
                .imageType(imageType)
                .fileSize(fileSize)
                .s3Key(s3Key)
                .build();
    }
}