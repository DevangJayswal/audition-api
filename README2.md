## Implementation Details

### Updates Made

- Updated the Gradle version to 8 from 7
- Implemented tests with a code coverage of around 85% (wiremock)
- Implemented custom logging and tracing where `traceId` and `spanId` are responded in each request
- Added proper input validation for requests
- Introduced `Comment` model with fields for `postId`, `id`, `name`, `email`, and `body`
- Updated `AuditionController` to include endpoints for retrieving comments by `postId` and filtering posts
- Enhanced `AuditionIntegrationClient` to fetch comments from the external API
- Improved error handling in various components, including `SystemException` and `ExceptionControllerAdvice`
- Updated application configuration and added necessary dependencies for security and validation
- Added logging interceptor for request/response logging
- Integrated Spring Security and exposed only health and info endpoints publicly while we can still access other actuator endpoints securely.

### Build Configuration

The `build.gradle` file has been updated to enhance security, improve testing capabilities, and remove redundant dependencies. Below are the key changes:

1. **Added Dependencies**:
   - `org.springframework.boot:spring-boot-starter-security`: Provides security features for the application.
   - `org.springframework.boot:spring-boot-starter-validation`: Adds validation support.
   - `org.springframework.cloud:spring-cloud-starter-contract-stub-runner`: Supports WireMock tests for contract testing.
   - `org.springframework.security:spring-security-test`: Facilitates security testing.   

2. **Removed Dependencies**:
   - `org.springframework.boot:spring-boot-starter-webflux`: Removed as we're using servlet-based Spring MVC and not reactive.   
   - `io.projectreactor:reactor-test`: Removed as it was unnecessary.
   - `org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j`: As we have not implemented any reliability and resilience features, this dependency was removed.   
   - `org.springframework.boot:spring-boot-starter-data-jpa`: Removed as we're not using JPA features in this project.
   - `org.springframework.boot:spring-boot-starter-aop`: Removed as Spring Boot includes AOP capabilities by default in the core starter dependencies.

3. **Removed Duplicate Dependency**:
   - `org.springframework.cloud:spring-cloud-starter-config`: This dependency was defined twice and has been cleaned up.

### Code Formatting

The code is formatted according to the instructions provided in the "Importing Google Java Code Style into IntelliJ" section.

### Branch Information

All updates were made in the `feature/solution-by-devang-jayswal` branch. To understand the detailed changes made, compare this feature branch with the `master` branch.

## Prerequisites
- Java Development Kit (JDK) 17 or higher
- Gradle

## Build and Run

### Clean and Build the Project
To clean and build the project, run:
```shell
./gradlew clean build
```

### Build the Project Without Running Tests
To build the project without running tests, use:
```shell
./gradlew clean build -x test
```

### Set Environment Variables
Before running the application, set the following environment variables:
```shell
export USER_NAME=devang
export USER_PASSWORD=strongPassword
```

### Run the Application
To run the application, execute:
```shell
./gradlew bootRun
```

## API Endpoints

The following CURL commands can be used to interact with the API endpoints.

### Get all posts
```sh
curl -X GET 'localhost:8080/posts' -u devang:strongPassword
```

### Get posts with a specific title
```sh
curl -X GET 'localhost:8080/posts?title=omnis' -u devang:strongPassword
```

### Get a specific post by ID
```sh
curl -X GET 'localhost:8080/posts/1' -u devang:strongPassword
```

### Get comments for a specific post
```sh
curl -X GET 'localhost:8080/posts/1/comments' -u devang:strongPassword
```

### Get all comments
```sh
curl -X GET 'localhost:8080/comments' -u devang:strongPassword
```

### Get comments for a specific post by post ID
```sh
curl -X GET 'localhost:8080/comments?postId=1' -u devang:strongPassword
```