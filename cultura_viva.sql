CREATE DATABASE IF NOT EXISTS cultura_viva
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'cultura_viva_app'@'localhost'
IDENTIFIED BY 'Cultur@_viv@_pucp';

ALTER USER 'cultura_viva_app'@'localhost'
IDENTIFIED BY 'Cultur@_viv@_pucp';

GRANT ALL PRIVILEGES
ON cultura_viva.*
TO 'cultura_viva_app'@'localhost';

FLUSH PRIVILEGES;

SHOW DATABASES;

SELECT
    user,
    host
FROM mysql.user
WHERE user = 'cultura_viva_app';