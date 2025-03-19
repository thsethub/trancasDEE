package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Usuarios.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
}
