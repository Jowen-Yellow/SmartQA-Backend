package com.jowen.smartqa.controller;

import cn.hutool.core.io.FileUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jowen.smartqa.common.BaseResponse;
import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.common.ResultUtils;
import com.jowen.smartqa.exception.BusinessException;
import com.jowen.smartqa.manager.MinioManager;
import com.jowen.smartqa.model.entity.User;
import com.jowen.smartqa.model.enums.FileUploadBizEnum;
import com.jowen.smartqa.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private MinioManager minioManager;

    private final long EXPIRE_DAY = 30;

    private final Cache<String, String> fileUrlCache = Caffeine.newBuilder()
            .initialCapacity(1024)
            .expireAfterWrite(EXPIRE_DAY, TimeUnit.DAYS)
            .build();


    /**
     * 文件上传
     *
     * @param multipartFile
     * @param biz
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           String biz, HttpServletRequest request) {
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        // 上传文件
        minioManager.uploadFile(filepath, multipartFile);
        // 返回可访问地址
        return ResultUtils.success(filepath);
    }

    @GetMapping("/getFileUrl")
    public BaseResponse<String> getFileUrl(@RequestParam("filePath") String filePath) {
        if (fileUrlCache.getIfPresent(filePath) != null) {
            return ResultUtils.success(fileUrlCache.getIfPresent(filePath));
        }
        String fileUrl = minioManager.getFileUrl(filePath);
        fileUrlCache.put(filePath, fileUrl);
        return ResultUtils.success(fileUrl);
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        final long FIVE_M = 5 * ONE_M;

        switch (fileUploadBizEnum) {
            case USER_AVATAR:
                if (fileSize > ONE_M) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
                }
                if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
                }
                break;
            case APP_ICON:
            case RESULT_PICTURE:
                if (fileSize > FIVE_M) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 5M");
                }
            default:
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "业务类型不支持");
        }
    }
}
