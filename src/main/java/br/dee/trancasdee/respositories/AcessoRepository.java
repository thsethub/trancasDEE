package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {
    List<Acesso> findAcessoByUsuarios(Usuarios usuarios);
}
