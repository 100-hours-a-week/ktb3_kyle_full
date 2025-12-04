package com.kyle.week4.utils;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Component
@RequiredArgsConstructor
public class GCSImageUploader implements ImageUploader {
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucket;

    @Override
    public void delete(String path) {
        if (path == null) return;
        storage.delete(BlobId.of(bucket, path));
    }

    @Override
    public String upload(MultipartFile image) {
        if (image == null) return null;
        try {
            String uuid = UUID.randomUUID().toString();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket, uuid)
                .setContentType(image.getContentType())
                .build();
            storage.create(blobInfo, image.getBytes());
            return uuid;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.GCS_IMAGE_UPLOAD_ERROR);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> result = new ArrayList<>();
        for (MultipartFile image : images) {
            String uuid = upload(image);
            result.add(uuid);
        }
        return result;
    }
}
