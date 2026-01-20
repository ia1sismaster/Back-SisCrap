FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 5050

CMD ["java", "-jar", "target/siscrap-api-0.0.1-SNAPSHOT.jar"]
