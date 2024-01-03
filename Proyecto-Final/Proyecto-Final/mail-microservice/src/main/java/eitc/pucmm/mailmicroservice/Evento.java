package eitc.pucmm.mailmicroservice;

public class Evento {
    String nombre;
    int precio;
    int cantidad;

    public Evento(String nombre, int precio2, int carrito) {
        this.nombre = nombre;
        this.precio = precio2;
        this.cantidad = carrito;
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
}

    
