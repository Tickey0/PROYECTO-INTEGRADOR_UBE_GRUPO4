package inventario.dao;

import inventario.Conexion;
import inventario.modelo.Equipo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<Equipo> filtrar(String categoria, String estado, LocalDate desde, LocalDate hasta,
            double costoMinimo, double costoMaximo) throws SQLException {
        StringBuilder sql = new StringBuilder(EquipoDAO.CONSULTA);
        sql.append(" WHERE e.costo BETWEEN ? AND ?");
        List<Object> valores = new ArrayList<>();
        valores.add(costoMinimo);
        valores.add(costoMaximo);
        if (categoria != null) {
            sql.append(" AND c.nombre = ?");
            valores.add(categoria);
        }
        if (estado != null) {
            sql.append(" AND e.estado = ?");
            valores.add(estado);
        }
        if (desde != null && hasta != null) {
            sql.append(" AND e.fecha_ingreso BETWEEN ? AND ?");
            valores.add(desde.toString());
            valores.add(hasta.toString());
        }
        sql.append(" ORDER BY e.codigo");

        List<Equipo> lista = new ArrayList<>();
        try (PreparedStatement ps = Conexion.obtener().prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(EquipoDAO.leer(rs));
                }
            }
        }
        return lista;
    }
}
