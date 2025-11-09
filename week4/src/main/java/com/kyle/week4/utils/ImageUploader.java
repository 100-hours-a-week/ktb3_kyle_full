package com.kyle.week4.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageUploader {
    void delete(String path);
    String upload(MultipartFile file);
    List<String> uploadImages(List<MultipartFile> images);
}
