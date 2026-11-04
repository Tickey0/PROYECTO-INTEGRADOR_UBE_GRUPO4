package inventario.dao;

import inventario.Conexion;
import inventario.modelo.Equipo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    static final String CONSULTA = "SELECT e.id, e.codigo, e.nombre, e.id_categoria, c.nombre AS categoria, e.estado, "
            + "e.ubicacion, e.fecha_ingreso, e.costo, e.garantia FROM equipos e JOIN categorias c ON c.id = e.id_categoria";

    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(CONSULTA + " ORDER BY e.codigo");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leer(rs));
            }
        }
        return lista;
    }

    public void insertar(Equipo e) throws SQLException {
        String sql = "INSERT INTO equipos (codigo, nombre, id_categoria, estado, ubicacion, fecha_ingreso, costo, garantia) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql)) {
            llenar(ps, e);
            ps.executeUpdate();
        }
    }

    public void actualizar(Equipo e) throws SQLException {
        String sql = "UPDATE equipos SET codigo = ?, nombre = ?, id_categoria = ?, estado = ?, ubicacion = ?, "
                + "fecha_ingreso = ?, costo = ?, garantia = ? WHERE id = ?";
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql)) {
            llenar(ps, e);
            ps.setInt(9, e.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("DELETE FROM equipos WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String> categorias() throws SQLException {
        List<String> lista = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("SELECT nombre FROM categorias ORDER BY id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        }
        return lista;
    }

    public int idCategoria(String nombre) throws SQLException {
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("SELECT id FROM categorias WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int[] resumen() throws SQLException {
        int[] cifras = new int[4];
        try (PreparedStatement ps = Conexion.obtener().prepareStatement("SELECT estado, COUNT(*) AS cantidad FROM equipos GROUP BY estado");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                cifras[0] += cantidad;
                String estado = rs.getString("estado");
                if (estado.equals(Equipo.ESTADOS[0])) {
                    cifras[1] = cantidad;
                } else if (estado.equals(Equipo.ESTADOS[1])) {
                    cifras[2] = cantidad;
                } else if (estado.equals(Equipo.ESTADOS[2])) {
                    cifras[3] = cantidad;
                }
            }
        }
        return cifras;
    }

    static Equipo leer(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setIdCategoria(rs.getInt("id_categoria"));
        e.setCategoria(rs.getString("categoria"));
        e.setEstado(rs.getString("estado"));
        e.setUbicacion(rs.getString("ubicacion"));
        e.setFechaIngreso(LocalDate.parse(rs.getString("fecha_ingreso").substring(0, 10)));
        e.setCosto(rs.getDouble("costo"));
        e.setGarantia(rs.getBoolean("garantia"));
        return e;
    }

    private void llenar(PreparedStatement ps, Equipo e) throws SQLException {
        ps.setString(1, e.getCodigo());
        ps.setString(2, e.getNombre());
        ps.setInt(3, e.getIdCategoria());
        ps.setString(4, e.getEstado());
        ps.setString(5, e.getUbicacion());
        ps.setString(6, e.getFechaIngreso().toString());
        ps.setDouble(7, e.getCosto());
        ps.setBoolean(8, e.isGarantia());
    }
}
