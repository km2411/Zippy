FROM adoptopenjdk/maven-openjdk11:latest
WORKDIR /app
COPY . .
RUN mvn clean package
