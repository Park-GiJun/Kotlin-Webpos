#!/bin/bash

# Docker Image Build Script for WebPOS MSA
# Usage: ./build-images.sh [tag]

set -e

TAG=${1:-latest}
REGISTRY=${REGISTRY:-"webpos"}

echo "🐳 Building Docker Images for WebPOS MSA"
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

# Navigate to project root
cd "$(dirname "$0")/.."

echo "📁 Current directory: $(pwd)"

# Build main-server
build_image "main-server" "./main-server"

# Build pos-server
build_image "pos-server" "./pos-server"

echo ""
echo "🎉 All images built successfully!"
echo ""
echo "📋 Built Images:"
docker images | grep $REGISTRY

echo ""
echo "🚀 Next Steps:"
echo "1. Test images locally:"
echo "   docker run -p 8080:8080 $REGISTRY/main-server:$TAG"
echo ""
echo "2. Push to registry (if using remote registry):"
echo "   docker push $REGISTRY/main-server:$TAG"
echo "   docker push $REGISTRY/pos-server:$TAG"
echo ""
echo "3. Deploy to Kubernetes:"
echo "   cd k8s && ./deploy.sh"