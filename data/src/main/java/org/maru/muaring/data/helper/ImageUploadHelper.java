package org.maru.muaring.data.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;
import org.maru.muaring.data.repository.ImageRepository;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUploadHelper {

    public static void uploadImage(
            Uri imageUri,
            Context context,
            ImageRepository imageRepository,
            Callback<ProfileSetupState.ImageUploaded> finalCallback
    ) {

        byte[] bytes = readBytes(imageUri, context);
        String fileName = getFileName(context, imageUri);
        String fileType = getMimeType(context, imageUri);

        ImageUploadRequest request = ImageUploadRequest.create(
                fileName,
                fileType,
                "MEMBER",
                (long) bytes.length,
                null
        );

        imageRepository.getUploadPresignedUrl(request, new Callback<>() {
            @Override
            public void onSuccess(PresignedUrlResponse response) {
                imageRepository.uploadToS3(response.presignedUrl, fileName, bytes, fileType,
                        new Callback<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                ProfileSetupState.ImageUploaded result =
                                        new ProfileSetupState.ImageUploaded(
                                                response.s3Key,
                                                fileName,
                                                fileType,
                                                (long) bytes.length
                                        );

                                finalCallback.onSuccess(result);
                            }

                            @Override
                            public void onError(Exception e) {
                                finalCallback.onError(e);
                            }
                        });
            }

            @Override
            public void onError(Exception e) {
                finalCallback.onError(e);
            }
        });
    }

    public static byte[] readBytes(Uri uri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }


    public static String getMimeType(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        if (type != null) return type;

        String path = uri.getPath();
        if (path == null) return "image/*";

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }
}
