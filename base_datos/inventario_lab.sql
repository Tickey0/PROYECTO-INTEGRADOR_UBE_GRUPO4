CREATE DATABASE IF NOT EXISTS inventario_lab CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE inventario_lab;

CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(30) NOT NULL UNIQUE,
    clave CHAR(64) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    id_rol INT NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS permisos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    modulo VARCHAR(30) NOT NULL,
    puede_ver TINYINT(1) NOT NULL DEFAULT 0,
    puede_editar TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE (id_rol, modulo),
    FOREIGN KEY (id_rol) REFERENCES roles(id)
);

INSERT IGNORE INTO roles (id, nombre) VALUES
    (1, 'Administrador'),
    (2, 'Operador'),
    (3, 'Consulta');

INSERT IGNORE INTO usuarios (usuario, clave, nombre, id_rol) VALUES
    ('admin', SHA2('admin123', 256), 'Administrador del sistema', 1),
    ('operador', SHA2('operador123', 256), 'Operador del laboratorio', 2),
    ('invitado', SHA2('invitado123', 256), 'Usuario de consulta', 3);

INSERT IGNORE INTO permisos (id_rol, modulo, puede_ver, puede_editar) VALUES
    (1, 'equipos', 1, 1),
    (1, 'reportes', 1, 1),
    (1, 'usuarios', 1, 1),
    (1, 'accesos', 1, 1),
    (2, 'equipos', 1, 1),
    (2, 'reportes', 1, 0),
    (2, 'usuarios', 0, 0),
    (2, 'accesos', 0, 0),
    (3, 'equipos', 0, 0),
    (3, 'reportes', 1, 0),
    (3, 'usuarios', 0, 0),
    (3, 'accesos', 0, 0);
