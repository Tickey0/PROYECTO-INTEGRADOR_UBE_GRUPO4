package inventario.vista;

import inventario.dao.AccesoDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AccesosFrame extends JFrame {

    private final AccesoDAO dao = new AccesoDAO();
    private final DefaultTableModel modelo = Utilidades.modelo("Fecha y hora", "Usuario", "Acción");

    public AccesosFrame() {
        super("Auditoría de accesos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 480);
        setLocationRelativeTo(null);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnActualizar);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        cargar();
    }

    private void cargar() {
        try {
            modelo.setRowCount(0);
            for (String[] fila : dao.ultimos(200)) {
                modelo.addRow(fila);
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo leer la auditoría: " + ex.getMessage());
        }
    }
}
