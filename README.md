# Immfly Assessment Test

## Architecture Overview
For implementation of in-flight Order Management System (OMP) we will use [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) with [Spring Boot](https://spring.io/projects/spring-boot).
Also we will use [Testcontainers](https://www.testcontainers.org/) for integration tests and [Gradle](https://gradle.org/) as build tool.
Local development will be done using docker compose for running local database.
See [Environment Setup](#environment-setup) section for instructions on how to set up your local environment.

## Environment Setup 
Assuming you have brew installed, so let's install the necessary tools to build project.

```bash
brew install jq gradle openjdk@21 colima docker docker-compose
```

Start Colima with Docker:
```bash
/opt/homebrew/opt/colima/bin/colima \
                start --network-address  \
                -f
```
Wait for Colima to start, it may take a few minutes.

Configure environment variables for Java and Docker:
```bash
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zprofile
echo 'export JAVA_HOME="$(brew --prefix openjdk@21)"' >> ~/.zprofile
echo 'export DOCKER_HOST=unix://$HOME/.colima/default/docker.sock' >> ~/.zprofile
echo 'export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=$HOME/.colima/default/docker.sock' >> ~/.zprofile
echo 'export TESTCONTAINERS_HOST_OVERRIDE=$(colima ls -j | jq -r '.address')' >> ~/.zprofile
echo 'export TESTCONTAINERS_RYUK_DISABLED=true' >> ~/.zprofile
```

Load environment variables:
```bash
. ~/.zprofile
```