# Java Frontend Web Application

This project is a simple Spring Boot web application with a responsive frontend built using Thymeleaf, Bootstrap, and custom JavaScript.

## Features
- Homepage with dynamic task list and creation form
- REST API for tasks
- Responsive UI and accessible design
- Unit tests for service and controller layers
- Maven build and WAR packaging for Tomcat deployment
- Jenkins pipeline for EC2/Tomcat deployment

## Build
```bash
mvn clean package
```

## Run locally
```bash
mvn spring-boot:run
```

## Deploy to Tomcat
1. Build the WAR:
   ```bash
   mvn clean package
   ```
2. Copy `target/java-frontend-app.war` to Tomcat `webapps/`.

## Jenkins
The included `Jenkinsfile` builds the project, runs tests, and deploys the generated WAR to a Tomcat instance on EC2.
