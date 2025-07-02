# Immfly Assessment Test

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