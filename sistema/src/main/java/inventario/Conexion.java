package inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String OPCIONES = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private static String servidor = "localhost";
    private static int puerto = 3306;
    private static String usuario = "root";
    private static String clave = "";
    private static String base = "inventario_lab";
    private static String protocolo = "mysql";
    private static Connection conexion;

    private Conexion() {
    }

    public static void configurar(String servidor, int puerto, String usuario, String clave, String base) {
        if (!base.matches("[A-Za-z0-9_]{1,40}")) {
            throw new IllegalArgumentException("El nombre de la base de datos solo puede tener letras, números y guion bajo.");
        }
        cerrar();
        Conexion.servidor = servidor;
        Conexion.puerto = puerto;
        Conexion.usuario = usuario;
        Conexion.clave = clave;
        Conexion.base = base;
        Conexion.protocolo = "mysql";
    }

    public static String getBase() {
        return base;
    }

    public static Connection obtener() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = conectar(base);
        }
        return conexion;
    }

    public static Connection servidor() throws SQLException {
        return conectar("");
    }

    private static Connection conectar(String nombreBase) throws SQLException {
        try {
            return DriverManager.getConnection(url(nombreBase), usuario, clave);
        } catch (SQLException ex) {
            if (!protocolo.equals("mysql") || !falloDePlugin(ex)) {
                throw ex;
            }
            protocolo = "mariadb";
            return DriverManager.getConnection(url(nombreBase), usuario, clave);
        }
    }

    private static boolean falloDePlugin(SQLException ex) {
        String texto = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
        return texto.contains("authenticationplugin") || texto.contains("authentication plugin");
    }

    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
        conexion = null;
    }

    private static String url(String nombreBase) {
        String direccion = servidor + ":" + puerto + "/" + nombreBase;
        if (protocolo.equals("mariadb")) {
            return "jdbc:mariadb://" + direccion;
        }
        return "jdbc:mysql://" + direccion + OPCIONES;
    }
}
