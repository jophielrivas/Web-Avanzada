package com.example.Tarea1.repo;

import com.example.Tarea1.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepo extends JpaRepository<Estudiante,Long> {
}
