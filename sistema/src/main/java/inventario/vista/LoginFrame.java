package inventario.vista;

import inventario.Sesion;
import inventario.dao.AccesoDAO;
import inventario.dao.UsuarioDAO;
import inventario.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AccesoDAO accesoDAO = new AccesoDAO();

    public LoginFrame() {
        super("Inventario del Laboratorio - Ingreso");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Control de Inventario del Laboratorio", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 17f));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        Utilidades.agregarFila(formulario, 0, "Usuario:", txtUsuario);
        Utilidades.agregarFila(formulario, 1, "Contraseña:", txtClave);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> ingresar());
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnIngresar);
        botones.add(btnSalir);

        getRootPane().setDefaultButton(btnIngresar);
        add(titulo, BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            Utilidades.aviso(this, "Ingrese el usuario y la contraseña.");
            return;
        }
        try {
            Usuario u = usuarioDAO.autenticar(usuario, clave);
            if (u == null) {
                accesoDAO.registrar(usuario, "Intento fallido");
                Utilidades.error(this, "Usuario o contraseña incorrectos, o el usuario está inactivo.");
                txtClave.setText("");
                txtClave.requestFocus();
                return;
            }
            Sesion.iniciar(u, usuarioDAO.permisosDe(u.getIdRol()));
            accesoDAO.registrar(u.getUsuario(), "Ingreso");
            new MenuPrincipal().setVisible(true);
            dispose();
        } catch (SQLException ex) {
            Utilidades.error(this, "Error de base de datos: " + ex.getMessage());
        }
    }
}
