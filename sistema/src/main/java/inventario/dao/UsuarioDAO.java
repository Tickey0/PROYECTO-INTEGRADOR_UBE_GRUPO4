package inventario.dao;

import inventario.Conexion;
import inventario.modelo.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    private static final String CONSULTA = "SELECT u.id, u.usuario, u.nombre, u.id_rol, r.nombre AS rol, u.activo "
            + "FROM usuarios u JOIN roles r ON r.id = u.id_rol";

    public Usuario autenticar(String usuario, String clave) throws SQLException {
        String sql = CONSULTA + " WHERE u.usuario = ? AND u.clave = SHA2(?, 256) AND u.activo = 1";
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return leer(rs);
                }
            }
        }
        return null;
    }

    public Map<String, boolean[]> permisosDe(int idRol) throws SQLException {
        Map<String, boolean[]> permisos = new HashMap<>();
        String sql = "SELECT modulo, puede_ver, puede_editar FROM permisos WHERE id_rol = ?";
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permisos.put(rs.getString("modulo"), new boolean[]{rs.getBoolean("puede_ver"), rs.getBoolean("puede_editar")});
                }
            }
        }
        return permisos;
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(CONSULTA + " ORDER BY u.id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leer(rs));
            }
        }
        return lista;
    }

    public List<String> nombresDeRoles() throws SQLException {
        List<String> roles = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("SELECT nombre FROM roles ORDER BY id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(rs.getString("nombre"));
            }
        }
        return roles;
    }

    public void crear(String usuario, String clave, String nombre, String rol) throws SQLException {
        String sql = "INSERT INTO usuarios (usuario, clave, nombre, id_rol) "
                + "VALUES (?, SHA2(?, 256), ?, (SELECT id FROM roles WHERE nombre = ?))";
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ps.setString(3, nombre);
            ps.setString(4, rol);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException {
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("UPDATE usuarios SET activo = ? WHERE id = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Usuario leer(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"), rs.getString("usuario"), rs.getString("nombre"),
                rs.getInt("id_rol"), rs.getString("rol"), rs.getBoolean("activo"));
    }
}
