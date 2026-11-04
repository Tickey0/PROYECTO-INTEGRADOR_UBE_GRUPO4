package inventario.vista;

import inventario.Sesion;
import inventario.dao.EquipoDAO;
import inventario.modelo.Equipo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class EquipoFrame extends JFrame {

    private final EquipoDAO dao = new EquipoDAO();
    private final JTextField txtCodigo = new JTextField(14);
    private final JTextField txtNombre = new JTextField(24);
    private final JComboBox<String> cbCategoria = new JComboBox<>();
    private final JComboBox<String> cbEstado = new JComboBox<>(Equipo.ESTADOS);
    private final JComboBox<String> cbUbicacion = new JComboBox<>(Equipo.UBICACIONES);
    private final JSpinner spFecha = new JSpinner(new SpinnerDateModel());
    private final JSpinner spCosto = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 100000.0, 10.0));
    private final JCheckBox chkGarantia = new JCheckBox("Con garantía vigente");
    private final DefaultTableModel modelo = Utilidades.modelo("ID", "Código", "Nombre", "Categoría", "Estado",
            "Ubicación", "Ingreso", "Costo (USD)", "Garantía");
    private final JTable tabla = new JTable(modelo);
    private final JButton btnGuardar = new JButton("Guardar nuevo");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnNuevo = new JButton("Nuevo / Limpiar");
    private List<Equipo> equipos = new ArrayList<>();
    private int idSeleccionado = 0;

    public EquipoFrame() {
        super("Gestión de equipos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));

        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del equipo"));
        Utilidades.agregarFila(formulario, 0, "Código (ej. EQ-018):", txtCodigo);
        Utilidades.agregarFila(formulario, 1, "Nombre:", txtNombre);
        Utilidades.agregarFila(formulario, 2, "Categoría:", cbCategoria);
        Utilidades.agregarFila(formulario, 3, "Estado:", cbEstado);
        Utilidades.agregarFila(formulario, 4, "Ubicación:", cbUbicacion);
        Utilidades.agregarFila(formulario, 5, "Fecha de ingreso:", spFecha);
        Utilidades.agregarFila(formulario, 6, "Costo (USD):", spCosto);
        Utilidades.agregarFila(formulario, 7, "Garantía:", chkGarantia);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        spCosto.setEditor(new JSpinner.NumberEditor(spCosto, "0.00"));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        Utilidades.anchos(tabla, 40, 70, 230, 100, 120, 100, 90, 90, 70);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(860, 230));

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });

        boolean puedeEditar = Sesion.puedeEditar("equipos");
        btnNuevo.setEnabled(puedeEditar);
        cargarCategorias();
        refrescarTabla();
        limpiar();
    }

    private void cargarCategorias() {
        try {
            for (String nombre : dao.categorias()) {
                cbCategoria.addItem(nombre);
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudieron cargar las categorías: " + ex.getMessage());
        }
    }

    private void refrescarTabla() {
        try {
            equipos = dao.listar();
            modelo.setRowCount(0);
            for (Equipo e : equipos) {
                modelo.addRow(new Object[]{e.getId(), e.getCodigo(), e.getNombre(), e.getCategoria(), e.getEstado(),
                    e.getUbicacion(), e.getFechaIngreso(), Utilidades.dinero(e.getCosto()), e.isGarantia() ? "Sí" : "No"});
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo leer el inventario: " + ex.getMessage());
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        tabla.clearSelection();
        txtCodigo.setText("");
        txtNombre.setText("");
        if (cbCategoria.getItemCount() > 0) {
            cbCategoria.setSelectedIndex(0);
        }
        cbEstado.setSelectedIndex(0);
        cbUbicacion.setSelectedIndex(0);
        spFecha.setValue(new Date());
        spCosto.setValue(100.0);
        chkGarantia.setSelected(false);
        actualizarBotones();
        txtCodigo.requestFocus();
    }

    private void actualizarBotones() {
        boolean puedeEditar = Sesion.puedeEditar("equipos");
        boolean haySeleccion = idSeleccionado != 0;
        btnGuardar.setEnabled(puedeEditar && !haySeleccion);
        btnActualizar.setEnabled(puedeEditar && haySeleccion);
        btnEliminar.setEnabled(Sesion.esAdministrador() && haySeleccion);
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (Integer) modelo.getValueAt(fila, 0);
        for (Equipo e : equipos) {
            if (e.getId() == id) {
                idSeleccionado = id;
                txtCodigo.setText(e.getCodigo());
                txtNombre.setText(e.getNombre());
                cbCategoria.setSelectedItem(e.getCategoria());
                cbEstado.setSelectedItem(e.getEstado());
                cbUbicacion.setSelectedItem(e.getUbicacion());
                spFecha.setValue(Utilidades.aDate(e.getFechaIngreso()));
                spCosto.setValue(e.getCosto());
                chkGarantia.setSelected(e.isGarantia());
                break;
            }
        }
        actualizarBotones();
    }

    private Equipo leerFormulario() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();
        if (!codigo.matches("[A-Z0-9-]{3,20}")) {
            Utilidades.aviso(this, "El código debe tener entre 3 y 20 letras, números o guiones.");
            return null;
        }
        if (nombre.isEmpty() || nombre.length() > 80) {
            Utilidades.aviso(this, "El nombre es obligatorio y no puede pasar de 80 caracteres.");
            return null;
        }
        LocalDate fecha = Utilidades.aFecha(spFecha.getValue());
        if (fecha.isAfter(LocalDate.now())) {
            Utilidades.aviso(this, "La fecha de ingreso no puede ser futura.");
            return null;
        }
        double costo = ((Number) spCosto.getValue()).doubleValue();
        if (costo <= 0) {
            Utilidades.aviso(this, "El costo debe ser mayor que cero.");
            return null;
        }
        try {
            Equipo e = new Equipo();
            e.setId(idSeleccionado);
            e.setCodigo(codigo);
            e.setNombre(nombre);
            e.setIdCategoria(dao.idCategoria((String) cbCategoria.getSelectedItem()));
            e.setEstado((String) cbEstado.getSelectedItem());
            e.setUbicacion((String) cbUbicacion.getSelectedItem());
            e.setFechaIngreso(fecha);
            e.setCosto(costo);
            e.setGarantia(chkGarantia.isSelected());
            return e;
        } catch (SQLException ex) {
            Utilidades.error(this, "Error de base de datos: " + ex.getMessage());
            return null;
        }
    }

    private void guardar() {
        Equipo e = leerFormulario();
        if (e == null) {
            return;
        }
        try {
            dao.insertar(e);
            Utilidades.info(this, "Equipo registrado correctamente.");
            refrescarTabla();
            limpiar();
        } catch (SQLException ex) {
            mostrarErrorGuardado(ex);
        }
    }

    private void actualizar() {
        Equipo e = leerFormulario();
        if (e == null) {
            return;
        }
        try {
            dao.actualizar(e);
            Utilidades.info(this, "Equipo actualizado correctamente.");
            refrescarTabla();
            limpiar();
        } catch (SQLException ex) {
            mostrarErrorGuardado(ex);
        }
    }

    private void eliminar() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el equipo " + txtCodigo.getText() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.eliminar(idSeleccionado);
            refrescarTabla();
            limpiar();
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo eliminar: " + ex.getMessage());
        }
    }

    private void mostrarErrorGuardado(SQLException ex) {
        if (ex.getErrorCode() == 1062) {
            Utilidades.aviso(this, "Ya existe un equipo con ese código.");
        } else {
            Utilidades.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }
}
