# 使用 Eclipse Temurin 17 和 OpenJ9 的基础镜像
FROM ibm-semeru-runtimes:open-17-jre as builder

# 将应用程序 JAR 文件复制到镜像中
WORKDIR /app
COPY SmartQA-Backend-1.0.0.jar .

# 应用内存限制并启动
CMD ["java","-Xms256m","-Xmx256m","-XX:MaxMetaspaceSize=128m","-Xss256k","-jar","/app/SmartQA-Backend-1.0.0.jar","--spring.profiles.active=prod"]