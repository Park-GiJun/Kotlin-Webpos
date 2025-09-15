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
echo "🚀 Next Steps for Docker Desktop:"
echo "1. Ensure Docker Desktop Kubernetes is enabled"
echo ""
echo "2. Add to hosts file (as Administrator):"
echo "   echo '127.0.0.1 webpos.local' >> C:\\Windows\\System32\\drivers\\etc\\hosts"
echo ""
echo "3. Deploy to Kubernetes:"
echo "   ./deploy.sh"
echo ""
echo "4. Access services:"
echo "   Main Server: http://webpos.local/main"
echo "   POS Server:  http://webpos.local/pos"