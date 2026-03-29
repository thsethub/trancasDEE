package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Usuarios.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    @Query("SELECT u FROM Usuarios u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuarios> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("""
            SELECT u FROM Usuarios u
            WHERE :nome = '' OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    Page<Usuarios> findAllFiltered(@Param("nome") String nome, Pageable pageable);

    @Query("""
            SELECT u FROM Usuarios u
            WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            AND u.cpf NOT IN (
                SELECT a.usuarios.cpf FROM Acesso a
                WHERE a.ambientes.id = :salaId
                AND a.usuarios IS NOT NULL
            )
            """)
    Page<Usuarios> findDisponiveisBySalaId(
            @Param("nome") String nome,
            @Param("salaId") Long salaId,
            Pageable pageable);
}
