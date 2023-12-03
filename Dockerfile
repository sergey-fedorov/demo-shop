FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/shop-0.0.1.jar shop-0.0.1.jar
EXPOSE 8081
CMD ["java","-jar","shop-0.0.1.jar"]