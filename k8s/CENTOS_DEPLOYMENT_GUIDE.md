# CentOS Server에서 WebPOS Kubernetes 배포 가이드

이 가이드는 CentOS 서버에서 WebPOS MSA를 Kubernetes(k3s)로 배포하는 방법을 설명합니다.

## 📋 사전 준비사항

### 시스템 요구사항
- **OS**: CentOS 7/8/9 또는 RHEL 호환
- **RAM**: 최소 4GB (권장 8GB+)
- **CPU**: 최소 2 cores (권장 4 cores+)
- **Storage**: 최소 50GB 여유 공간
- **Network**: 인터넷 연결

### 사용자 권한
- sudo 권한을 가진 일반 사용자 (root 직접 사용 비권장)

## 🚀 1단계: CentOS 서버 초기 설정

### 서버에 파일 업로드
```bash
# 로컬에서 CentOS 서버로 파일 전송
scp -r Kotlin-Webpos/ username@your-server-ip:/home/username/
```

### 서버 접속
```bash
ssh username@your-server-ip
cd /home/username/Kotlin-Webpos/k8s
```

## 🔧 2단계: 환경 설정

### 스크립트 실행 권한 부여
```bash
chmod +x *.sh
```

### 시스템 초기 설정 실행
```bash
./centos-setup.sh
```

이 스크립트는 다음을 수행합니다:
- 시스템 패키지 업데이트
- Docker 설치 및 설정
- kubectl 설치
- k3s (경량 Kubernetes) 설치
- 방화벽 설정
- SELinux 설정

### 재로그인 필수
```bash
# Docker 그룹 변경사항 적용을 위해 재로그인
exit
ssh username@your-server-ip
cd /home/username/Kotlin-Webpos/k8s
```

## 🐳 3단계: Docker 이미지 빌드

```bash
# Docker 이미지 빌드
./build-images-centos.sh latest
```

이 과정에서:
- main-server와 pos-server의 Docker 이미지 생성
- 이미지를 k3s에서 사용 가능하도록 준비

## 🚢 4단계: Kubernetes 배포

```bash
# 서버 IP와 함께 배포 (자동 감지 또는 수동 입력)
./deploy-centos.sh

# 또는 서버 IP를 직접 지정
./deploy-centos.sh 192.168.1.100
```

배포 과정:
1. 네임스페이스 생성 (webpos, webpos-infra)
2. 시크릿 및 설정맵 생성
3. 영구 저장소 설정
4. 인프라 서비스 배포 (MySQL, Redis, Kafka)
5. 애플리케이션 서비스 배포
6. NodePort 서비스 생성 (외부 접근용)

## 🌐 5단계: 방화벽 설정 (필요시)

```bash
# 애플리케이션 포트 열기
sudo firewall-cmd --permanent --add-port=30080/tcp  # main-server
sudo firewall-cmd --permanent --add-port=30081/tcp  # pos-server
sudo firewall-cmd --reload

# 방화벽 상태 확인
sudo firewall-cmd --list-ports
```

## ✅ 6단계: 배포 확인

### 클러스터 상태 확인
```bash
# 노드 상태
kubectl get nodes

# Pod 상태 확인
kubectl get pods -n webpos
kubectl get pods -n webpos-infra

# 서비스 확인
kubectl get services -n webpos
```

### 애플리케이션 접근 테스트
```bash
# 서버 IP를 실제 IP로 변경
curl http://YOUR_SERVER_IP:30080/actuator/health
curl http://YOUR_SERVER_IP:30081/actuator/health
```

## 🔗 접근 URL

배포 완료 후 다음 URL로 접근 가능합니다:

- **Main Server**: `http://YOUR_SERVER_IP:30080/main`
- **POS Server**: `http://YOUR_SERVER_IP:30081/pos`
- **Health Check**: `http://YOUR_SERVER_IP:30080/actuator/health`

## 📊 모니터링

### 로그 확인
```bash
# 애플리케이션 로그
kubectl logs -f deployment/main-server -n webpos
kubectl logs -f deployment/pos-server -n webpos

# 인프라 로그
kubectl logs -f deployment/mysql-main -n webpos-infra
kubectl logs -f deployment/redis -n webpos-infra
kubectl logs -f deployment/kafka -n webpos-infra
```

### 리소스 사용량 확인
```bash
# 노드 리소스
kubectl top nodes

# Pod 리소스
kubectl top pods -n webpos
kubectl top pods -n webpos-infra

# 전체 상태
kubectl get all -n webpos
```

### 스케일링
```bash
# 복제본 수 조정
kubectl scale deployment main-server --replicas=3 -n webpos
kubectl scale deployment pos-server --replicas=2 -n webpos
```

## 🛠️ 문제 해결

### 일반적인 문제들

1. **Pod가 Pending 상태**
   ```bash
   kubectl describe pod <pod-name> -n <namespace>
   # 리소스 부족 또는 이미지 pull 문제 확인
   ```

2. **이미지 Pull 실패**
   ```bash
   # 이미지가 로컬에 있는지 확인
   docker images | grep webpos
   
   # 이미지 재빌드
   ./build-images-centos.sh latest
   ```

3. **서비스 접근 불가**
   ```bash
   # 포트 확인
   netstat -tulpn | grep :30080
   netstat -tulpn | grep :30081
   
   # 방화벽 상태 확인
   sudo firewall-cmd --list-ports
   ```

4. **MySQL 연결 문제**
   ```bash
   # MySQL Pod 로그 확인
   kubectl logs -f deployment/mysql-main -n webpos-infra
   
   # MySQL 포트 포워딩으로 직접 접근 테스트
   kubectl port-forward svc/mysql-main 3306:3306 -n webpos-infra
   ```

### 클러스터 재시작
```bash
# k3s 재시작
sudo systemctl restart k3s

# 상태 확인
sudo systemctl status k3s
kubectl cluster-info
```

### 완전 정리
```bash
# 모든 리소스 삭제
./cleanup.sh

# k3s 완전 제거 (필요시)
sudo /usr/local/bin/k3s-uninstall.sh
```

## 🔧 설정 커스터마이징

### 리소스 제한 조정
애플리케이션의 리소스 요구사항에 따라 `applications/` 폴더의 YAML 파일에서 `resources` 섹션을 조정할 수 있습니다.

### 데이터베이스 설정
`configmaps/mysql-config.yaml`과 `secrets/mysql-secrets.yaml`에서 데이터베이스 설정을 변경할 수 있습니다.

### 네트워크 설정
`deploy-centos.sh`에서 NodePort 번호를 변경할 수 있습니다 (30080, 30081).

## 📚 참고 자료

- [k3s 공식 문서](https://k3s.io/)
- [Kubernetes 명령어 참조](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [CentOS 방화벽 설정](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/security_guide/sec-using_firewalls)

## 🎯 성능 최적화 팁

1. **메모리 사용량 모니터링**: `kubectl top pods -n webpos`
2. **로그 로테이션 설정**: Docker 로그 크기 제한
3. **불필요한 서비스 비활성화**: CentOS에서 사용하지 않는 서비스 중지
4. **스토리지 모니터링**: `/data/` 디렉토리 사용량 확인

이제 CentOS 서버에서 WebPOS MSA가 성공적으로 실행됩니다! 🎉