package inventario;

import inventario.vista.ConexionFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Se usa el estilo por defecto: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new ConexionFrame().setVisible(true));
    }
}
