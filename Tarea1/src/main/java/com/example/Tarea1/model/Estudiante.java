package com.example.Tarea1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ESTUDIANTES")
@Setter
@Getter
@ToString
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "matricula",nullable = false,length = 50)
    private String matricula;

    @Column(name = "nombre",nullable = false,length = 50)
    private String nombre;

    @Column(name = "apellido",nullable = false,length = 50)
    private String apellido;

    @Column(name = "telefono",nullable = false,length = 50)
    private String telefono;

    public Estudiante( String matricula, String nombre, String apellido, String telefono) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public Estudiante() {

    }
}
