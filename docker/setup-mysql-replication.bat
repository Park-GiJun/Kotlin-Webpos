@echo off
REM MySQL Master-Slave Replication Setup Script for Windows

echo Setting up MySQL Master-Slave Replication...

REM Wait for both databases to be ready
echo Waiting for MySQL Master and Slave to be ready...
timeout /t 10 /nobreak > nul

REM Configure Master
echo Configuring Master...
docker exec -it mysql-master mysql -uroot -proot -e "CREATE USER IF NOT EXISTS 'replication'@'%%' IDENTIFIED BY 'replication_password'; GRANT REPLICATION SLAVE ON *.* TO 'replication'@'%%'; CREATE USER IF NOT EXISTS 'readonly_user'@'%%' IDENTIFIED BY 'readonly_password'; GRANT SELECT ON main_server.* TO 'readonly_user'@'%%'; FLUSH PRIVILEGES; FLUSH TABLES WITH READ LOCK;"

REM Get master status - simplified for Windows batch
docker exec mysql-master mysql -uroot -proot -e "SHOW MASTER STATUS;" > master_status.tmp

REM Create initial database on slave if not exists
echo Creating database on slave...
docker exec mysql-slave mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS main_server;"

REM Configure Slave (using approximate log position - adjust as needed)
echo Configuring Slave...
docker exec mysql-slave mysql -uroot -proot -e "STOP SLAVE; CHANGE MASTER TO MASTER_HOST='mysql-master', MASTER_USER='replication', MASTER_PASSWORD='replication_password', MASTER_LOG_FILE='mysql-bin.000001', MASTER_LOG_POS=1; START SLAVE;"

REM Unlock tables on master
docker exec mysql-master mysql -uroot -proot -e "UNLOCK TABLES;"

REM Create readonly user on slave
docker exec mysql-slave mysql -uroot -proot -e "CREATE USER IF NOT EXISTS 'readonly_user'@'%%' IDENTIFIED BY 'readonly_password'; GRANT SELECT ON main_server.* TO 'readonly_user'@'%%'; FLUSH PRIVILEGES;"

REM Check slave status
echo Checking slave status...
docker exec mysql-slave mysql -uroot -proot -e "SHOW SLAVE STATUS\G"

echo MySQL Master-Slave replication setup completed!
echo Master: localhost:3306 (mainuser/mainpassword)
echo Slave:  localhost:3307 (readonly_user/readonly_password)

REM Clean up
if exist master_status.tmp del master_status.tmp

pause