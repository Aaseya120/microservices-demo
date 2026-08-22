# Build the whole reactor first from the repo root:
#   mvn clean package -DskipTests
# then build this image from the repo root, e.g.:
#   docker build -f product-service/Dockerfile -t product-service:1.0.0 .
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY product-service/target/product-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
