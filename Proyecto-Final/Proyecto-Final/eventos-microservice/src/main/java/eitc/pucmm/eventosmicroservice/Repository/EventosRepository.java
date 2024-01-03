package eitc.pucmm.eventosmicroservice.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eitc.pucmm.eventosmicroservice.entidades.Evento;


public interface EventosRepository extends JpaRepository<Evento,Long>{
    List<Evento> findAllByIdCompra(long idCompra);
}
