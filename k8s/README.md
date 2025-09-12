# Kubernetes Deployment for WebPOS MSA

이 가이드는 WebPOS 마이크로서비스 아키텍처를 Kubernetes 클러스터에 배포하는 방법을 설명합니다.

## 📋 목차

- [사전 요구사항](#사전-요구사항)
- [아키텍처 개요](#아키텍처-개요)
- [디렉토리 구조](#디렉토리-구조)
- [빠른 시작](#빠른-시작)
- [상세 배포 과정](#상세-배포-과정)
- [모니터링 및 관리](#모니터링-및-관리)
- [문제 해결](#문제-해결)

## 🔧 사전 요구사항

### 필수 도구
- [kubectl](https://kubernetes.io/docs/tasks/tools/) - Kubernetes 클러스터 관리
- [Docker](https://docs.docker.com/get-docker/) - 컨테이너 이미지 빌드
- Kubernetes 클러스터 (다음 중 하나):
  - [minikube](https://minikube.sigs.k8s.io/) (로컬 개발용)
  - [k3s](https://k3s.io/) (경량 Kubernetes)
  - AWS EKS, Google GKE, Azure AKS (프로덕션)

### 선택적 도구
- [Helm](https://helm.sh/) - Kubernetes 패키지 관리 (향후 지원 예정)
- [k9s](https://k9scli.io/) - 터미널 UI for Kubernetes

## 🏗️ 아키텍처 개요

```
┌─────────────────┐    ┌─────────────────┐
│   Ingress       │    │   Ingress       │
│   Controller    │    │   (webpos.local)│
└─────────────────┘    └─────────────────┘
          │                       │
┌─────────────────┐    ┌─────────────────┐
│  Main Server    │    │   POS Server    │
│  (webpos ns)    │    │  (webpos ns)    │
└─────────────────┘    └─────────────────┘
          │                       │
┌───────────────────────────────────────────┐
│         Infrastructure Services           │
│           (webpos-infra ns)               │
├─────────────────┬─────────────────┬───────┤
│     MySQL       │     Redis       │ Kafka │
│  (main + pos)   │   (Cache)       │(Event)│
└─────────────────┴─────────────────┴───────┘
```

## 📁 디렉토리 구조

```
k8s/
├── namespace.yaml              # 네임스페이스 정의
├── configmaps/                 # 설정 데이터
│   ├── mysql-config.yaml
│   └── app-config.yaml
├── secrets/                    # 민감한 데이터
│   ├── mysql-secrets.yaml
│   └── app-secrets.yaml
├── storage/                    # 영구 저장소
│   └── mysql-storage.yaml
├── infrastructure/             # 인프라 서비스
│   ├── mysql-deployment.yaml
│   ├── redis-deployment.yaml
│   └── kafka-deployment.yaml
├── applications/               # 애플리케이션 서비스
│   ├── main-server-deployment.yaml
│   └── pos-server-deployment.yaml
├── ingress/                    # 외부 접근 설정
│   └── ingress.yaml
├── deploy.sh                   # 자동 배포 스크립트
├── cleanup.sh                  # 정리 스크립트
├── build-images.sh             # 이미지 빌드 스크립트
└── README.md                   # 이 파일
```

## 🚀 빠른 시작

### 1. Docker 이미지 빌드

```bash
# 프로젝트 루트에서
cd k8s
./build-images.sh latest
```

### 2. Kubernetes 클러스터 시작 (minikube 사용 시)

```bash
# minikube 시작
minikube start --memory=4096 --cpus=2

# Nginx Ingress Controller 활성화
minikube addons enable ingress

# minikube IP 확인
minikube ip
```

### 3. 호스트 파일 설정

```bash
# Linux/macOS
echo "$(minikube ip) webpos.local" | sudo tee -a /etc/hosts

# Windows (관리자 권한 PowerShell)
Add-Content -Path C:\Windows\System32\drivers\etc\hosts -Value "$(minikube ip) webpos.local"
```

### 4. 배포 실행

```bash
./deploy.sh
```

### 5. 서비스 접근

- Main Server: http://webpos.local/main
- POS Server: http://webpos.local/pos
- Health Check: http://webpos.local/main/actuator/health

## 🔍 상세 배포 과정

### 단계별 배포

1. **네임스페이스 생성**
   ```bash
   kubectl apply -f namespace.yaml
   ```

2. **시크릿 및 설정 맵**
   ```bash
   kubectl apply -f secrets/
   kubectl apply -f configmaps/
   ```

3. **스토리지**
   ```bash
   kubectl apply -f storage/
   ```

4. **인프라스트럭처**
   ```bash
   kubectl apply -f infrastructure/
   ```

5. **애플리케이션**
   ```bash
   kubectl apply -f applications/
   ```

6. **Ingress**
   ```bash
   kubectl apply -f ingress/
   ```

### 배포 확인

```bash
# Pod 상태 확인
kubectl get pods -n webpos
kubectl get pods -n webpos-infra

# 서비스 확인
kubectl get services -n webpos
kubectl get services -n webpos-infra

# Ingress 확인
kubectl get ingress -n webpos
```

## 📊 모니터링 및 관리

### 로그 확인

```bash
# 특정 서비스 로그
kubectl logs -f deployment/main-server -n webpos
kubectl logs -f deployment/pos-server -n webpos

# 모든 Pod 로그
kubectl logs -f -l app=main-server -n webpos
```

### 스케일링

```bash
# 복제본 수 조정
kubectl scale deployment main-server --replicas=3 -n webpos
kubectl scale deployment pos-server --replicas=2 -n webpos
```

### 포트 포워딩 (로컬 테스트용)

```bash
# Main Server 포트 포워딩
kubectl port-forward svc/main-server 8080:8080 -n webpos

# POS Server 포트 포워딩
kubectl port-forward svc/pos-server 8081:8080 -n webpos

# MySQL 직접 접근
kubectl port-forward svc/mysql-main 3306:3306 -n webpos-infra
```

### 업데이트 및 롤백

```bash
# 새로운 이미지로 업데이트
kubectl set image deployment/main-server main-server=webpos/main-server:v2.0 -n webpos

# 롤백
kubectl rollout undo deployment/main-server -n webpos

# 롤아웃 히스토리 확인
kubectl rollout history deployment/main-server -n webpos
```

## 🛠️ 문제 해결

### 일반적인 문제들

1. **Pod가 Pending 상태**
   ```bash
   kubectl describe pod <pod-name> -n <namespace>
   # 리소스 부족 또는 스케줄링 문제 확인
   ```

2. **서비스 연결 실패**
   ```bash
   # 서비스 엔드포인트 확인
   kubectl get endpoints -n webpos
   kubectl get endpoints -n webpos-infra
   ```

3. **이미지 Pull 실패**
   ```bash
   # 로컬 이미지 사용 설정 (minikube)
   eval $(minikube docker-env)
   # 이미지 다시 빌드
   ./build-images.sh
   ```

4. **Ingress 접근 불가**
   ```bash
   # Ingress Controller 상태 확인
   kubectl get pods -n ingress-nginx
   
   # Ingress 규칙 확인
   kubectl describe ingress webpos-ingress -n webpos
   ```

### 디버깅 명령어

```bash
# 클러스터 전체 상태
kubectl cluster-info
kubectl get nodes

# 네임스페이스별 리소스
kubectl get all -n webpos
kubectl get all -n webpos-infra

# 이벤트 확인
kubectl get events -n webpos --sort-by='.lastTimestamp'
kubectl get events -n webpos-infra --sort-by='.lastTimestamp'

# 리소스 사용량
kubectl top nodes
kubectl top pods -n webpos
```

### 정리

```bash
# 전체 정리
./cleanup.sh

# 특정 리소스만 정리
kubectl delete namespace webpos
kubectl delete namespace webpos-infra
```

## 🔧 환경별 설정

### 개발 환경
- 복제본 수: 1-2
- 리소스 제한: 낮음
- 로깅 레벨: DEBUG

### 스테이징 환경
- 복제본 수: 2-3  
- 리소스 제한: 중간
- 로깅 레벨: INFO

### 프로덕션 환경
- 복제본 수: 3+
- 리소스 제한: 높음
- 로깅 레벨: WARN/ERROR
- 고가용성 설정

## 📚 참고 자료

- [Kubernetes 공식 문서](https://kubernetes.io/docs/)
- [Spring Boot Kubernetes 가이드](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [Minikube 시작하기](https://minikube.sigs.k8s.io/docs/start/)

## 🤝 기여

버그 리포트나 기능 요청은 GitHub Issues를 통해 제출해주세요.