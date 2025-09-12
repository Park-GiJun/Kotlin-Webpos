CREATE DATABASE IF NOT EXISTS main_server;
CREATE DATABASE IF NOT EXISTS pos_server;

CREATE USER IF NOT EXISTS 'mainuser'@'%' IDENTIFIED BY 'mainpassword';
CREATE USER IF NOT EXISTS 'posuser'@'%' IDENTIFIED BY 'pospassword';

GRANT ALL PRIVILEGES ON main_server.* TO 'mainuser'@'%';
GRANT ALL PRIVILEGES ON pos_server.* TO 'posuser'@'%';

FLUSH PRIVILEGES;