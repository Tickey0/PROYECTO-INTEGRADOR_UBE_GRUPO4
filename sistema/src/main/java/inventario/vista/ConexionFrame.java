package inventario.vista;

import inventario.BaseDatos;
import inventario.Conexion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ConexionFrame extends JFrame {

    private final Preferences preferencias = Preferences.userNodeForPackage(ConexionFrame.class);
    private final JTextField txtServidor = new JTextField(preferencias.get("servidor", "localhost"), 18);
    private final JTextField txtPuerto = new JTextField(preferencias.get("puerto", "3306"), 18);
    private final JTextField txtUsuario = new JTextField(preferencias.get("usuario", "root"), 18);
    private final JPasswordField txtClave = new JPasswordField(18);
    private final JTextField txtBase = new JTextField(preferencias.get("base", "inventario_lab"), 18);

    public ConexionFrame() {
        super("Conexión a MySQL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Conexión a la base de datos MySQL", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
        Utilidades.agregarFila(formulario, 0, "Servidor:", txtServidor);
        Utilidades.agregarFila(formulario, 1, "Puerto:", txtPuerto);
        Utilidades.agregarFila(formulario, 2, "Usuario:", txtUsuario);
        Utilidades.agregarFila(formulario, 3, "Contraseña:", txtClave);
        Utilidades.agregarFila(formulario, 4, "Base de datos:", txtBase);

        JLabel nota = new JLabel("<html><center>Si la base de datos no existe, el programa la crea sola.<br>"
                + "Contraseña vacía si su MySQL no tiene.</center></html>", SwingConstants.CENTER);
        nota.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));

        JButton btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(e -> conectar());
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnConectar);
        botones.add(btnSalir);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(nota, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnConectar);
        add(titulo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void conectar() {
        String servidor = txtServidor.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        String base = txtBase.getText().trim();
        if (servidor.isEmpty() || usuario.isEmpty() || base.isEmpty()) {
            Utilidades.aviso(this, "Complete el servidor, el usuario y el nombre de la base de datos.");
            return;
        }
        int puerto;
        try {
            puerto = Integer.parseInt(txtPuerto.getText().trim());
        } catch (NumberFormatException ex) {
            Utilidades.aviso(this, "El puerto debe ser un número, por ejemplo 3306.");
            return;
        }
        if (puerto < 1 || puerto > 65535) {
            Utilidades.aviso(this, "El puerto debe estar entre 1 y 65535.");
            return;
        }
        if (!base.matches("[A-Za-z0-9_]{1,40}")) {
            Utilidades.aviso(this, "El nombre de la base de datos solo puede tener letras, números y guion bajo.");
            return;
        }
        Conexion.configurar(servidor, puerto, usuario, clave, base);
        try {
            BaseDatos.preparar();
        } catch (SQLException ex) {
            Utilidades.error(this, mensaje(ex, servidor, puerto));
            return;
        } catch (IOException ex) {
            Utilidades.error(this, ex.getMessage());
            return;
        }
        preferencias.put("servidor", servidor);
        preferencias.put("puerto", String.valueOf(puerto));
        preferencias.put("usuario", usuario);
        preferencias.put("base", base);
        new LoginFrame().setVisible(true);
        dispose();
    }

    private String mensaje(SQLException ex, String servidor, int puerto) {
        String estado = ex.getSQLState() == null ? "" : ex.getSQLState();
        if (ex.getErrorCode() == 1045 || estado.startsWith("28")) {
            return "El usuario o la contraseña de MySQL no son correctos.";
        }
        if (estado.startsWith("08")) {
            return "No se pudo conectar a " + servidor + ":" + puerto
                    + ".\nVerifique que MySQL esté encendido y que el puerto sea el correcto.";
        }
        return "No se pudo preparar la base de datos: " + ex.getMessage();
    }
}
