package inventario.modelo;

public class Usuario {

    private int id;
    private String usuario;
    private String nombre;
    private int idRol;
    private String rol;
    private boolean activo;

    public Usuario(int id, String usuario, String nombre, int idRol, String rol, boolean activo) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.idRol = idRol;
        this.rol = rol;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }
}
