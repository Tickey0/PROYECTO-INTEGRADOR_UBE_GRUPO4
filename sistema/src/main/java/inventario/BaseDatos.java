package inventario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDatos {

    private static final String SCRIPT = "/inventario_lab.sql";

    private BaseDatos() {
    }

    public static void preparar() throws SQLException, IOException {
        try (Connection c = Conexion.servidor()) {
            if (!existeTabla(c, "accesos")) {
                ejecutarScript(c);
            }
        }
    }

    private static boolean existeTabla(Connection c, String tabla) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, Conexion.getBase());
            ps.setString(2, tabla);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static void ejecutarScript(Connection c) throws SQLException, IOException {
        InputStream entrada = BaseDatos.class.getResourceAsStream(SCRIPT);
        if (entrada == null) {
            throw new IOException("No se encontró el archivo inventario_lab.sql. Abra el repositorio completo (carpetas base_datos y sistema).");
        }
        StringBuilder texto = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                texto.append(linea).append('\n');
            }
        }
        String base = Conexion.getBase();
        try (Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS `" + base + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            st.execute("USE `" + base + "`");
            for (String sentencia : texto.toString().split(";")) {
                String s = sentencia.trim();
                if (s.isEmpty() || s.startsWith("CREATE DATABASE") || s.startsWith("USE ")) {
                    continue;
                }
                st.execute(s);
            }
        }
    }
}
