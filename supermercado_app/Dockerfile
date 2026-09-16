FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/supermercado_app-0.0.1.jar
COPY ${JAR_FILE} app_supermercado.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_supermercado.jar"]