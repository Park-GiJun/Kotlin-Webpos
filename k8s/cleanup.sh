#!/bin/bash

# Kubernetes Cleanup Script for WebPOS MSA
# Usage: ./cleanup.sh

set -e

NAMESPACE_INFRA="webpos-infra"
NAMESPACE_APP="webpos"

echo "🧹 Starting WebPOS Kubernetes Cleanup"

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

echo "🚫 Step 1: Deleting Application Services"
safe_delete "ingress webpos-ingress -n $NAMESPACE_APP"
safe_delete "deployment main-server -n $NAMESPACE_APP"
safe_delete "deployment pos-server -n $NAMESPACE_APP"
safe_delete "service main-server -n $NAMESPACE_APP"
safe_delete "service pos-server -n $NAMESPACE_APP"

echo "🚫 Step 2: Deleting Infrastructure Services"
safe_delete "deployment mysql-main -n $NAMESPACE_INFRA"
safe_delete "deployment mysql-pos -n $NAMESPACE_INFRA"
safe_delete "deployment redis -n $NAMESPACE_INFRA"
safe_delete "deployment kafka -n $NAMESPACE_INFRA"
safe_delete "deployment zookeeper -n $NAMESPACE_INFRA"
safe_delete "service mysql-main -n $NAMESPACE_INFRA"
safe_delete "service mysql-pos -n $NAMESPACE_INFRA"
safe_delete "service redis -n $NAMESPACE_INFRA"
safe_delete "service kafka -n $NAMESPACE_INFRA"
safe_delete "service zookeeper -n $NAMESPACE_INFRA"

echo "🚫 Step 3: Deleting ConfigMaps and Secrets"
safe_delete "configmap mysql-main-config -n $NAMESPACE_INFRA"
safe_delete "configmap mysql-pos-config -n $NAMESPACE_INFRA"
safe_delete "configmap main-server-config -n $NAMESPACE_APP"
safe_delete "configmap pos-server-config -n $NAMESPACE_APP"
safe_delete "secret mysql-main-secret -n $NAMESPACE_INFRA"
safe_delete "secret mysql-pos-secret -n $NAMESPACE_INFRA"
safe_delete "secret main-server-secret -n $NAMESPACE_APP"
safe_delete "secret pos-server-secret -n $NAMESPACE_APP"

echo "🚫 Step 4: Deleting Persistent Storage"
safe_delete "pvc mysql-main-pvc -n $NAMESPACE_INFRA"
safe_delete "pvc mysql-pos-pvc -n $NAMESPACE_INFRA"
safe_delete "pv mysql-main-pv"
safe_delete "pv mysql-pos-pv"

echo "🚫 Step 5: Deleting Namespaces"
safe_delete "namespace $NAMESPACE_INFRA"
safe_delete "namespace $NAMESPACE_APP"

echo ""
echo "✅ Cleanup completed!"
echo ""
echo "⚠️  Note: Persistent Volumes may need manual cleanup depending on storage class"
echo "🔍 Check remaining resources: kubectl get all --all-namespaces | grep webpos"