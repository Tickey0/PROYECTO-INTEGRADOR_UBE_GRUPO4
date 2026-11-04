package inventario.vista;

import inventario.Conexion;
import inventario.Sesion;
import inventario.dao.AccesoDAO;
import inventario.dao.EquipoDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuPrincipal extends JFrame {

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final AccesoDAO accesoDAO = new AccesoDAO();
    private final JLabel lblTotal = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblOperativos = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblMantenimiento = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblBaja = new JLabel("0", SwingConstants.CENTER);
    private final JButton btnEquipos = new JButton("Gestión de equipos");
    private final JButton btnReportes = new JButton("Reportes y gráficas");
    private final JButton btnUsuarios = new JButton("Gestión de usuarios");
    private final JButton btnAccesos = new JButton("Auditoría de accesos");
    private final JButton btnSalir = new JButton("Cerrar sesión");

    public MenuPrincipal() {
        super("Inventario del Laboratorio - Menú principal");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(760, 420);
        setLocationRelativeTo(null);

        JLabel bienvenida = new JLabel("Bienvenido, " + Sesion.getUsuario().getNombre()
                + "  |  Rol: " + Sesion.getUsuario().getRol());
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 15f));
        bienvenida.setBorder(BorderFactory.createEmptyBorder(12, 15, 8, 15));

        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 12, 0));
        tarjetas.setBorder(BorderFactory.createTitledBorder("Panel informativo del inventario"));
        tarjetas.add(crearTarjeta("Total de equipos", lblTotal, new Color(33, 97, 140)));
        tarjetas.add(crearTarjeta("Operativos", lblOperativos, new Color(30, 132, 73)));
        tarjetas.add(crearTarjeta("En mantenimiento", lblMantenimiento, new Color(202, 111, 30)));
        tarjetas.add(crearTarjeta("Dados de baja", lblBaja, new Color(176, 58, 46)));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        centro.add(tarjetas, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(2, 3, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(12, 12, 15, 12));
        botones.add(btnEquipos);
        botones.add(btnReportes);
        botones.add(btnUsuarios);
        botones.add(btnAccesos);
        botones.add(new JLabel());
        botones.add(btnSalir);

        btnEquipos.setEnabled(Sesion.puedeVer("equipos"));
        btnReportes.setEnabled(Sesion.puedeVer("reportes"));
        btnUsuarios.setEnabled(Sesion.puedeVer("usuarios"));
        btnAccesos.setEnabled(Sesion.puedeVer("accesos"));

        btnEquipos.addActionListener(e -> abrir(new EquipoFrame()));
        btnReportes.addActionListener(e -> abrir(new ReporteFrame()));
        btnUsuarios.addActionListener(e -> abrir(new UsuariosFrame()));
        btnAccesos.addActionListener(e -> abrir(new AccesosFrame()));
        btnSalir.addActionListener(e -> cerrarSesion());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                accesoDAO.registrar(Sesion.getUsuario().getUsuario(), "Salida");
                Conexion.cerrar();
                System.exit(0);
            }
        });

        add(bienvenida, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        actualizar();
    }

    private JPanel crearTarjeta(String titulo, JLabel valor, Color color) {
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 34f));
        valor.setForeground(color);
        JLabel etiqueta = new JLabel(titulo, SwingConstants.CENTER);
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createLineBorder(color, 2));
        tarjeta.add(valor, BorderLayout.CENTER);
        tarjeta.add(etiqueta, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void actualizar() {
        try {
            int[] cifras = equipoDAO.resumen();
            lblTotal.setText(String.valueOf(cifras[0]));
            lblOperativos.setText(String.valueOf(cifras[1]));
            lblMantenimiento.setText(String.valueOf(cifras[2]));
            lblBaja.setText(String.valueOf(cifras[3]));
        } catch (SQLException ex) {
            Utilidades.error(this, "No se pudo leer el resumen: " + ex.getMessage());
        }
    }

    private void abrir(JFrame ventana) {
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                actualizar();
            }
        });
        ventana.setVisible(true);
    }

    private void cerrarSesion() {
        accesoDAO.registrar(Sesion.getUsuario().getUsuario(), "Salida");
        Sesion.cerrar();
        for (Window w : Window.getWindows()) {
            w.dispose();
        }
        new LoginFrame().setVisible(true);
    }
}
