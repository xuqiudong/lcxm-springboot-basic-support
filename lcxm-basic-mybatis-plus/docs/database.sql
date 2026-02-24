CREATE
    DATABASE database_name DEFAULT CHARSET utf8mb4;
-- GRANT ALL PRIVILEGES ON user_name.* TO 'database_name'@'localhost' IDENTIFIED BY '12345678';
-- mysql8
CREATE USER
    'user_name'@'localhost' IDENTIFIED BY '12345678';
GRANT ALL PRIVILEGES ON database_name.* TO 'user_name'@'localhost' WITH GRANT OPTION;

FLUSH
    PRIVILEGES;
