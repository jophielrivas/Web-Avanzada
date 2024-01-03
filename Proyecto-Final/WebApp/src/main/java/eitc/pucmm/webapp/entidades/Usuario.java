package eitc.pucmm.webapp.entidades;

public class Usuario {
    private int id;
    private String correo;
    private String nombre;
    private String permiso;
    private String telefono;
    private String direccion;

    public Usuario(){}
    public Usuario(String correo2, String nombre, String telefono2, String direccion2) {
        this.correo = correo2;
        this.nombre = nombre;
        this.telefono = telefono2;
        this.direccion = direccion2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPermiso() {
        return this.permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
