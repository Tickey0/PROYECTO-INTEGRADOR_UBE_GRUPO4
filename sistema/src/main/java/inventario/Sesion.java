package inventario;

import inventario.modelo.Usuario;
import java.util.HashMap;
import java.util.Map;

public class Sesion {

    private static Usuario usuario;
    private static Map<String, boolean[]> permisos = new HashMap<>();

    private Sesion() {
    }

    public static void iniciar(Usuario u, Map<String, boolean[]> p) {
        usuario = u;
        permisos = p;
    }

    public static void cerrar() {
        usuario = null;
        permisos = new HashMap<>();
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static boolean puedeVer(String modulo) {
        boolean[] p = permisos.get(modulo);
        return p != null && p[0];
    }

    public static boolean puedeEditar(String modulo) {
        boolean[] p = permisos.get(modulo);
        return p != null && p[1];
    }

    public static boolean esAdministrador() {
        return usuario != null && "Administrador".equals(usuario.getRol());
    }
}
