FROM eclipse-temurin
LABEL authors="Soumen"
WORKDIR /uttar
COPY target/*.jar uttar.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","uttar.jar"]