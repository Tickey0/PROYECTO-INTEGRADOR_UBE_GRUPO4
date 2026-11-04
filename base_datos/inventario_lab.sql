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

CREATE TABLE IF NOT EXISTS categorias (
    id INT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS equipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    id_categoria INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    ubicacion VARCHAR(60) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    garantia TINYINT(1) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);

INSERT IGNORE INTO categorias (id, nombre) VALUES
    (1, 'Computadora'),
    (2, 'Monitor'),
    (3, 'Impresora'),
    (4, 'Proyector'),
    (5, 'Red');

INSERT IGNORE INTO equipos (codigo, nombre, id_categoria, estado, ubicacion, fecha_ingreso, costo, garantia) VALUES
    ('EQ-001', 'Computadora Dell OptiPlex 3080', 1, 'Operativo', 'Laboratorio 1', '2023-03-15', 780.00, 0),
    ('EQ-002', 'Computadora Dell OptiPlex 3080', 1, 'Operativo', 'Laboratorio 1', '2023-03-15', 780.00, 0),
    ('EQ-003', 'Computadora HP ProDesk 400', 1, 'Operativo', 'Laboratorio 2', '2024-02-20', 820.00, 1),
    ('EQ-004', 'Computadora HP ProDesk 400', 1, 'En mantenimiento', 'Laboratorio 2', '2024-02-20', 820.00, 1),
    ('EQ-005', 'Computadora Lenovo ThinkCentre M70', 1, 'Operativo', 'Laboratorio 3', '2025-08-11', 905.50, 1),
    ('EQ-006', 'Computadora Lenovo ThinkCentre M55', 1, 'Dado de baja', 'Bodega', '2019-05-06', 450.00, 0),
    ('EQ-007', 'Monitor LG 24 pulgadas', 2, 'Operativo', 'Laboratorio 1', '2023-03-15', 140.00, 0),
    ('EQ-008', 'Monitor LG 24 pulgadas', 2, 'Operativo', 'Laboratorio 2', '2024-02-20', 145.00, 1),
    ('EQ-009', 'Monitor Samsung 22 pulgadas', 2, 'En mantenimiento', 'Laboratorio 3', '2022-10-03', 120.00, 0),
    ('EQ-010', 'Monitor AOC 27 pulgadas', 2, 'Operativo', 'Laboratorio 3', '2025-08-11', 210.00, 1),
    ('EQ-011', 'Impresora Epson L3250', 3, 'Operativo', 'Laboratorio 1', '2024-06-18', 260.00, 1),
    ('EQ-012', 'Impresora HP LaserJet 107', 3, 'Dado de baja', 'Bodega', '2020-01-27', 180.00, 0),
    ('EQ-013', 'Proyector Epson X49', 4, 'Operativo', 'Laboratorio 2', '2023-09-09', 520.00, 0),
    ('EQ-014', 'Proyector BenQ MS550', 4, 'En mantenimiento', 'Laboratorio 3', '2022-04-22', 480.00, 0),
    ('EQ-015', 'Switch TP-Link de 24 puertos', 5, 'Operativo', 'Laboratorio 1', '2024-01-12', 150.00, 1),
    ('EQ-016', 'Router Mikrotik hAP ac2', 5, 'Operativo', 'Laboratorio 2', '2025-02-05', 95.00, 1),
    ('EQ-017', 'Access Point Ubiquiti UniFi', 5, 'Operativo', 'Laboratorio 3', '2025-02-05', 130.00, 1);

CREATE TABLE IF NOT EXISTS accesos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(30) NOT NULL,
    accion VARCHAR(20) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
