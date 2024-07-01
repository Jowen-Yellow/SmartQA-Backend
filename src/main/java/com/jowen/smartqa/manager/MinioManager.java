package com.jowen.smartqa.manager;

import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.exception.BusinessException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 管理
 *
 * @author Jowen Yellow
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MinioManager {
    private static final Logger log = LoggerFactory.getLogger(MinioManager.class);
    private final MinioClient minioClient;
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 上传文件
     *
     * @param objectName 对象名称
     * @param fileName   文件名
     */
    public void uploadFile(String objectName, String fileName) {
        try {
            // 判断Bucket是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            // 创建Bucket
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 上传文件
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(fileName)
                    .build());
        } catch (Exception e) {
            log.error("文件{}上传失败:{}", objectName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败:" + e.getMessage());
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     */
    public void uploadFile(String objectName, MultipartFile multipartFile) {
        try {
            // 判断Bucket是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            // 创建Bucket
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            try (InputStream inputStream = multipartFile.getInputStream()) {
                // 上传文件
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, multipartFile.getSize(), -1)
                        .contentType(multipartFile.getContentType())
                        .build());
            }

        } catch (Exception e) {
            log.error("文件{}上传失败:{}", multipartFile.getOriginalFilename(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败:" + e.getMessage());
        }
    }

    /**
     * 获取文件url
     *
     * @param objectName 文件名称
     * @return url
     */
    public String getFileUrl(String objectName) {
        try {
            int EXPIRE_TIME = 60 * 60 * 24;
            return minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(io.minio.http.Method.GET)
                            .expiry(EXPIRE_TIME, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在:" + e.getMessage());
        }
    }


}
