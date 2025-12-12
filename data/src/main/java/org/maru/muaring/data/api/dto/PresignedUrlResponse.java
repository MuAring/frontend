package org.maru.muaring.data.api.dto;

public class PresignedUrlResponse {

    public String presignedUrl;
    public String s3Key;

    public String getPresignedUrl() { return presignedUrl; }
    public String getS3Key() { return s3Key; }
}