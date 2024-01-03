package eitc.pucmm.eventosmicroservice.entidades;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Evento implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    String nombre;
    int precio;
    int cantidad; 
    private long idCompra;

    public Evento(String nombre, int precio2, int carrito) {
        this.nombre = nombre;
        this.precio = precio2;
        this.cantidad = carrito;
    }

    public Evento(){
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return this.precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdCompra() {
        return this.idCompra;
    }

    public void setIdCompra(long idCompra) {
        this.idCompra = idCompra;
    }

}
