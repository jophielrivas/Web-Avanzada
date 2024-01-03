package eitc.pucmm.usuariosmicroservice.entidades;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;
    
    @Column(unique=true)
    private String correo;
    private String direccion;
    private String password;

    @Enumerated(EnumType.STRING)
    private EnumPermiso permiso;


    public Usuario() {}
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EnumPermiso getPermiso() {
        return this.permiso;
    }

    public void setPermiso(EnumPermiso permiso) {
        this.permiso = permiso;
    }

}
