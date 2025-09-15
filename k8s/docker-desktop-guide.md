# Docker Desktop에서 WebPOS 실행하기

## 🐳 Docker Desktop 설정

### 1. Kubernetes 활성화
1. Docker Desktop 우측 하단 트레이 아이콘 클릭
2. **Settings** → **Kubernetes**
3. ✅ **Enable Kubernetes** 체크
4. **Apply & Restart** 클릭

### 2. 리소스 설정 (권장)
- **Memory**: 4GB 이상
- **CPUs**: 2개 이상
- **Disk**: 10GB 이상 여유 공간

## 🚀 빠른 시작

### 1. 이미지 빌드
```bash
cd k8s
./build-images.sh latest
```

### 2. 호스트 파일 설정
Windows에서 **관리자 권한** PowerShell로:
```powershell
Add-Content -Path C:\Windows\System32\drivers\etc\hosts -Value "127.0.0.1 webpos.local"
```

또는 수동으로 `C:\Windows\System32\drivers\etc\hosts` 파일에 추가:
```
127.0.0.1 webpos.local
```

### 3. 배포 실행
```bash
./deploy.sh
```

### 4. 서비스 접근
- **Main Server**: http://webpos.local/main
- **POS Server**: http://webpos.local/pos
- **Health Check**: http://webpos.local/main/actuator/health

## 🔍 상태 확인

### Pod 상태
```bash
kubectl get pods -n webpos
kubectl get pods -n webpos-infra
```

### 서비스 상태
```bash
kubectl get services -n webpos
kubectl get services -n webpos-infra
```

### Ingress 상태
```bash
kubectl get ingress -n webpos
```

## 🛠️ 유용한 명령어

### 로그 확인
```bash
# 실시간 로그
kubectl logs -f deployment/main-server -n webpos
kubectl logs -f deployment/pos-server -n webpos

# 인프라 로그
kubectl logs -f deployment/mysql-main -n webpos-infra
kubectl logs -f deployment/redis -n webpos-infra
```

### 포트 포워딩 (직접 접근)
```bash
# Main Server
kubectl port-forward svc/main-server 8080:8080 -n webpos

# POS Server
kubectl port-forward svc/pos-server 8081:8080 -n webpos

# MySQL 직접 접근
kubectl port-forward svc/mysql-main 3306:3306 -n webpos-infra
```

### Pod 내부 접근
```bash
# Pod 내부 쉘 실행
kubectl exec -it deployment/main-server -n webpos -- /bin/bash

# MySQL 접속
kubectl exec -it deployment/mysql-main -n webpos-infra -- mysql -u mainuser -p
```

## 🔧 문제 해결

### 1. Pod가 Pending 상태
```bash
kubectl describe pod <pod-name> -n <namespace>
# 리소스 부족 확인
```

### 2. 이미지 Pull 실패
Docker Desktop에서 이미지가 로컬에 빌드되었는지 확인:
```bash
docker images | grep webpos
```

### 3. Ingress 접근 불가
```bash
# NGINX Ingress Controller 설치 (필요한 경우)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
```

### 4. 데이터베이스 연결 실패
```bash
# MySQL Pod 로그 확인
kubectl logs -f deployment/mysql-main -n webpos-infra

# 네트워크 확인
kubectl get endpoints -n webpos-infra
```

## 🧹 정리

### 전체 정리
```bash
./cleanup.sh
```

### 개별 정리
```bash
kubectl delete namespace webpos
kubectl delete namespace webpos-infra
```

## 💡 팁

- Docker Desktop에서 Kubernetes를 비활성화하면 모든 Pod가 종료됩니다
- Windows 재시작 후에는 Pod들이 자동으로 다시 시작됩니다
- 메모리 부족시 Docker Desktop 메모리 할당량을 늘리세요
- localhost 대신 webpos.local을 사용하여 Ingress를 테스트하세요