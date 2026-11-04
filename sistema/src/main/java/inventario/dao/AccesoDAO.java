package inventario.dao;

import inventario.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccesoDAO {

    public void registrar(String usuario, String accion) {
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("INSERT INTO accesos (usuario, accion) VALUES (?, ?)")) {
            ps.setString(1, usuario);
            ps.setString(2, accion);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No se pudo registrar el acceso: " + e.getMessage());
        }
    }

    public List<String[]> ultimos(int limite) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("SELECT fecha_hora, usuario, accion FROM accesos ORDER BY id DESC LIMIT ?")) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha_hora");
                    lista.add(new String[]{fecha.substring(0, 19), rs.getString("usuario"), rs.getString("accion")});
                }
            }
        }
        return lista;
    }
}
