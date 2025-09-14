# Docker Desktop Kubernetes 설정 가이드

Docker Desktop의 Kubernetes에 WebPOS MSA를 배포하는 방법입니다.

## 📋 사전 준비

### 1. Docker Desktop 설정
1. Docker Desktop 실행
2. Settings → Kubernetes → Enable Kubernetes 체크
3. Apply & Restart

### 2. 설정 확인
```bash
# kubectl 설치 확인
kubectl version --client

# Kubernetes 클러스터 연결 확인
kubectl cluster-info

# 컨텍스트 확인 (docker-desktop이어야 함)
kubectl config current-context
```

## 🚀 배포 방법

### Option 1: 인프라만 배포 (권장)
```bash
cd k8s
./deploy-docker-desktop-infra.sh
```

### Option 2: 전체 배포
```bash
cd k8s
./deploy-docker-desktop.sh
```

## 🔗 접근 방법

배포 후 다음과 같이 접근할 수 있습니다:

- **MySQL (main)**: localhost:3306
- **MySQL (pos)**: localhost:3307  
- **Redis**: localhost:6379
- **Kafka**: localhost:9092
- **Main Server**: localhost:8080 (애플리케이션 배포시)
- **POS Server**: localhost:8081 (애플리케이션 배포시)

## 💡 장단점

### 장점
✅ 로컬 개발 환경으로 완벽  
✅ 포트 포워딩으로 직접 접근 가능  
✅ Docker Desktop GUI로 쉬운 모니터링  
✅ Windows/Mac에서 즉시 사용 가능

### 단점
❌ 리소스 사용량이 높음  
❌ 개발용으로만 적합 (프로덕션 부적합)  
❌ Docker Desktop 의존성

## 📊 리소스 모니터링

Docker Desktop에서 리소스 사용량을 모니터링할 수 있습니다:
- Docker Desktop → Dashboard
- Kubernetes Dashboard (별도 설치 필요)