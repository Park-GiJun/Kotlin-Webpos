#!/bin/bash

# MySQL Master-Slave Replication Setup Script

echo "Setting up MySQL Master-Slave Replication..."

# Wait for both databases to be ready
echo "Waiting for MySQL Master and Slave to be ready..."
sleep 10

# Configure Master
echo "Configuring Master..."
docker exec -it mysql-master mysql -uroot -proot -e "
CREATE USER IF NOT EXISTS 'replication'@'%' IDENTIFIED BY 'replication_password';
GRANT REPLICATION SLAVE ON *.* TO 'replication'@'%';
CREATE USER IF NOT EXISTS 'readonly_user'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON main_server.* TO 'readonly_user'@'%';
FLUSH PRIVILEGES;
FLUSH TABLES WITH READ LOCK;
"

# Get master status
MASTER_STATUS=$(docker exec mysql-master mysql -uroot -proot -e "SHOW MASTER STATUS\G" | grep -E "File|Position")
MASTER_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
MASTER_POSITION=$(echo "$MASTER_STATUS" | grep "Position:" | awk '{print $2}')

echo "Master File: $MASTER_FILE"
echo "Master Position: $MASTER_POSITION"

# Create database dump
echo "Creating database dump..."
docker exec mysql-master mysqldump -uroot -proot --single-transaction --routines --triggers main_server > /tmp/main_server_dump.sql

# Import dump to slave
echo "Importing dump to slave..."
docker cp /tmp/main_server_dump.sql mysql-slave:/tmp/
docker exec mysql-slave mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS main_server;"
docker exec mysql-slave mysql -uroot -proot main_server < /tmp/main_server_dump.sql

# Configure Slave
echo "Configuring Slave..."
docker exec mysql-slave mysql -uroot -proot -e "
STOP SLAVE;
CHANGE MASTER TO 
  MASTER_HOST='mysql-master',
  MASTER_USER='replication',
  MASTER_PASSWORD='replication_password',
  MASTER_LOG_FILE='$MASTER_FILE',
  MASTER_LOG_POS=$MASTER_POSITION;
START SLAVE;
"

# Unlock tables on master
docker exec mysql-master mysql -uroot -proot -e "UNLOCK TABLES;"

# Create readonly user on slave
docker exec mysql-slave mysql -uroot -proot -e "
CREATE USER IF NOT EXISTS 'readonly_user'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON main_server.* TO 'readonly_user'@'%';
FLUSH PRIVILEGES;
"

# Check slave status
echo "Checking slave status..."
docker exec mysql-slave mysql -uroot -proot -e "SHOW SLAVE STATUS\G" | grep -E "Slave_IO_Running|Slave_SQL_Running"

echo "MySQL Master-Slave replication setup completed!"
echo "Master: localhost:3306 (mainuser/mainpassword)"
echo "Slave:  localhost:3307 (readonly_user/readonly_password)"