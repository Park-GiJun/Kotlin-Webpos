#!/bin/bash

# CentOS Kubernetes Deployment Script for WebPOS MSA
# Usage: ./deploy-centos.sh [server_ip]
# Example: ./deploy-centos.sh 192.168.1.100

set -e

SERVER_IP=${1}
NAMESPACE_INFRA="webpos-infra"
NAMESPACE_APP="webpos"
REGISTRY="webpos"

echo "🚀 Starting WebPOS Kubernetes Deployment on CentOS"

# Get server IP if not provided
if [ -z "$SERVER_IP" ]; then
    echo "🔍 Auto-detecting server IP..."
    SERVER_IP=$(hostname -I | awk '{print $1}')
    echo "📍 Detected IP: $SERVER_IP"
    
    read -p "✅ Is this IP correct? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        read -p "🔧 Please enter your server IP: " SERVER_IP
    fi
fi

echo "📍 Using Server IP: $SERVER_IP"

# Check if kubectl is available and cluster is accessible
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please run centos-setup.sh first."
    exit 1
fi

if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Cannot access Kubernetes cluster. Please check if k3s is running:"
    echo "   sudo systemctl status k3s"
    exit 1
fi

echo "✅ Kubernetes cluster is accessible"

# Function to apply manifests with substitution
apply_manifest_with_ip() {
    local file=$1
    local temp_file="/tmp/$(basename $file)"
    
    echo "📋 Applying $file (IP: $SERVER_IP)"
    
    # Replace placeholder with actual IP
    sed "s/SERVER_IP_PLACEHOLDER/$SERVER_IP/g" "$file" > "$temp_file"
    kubectl apply -f "$temp_file"
    rm "$temp_file"
}

# Function to apply manifests normally
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
    kubectl rollout status deployment/$deployment -n $namespace --timeout=600s
}

# Check if images exist in local docker or k3s
check_images() {
    echo "🔍 Checking for required images..."
    
    local missing_images=false
    
    if ! docker images | grep -q "$REGISTRY/main-server"; then
        echo "❌ main-server image not found in docker"
        missing_images=true
    fi
    
    if ! docker images | grep -q "$REGISTRY/pos-server"; then
        echo "❌ pos-server image not found in docker"
        missing_images=true
    fi
    
    if [ "$missing_images" = true ]; then
        echo "🏗️  Building missing images..."
        ./build-images-centos.sh latest
    else
        echo "✅ All required images found"
    fi
}

echo "🏗️  Step 1: Checking Images"
check_images

echo "🏗️  Step 2: Creating Namespaces"
apply_manifest "namespace.yaml"

echo "🔐 Step 3: Creating Secrets"
apply_manifest "secrets/mysql-secrets.yaml"
apply_manifest "secrets/app-secrets.yaml"

echo "⚙️  Step 4: Creating ConfigMaps"
apply_manifest "configmaps/mysql-config.yaml"
apply_manifest "configmaps/app-config.yaml"

echo "💾 Step 5: Creating Storage"
apply_manifest "storage/mysql-storage.yaml"

echo "🗄️  Step 6: Deploying Infrastructure Services"
apply_manifest "infrastructure/mysql-deployment.yaml"
apply_manifest "infrastructure/redis-deployment.yaml"
apply_manifest "infrastructure/kafka-deployment.yaml"

echo "⏳ Step 7: Waiting for Infrastructure Services"
wait_for_deployment $NAMESPACE_INFRA "mysql-main"
wait_for_deployment $NAMESPACE_INFRA "mysql-pos"
wait_for_deployment $NAMESPACE_INFRA "redis"
wait_for_deployment $NAMESPACE_INFRA "zookeeper"
wait_for_deployment $NAMESPACE_INFRA "kafka"

echo "🚀 Step 8: Deploying Application Services"
apply_manifest "applications/main-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "main-server"

apply_manifest "applications/pos-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "pos-server"

echo "🌐 Step 9: Setting up Services (NodePort for CentOS)"

# Create NodePort services for external access
cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Service
metadata:
  name: main-server-nodeport
  namespace: webpos
spec:
  type: NodePort
  selector:
    app: main-server
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30080
---
apiVersion: v1
kind: Service
metadata:
  name: pos-server-nodeport
  namespace: webpos
spec:
  type: NodePort
  selector:
    app: pos-server
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30081
EOF

echo "✅ Step 10: Deployment Summary"
echo "==========================================="
kubectl get pods -n $NAMESPACE_INFRA
echo "==========================================="
kubectl get pods -n $NAMESPACE_APP
echo "==========================================="
kubectl get services -n $NAMESPACE_INFRA
echo "==========================================="
kubectl get services -n $NAMESPACE_APP

echo ""
echo "🎉 WebPOS MSA Deployment Completed on CentOS!"
echo ""
echo "📋 Access Information:"
echo "- Main Server: http://$SERVER_IP:30080/main"
echo "- POS Server: http://$SERVER_IP:30081/pos"
echo "- Health Check: http://$SERVER_IP:30080/actuator/health"
echo ""
echo "🔧 Useful Commands:"
echo "- View logs: kubectl logs -f deployment/main-server -n webpos"
echo "- Scale up: kubectl scale deployment main-server --replicas=3 -n webpos"
echo "- Check status: kubectl get pods -n webpos"
echo "- Port forward: kubectl port-forward svc/main-server 8080:8080 -n webpos"
echo ""
echo "📊 Monitor Resources:"
echo "- Node status: kubectl get nodes"
echo "- Resource usage: kubectl top nodes"
echo "- Pod status: kubectl top pods -n webpos"
echo ""
echo "🔥 Firewall Commands (if needed):"
echo "- sudo firewall-cmd --permanent --add-port=30080/tcp"
echo "- sudo firewall-cmd --permanent --add-port=30081/tcp"
echo "- sudo firewall-cmd --reload"