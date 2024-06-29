# Docker 镜像构建
FROM openjdk:17 as builder

# Copy local code to the container image.
WORKDIR /app
COPY SmartQA-Backend-0.0.1-SNAPSHOT.jar .

# Run the web service on container startup.
CMD ["java","-jar","/app/SmartQA-Backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]