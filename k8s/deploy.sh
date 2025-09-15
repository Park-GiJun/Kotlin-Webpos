#!/bin/bash

# Kubernetes Deployment Script for WebPOS MSA
# Usage: ./deploy.sh [environment]
# Environment: dev, staging, prod (default: dev)

set -e

ENVIRONMENT=${1:-dev}
NAMESPACE_INFRA="webpos-infra"
NAMESPACE_APP="webpos"

echo "🚀 Starting WebPOS Kubernetes Deployment (Environment: $ENVIRONMENT)"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if cluster is accessible
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Cannot access Kubernetes cluster. Please check your kubeconfig."
    exit 1
fi

echo "✅ Kubernetes cluster is accessible"

# Function to apply manifests
apply_manifest() {
    local file=$1
    echo "📋 Applying $file"
    kubectl apply -f "$file"
}

# Function to wait for deployment
wait_for_deployment() {
    local namespace=$1
    local deployment=$2
    echo "⏳ Waiting for $deployment in namespace $namespace to be ready..."
    kubectl rollout status deployment/$deployment -n $namespace --timeout=300s
}

# Function to wait for service to be ready
wait_for_service() {
    local namespace=$1
    local service=$2
    echo "⏳ Waiting for service $service in namespace $namespace..."
    kubectl wait --for=condition=ready pod -l app=$service -n $namespace --timeout=300s
}

echo "🏗️  Step 1: Creating Namespaces"
apply_manifest "namespace.yaml"

echo "🔐 Step 2: Creating Secrets"
apply_manifest "secrets/mysql-secrets.yaml"
apply_manifest "secrets/app-secrets.yaml"

echo "⚙️  Step 3: Creating ConfigMaps"
apply_manifest "configmaps/mysql-config.yaml"
apply_manifest "configmaps/app-config.yaml"

echo "💾 Step 4: Creating Storage"
apply_manifest "storage/mysql-storage.yaml"

echo "🗄️  Step 5: Deploying Infrastructure Services"
apply_manifest "infrastructure/mysql-deployment.yaml"
apply_manifest "infrastructure/redis-deployment.yaml"
apply_manifest "infrastructure/kafka-deployment.yaml"

echo "⏳ Step 6: Waiting for Infrastructure Services"
wait_for_deployment $NAMESPACE_INFRA "mysql-main"
wait_for_deployment $NAMESPACE_INFRA "mysql-pos"
wait_for_deployment $NAMESPACE_INFRA "redis"
wait_for_deployment $NAMESPACE_INFRA "zookeeper"
wait_for_deployment $NAMESPACE_INFRA "kafka"

echo "🚀 Step 7: Deploying Application Services"
apply_manifest "applications/main-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "main-server"

apply_manifest "applications/pos-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "pos-server"

echo "🌐 Step 8: Setting up Ingress"
apply_manifest "ingress/ingress.yaml"

echo "✅ Step 9: Deployment Summary"
echo "==========================================="
kubectl get pods -n $NAMESPACE_INFRA
echo "==========================================="
kubectl get pods -n $NAMESPACE_APP
echo "==========================================="
kubectl get services -n $NAMESPACE_INFRA
echo "==========================================="
kubectl get services -n $NAMESPACE_APP
echo "==========================================="
kubectl get ingress -n $NAMESPACE_APP

echo ""
echo "🎉 WebPOS MSA Deployment Completed!"
echo ""
echo "📋 Access Information:"
echo "- Main Server: http://webpos.local/main"
echo "- POS Server: http://webpos.local/pos"
echo "- Health Check: http://webpos.local/main/actuator/health"
echo ""
echo "⚠️  Note: Add '127.0.0.1 webpos.local' to your hosts file"
echo "Windows: C:\\Windows\\System32\\drivers\\etc\\hosts (as Administrator)"
echo ""
echo "🔧 Useful Commands:"
echo "- View logs: kubectl logs -f deployment/main-server -n webpos"
echo "- Scale up: kubectl scale deployment main-server --replicas=3 -n webpos"
echo "- Port forward: kubectl port-forward svc/main-server 8080:8080 -n webpos"