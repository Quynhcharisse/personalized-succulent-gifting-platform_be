FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables (override in Render dashboard with actual values)
ENV DB_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV JWT_SECRET=""
ENV JWT_ACCESS_EXPIRED=""
ENV JWT_REFRESH_EXPIRED=""
ENV CLIENT_URL=""
ENV CLIENT_SERVER_URL=""
ENV SWAGGER_URL=""
ENV PORT=8080

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]