package inventario.vista;

import inventario.dao.EquipoDAO;
import inventario.dao.ReporteDAO;
import inventario.modelo.Equipo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class ReporteFrame extends JFrame {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final JComboBox<String> cbCategoria = new JComboBox<>();
    private final JComboBox<String> cbEstado = new JComboBox<>();
    private final JCheckBox chkFechas = new JCheckBox("Filtrar por fecha de ingreso:");
    private final JSpinner spDesde = new JSpinner(new SpinnerDateModel());
    private final JSpinner spHasta = new JSpinner(new SpinnerDateModel());
    private final JSpinner spMinimo = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100000.0, 50.0));
    private final JSpinner spMaximo = new JSpinner(new SpinnerNumberModel(100000.0, 0.0, 100000.0, 50.0));
    private final JComboBox<String> cbAgrupar = new JComboBox<>(new String[]{"Categoría", "Estado", "Ubicación"});
    private final DefaultTableModel modelo = Utilidades.modelo("Código", "Nombre", "Categoría", "Estado",
            "Ubicación", "Ingreso", "Costo (USD)", "Garantía");
    private final JTable tabla = new JTable(modelo);
    private final GraficaPanel grafica = new GraficaPanel();
    private final JLabel lblResumen = new JLabel(" ");
    private List<Equipo> resultado;

    public ReporteFrame() {
        super("Reportes y gráficas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 700);
        setLocationRelativeTo(null);

        cbCategoria.addItem("Todas");
        cbEstado.addItem("Todos");
        for (String estado : Equipo.ESTADOS) {
            cbEstado.addItem(estado);
        }
        try {
            for (String nombre : new EquipoDAO().categorias()) {
                cbCategoria.addItem(nombre);
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudieron cargar las categorías: " + ex.getMessage());
        }

        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));
        spDesde.setValue(Utilidades.aDate(LocalDate.of(2019, 1, 1)));
        spHasta.setValue(new Date());
        spDesde.setEnabled(false);
        spHasta.setEnabled(false);
        chkFechas.addActionListener(e -> {
            spDesde.setEnabled(chkFechas.isSelected());
            spHasta.setEnabled(chkFechas.isSelected());
        });

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila1.add(new JLabel("Categoría:"));
        fila1.add(cbCategoria);
        fila1.add(new JLabel("Estado:"));
        fila1.add(cbEstado);
        fila1.add(new JLabel("Costo entre USD"));
        fila1.add(spMinimo);
        fila1.add(new JLabel("y USD"));
        fila1.add(spMaximo);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila2.add(chkFechas);
        fila2.add(new JLabel("Desde"));
        fila2.add(spDesde);
        fila2.add(new JLabel("Hasta"));
        fila2.add(spHasta);
        JButton btnAplicar = new JButton("Aplicar filtros");
        JButton btnLimpiar = new JButton("Limpiar filtros");
        fila2.add(btnAplicar);
        fila2.add(btnLimpiar);

        JPanel filtros = new JPanel(new java.awt.GridLayout(2, 1));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros del reporte"));
        filtros.add(fila1);
        filtros.add(fila2);

        JPanel panelGrafica = new JPanel(new BorderLayout());
        JPanel opcionesGrafica = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        opcionesGrafica.add(new JLabel("Agrupar la gráfica por:"));
        opcionesGrafica.add(cbAgrupar);
        panelGrafica.add(opcionesGrafica, BorderLayout.NORTH);
        panelGrafica.add(grafica, BorderLayout.CENTER);

        spMinimo.setEditor(new JSpinner.NumberEditor(spMinimo, "0.00"));
        spMaximo.setEditor(new JSpinner.NumberEditor(spMaximo, "0.00"));
        tabla.setRowHeight(22);
        Utilidades.anchos(tabla, 70, 240, 110, 120, 110, 90, 90, 70);

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabla), panelGrafica);
        division.setResizeWeight(0.45);
        division.setDividerLocation(240);

        lblResumen.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        add(filtros, BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);

        btnAplicar.addActionListener(e -> consultar());
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        cbAgrupar.addActionListener(e -> dibujar());
        consultar();
    }

    private void limpiarFiltros() {
        cbCategoria.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        spMinimo.setValue(0.0);
        spMaximo.setValue(100000.0);
        chkFechas.setSelected(false);
        spDesde.setEnabled(false);
        spHasta.setEnabled(false);
        consultar();
    }

    private void consultar() {
        double minimo = ((Number) spMinimo.getValue()).doubleValue();
        double maximo = ((Number) spMaximo.getValue()).doubleValue();
        if (minimo > maximo) {
            Utilidades.aviso(this, "El costo mínimo no puede ser mayor que el máximo.");
            return;
        }
        LocalDate desde = null;
        LocalDate hasta = null;
        if (chkFechas.isSelected()) {
            desde = Utilidades.aFecha(spDesde.getValue());
            hasta = Utilidades.aFecha(spHasta.getValue());
            if (desde.isAfter(hasta)) {
                Utilidades.aviso(this, "La fecha inicial no puede ser posterior a la fecha final.");
                return;
            }
        }
        String categoria = cbCategoria.getSelectedIndex() == 0 ? null : (String) cbCategoria.getSelectedItem();
        String estado = cbEstado.getSelectedIndex() == 0 ? null : (String) cbEstado.getSelectedItem();
        try {
            resultado = reporteDAO.filtrar(categoria, estado, desde, hasta, minimo, maximo);
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo generar el reporte: " + ex.getMessage());
            return;
        }
        modelo.setRowCount(0);
        double total = 0;
        for (Equipo e : resultado) {
            modelo.addRow(new Object[]{e.getCodigo(), e.getNombre(), e.getCategoria(), e.getEstado(), e.getUbicacion(),
                e.getFechaIngreso(), Utilidades.dinero(e.getCosto()), e.isGarantia() ? "Sí" : "No"});
            total += e.getCosto();
        }
        lblResumen.setText("Equipos encontrados: " + resultado.size() + "   |   Costo total: USD " + Utilidades.dinero(total));
        dibujar();
    }

    private void dibujar() {
        if (resultado == null) {
            return;
        }
        String criterio = (String) cbAgrupar.getSelectedItem();
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Equipo e : resultado) {
            String clave;
            if ("Estado".equals(criterio)) {
                clave = e.getEstado();
            } else if ("Ubicación".equals(criterio)) {
                clave = e.getUbicacion();
            } else {
                clave = e.getCategoria();
            }
            Integer actual = conteo.get(clave);
            conteo.put(clave, actual == null ? 1 : actual + 1);
        }
        grafica.setDatos("Cantidad de equipos por " + criterio.toLowerCase(), conteo);
    }
}
