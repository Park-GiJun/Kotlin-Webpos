#!/bin/bash

# Docker Image Build Script for CentOS Server
# Usage: ./build-images-centos.sh [tag]

set -e

TAG=${1:-latest}
REGISTRY=${REGISTRY:-"webpos"}

echo "🐳 Building Docker Images for WebPOS on CentOS"
echo "Registry: $REGISTRY"
echo "Tag: $TAG"

# Function to build and tag image
build_image() {
    local service=$1
    local context=$2
    
    echo "🏗️  Building $service..."
    
    # Build the image
    docker build -t "$REGISTRY/$service:$TAG" "$context"
    
    # Also tag as latest
    if [ "$TAG" != "latest" ]; then
        docker tag "$REGISTRY/$service:$TAG" "$REGISTRY/$service:latest"
    fi
    
    echo "✅ Built $REGISTRY/$service:$TAG"
}

# Navigate to project root (assuming script is in k8s directory)
cd "$(dirname "$0")/.."

echo "📁 Current directory: $(pwd)"

# Check if Dockerfiles exist
if [ ! -f "./main-server/Dockerfile" ]; then
    echo "❌ main-server/Dockerfile not found. Creating basic Dockerfile..."
    cat > ./main-server/Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Copy the built jar
RUN cp build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
fi

if [ ! -f "./pos-server/Dockerfile" ]; then
    echo "❌ pos-server/Dockerfile not found. Creating basic Dockerfile..."
    cat > ./pos-server/Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Copy the built jar
RUN cp build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
fi

# Build main-server
if [ -d "./main-server" ]; then
    build_image "main-server" "./main-server"
else
    echo "❌ main-server directory not found"
    exit 1
fi

# Build pos-server
if [ -d "./pos-server" ]; then
    build_image "pos-server" "./pos-server"
else
    echo "❌ pos-server directory not found"
    exit 1
fi

echo ""
echo "🎉 All images built successfully!"
echo ""
echo "📋 Built Images:"
docker images | grep $REGISTRY

echo ""
echo "🔧 Image Information:"
echo "Main Server Image: $REGISTRY/main-server:$TAG"
echo "POS Server Image: $REGISTRY/pos-server:$TAG"

echo ""
echo "🚀 Next Steps:"
echo "1. Test images locally:"
echo "   docker run -p 8080:8080 $REGISTRY/main-server:$TAG"
echo ""
echo "2. Import images to k3s:"
echo "   sudo k3s ctr images import <image-tar>"
echo ""
echo "3. Deploy to Kubernetes:"
echo "   ./deploy-centos.sh"

# Save images as tar files for k3s import (optional)
read -p "💾 Do you want to save images as tar files for k3s? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "💾 Saving images as tar files..."
    docker save $REGISTRY/main-server:$TAG -o main-server-$TAG.tar
    docker save $REGISTRY/pos-server:$TAG -o pos-server-$TAG.tar
    
    echo "✅ Images saved:"
    echo "- main-server-$TAG.tar"
    echo "- pos-server-$TAG.tar"
    
    echo ""
    echo "📤 To import to k3s:"
    echo "sudo k3s ctr images import main-server-$TAG.tar"
    echo "sudo k3s ctr images import pos-server-$TAG.tar"
fi