FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /app
COPY pom.xml ./
COPY sailing-api/pom.xml ./sailing-api/
RUN mvn dependency:go-offline -pl sailing-api -am
COPY . .
RUN mvn clean package -pl sailing-api -am -DskipTests

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=builder /app/sailing-api/target/sailing-api.jar /app/app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
