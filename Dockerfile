# Stage 1: Build
# Start with a Maven image that includes JDK 21
FROM maven:3.9.8-amazoncorretto-21 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM ibm-semeru-runtimes:open-17-jre

WORKDIR /app

# Copy và rename JAR file với tên cụ thể
COPY --from=build /app/target/*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-Xms512m -Xmx1024m"

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]