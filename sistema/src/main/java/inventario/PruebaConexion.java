package inventario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {
        if (args.length == 5) {
            Conexion.configurar(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
        }
        try {
            BaseDatos.preparar();
            try (Connection c = Conexion.obtener();
                    Statement st = c.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM equipos")) {
                rs.next();
                System.out.println("Conexión correcta a " + Conexion.getBase() + ". Equipos registrados: " + rs.getInt(1));
            }
        } catch (Exception e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
        } finally {
            Conexion.cerrar();
        }
    }
}
