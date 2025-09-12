# CQRS 설정 가이드

## 1. 인프라 시작

### 기본 인프라 시작

```bash
cd docker
docker-compose -f docker-compose-cqrs.yml up -d
```

### 컨테이너 상태 확인

```bash
docker ps
```

## 2. MySQL 복제 설정

### Windows 환경

```bash
setup-mysql-replication.bat
```

### Linux/Mac 환경

```bash
chmod +x setup-mysql-replication.sh
./setup-mysql-replication.sh
```

### 수동 복제 설정 (Windows에서 권장)

1. **Master 설정**

```sql
-- Master에 접속
docker exec -it mysql-master mysql -uroot -proot

-- 복제 사용자 생성
CREATE USER 'replication'@'%' IDENTIFIED BY 'replication_password';
GRANT REPLICATION SLAVE ON *.* TO 'replication'@'%';

-- 읽기 전용 사용자 생성 (Slave용)
CREATE USER 'readonly_user'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON main_server.* TO 'readonly_user'@'%';
FLUSH PRIVILEGES;

-- Master 상태 확인
SHOW MASTER STATUS;
-- File과 Position 값을 기록하세요!
```

2. **Slave 설정**

```sql
-- Slave에 접속
docker exec -it mysql-slave mysql -uroot -proot

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS main_server;

-- Master 연결 설정 (위에서 확인한 File과 Position 값 사용)
CHANGE MASTER TO 
  MASTER_HOST='mysql-master',
  MASTER_USER='replication',
  MASTER_PASSWORD='replication_password',
  MASTER_LOG_FILE='mysql-bin.000001',  -- 실제 값으로 변경
  MASTER_LOG_POS=156;                  -- 실제 값으로 변경

-- 복제 시작
START SLAVE;

-- 복제 상태 확인
SHOW SLAVE STATUS\G;
-- Slave_IO_Running과 Slave_SQL_Running이 모두 Yes인지 확인

-- 읽기 전용 사용자 생성
CREATE USER 'readonly_user'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON main_server.* TO 'readonly_user'@'%';
FLUSH PRIVILEGES;
```

## 3. 애플리케이션 실행

### 완전한 CQRS (Master/Slave 분리)

```bash
./gradlew bootRun --args='--spring.profiles.active=cqrs'
```

### 간단한 CQRS (단일 DB + Redis 캐싱)

```bash
./gradlew bootRun --args='--spring.profiles.active=cqrs-simple'
```

## 4. 동작 확인

### 트랜잭션 로그 확인

애플리케이션 실행 시 다음 로그를 확인하여 올바른 DB로 라우팅되는지 확인:

```
# Write 작업 (Command)
WriteHikariPool - Connection acquired

# Read 작업 (Query)  
ReadHikariPool - Connection acquired
```

### 테스트 API 호출

```bash
# HQ 생성 (Write - Master DB)
curl -X POST http://localhost:8080/main/organization/hq \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test HQ",
    "representation": "Test Rep",
    "street": "Test Street",
    "city": "Test City",
    "zipCode": "12345",
    "email": "test@example.com",
    "phoneNumber": "010-1234-5678"
  }'

# HQ 조회 (Read - Slave DB, Redis 캐싱)
curl -X GET http://localhost:8080/main/organization/hq/1
```

## 5. 모니터링

### Redis 캐시 확인

```bash
docker exec -it redis redis-cli
127.0.0.1:6379> KEYS *
127.0.0.1:6379> TTL "cache_key"
```

### MySQL 복제 상태 확인

```bash
# Master 상태
docker exec mysql-master mysql -uroot -proot -e "SHOW MASTER STATUS;"

# Slave 상태
docker exec mysql-slave mysql -uroot -proot -e "SHOW SLAVE STATUS\G;"
```

## 6. 트러블슈팅

### 복제 에러 해결

```sql
-- Slave에서 복제 재시작
STOP SLAVE;
RESET SLAVE;
-- Master 설정 다시 실행
START SLAVE;
```

### 연결 문제 해결

- Master: localhost:3306
- Slave: localhost:3307
- 방화벽 및 Docker 네트워크 확인

## 7. 성능 이점

### 읽기/쓰기 분리

- **Write**: Master DB (일관성 보장)
- **Read**: Slave DB + Redis 캐시 (성능 최적화)

### 캐시 전략

- HQ: 10분 캐시
- Product: 30분 캐시 (변경 빈도 낮음)
- List 조회: 5분 캐시