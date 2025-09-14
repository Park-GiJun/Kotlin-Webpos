#!/bin/bash

# Docker Desktop Kubernetes - Infrastructure Only Deployment
# Usage: ./deploy-docker-desktop-infra.sh

set -e

NAMESPACE_INFRA="webpos-infra"

echo "🐳 Docker Desktop Kubernetes - Infrastructure Deployment"
echo "======================================================="

# Check if kubectl is available and pointing to docker-desktop
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

CURRENT_CONTEXT=$(kubectl config current-context)
if [ "$CURRENT_CONTEXT" != "docker-desktop" ]; then
    echo "⚠️  Current context is: $CURRENT_CONTEXT"
    echo "❌ Please switch to docker-desktop context:"
    echo "   kubectl config use-context docker-desktop"
    exit 1
fi

echo "✅ Using Docker Desktop Kubernetes"

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

echo "🏗️  Step 1: Creating Infrastructure Namespace"
kubectl create namespace $NAMESPACE_INFRA --dry-run=client -o yaml | kubectl apply -f -

echo "🔐 Step 2: Creating Infrastructure Secrets"
apply_manifest "secrets/mysql-secrets.yaml"

echo "⚙️  Step 3: Creating Infrastructure ConfigMaps"
apply_manifest "configmaps/mysql-config.yaml"

echo "💾 Step 4: Creating Storage (using default storage class)"
# Use default storage class for Docker Desktop
cat << EOF | kubectl apply -f -
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-main-pvc
  namespace: webpos-infra
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pos-pvc
  namespace: webpos-infra
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
EOF

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

echo "🌐 Step 7: Creating LoadBalancer Services for Docker Desktop"
cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Service
metadata:
  name: mysql-main-lb
  namespace: webpos-infra
spec:
  type: LoadBalancer
  selector:
    app: mysql-main
  ports:
    - port: 3306
      targetPort: 3306
---
apiVersion: v1
kind: Service
metadata:
  name: mysql-pos-lb
  namespace: webpos-infra
spec:
  type: LoadBalancer
  selector:
    app: mysql-pos
  ports:
    - port: 3307
      targetPort: 3306
---
apiVersion: v1
kind: Service
metadata:
  name: redis-lb
  namespace: webpos-infra
spec:
  type: LoadBalancer
  selector:
    app: redis
  ports:
    - port: 6379
      targetPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: kafka-lb
  namespace: webpos-infra
spec:
  type: LoadBalancer
  selector:
    app: kafka
  ports:
    - port: 9092
      targetPort: 9092
EOF

echo "✅ Infrastructure Deployment Summary"
echo "====================================="
kubectl get pods -n $NAMESPACE_INFRA
echo "====================================="
kubectl get services -n $NAMESPACE_INFRA

echo ""
echo "🎉 Infrastructure Deployment Completed!"
echo ""
echo "📋 Access Information:"
echo "- MySQL (main): localhost:3306 (user: mainuser, password: mainpassword)"
echo "- MySQL (pos): localhost:3307 (user: posuser, password: pospassword)"
echo "- Redis: localhost:6379"
echo "- Kafka: localhost:9092"
echo ""
echo "🔧 Connection Commands:"
echo "- MySQL Main: mysql -h localhost -P 3306 -u mainuser -p"
echo "- MySQL POS: mysql -h localhost -P 3307 -u posuser -p"
echo "- Redis CLI: redis-cli -h localhost -p 6379"
echo ""
echo "🐳 Docker Desktop Dashboard:"
echo "- Open Docker Desktop → Containers/Apps → kubernetes"
echo "- Monitor resource usage and logs"
echo ""
echo "⚡ Next Steps:"
echo "1. Test connections to verify services are working"
echo "2. Run your Spring Boot applications locally connecting to these services"
echo "3. Use IntelliJ/VS Code to develop against this infrastructure"
echo ""
echo "🧹 To cleanup: kubectl delete namespace webpos-infra"