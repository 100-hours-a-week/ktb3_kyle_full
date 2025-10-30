package com.kyle.week4.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {
    List<String> uploadImages(List<MultipartFile> images);
}
