# Identity Service

A Spring Boot service for identity management.

Requirements
- Java 17+
- Gradle

Build & run
- Windows: gradlew.bat build && java -jar build\libs\identity-*.jar
- Dev run: gradlew.bat bootRun --args="--spring.profiles.active=dev"
- Tests: gradlew.bat test

Configuration
- Context path: set server.servlet.context-path=/identity in src\main\resources\application.properties
- Profile ports: dev=8080, sit=8081, uat=8082, prod=8083

Database
- PostgreSQL schema included: mysociety_postgresql_complete.sql

License / Contributing
- Add project license and contribution guidelines as needed.
