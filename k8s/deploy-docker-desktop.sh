#!/bin/bash

# Docker Desktop Kubernetes - Full Deployment (Infrastructure + Applications)
# Usage: ./deploy-docker-desktop.sh

set -e

NAMESPACE_INFRA="webpos-infra"
NAMESPACE_APP="webpos"

echo "🐳 Docker Desktop Kubernetes - Full WebPOS Deployment"
echo "===================================================="

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

echo "🏗️  Step 1: Creating Namespaces"
kubectl create namespace $NAMESPACE_INFRA --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace $NAMESPACE_APP --dry-run=client -o yaml | kubectl apply -f -

echo "🔐 Step 2: Creating Secrets"
apply_manifest "secrets/mysql-secrets.yaml"
apply_manifest "secrets/app-secrets.yaml"

echo "⚙️  Step 3: Creating ConfigMaps"
apply_manifest "configmaps/mysql-config.yaml"

# Create Docker Desktop specific app config
cat << EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: main-server-config
  namespace: webpos
data:
  SPRING_PROFILES_ACTIVE: "docker"
  SPRING_DATASOURCE_URL: "jdbc:mysql://mysql-main.webpos-infra:3306/main_server"
  SPRING_DATASOURCE_USERNAME: "mainuser"
  SPRING_REDIS_HOST: "redis.webpos-infra"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka.webpos-infra:9092"
  SERVER_PORT: "8080"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: pos-server-config
  namespace: webpos
data:
  SPRING_PROFILES_ACTIVE: "docker"
  SPRING_DATASOURCE_URL: "jdbc:mysql://mysql-pos.webpos-infra:3306/pos_server"
  SPRING_DATASOURCE_USERNAME: "posuser"
  SPRING_REDIS_HOST: "redis.webpos-infra"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka.webpos-infra:9092"
  MAIN_SERVER_URL: "http://main-server.webpos:8080"
  SERVER_PORT: "8080"
EOF

echo "💾 Step 4: Creating Storage (using default storage class)"
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

echo "🚀 Step 7: Deploying Application Services"
apply_manifest "applications/main-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "main-server"

apply_manifest "applications/pos-server-deployment.yaml"
wait_for_deployment $NAMESPACE_APP "pos-server"

echo "🌐 Step 8: Creating LoadBalancer Services"
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
---
apiVersion: v1
kind: Service
metadata:
  name: main-server-lb
  namespace: webpos
spec:
  type: LoadBalancer
  selector:
    app: main-server
  ports:
    - port: 8080
      targetPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: pos-server-lb
  namespace: webpos
spec:
  type: LoadBalancer
  selector:
    app: pos-server
  ports:
    - port: 8081
      targetPort: 8080
EOF

echo "✅ Full Deployment Summary"
echo "=========================="
kubectl get pods -n $NAMESPACE_INFRA
echo "=========================="
kubectl get pods -n $NAMESPACE_APP
echo "=========================="
kubectl get services -n $NAMESPACE_INFRA
echo "=========================="
kubectl get services -n $NAMESPACE_APP

echo ""
echo "🎉 Full WebPOS Deployment Completed!"
echo ""
echo "📋 Access Information:"
echo "Infrastructure:"
echo "- MySQL (main): localhost:3306 (user: mainuser, password: mainpassword)"
echo "- MySQL (pos): localhost:3307 (user: posuser, password: pospassword)"
echo "- Redis: localhost:6379"
echo "- Kafka: localhost:9092"
echo ""
echo "Applications:"
echo "- Main Server: http://localhost:8080/main"
echo "- POS Server: http://localhost:8081/pos"
echo "- Health Check: http://localhost:8080/actuator/health"
echo ""
echo "🐳 Docker Desktop Dashboard:"
echo "- Open Docker Desktop → Containers/Apps → kubernetes"
echo "- Monitor resource usage and logs in real-time"
echo ""
echo "🔧 Useful Commands:"
echo "- View logs: kubectl logs -f deployment/main-server -n webpos"
echo "- Scale up: kubectl scale deployment main-server --replicas=3 -n webpos"
echo "- Port forward: kubectl port-forward svc/main-server 8080:8080 -n webpos"
echo ""
echo "🧹 To cleanup:"
echo "  kubectl delete namespace webpos"
echo "  kubectl delete namespace webpos-infra"