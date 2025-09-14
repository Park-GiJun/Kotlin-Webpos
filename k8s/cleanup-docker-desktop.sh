#!/bin/bash

# Docker Desktop Kubernetes Cleanup Script
# Usage: ./cleanup-docker-desktop.sh [infra-only]

set -e

NAMESPACE_INFRA="webpos-infra"
NAMESPACE_APP="webpos"
INFRA_ONLY=${1:-false}

echo "🧹 Docker Desktop Kubernetes Cleanup"
echo "===================================="

# Check current context
CURRENT_CONTEXT=$(kubectl config current-context)
if [ "$CURRENT_CONTEXT" != "docker-desktop" ]; then
    echo "⚠️  Current context is: $CURRENT_CONTEXT"
    echo "❌ Please switch to docker-desktop context:"
    echo "   kubectl config use-context docker-desktop"
    exit 1
fi

echo "✅ Using Docker Desktop Kubernetes"

# Function to delete resources safely
safe_delete() {
    local resource=$1
    if kubectl get $resource &> /dev/null; then
        echo "🗑️  Deleting $resource"
        kubectl delete $resource --ignore-not-found=true
    else
        echo "ℹ️  $resource not found, skipping"
    fi
}

if [ "$INFRA_ONLY" != "infra-only" ]; then
    echo "🚫 Step 1: Deleting Application Resources"
    safe_delete "namespace $NAMESPACE_APP"
    
    # Wait a bit for namespace deletion
    echo "⏳ Waiting for application namespace deletion..."
    kubectl wait --for=delete namespace/$NAMESPACE_APP --timeout=60s || true
fi

echo "🚫 Step 2: Deleting Infrastructure Resources"
safe_delete "namespace $NAMESPACE_INFRA"

# Wait for namespace deletion
echo "⏳ Waiting for infrastructure namespace deletion..."
kubectl wait --for=delete namespace/$NAMESPACE_INFRA --timeout=60s || true

echo ""
echo "✅ Cleanup completed!"
echo ""

# Show remaining resources
echo "📊 Remaining WebPOS Resources:"
kubectl get all --all-namespaces | grep webpos || echo "No WebPOS resources found"

echo ""
echo "🐳 Docker Desktop Status:"
echo "- Check Docker Desktop Dashboard for any remaining containers"
echo "- Kubernetes resources should be cleaned up"
echo ""

if [ "$INFRA_ONLY" == "infra-only" ]; then
    echo "🔧 Infrastructure-only cleanup completed"
    echo "- Application namespace preserved: $NAMESPACE_APP"
else
    echo "🔧 Full cleanup completed"
    echo "- All WebPOS resources removed"
fi

echo ""
echo "💡 To verify cleanup:"
echo "  kubectl get all --all-namespaces | grep webpos"
echo ""
echo "🚀 To redeploy:"
if [ "$INFRA_ONLY" == "infra-only" ]; then
    echo "  ./deploy-docker-desktop-infra.sh"
else
    echo "  ./deploy-docker-desktop.sh        # Full deployment"
    echo "  ./deploy-docker-desktop-infra.sh  # Infrastructure only"
fi