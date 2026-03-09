# Whydah-TestWebApp

## Purpose
Reference application demonstrating Whydah SSO integration. Provides code examples for integrating with Whydah from JavaScript, Django, Microsoft SharePoint, Spring Security, and Mobile/PhoneGap platforms.

## Tech Stack
- Language: Java 8+
- Framework: Various (examples for multiple platforms)
- Build: Maven
- Key dependencies: Whydah-Java-SDK

## Architecture
Sample web application that requires Whydah login to access. Contains implementation examples for multiple platforms showing how to integrate with Whydah SSO. Serves as both a test harness and a reference implementation for developers integrating Whydah into their applications.

## Key Entry Points
- `http://localhost:9990/test/hello` - Triggers login flow
- `ImplementationExamples/` - Platform-specific integration examples
- `pom.xml` - Maven coordinates: `net.whydah.sso.web:TestWebApp`

## Development
```bash
# Build
mvn clean install

# Run
java -jar target/TestWebApp-*.jar
```

## Domain Context
Whydah IAM integration reference. Demonstrates how to integrate Whydah SSO into applications across multiple technology stacks. Note: may lag behind current Whydah development and should be used as study material.
