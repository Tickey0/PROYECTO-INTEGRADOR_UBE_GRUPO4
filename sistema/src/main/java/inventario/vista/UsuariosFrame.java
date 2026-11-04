package inventario.vista;

import inventario.Sesion;
import inventario.dao.UsuarioDAO;
import inventario.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class UsuariosFrame extends JFrame {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final JTextField txtUsuario = new JTextField(14);
    private final JPasswordField txtClave = new JPasswordField(14);
    private final JTextField txtNombre = new JTextField(24);
    private final JComboBox<String> cbRol = new JComboBox<>();
    private final DefaultTableModel modelo = Utilidades.modelo("ID", "Usuario", "Nombre", "Rol", "Estado");
    private final JTable tabla = new JTable(modelo);
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuariosFrame() {
        super("Gestión de usuarios");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Nuevo usuario"));
        Utilidades.agregarFila(formulario, 0, "Usuario (minúsculas, sin espacios):", txtUsuario);
        Utilidades.agregarFila(formulario, 1, "Contraseña (mínimo 6 caracteres):", txtClave);
        Utilidades.agregarFila(formulario, 2, "Nombre completo:", txtNombre);
        Utilidades.agregarFila(formulario, 3, "Rol:", cbRol);

        JButton btnCrear = new JButton("Crear usuario");
        JButton btnEstado = new JButton("Activar / Desactivar seleccionado");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnCrear);
        botones.add(btnEstado);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crear());
        btnEstado.addActionListener(e -> cambiarEstado());

        try {
            for (String rol : dao.nombresDeRoles()) {
                cbRol.addItem(rol);
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudieron cargar los roles: " + ex.getMessage());
        }
        refrescar();
    }

    private void refrescar() {
        try {
            usuarios = dao.listar();
            modelo.setRowCount(0);
            for (Usuario u : usuarios) {
                modelo.addRow(new Object[]{u.getId(), u.getUsuario(), u.getNombre(), u.getRol(), u.isActivo() ? "Activo" : "Inactivo"});
            }
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo leer la lista de usuarios: " + ex.getMessage());
        }
    }

    private void crear() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        String nombre = txtNombre.getText().trim();
        if (!usuario.matches("[a-z0-9_]{4,20}")) {
            Utilidades.aviso(this, "El usuario debe tener de 4 a 20 letras minúsculas, números o guion bajo.");
            return;
        }
        if (clave.length() < 6) {
            Utilidades.aviso(this, "La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        if (nombre.isEmpty()) {
            Utilidades.aviso(this, "Ingrese el nombre completo.");
            return;
        }
        try {
            dao.crear(usuario, clave, nombre, (String) cbRol.getSelectedItem());
            Utilidades.info(this, "Usuario creado correctamente.");
            txtUsuario.setText("");
            txtClave.setText("");
            txtNombre.setText("");
            refrescar();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Utilidades.aviso(this, "Ese nombre de usuario ya existe.");
            } else {
                Utilidades.error(this, "No se pudo crear el usuario: " + ex.getMessage());
            }
        }
    }

    private void cambiarEstado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Utilidades.aviso(this, "Seleccione un usuario de la tabla.");
            return;
        }
        Usuario u = usuarios.get(fila);
        if (u.getId() == Sesion.getUsuario().getId()) {
            Utilidades.aviso(this, "No puede desactivar su propio usuario.");
            return;
        }
        try {
            dao.cambiarEstado(u.getId(), !u.isActivo());
            refrescar();
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo cambiar el estado: " + ex.getMessage());
        }
    }
}
