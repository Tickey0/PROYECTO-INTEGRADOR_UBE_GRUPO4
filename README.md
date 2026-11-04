# Proyecto Integrador - Lógica de la Programación

Aplicación de escritorio en Java para el control de inventario de equipos del laboratorio de cómputo.

Universidad Bolivariana del Ecuador - Carrera de Ingeniería en Sistemas Inteligentes - Grupo 4.

## Estructura

- `base_datos/`: script SQL de la base de datos `inventario_lab` (MySQL).
- `sistema/`: proyecto Java (Maven) que se abre con NetBeans.

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| operador | operador123 | Operador |
| invitado | invitado123 | Consulta |

## Cómo ejecutarlo

1. Tener MySQL encendido (MySQL Server instalado, con MySQL Workbench o cualquier otro cliente). Anotar el puerto, el usuario y la contraseña con los que se conecta en Workbench.
2. Abrir NetBeans y elegir File > Open Project.
3. Seleccionar la carpeta `sistema` del repositorio y presionar Open Project.
4. Clic derecho sobre el proyecto y elegir Run (la primera vez descarga el conector de MySQL, por eso necesita internet).
5. Al abrir el programa aparece la ventana **Conexión a MySQL**. Completar los datos de su propio MySQL:

| Campo | Qué escribir |
|---|---|
| Servidor | `localhost` |
| Puerto | El de su MySQL (normalmente `3306`) |
| Usuario | El de su MySQL (normalmente `root`) |
| Contraseña | La que usa en MySQL Workbench (vacía si no tiene) |
| Base de datos | El nombre que desee, por ejemplo `inventario_lab` |

6. Presionar Conectar. Si la base de datos no existe, el programa la crea solo con el script `base_datos/inventario_lab.sql`. No hace falta importar nada.
7. Ingresar con alguno de los usuarios de prueba.

Desde MySQL Workbench se puede ver la base creada (clic en el botón de refrescar en el panel Schemas).

## Servidores compatibles

El programa funciona con MySQL y con MariaDB. Usa primero el conector de MySQL y, si el servidor no lo acepta (por ejemplo, un MariaDB), cambia solo al conector de MariaDB. No hay que configurar nada.

## Prueba de conexión

Clic derecho sobre `PruebaConexion.java` y elegir Run File. Usa `localhost:3306`, usuario `root` y contraseña vacía; si su MySQL tiene otros datos, use la ventana de conexión del programa. En la consola debe aparecer: `Conexión correcta a inventario_lab`.
