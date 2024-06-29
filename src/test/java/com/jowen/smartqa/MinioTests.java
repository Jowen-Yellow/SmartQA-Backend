package com.jowen.smartqa;

import com.jowen.smartqa.manager.MinioManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MinioTests {
    @Autowired
    private MinioManager minioManager;

    @Test
    public void uploadTest() {
        String objectName = "Call it cruel summer.png";

        String fileName = "F:\\WorkSpace\\SmartQA\\SmartQA-Backend\\src\\main\\resources\\Call it cruel summer.png";
        minioManager.uploadFile(objectName, fileName);
    }

    @Test
    public void getFileUrlTest() {
        String objectName = "Call it cruel summer.png";
        System.out.println(minioManager.getFileUrl(objectName));
    }
}
