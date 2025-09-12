#!/bin/bash

# CentOS Server Kubernetes Setup Script for WebPOS
# Usage: ./centos-setup.sh

set -e

echo "🐧 CentOS Kubernetes Setup for WebPOS"
echo "======================================"

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   echo "❌ This script should not be run as root for safety reasons"
   echo "Please run as a regular user with sudo privileges"
   exit 1
fi

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Update system
echo "📦 Updating system packages..."
sudo yum update -y

# Install required packages
echo "📦 Installing required packages..."
sudo yum install -y yum-utils device-mapper-persistent-data lvm2 curl wget git

# Install Docker
if ! command_exists docker; then
    echo "🐳 Installing Docker..."
    sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    
    # Start and enable Docker
    sudo systemctl start docker
    sudo systemctl enable docker
    
    # Add current user to docker group
    sudo usermod -aG docker $USER
    
    echo "✅ Docker installed successfully"
    echo "⚠️  Please log out and log back in for docker group changes to take effect"
else
    echo "✅ Docker already installed"
fi

# Install kubectl
if ! command_exists kubectl; then
    echo "⚙️  Installing kubectl..."
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
    sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
    rm kubectl
    echo "✅ kubectl installed successfully"
else
    echo "✅ kubectl already installed"
fi

# Install k3s (lightweight Kubernetes)
if ! command_exists k3s; then
    echo "🚢 Installing k3s..."
    curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644
    
    # Set up kubeconfig
    mkdir -p ~/.kube
    sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
    sudo chown $USER:$USER ~/.kube/config
    
    echo "✅ k3s installed successfully"
else
    echo "✅ k3s already installed"
fi

# Configure firewall
echo "🔥 Configuring firewall..."
sudo systemctl start firewalld
sudo systemctl enable firewalld

# Open required ports
sudo firewall-cmd --permanent --add-port=6443/tcp  # k3s API server
sudo firewall-cmd --permanent --add-port=80/tcp    # HTTP
sudo firewall-cmd --permanent --add-port=443/tcp   # HTTPS
sudo firewall-cmd --permanent --add-port=8080/tcp  # Main server
sudo firewall-cmd --permanent --add-port=8081/tcp  # POS server
sudo firewall-cmd --permanent --add-port=3306/tcp  # MySQL
sudo firewall-cmd --permanent --add-port=6379/tcp  # Redis
sudo firewall-cmd --permanent --add-port=9092/tcp  # Kafka

sudo firewall-cmd --reload

echo "✅ Firewall configured"

# Disable SELinux (for k3s compatibility)
echo "🔒 Configuring SELinux..."
sudo setenforce 0
sudo sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config

# Check cluster status
echo "🔍 Checking cluster status..."
sleep 10  # Wait for k3s to start
kubectl cluster-info

echo ""
echo "🎉 CentOS Kubernetes setup completed!"
echo ""
echo "📋 Setup Summary:"
echo "- Docker: $(docker --version)"
echo "- kubectl: $(kubectl version --client --short 2>/dev/null || echo 'kubectl installed')"
echo "- k3s: $(k3s --version | head -1)"
echo ""
echo "🔧 Next Steps:"
echo "1. Log out and log back in (for docker group)"
echo "2. Verify cluster: kubectl get nodes"
echo "3. Build and deploy WebPOS:"
echo "   cd /path/to/Kotlin-Webpos/k8s"
echo "   ./build-images-centos.sh"
echo "   ./deploy-centos.sh"
echo ""
echo "🌐 Access URLs (replace SERVER_IP with your CentOS server IP):"
echo "- Main Server: http://SERVER_IP:8080/main"
echo "- POS Server: http://SERVER_IP:8081/pos"
echo ""
echo "⚠️  Important Notes:"
echo "- Make sure to update SERVER_IP in deployment configs"
echo "- Monitor resource usage: kubectl top nodes"
echo "- Check logs: kubectl logs -f deployment/main-server -n webpos"