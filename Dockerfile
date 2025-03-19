FROM eclipse-temurin:21-jdk-jammy
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests
ENTRYPOINT [ "java" , "-jar", "target/trancasDEE-0.0.1-SNAPSHOT.jar"]