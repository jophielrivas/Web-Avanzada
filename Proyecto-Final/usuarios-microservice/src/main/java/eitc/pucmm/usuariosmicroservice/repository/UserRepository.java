package eitc.pucmm.usuariosmicroservice.repository;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eitc.pucmm.usuariosmicroservice.entidades.EnumPermiso;
import eitc.pucmm.usuariosmicroservice.entidades.Usuario;

public interface UserRepository extends JpaRepository<Usuario,Long> {
    Usuario findByCorreo(String correo);
    @Query("select u.correo from Usuario u where u.permiso = :permiso")
    List<String> findCorreoByPermiso(@Param("permiso") EnumPermiso permiso);

    List<Usuario> findByPermiso(EnumPermiso permiso);
}
