FROM maven:3-eclipse-temurin-17 as builder

WORKDIR /app

# Copy the pom.xml file and verify that dependencies are available
# This is done before copying the source code to take advantage of Docker's layer caching
COPY generator/pom.xml /app
RUN mvn verify --fail-never

# Copy your source code into the image
COPY generator/src /app/src

# Build the application
RUN mvn -B -am package -DskipTests

# Use openapitools/openapi-generator-cli as base image
FROM openapitools/openapi-generator-cli:v7.5.0

# Install Go 1.20.6
RUN apt-get update \
    && apt-get install -y wget \
    && ARCH=$(dpkg --print-architecture) \
    && if [ "$ARCH" = "amd64" ]; then GOARCH="amd64"; \
       elif [ "$ARCH" = "arm64" ]; then GOARCH="arm64"; \
       elif [ "$ARCH" = "386" ]; then GOARCH="386"; \
       else echo "Unsupported architecture"; exit 1; fi \
    && wget https://golang.org/dl/go1.20.6.linux-$GOARCH.tar.gz -O go.tar.gz \
    && tar -C /usr/local -xzf go.tar.gz \
    && rm go.tar.gz

# Set the environment variable for Go
ENV PATH="/usr/local/go/bin:${PATH}"
ENV GOBIN=/usr/local/bin/

# Install goimports
RUN go install golang.org/x/tools/cmd/goimports@v0.20.0

ENV GO_POST_PROCESS_FILE="goimports -w"

# Install Python
RUN apt-get update \
    && apt-get install -y python3 python3-pip \
    && pip3 install yapf

# Set the environment variable for yapf
ENV PYTHON_POST_PROCESS_FILE="/usr/local/bin/yapf -i"

# Copy the current directory contents into the container at /gen
COPY --from=builder /app/target/*.jar /opt/bloxone/
