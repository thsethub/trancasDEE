package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {
    List<Acesso> findAcessoByUsuarios(Usuarios usuarios);
    List<Acesso> findAcessoByAmbientes(Ambientes ambientes);

    @Query("SELECT a FROM Acesso a WHERE a.usuarios.uniqueID = :uniqueID")
    List<Acesso> findAcessoByUniqueID(@Param("uniqueID") Long uniqueID);

    @Query("SELECT a FROM Acesso a WHERE a.usuarios.uniqueID = :uniqueID AND a.ambientes.sala = :sala")
    List<Acesso> findAcessoBySalaAndUniqueID(@Param("uniqueID") Long uniqueID, @Param("sala") String sala);
}

