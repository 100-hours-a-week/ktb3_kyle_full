package com.kyle.week4.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class SimpleImageUploader implements ImageUploader {

    @Override
    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> result = new ArrayList<>();
        for (MultipartFile image : images) {
            UUID uuid = UUID.randomUUID();
            // 이미지 업로드 수행 . . .
            result.add(uuid.toString());
        }
        return result;
    }
}
